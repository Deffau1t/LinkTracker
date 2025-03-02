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

@Service
public class LinkTrackerBot {
    private final TelegramBot bot;
    private final LinkUpdateService linkUpdateService;
    private final Map<Long, BotState> userStates = new HashMap<>();
    private final Map<Long, String> userLinks = new HashMap<>();
    private final Map<Long, String> userTags = new HashMap<>();
    private final ScrapperClient scrapperClient;

    public LinkTrackerBot(BotConfig botConfig,
                          LinkUpdateService linkUpdateService,
                          ScrapperClient scrapperClient) {
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

    private void confirmUpdate(Update update) {
        if (update.message() == null || update.message().text() == null) return;

        Long chatId = update.message().chat().id();
        String text = update.message().text();

        switch (userStates.getOrDefault(chatId, BotState.IDLE)) {
            case IDLE -> handleCommand(chatId, text);
            case WAITING_FOR_LINK -> handleLink(chatId, text);
            case WAITING_FOR_TAGS -> handleTags(chatId, text);
            case WAITING_FOR_FILTERS -> handleFilters(chatId, text);
            case WAITING_FOR_UNTRACK -> handleUntrack(chatId, text);
        }
    }

    private void handleCommand(Long chatId, String text) {
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

    private void handleLink(Long chatId, String text) {
        userLinks.put(chatId, text);
        userStates.put(chatId, BotState.WAITING_FOR_TAGS);
        sendMessage(chatId, "Введите фильтры (через пробел) или напишите `-`, чтобы пропустить:");
    }

    private void handleUntrack(Long chatId, String text) {
        if (!linkUpdateService.isTrackingLink(chatId, text)) {
            sendMessage(chatId, "Вы не отслеживаете эту ссылку.");
            userStates.put(chatId, BotState.IDLE);
            return;
        }

        scrapperClient.untrackLink(chatId, text);

        linkUpdateService.untrackCommand(chatId, text, this);

        userStates.put(chatId, BotState.IDLE);
    }


    private void handleTags(Long chatId, String text) {
        if (!text.equals("-")) {
            userTags.put(chatId, text);
        }
        userStates.put(chatId, BotState.WAITING_FOR_FILTERS);
        sendMessage(chatId, "Введите фильтры (через пробел) или напишите `-`, чтобы пропустить:");
    }

    private void handleFilters(Long chatId, String text) {
        String link = userLinks.get(chatId);
        String tags = userTags.getOrDefault(chatId, "");
        String filters = text.equals("-") ? "" : text;

        scrapperClient.trackLink(chatId, link, tags, filters);

        linkUpdateService.trackCommand(chatId, link, tags, filters, this);

        userStates.put(chatId, BotState.IDLE);
    }


    public void sendMessage(Long chatId, String text) {
        bot.execute(new SendMessage(chatId, text));
    }
}
