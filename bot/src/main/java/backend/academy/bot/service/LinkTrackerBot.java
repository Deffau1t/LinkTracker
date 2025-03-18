package backend.academy.bot.service;

import backend.academy.bot.BotConfig;
import backend.academy.bot.client.ScrapperClient;
import backend.academy.bot.commandHandler.CommandHandler;
import backend.academy.bot.commandHandler.HelpCommandHandler;
import backend.academy.bot.commandHandler.StartCommandHandler;
import backend.academy.bot.commandHandler.TrackCommandHandler;
import backend.academy.bot.commandHandler.UntrackCommandHandler;
import backend.academy.bot.model.BotState;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 * LinkTrackerBot - класс, отвечающий за работу бота.
 */

@Service
public class LinkTrackerBot {
    /**
     * Бот Telegram.
     */
    private final TelegramBot bot;

    /**
     * Сервис обновления ссылок.
     */
    private final LinkUpdateService botLinkUpdateService;

    /**
     * Состояния пользователей.
     */
    private final Map<Long, BotState> userStates = new HashMap<>();

    /**
     * Ссылки пользователей.
     */
    private final Map<Long, String> userLinks = new HashMap<>();

    /**
     * Теги пользователей.
     */
    private final Map<Long, String> userTags = new HashMap<>();

    /**
     * Клиент скраппера.
     */
    private final ScrapperClient scrapperBotClient;

    /**
     * Команды бота - хранит обработчики команд.
     */
    private final Map<String, CommandHandler> commandHandlers = new HashMap<>();

    /**
     * LinkTrackerBot - конструктор класса.
     * @param botConfig - конфигурация бота.
     * @param linkUpdateService - сервис обновления ссылок.
     * @param scrapperClient - клиент для получения данных с сайта.
     */

    public LinkTrackerBot(final BotConfig botConfig,
                          final LinkUpdateService linkUpdateService,
                          final ScrapperClient scrapperClient) {
        this.botLinkUpdateService = linkUpdateService;
        this.scrapperBotClient = scrapperClient;
        this.bot = new TelegramBot(botConfig.telegramToken());

        commandHandlers.put("/start", new StartCommandHandler());
        commandHandlers.put("/help", new HelpCommandHandler());
        commandHandlers.put("/track", new TrackCommandHandler(userStates));
        commandHandlers.put("/untrack", new UntrackCommandHandler(
            linkUpdateService, scrapperClient, userStates));

        this.bot.setUpdatesListener(
            updates -> {
                for (Update update : updates) {
                    confirmUpdate(update);
                }
                return UpdatesListener.CONFIRMED_UPDATES_ALL;
            }
        );
    }

    /**
     * confirmUpdate - метод, который обрабатывает обновления от Telegram.
     * @param update - обновление от Telegram.
     */

    private void confirmUpdate(final Update update) {
        if (update.message() == null || update.message().text() == null) {
            return;
        }

        Long chatId = update.message().chat().id();
        String text = update.message().text();

        switch (userStates.getOrDefault(chatId, BotState.IDLE)) {
            case IDLE -> handleCommand(chatId, text);
            case WAITING_FOR_LINK -> handleLink(chatId, text);
            case WAITING_FOR_TAGS -> handleTags(chatId, text);
            case WAITING_FOR_FILTERS -> handleFilters(chatId, text);
            case WAITING_FOR_UNTRACK -> handleUntrack(chatId, text);
            default -> botLinkUpdateService.unknownCommand(chatId, this);
        }
    }

    /**
     * handleCommand - метод, который обрабатывает команды от пользователя.
     * @param chatId - идентификатор чата.
     * @param text - текст сообщения.
     */

    private void handleCommand(final Long chatId, final String text) {
        Optional<CommandHandler> handlerOpt = commandHandlers.values().stream()
                .filter(handler -> text.startsWith(handler.command()))
                .findFirst();

        handlerOpt.ifPresentOrElse(
                handler -> handler.execute(chatId, text, this),
                () -> sendMessage(chatId, "Неизвестная команда. Введите /help для списка команд.")
        );
    }

    private void handleLink(final Long chatId, final String text) {
        userLinks.put(chatId, text);
        userStates.put(chatId, BotState.WAITING_FOR_TAGS);
        sendMessage(
            chatId,
            "Введите фильтры через пробел(напишите `-`, чтобы пропустить):"
        );
    }

    private void handleUntrack(final Long chatId, final String text) {
        if (!botLinkUpdateService.isTrackingLink(chatId, text)) {
            sendMessage(chatId, "Вы не отслеживаете эту ссылку.");
            userStates.put(chatId, BotState.IDLE);
            return;
        }

        scrapperBotClient.untrackLink(chatId, text);

        botLinkUpdateService.untrackCommand(chatId, text, this);

        userStates.put(chatId, BotState.IDLE);
    }


    private void handleTags(final Long chatId, final String text) {
        if (!text.equals("-")) {
            userTags.put(chatId, text);
        }
        userStates.put(chatId, BotState.WAITING_FOR_FILTERS);
        sendMessage(
            chatId,
            "Введите фильтры через пробел(напишите `-`, чтобы пропустить):"
        );
    }

    private void handleFilters(final Long chatId, final String text) {
        String link = userLinks.get(chatId);
        String tags = userTags.getOrDefault(chatId, "");
        String filters = text.equals("-") ? "" : text;

        scrapperBotClient.trackLink(chatId, link, tags, filters);

        botLinkUpdateService.trackCommand(chatId, link, tags, filters, this);

        userStates.put(chatId, BotState.IDLE);
    }

    /**
     * sendMessage - метод, который отправляет сообщение пользователю.
     * @param chatId - идентификатор чата.
     * @param text - текст сообщения.
     */
    public void sendMessage(final Long chatId, final String text) {
        bot.execute(new SendMessage(chatId, text));
    }
}
