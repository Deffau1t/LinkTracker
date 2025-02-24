package backend.academy.bot.service;

import backend.academy.bot.model.LinkUpdate;
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

@Getter
@Service
@RequiredArgsConstructor
@Slf4j
@AllArgsConstructor
@Setter
public class LinkUpdateService {

    private Map<Long, List<String>> chatSubscribes = new HashMap<>();

    public void startCommand(Long chatId, LinkTrackerBot linkTrackerBot) {
        linkTrackerBot.sendMessage(chatId, "Вы успешно зарегистрированы.");
    }

    public void helpCommand(Long chatId, LinkTrackerBot linkTrackerBot) {
        String helpMessage = """
            /start - Начало работы бота
            /help - Все доступные команды бота
            /track - Подписаться на какой-то источник
            /untrack - Отписаться от какого-то источника
            /list - Показать все отслеживаемые источники
            """;
        linkTrackerBot.sendMessage(chatId, helpMessage);
    }

    public void trackCommand(Long chatId, String link, LinkTrackerBot linkTrackerBot) {
        if (link.isEmpty()) {
            linkTrackerBot.sendMessage(chatId, "Некорректная ссылка для добавления.");
        } else {
            chatSubscribes.computeIfAbsent(chatId, _ -> new ArrayList<>()).add(link);
            linkTrackerBot.sendMessage(chatId, "Ссылка успешно добавлена.");
        }
    }

    public void untrackCommand(Long chatId, String link, LinkTrackerBot linkTrackerBot) {
        try {
            List<String> links = chatSubscribes.get(chatId);
            boolean removed = links.remove(link);
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
            List<String> linksOfChatId = chatSubscribes.get(chatId);
            for (String link : linksOfChatId) {
                linksList.append(link);
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
            List<Long> chatIds = linkUpdate.tgChatIds();
            for (Long chatId : chatIds) {
                linkTrackerBot.sendMessage(chatId, """
                    Есть обновление на %s
                    %s
                    """.formatted(linkUpdate.url(), linkUpdate.description())
                );
            }
        } catch (NullPointerException nullPointerException) {
            log.error(nullPointerException.getMessage());
        }
    }
}
