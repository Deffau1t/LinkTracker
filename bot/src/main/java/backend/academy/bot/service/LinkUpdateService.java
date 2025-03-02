package backend.academy.bot.service;

import backend.academy.bot.model.LinkUpdate;
import java.util.*;
import java.util.stream.Collectors;
import backend.academy.bot.model.TrackedLink;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Getter
@Service
@RequiredArgsConstructor
@Slf4j
@AllArgsConstructor
@Setter
public class LinkUpdateService {

    private Map<Long, List<TrackedLink>> chatSubscribes = new HashMap<>();

    public void startCommand(Long chatId, LinkTrackerBot linkTrackerBot) {
        linkTrackerBot.sendMessage(chatId, "Вы успешно зарегистрированы.");
        chatSubscribes.put(chatId, new ArrayList<>());
    }

    public void helpCommand(Long chatId, LinkTrackerBot linkTrackerBot) {
        String helpMessage = """
            /start - регистрация пользователя.
            /help - вывод списка доступных команд.
            /track - начать отслеживание ссылки.
            /untrack - прекратить отслеживание ссылки.
            /list - показать список отслеживаемых ссылок.
            """;
        linkTrackerBot.sendMessage(chatId, helpMessage);
    }

    public void trackCommand(Long chatId, String link, String tags, String filters, LinkTrackerBot linkTrackerBot) {
        if (link.isEmpty()) {
            linkTrackerBot.sendMessage(chatId, "Некорректная ссылка для добавления.");
        } else {
            if (chatSubscribes.get(chatId) != null){
                List<TrackedLink> trackedLinks = chatSubscribes.get(chatId);
                for (TrackedLink trackedLink : trackedLinks) {
                    if (trackedLink.url().equals(link)) {
                        linkTrackerBot.sendMessage(chatId, "Вы уже подписаны на эту ссылку.");
                        return;
                    }
                }
            }
            chatSubscribes.computeIfAbsent(chatId, _ -> new ArrayList<>())
                    .add(new TrackedLink(link, tags, filters));
            linkTrackerBot.sendMessage(chatId, "Ссылка успешно добавлена с тэгами: " + tags + "\nФильтрами: " + filters);
        }
    }

    public void untrackCommand(Long chatId, String link, LinkTrackerBot linkTrackerBot) {
        try {
            List<TrackedLink> links = chatSubscribes.get(chatId);
            boolean removed = links.removeIf(trackedLink -> trackedLink.url().equals(link));
            if (removed) {
                linkTrackerBot.sendMessage(chatId, "Ссылка успешно удалена.");
            } else {
                linkTrackerBot.sendMessage(chatId, "Вы не были подписаны на эту ссылку.");
            }
        } catch (NullPointerException nullPointerException) {
            linkTrackerBot.sendMessage(chatId, "Некорректная ссылка для удаления.");
            log.error(nullPointerException.getMessage());
        } catch (Exception e) {
            linkTrackerBot.sendMessage(chatId, "Что-то пошло не так...");
            log.error(e.getMessage());
        }
    }

    public void listCommand(Long chatId, LinkTrackerBot linkTrackerBot) {
        StringBuilder linksList = new StringBuilder();
        try {
            List<TrackedLink> linksOfChatId = chatSubscribes.get(chatId);
            for (TrackedLink trackedLink : linksOfChatId) {
                linksList.append(trackedLink).append("\n");
            }
            linkTrackerBot.sendMessage(chatId, "Ваши ссылки:\n" + linksList);
        } catch (NullPointerException nullPointerException) {
            linkTrackerBot.sendMessage(chatId, "Ссылки отсутствуют.");
            log.warn(nullPointerException.getMessage());
        }
    }

    public void unknownCommand(Long chatId, LinkTrackerBot linkTrackerBot) {
        linkTrackerBot.sendMessage(chatId, "Некорректная команда.");
    }

    public void updateLink(LinkUpdate linkUpdate, LinkTrackerBot linkTrackerBot) {
        try {
            if (linkUpdate == null || linkUpdate.tgChatIds() == null) {
                log.warn("Получено некорректное обновление: {}", linkUpdate);
                return;
            }

            for (Long chatId : linkUpdate.tgChatIds()) {
                List<TrackedLink> linksOfChatId = chatSubscribes.get(chatId);
                if (linksOfChatId == null || linksOfChatId.isEmpty()) {
                    log.warn("Пользователь {} не подписан ни на одну ссылку", chatId);
                    continue;
                }

                boolean isSubscribed = linksOfChatId.stream()
                        .anyMatch(trackedLink -> trackedLink.url().equals(linkUpdate.url()));

                if (isSubscribed) {
                    linkTrackerBot.sendMessage(chatId,
                        "\uD83D\uDD14 Обновление на " + linkUpdate.url() + "\n" + linkUpdate.description());
                } else {
                    log.warn("Пользователь {} не подписан на ссылку {}", chatId, linkUpdate.url());
                }
            }
        } catch (Exception e) {
            log.error("Ошибка при обработке обновления: {}", e.getMessage());
        }
    }

    public boolean isTrackingLink(Long chatId, String link) {
        List<TrackedLink> links = chatSubscribes.get(chatId);
        if (links == null) {
            return false;
        }
        return links.stream().anyMatch(trackedLink -> trackedLink.url().equals(link));
    }

    public Set<String> getAllTrackedLinks() {
        return chatSubscribes.values().stream()
            .flatMap(List::stream)
            .map(TrackedLink::url)
            .collect(Collectors.toSet());
    }


}
