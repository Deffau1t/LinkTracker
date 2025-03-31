package backend.academy.scrapper.service;

import backend.academy.scrapper.entity.LinkUpdate;
import java.util.List;

public interface TgChatService {
    void registerChat(Long chatId);
    void deleteChat(Long chatId);
    List<LinkUpdate> getAllLinks(Long chatId);
    List<Long> getAllChatIds();
    boolean isChatRegistered(Long chatId);
}
