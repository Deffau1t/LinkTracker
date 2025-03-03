package backend.academy.bot.service;

import backend.academy.bot.BotConfig;
import backend.academy.bot.client.ScrapperClient;
import backend.academy.bot.model.BotState;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import java.util.HashMap;
import java.util.Map;
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
    private final LinkUpdateService linkUpdateService;

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
    private final ScrapperClient scrapperClient;

    /**
     * LinkTrackerBot - конструктор класса.
     * @param botConfig - конфигурация бота.
     * @param linkUpdateService - сервис обновления ссылок.
     * @param scrapperClient - клиент для получения данных с сайта.
     */

    public LinkTrackerBot(final BotConfig botConfig,
                          final LinkUpdateService linkUpdateService,
                          final ScrapperClient scrapperClient) {
        this.linkUpdateService = linkUpdateService;
        this.scrapperClient = scrapperClient;
        this.bot = new TelegramBot(botConfig.telegramToken());

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
            default -> linkUpdateService.unknownCommand(chatId, this);
        }
    }

    /**
     * handleCommand - метод, который обрабатывает команды от пользователя.
     * @param chatId - идентификатор чата.
     * @param text - текст сообщения.
     */

    private void handleCommand(final Long chatId, final String text) {
        if (text.equals("/start")) {
            linkUpdateService.startCommand(chatId, this);
        } else if (text.equals("/help")) {
            linkUpdateService.helpCommand(chatId, this);
        } else if (text.equals("/track")) {
            userStates.put(chatId, BotState.WAITING_FOR_LINK);
            sendMessage(chatId, "Введите ссылку для отслеживания:");
        } else if (text.equals("/untrack")) {
            userStates.put(chatId, BotState.WAITING_FOR_UNTRACK);
            sendMessage(chatId, "Введите ссылку для удаления:");
        } else if (text.equals("/list")) {
            linkUpdateService.listCommand(chatId, this);
        } else {
            linkUpdateService.unknownCommand(chatId, this);
        }
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
        if (!linkUpdateService.isTrackingLink(chatId, text)) {
            sendMessage(chatId, "Вы не отслеживаете эту ссылку.");
            userStates.put(chatId, BotState.IDLE);
            return;
        }

        scrapperClient.untrackLink(chatId, text);

        linkUpdateService.untrackCommand(chatId, text, this);

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

        scrapperClient.trackLink(chatId, link, tags, filters);

        linkUpdateService.trackCommand(chatId, link, tags, filters, this);

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
