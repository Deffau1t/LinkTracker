package backend.academy.bot.commandHandler;

import backend.academy.bot.model.BotState;
import backend.academy.bot.service.LinkTrackerBot;
import java.util.Map;

/**
 * TrackCommandHandler - класс, который обрабатывает команду "/track".
 */
public class TrackCommandHandler implements CommandHandler {
    private final Map<Long, BotState> userStates;

    public TrackCommandHandler(Map<Long, BotState> userStates) {
        this.userStates = userStates;
    }

    @Override
    public String command() {
        return "/track";
    }

    @Override
    public void execute(Long chatId, String message, LinkTrackerBot bot) {
        userStates.put(chatId, BotState.WAITING_FOR_LINK);
        bot.sendMessage(chatId, "Введите ссылку для отслеживания:");
    }
}
