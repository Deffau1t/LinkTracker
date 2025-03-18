package backend.academy.bot.commandHandler;

import backend.academy.bot.model.BotState;
import backend.academy.bot.service.LinkTrackerBot;
import backend.academy.bot.service.LinkUpdateService;
import backend.academy.bot.client.ScrapperClient;
import java.util.Map;

/**
 * UntrackCommandHandler - класс, который обрабатывает команду "/untrack".
 */
public class UntrackCommandHandler implements CommandHandler {
    private final LinkUpdateService linkUpdateService;
    private final ScrapperClient scrapperClient;
    private final Map<Long, BotState> userStates;

    public UntrackCommandHandler(LinkUpdateService linkUpdateService,
                                 ScrapperClient scrapperClient,
                                 Map<Long, BotState> userStates) {
        this.linkUpdateService = linkUpdateService;
        this.scrapperClient = scrapperClient;
        this.userStates = userStates;
    }

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
