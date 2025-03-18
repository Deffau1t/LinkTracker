package backend.academy.bot.commandHandler;

import backend.academy.bot.service.LinkTrackerBot;

/**
 * StartCommandHandler - обработчик команды /start.
 */
public class StartCommandHandler implements CommandHandler {
    @Override
    public String command() {
        return "/start";
    }

    @Override
    public void execute(Long chatId, String message, LinkTrackerBot bot) {
        bot.sendMessage(chatId, "Вы успешно зарегистрированы.");
    }
}
