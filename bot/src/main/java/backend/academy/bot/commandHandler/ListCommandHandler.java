package backend.academy.bot.commandHandler;

import backend.academy.bot.client.ScrapperClient;
import backend.academy.bot.service.LinkTrackerBot;
import lombok.AllArgsConstructor;


@AllArgsConstructor
public class ListCommandHandler implements CommandHandler {
    private final ScrapperClient scrapperClient;

    @Override
    public String command() {
        return "/list";
    }

    @Override
    public void execute(Long chatId, String message, LinkTrackerBot bot) {
        bot.sendMessage(chatId, "Список ваших ссылок:\n" + scrapperClient.getTrackedLinks(chatId));
    }
}
