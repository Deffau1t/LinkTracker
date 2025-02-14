package backend.academy.bot.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        chatSubscribes.computeIfAbsent(chatId, _ -> new ArrayList<>()).add(link);
        linkTrackerBot.sendMessage(chatId, "Ссылка успешно добавлена.");
    }

    public void untrackCommand(Long chatId, String link) {
        if (!chatSubscribes.get(chatId).isEmpty()) {
            chatSubscribes.get(chatId).remove(link);
            linkTrackerBot.sendMessage(chatId, "Ссылка успешно удалена.");
        } else {
            linkTrackerBot.sendMessage(chatId, "Вы не были подписаны на эту ссылку.");
        }
    }

    public void listCommand(Long chatId) {
        StringBuilder linksList = new StringBuilder();
        for (String link : chatSubscribes.get(chatId)) {
            linksList.append(link);
        }
        linkTrackerBot.sendMessage(chatId, "Ваши ссылки:\n" + linksList.toString());
    }

    public void unknownCommand(Long chatId) {
        linkTrackerBot.sendMessage(chatId, "Некорректная команда.");
    }
}
