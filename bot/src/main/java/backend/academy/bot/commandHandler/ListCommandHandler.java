package backend.academy.bot.commandHandler;

import backend.academy.bot.client.CachedScrapperClient;
import backend.academy.bot.client.ScrapperClient;
import backend.academy.bot.service.LinkTrackerBot;
import lombok.AllArgsConstructor;

/**
 * ListCommandHandler - обработчик команды /list.
 */
@AllArgsConstructor
public class ListCommandHandler implements CommandHandler {
    /**
     * scrapperClient - клиент для получения списка отслеживаемых ссылок.
     */
    private final CachedScrapperClient cachedScrapperClient;

    /**
     * Возвращает /list, чтобы получить доступ к командам бота.
     * @return - /list.
     */
    @Override
    public String command() {
        return "/list";
    }

    /**
     * Отправляет пользователю сообщение с описанием команд бота.
     * @param chatId - идентификатор чата, в котором была отправлена команда.
     * @param message - текст сообщения.
     * @param bot - бот, который отправил сообщение.
     */
    @Override
    public void execute(final Long chatId,
                        final String message,
                        final LinkTrackerBot bot
    ) {
        bot.sendMessage(
            chatId,
            "Список ваших ссылок:\n" + cachedScrapperClient.getTrackedLinks(chatId)
        );
    }
}
