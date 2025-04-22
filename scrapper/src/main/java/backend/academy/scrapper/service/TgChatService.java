package backend.academy.scrapper.service;

import backend.academy.scrapper.entity.LinkUpdate;
import java.util.List;

/**
 * TgChatService - интерфейс сервиса для работы с чатами Telegram.
 */
public interface TgChatService {
    /**
     * registerChat - метод для регистрации чата в базе данных.
     * @param chatId - идентификатор чата в базе данных.
     */
    void registerChat(Long chatId);

    /**
     * deleteChat - метод для удаления чата из базы данных.
     * @param chatId - идентификатор чата в базе данных.
     */
    void deleteChat(Long chatId);

    /**
     * getAllLinks - метод для получения списка ссылок
     * на страницы обновлений для указанного чата.
     * @param chatId - идентификатор чата в базе данных.
     * @return список ссылок на страницы обновлений для указанного чата.
     */
    List<LinkUpdate> getAllLinks(Long chatId);

    /**
     * getChatIds - метод для получения списка идентификаторов чатов.
     * @return список идентификаторов чатов.
     */
    List<Long> getAllChatIds();

    /**
     * isChatRegistered - метод для проверки регистрации чата.
     * @param chatId - идентификатор чата в базе данных.
     * @return true, если чат зарегистрирован, иначе false.
     */
    boolean isChatRegistered(Long chatId);
}
