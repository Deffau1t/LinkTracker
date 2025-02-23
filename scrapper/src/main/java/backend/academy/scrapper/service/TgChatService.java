package backend.academy.scrapper.service;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TgChatService {

    private final Map<Long, Boolean> registeredChats = new ConcurrentHashMap<>();

    public boolean isChatsRegistered(Long chatId) {
        return registeredChats.containsKey(chatId);
    }

    public void registerChat(Long chatId) {
        registeredChats.putIfAbsent(chatId, true);
    }

    public void deleteChat(Long chatId) {
        registeredChats.remove(chatId);
    }
}
