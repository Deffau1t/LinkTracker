package backend.academy.bot.commandHandler;

import backend.academy.bot.client.CachedScrapperClient;
import backend.academy.bot.client.ScrapperClient;
import backend.academy.bot.service.LinkTrackerBot;
import lombok.AllArgsConstructor;

/**
 * StartCommandHandler - обработчик команды /start.
 */
@AllArgsConstructor
public class StartCommandHandler implements CommandHandler {

    /**
     * Сервис, позволяющий работать с чатами бота.
     */
    private final ScrapperClient scrapperClient;

    /**
     * Возвращает /start.
     * @return - /start.
     */
    @Override
    public String command() {
        return "/start";
    }

    /**
     * Обрабатывает команду /start.
     * @param chatId - идентификатор чата, в котором была отправлена команда.
     * @param message - текст сообщения.
     * @param bot - бот, который отправил сообщение.
     */
    @Override
    public void execute(final Long chatId,
                        final String message,
                        final LinkTrackerBot bot
    ) {
        scrapperClient.registerChatIfNeeded(chatId);
        bot.sendMessage(chatId, "Вы успешно зарегистрированы в системе.");
    }
}
