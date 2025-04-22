package backend.academy.scrapper.repository;

import backend.academy.scrapper.entity.LinkUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * TgChatRepository - Репозиторий для работы с таблицей tg_chats.
 */

public interface TgChatRepository extends JpaRepository<LinkUpdate, Long> {
    /**
     * Метод для регистрации чата в таблице tg_chats.
     * @param chatId - идентификатор чата
     */
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO tg_chats (id) VALUES (?1)"
        + " ON CONFLICT DO NOTHING",
        nativeQuery = true
    )
    void registerChat(Long chatId);

    /**
     * Метод для удаления чата из таблицы tg_chats.
     * @param chatId - идентификатор чата
     */
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM tg_chats WHERE id = ?1", nativeQuery = true)
    void deleteChat(Long chatId);

    /**
     * Метод для получения списка ссылок из таблицы links
     * и связанных таблиц по идентификатору чата.
     * @param chatId - идентификатор чата
     * @return список ссылок из таблицы links и связанных таблиц.
     */
    @Query(value = "SELECT l.id, l.url, l.tags, l.filters"
        + " FROM links l JOIN link_tg_chat lt ON"
        + " l.id = lt.link_id WHERE lt.tg_chat_id = ?1",
        nativeQuery = true
    )
    List<LinkUpdate> getAllLinks(Long chatId);

    /**
     * Метод для получения списка идентификаторов чатов из таблицы tg_chats.
     * @return список идентификаторов чатов из таблицы tg_chats.
     */
    @Query(value = "SELECT id from tg_chats", nativeQuery = true)
    List<Long> getAllChatIds();

    /**
     * Метод для проверки регистрации чата в базе данных.
     * @param chatId - идентификатор чата
     * @return true, если чат зарегистрирован в базе данных, иначе false
     */
    @Query(value = "SELECT EXISTS(SELECT 1 FROM tg_chats WHERE id = ?1)",
        nativeQuery = true
    )
    boolean existsChatById(Long chatId);
}
