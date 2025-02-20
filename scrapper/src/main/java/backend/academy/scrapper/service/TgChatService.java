package backend.academy.scrapper.service;


import backend.academy.bot.service.LinkUpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class TgChatService {

    @Autowired
    @Lazy
    private LinkUpdateService linkUpdateService;

    public boolean isChatsRegistered(Long chatId) {
        return linkUpdateService.chatSubscribes().containsKey(chatId);
    }

    public void deleteChat(Long chatId) {
        linkUpdateService.chatSubscribes().remove(chatId);
    }
}
