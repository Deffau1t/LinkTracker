package backend.academy.bot.service;

import backend.academy.bot.model.LinkUpdate;
import backend.academy.bot.model.TrackedLink;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * LinkUpdateService - сервис для обновления ссылок.
 */

@Getter
@Service
@RequiredArgsConstructor
@Slf4j
@AllArgsConstructor
@Setter
public class LinkUpdateService {
    /**
     * chatSubscribes - список подписок пользователей на ссылки.
     */
    private Map<Long, List<TrackedLink>> chatSubscribes = new HashMap<>();

    /**
     * startCommand - регистрация пользователя.
     * @param chatId - id чата
     * @param linkTrackerBot - бот Telegram
     */
    public void startCommand(final Long chatId,
                             final LinkTrackerBot linkTrackerBot) {
        linkTrackerBot.sendMessage(chatId, "Вы успешно зарегистрированы.");
        chatSubscribes.put(chatId, new ArrayList<>());
    }

    /**
     * helpCommand - вывод списка доступных команд.
     * @param chatId - id чата
     * @param linkTrackerBot - бот Telegram
     */
    public void helpCommand(final Long chatId,
                            final LinkTrackerBot linkTrackerBot) {
        String helpMessage = """
            /start - регистрация пользователя.
            /help - вывод списка доступных команд.
            /track - начать отслеживание ссылки.
            /untrack - прекратить отслеживание ссылки.
            /list - показать список отслеживаемых ссылок.
            """;
        linkTrackerBot.sendMessage(chatId, helpMessage);
    }

    /**
     * trackCommand - добавление ссылки.
     * @param chatId - id чата
     * @param link - ссылка для добавления
     * @param tags - теги для добавления
     * @param filters - фильтры для добавления
     * @param linkTrackerBot - бот Telegram
     */
    public void trackCommand(final Long chatId,
                             final String link,
                             final String tags,
                             final String filters,
                             final LinkTrackerBot linkTrackerBot) {
        if (link.isEmpty()) {
            linkTrackerBot.sendMessage(chatId,
                "Некорректная ссылка для добавления.");
        } else {
            if (chatSubscribes.get(chatId) != null) {
                List<TrackedLink> trackedLinks = chatSubscribes.get(chatId);
                for (TrackedLink trackedLink : trackedLinks) {
                    if (trackedLink.url().equals(link)) {
                        linkTrackerBot.sendMessage(chatId,
                            "Вы уже подписаны на эту ссылку.");
                        return;
                    }
                }
            }
            chatSubscribes.computeIfAbsent(chatId, _ -> new ArrayList<>())
                    .add(new TrackedLink(link, tags, filters));
            linkTrackerBot.sendMessage(chatId,
                "Ссылка успешно добавлена с тэгами: "
                    + tags
                    + "\nФильтрами: "
                    + filters);
        }
    }

    /**
     * untrackCommand - удаление ссылки.
     * @param chatId - id чата
     * @param link - ссылка для удаления
     * @param linkTrackerBot - бот Telegram
     */
    public void untrackCommand(final Long chatId,
                               final String link,
                               final LinkTrackerBot linkTrackerBot) {
        try {
            List<TrackedLink> links = chatSubscribes.get(chatId);

            boolean removed = links.removeIf(
                trackedLink -> trackedLink.url().equals(link)
            );

            if (removed) {
                linkTrackerBot.sendMessage(chatId, "Ссылка успешно удалена.");
            } else {
                linkTrackerBot.sendMessage(
                    chatId,
                    "Вы не были подписаны на эту ссылку.");
            }
        } catch (NullPointerException nullPointerException) {
            linkTrackerBot.sendMessage(chatId,
                "Некорректная ссылка для удаления.");
            log.error(nullPointerException.getMessage());
        } catch (Exception e) {
            linkTrackerBot.sendMessage(chatId, "Что-то пошло не так...");
            log.error(e.getMessage());
        }
    }

    /**
     * listCommand - вывод списка ссылок.
     * @param chatId - id чата
     * @param linkTrackerBot - бот Telegram
     */
    public void listCommand(final Long chatId,
                            final LinkTrackerBot linkTrackerBot) {
        StringBuilder linksList = new StringBuilder();
        try {
            List<TrackedLink> linksOfChatId = chatSubscribes.get(chatId);
            for (TrackedLink trackedLink : linksOfChatId) {
                linksList.append(trackedLink.url()).append("\n");
            }
            linkTrackerBot.sendMessage(chatId, "Ваши ссылки:\n" + linksList);
        } catch (NullPointerException nullPointerException) {
            linkTrackerBot.sendMessage(chatId, "Ссылки отсутствуют.");
            log.warn(nullPointerException.getMessage());
        }
    }

    /**
     * unknownCommand - вывод сообщения об ошибке.
     * @param chatId - id чата
     * @param linkTrackerBot - бот Telegram
     */
    public void unknownCommand(final Long chatId,
                               final LinkTrackerBot linkTrackerBot) {
        linkTrackerBot.sendMessage(chatId, "Некорректная команда.");
    }

    /**
     * Метод для обновления ссылок.
     * @param linkUpdate - обновление ссылки.
     * @param linkTrackerBot - бот.
     */
    public void updateLink(final LinkUpdate linkUpdate,
                           final LinkTrackerBot linkTrackerBot) {
        try {
            if (linkUpdate == null || linkUpdate.tgChatIds() == null) {
                log.warn("Получено некорректное обновление: {}", linkUpdate);
                return;
            }

            for (Long chatId : linkUpdate.tgChatIds()) {
                List<TrackedLink> linksOfChatId = chatSubscribes.get(chatId);
                if (linksOfChatId == null || linksOfChatId.isEmpty()) {
                    log.warn("Пользователь {} не подписан ни на одну ссылку",
                        chatId);
                    continue;
                }

                boolean isSubscribed = linksOfChatId.stream()
                        .anyMatch(
                            trackedLink -> trackedLink
                                .url()
                                .equals(linkUpdate.url())
                        );

                if (isSubscribed) {
                    linkTrackerBot.sendMessage(chatId,
                        "\uD83D\uDD14 Обновление на "
                            + linkUpdate.url()
                            + "\n"
                            + linkUpdate.description());
                } else {
                    log.warn("Пользователь {} не подписан на ссылку {}",
                        chatId,
                        linkUpdate.url());
                }
            }
        } catch (Exception e) {
            log.error("Ошибка при обработке обновления: {}", e.getMessage());
        }
    }

    /**
     * Метод для проверки наличия ссылки в списке отслеживаемых.
     * @param chatId - id чата.
     * @param link - ссылка.
     * @return true, если ссылка отслеживается, иначе false.
     */
    public boolean isTrackingLink(final Long chatId, final String link) {
        List<TrackedLink> links = chatSubscribes.get(chatId);
        if (links == null) {
            return false;
        }
        return links.stream().anyMatch(
            trackedLink -> trackedLink.url().equals(link)
        );
    }
}
