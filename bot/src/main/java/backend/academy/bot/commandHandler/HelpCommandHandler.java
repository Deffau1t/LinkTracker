package backend.academy.bot.commandHandler;

import backend.academy.bot.service.LinkTrackerBot;

/**
 * HelpCommandHandler - класс для обработки команды /help.
 */
public class HelpCommandHandler implements CommandHandler {

    /**
     * Возвращает /help, чтобы получить доступ к командам бота.
     * @return - /help.
     */
    @Override
    public String command() {
        return "/help";
    }

    /**
     * Отправляет пользователю сообщение с описанием команд бота.
     * @param chatId - идентификатор чата, в котором была отправлена команда.
     * @param message - текст сообщения.
     * @param bot - бот, который отправил сообщение.
     */
    @Override
    public void execute(
        final Long chatId,
        final String message,
        final LinkTrackerBot bot
    ) {
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
