package backend.academy.scrapper.service;

import lombok.Getter;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TgChatService - сервис для работы с чатами.
 */

@Service
@Getter
public class TgChatService {
    /**
     * registeredChats - мапа с зарегистрированными чатами.
     */
    private final Map<Long, Boolean> registeredChats
        = new ConcurrentHashMap<>();

    /**
     * isChatsRegistered - метод для проверки, зарегистрирован ли чат.
     * @param chatId - id чата.
     * @return - true, если чат зарегистрирован, иначе false.
     */
    public boolean isChatsRegistered(final Long chatId) {
        return registeredChats.containsKey(chatId);
    }

    /**
     * registerChat - метод для регистрации чата.
     * @param chatId - id чата.
     */
    public void registerChat(final Long chatId) {
        registeredChats.putIfAbsent(chatId, true);
    }

    /**
     * deleteChat - метод для удаления чата.
     * @param chatId - id чата.
     */
    public void deleteChat(final Long chatId) {
        registeredChats.remove(chatId);
    }
}
