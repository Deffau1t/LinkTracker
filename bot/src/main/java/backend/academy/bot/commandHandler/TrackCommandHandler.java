package backend.academy.bot.commandHandler;

import backend.academy.bot.model.BotState;
import backend.academy.bot.service.LinkTrackerBot;
import lombok.AllArgsConstructor;
import java.util.Map;

/**
 * TrackCommandHandler - класс, который обрабатывает команду "/track".
 */

@AllArgsConstructor
public class TrackCommandHandler implements CommandHandler {
    /**
     * userStates - Переменная для хранения состояний пользователей.
     */
    private final Map<Long, BotState> userStates;

    /**
     * command - Метод для получения команды "/track".
     * @return Возвращает строку "/track".
     */
    @Override
    public String command() {
        return "/track";
    }

    /**
     * execute - Метод для выполнения команды "/track".
     * @param chatId - идентификатор чата, в котором была отправлена команда.
     * @param message - текст сообщения.
     * @param bot - бот, который отправил сообщение.
     */
    @Override
    public void execute(final Long chatId,
                        final String message,
                        final LinkTrackerBot bot
    ) {
        userStates.put(chatId, BotState.WAITING_FOR_LINK);
        bot.sendMessage(chatId, "Введите ссылку для отслеживания:");
    }
}
