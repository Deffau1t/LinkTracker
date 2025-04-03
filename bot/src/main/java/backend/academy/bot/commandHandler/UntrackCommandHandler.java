package backend.academy.bot.commandHandler;

import backend.academy.bot.model.BotState;
import backend.academy.bot.service.LinkTrackerBot;
import java.util.Map;
import lombok.AllArgsConstructor;

/**
 * UntrackCommandHandler - класс, который обрабатывает команду "/untrack".
 */

@AllArgsConstructor
public class UntrackCommandHandler implements CommandHandler {
    /**
     * userStates - Переменная для хранения состояний пользователей.
     */
    private final Map<Long, BotState> userStates;

    /**
     * command - Метод для получения команды "/untrack".
     * @return - Возвращает строку "/untrack".
     */
    @Override
    public String command() {
        return "/untrack";
    }

    /**
     * execute - Метод для выполнения команды "/untrack".
     * @param chatId - идентификатор чата, в котором была отправлена команда.
     * @param message - текст сообщения.
     * @param bot - бот, который отправил сообщение.
     */
    @Override
    public void execute(final Long chatId,
                        final String message,
                        final LinkTrackerBot bot
    ) {
        userStates.put(chatId, BotState.WAITING_FOR_UNTRACK);
        bot.sendMessage(chatId, "Введите ссылку для удаления:");
    }
}
