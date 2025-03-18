package backend.academy.bot.commandHandler;

import backend.academy.bot.service.LinkTrackerBot;

/**
 * Обработчик команд бота.
 */
public interface CommandHandler {
    /**
     * Получить команду, которую обрабатывает этот обработчик.
     * @return Команда, которую обрабатывает этот обработчик.
     */
    String command();

    /**
     * Обработать команду.
     * @param chatId - идентификатор чата, в котором была отправлена команда.
     * @param message - текст сообщения.
     * @param bot - бот, который отправил сообщение.
     */
    void execute(Long chatId, String message, LinkTrackerBot bot);
}
