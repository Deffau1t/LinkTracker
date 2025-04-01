package backend.academy.bot.commandHandler;

import backend.academy.bot.client.ScrapperClient;
import backend.academy.bot.model.BotState;
import backend.academy.bot.service.LinkTrackerBot;
import lombok.AllArgsConstructor;
import java.util.Map;

/**
 * UntrackCommandHandler - класс, который обрабатывает команду "/untrack".
 */

@AllArgsConstructor
public class UntrackCommandHandler implements CommandHandler {
    private final ScrapperClient scrapperClient;
    private final Map<Long, BotState> userStates;

    @Override
    public String command() {
        return "/untrack";
    }

    @Override
    public void execute(Long chatId, String message, LinkTrackerBot bot) {
        userStates.put(chatId, BotState.WAITING_FOR_UNTRACK);
        bot.sendMessage(chatId, "Введите ссылку для удаления:");
    }
}
