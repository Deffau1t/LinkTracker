package backend.academy.bot.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import backend.academy.bot.model.LinkUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LinkUpdateService {

    @Autowired
    @Lazy
    private LinkTrackerBot linkTrackerBot;
    private Map<Long, List<String>> chatSubscribes = new HashMap<>();;

    public void startCommand(Long chatId) {
        linkTrackerBot.sendMessage(chatId, "Вы успешно зарегистрированы.");
    }

    public void helpCommand(Long chatId) {
        String helpMessage = """
            /start - Начало работы бота
            /help - Все доступные команды бота
            /track - Подписаться на какой-то источник
            /untrack - Отписаться от какого-то источника
            /list - Показать все отслеживаемые источники
            """;
        linkTrackerBot.sendMessage(chatId, helpMessage);
    }

    public void trackCommand(Long chatId, String link) {
        if (link.isEmpty()) {
            linkTrackerBot.sendMessage(chatId, "Некорректная ссылка для добавления.");
        } else {
            chatSubscribes.computeIfAbsent(chatId, _ -> new ArrayList<>()).add(link);
            linkTrackerBot.sendMessage(chatId, "Ссылка успешно добавлена.");
        }
    }

    public void untrackCommand(Long chatId, String link) {
        if (link.isEmpty()) {
            linkTrackerBot.sendMessage(chatId, "Некорректная ссылка для удаления.");
            return;
        }

        List<String> links = chatSubscribes.get(chatId);
        if (links == null || links.isEmpty()) {
            linkTrackerBot.sendMessage(chatId, "Вы не были подписаны на эту ссылку.");
            return;
        }

        boolean removed = links.remove(link);
        if (removed) {
            linkTrackerBot.sendMessage(chatId, "Ссылка успешно удалена.");
        } else {
            linkTrackerBot.sendMessage(chatId, "Вы не были подписаны на эту ссылку.");
        }
    }

    public void listCommand(Long chatId) {
        StringBuilder linksList = new StringBuilder();
        List<String> linksOfChatId = chatSubscribes.get(chatId);
        if (linksOfChatId == null || linksOfChatId.isEmpty()) {
            linkTrackerBot.sendMessage(chatId, "Ссылки отсутствуют.");
        } else {
            for (String link : chatSubscribes.get(chatId)) {
                linksList.append(link);
            }
            linkTrackerBot.sendMessage(chatId, "Ваши ссылки:\n" + linksList);
        }

    }

    public void unknownCommand(Long chatId) {
        linkTrackerBot.sendMessage(chatId, "Некорректная команда.");
    }

    public void updateLink(LinkUpdate linkUpdate) {
        if (linkUpdate == null) {
            return;
        }

        List<Long> chatIds = linkUpdate.tgChatIds();
        if (chatIds == null || chatIds.isEmpty()) {
            return;
        }

        for (Long chatId : linkUpdate.tgChatIds()) {
            linkTrackerBot.sendMessage(chatId, """
                Есть обновление на %s
                %s
                """.formatted(linkUpdate.url(), linkUpdate.description())
            );
        }
    }
}
