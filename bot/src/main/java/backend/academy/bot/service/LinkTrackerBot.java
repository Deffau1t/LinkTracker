package backend.academy.bot.service;

import backend.academy.bot.BotConfig;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Service;

@Service
public class LinkTrackerBot {
    private final TelegramBot bot;
    private final LinkUpdateService linkUpdateService;

    public LinkTrackerBot(BotConfig botConfig,
                          LinkUpdateService linkUpdateService) {
        this.linkUpdateService = linkUpdateService;
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
        if (update.message() != null && update.message().text() != null) {
            String text = update.message().text();
            Long chatId = update.message().chat().id();

            if (text.startsWith("/start")) {
                linkUpdateService.startCommand(chatId);
            } else if (text.startsWith("/help")) {
                linkUpdateService.helpCommand(chatId);
            } else if (text.startsWith("/track")) {
                String link = text.substring("/track".length()).trim();
                linkUpdateService.trackCommand(chatId, link);
            } else if (text.startsWith("/untrack")) {
                String link = text.substring("/untrack".length()).trim();
                linkUpdateService.untrackCommand(chatId, link);
            } else if (text.startsWith("/list")) {
                linkUpdateService.listCommand(chatId);
            } else {
                linkUpdateService.unknownCommand(chatId);
            }
        }
    }

    public void sendMessage(Long chatId, String text) {
        SendMessage request = new SendMessage(chatId, text);
        bot.execute(request);
    }
}
