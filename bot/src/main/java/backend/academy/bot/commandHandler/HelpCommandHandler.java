package backend.academy.bot.commandHandler;

import backend.academy.bot.service.LinkTrackerBot;

/**
 * HelpCommandHandler - класс для обработки команды /help.
 */
public class HelpCommandHandler implements CommandHandler {
    @Override
    public String command() {
        return "/help";
    }

    @Override
    public void execute(Long chatId, String message, LinkTrackerBot bot) {
        bot.sendMessage(chatId, """
            Доступные команды:
            /start - регистрация пользователя.
            /help - помощь.
            /track - начать отслеживание ссылки.
            /untrack - прекратить отслеживание ссылки.
            /list - показать список отслеживаемых ссылок.
            """);
    }
}
