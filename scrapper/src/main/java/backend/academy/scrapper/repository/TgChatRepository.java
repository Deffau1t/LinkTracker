package backend.academy.scrapper.repository;

import backend.academy.scrapper.entity.LinkUpdate;
import backend.academy.scrapper.entity.TgChat;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

/**
 * TgChatRepository - Репозиторий для работы с таблицей tg_chats.
 */
public interface TgChatRepository extends JpaRepository<TgChat, Long> {

    @Query(value = "SELECT l.* FROM links l JOIN link_tg_chat ltc ON l.id = ltc.link_id WHERE ltc.tg_chat_id = :chatId", nativeQuery = true)
    List<LinkUpdate> findAllLinksByChatId(Long chatId);

    @Query("SELECT t.id FROM TgChat t")
    List<Long> findAllChatIds();

    boolean existsById(Long chatId);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO link_tg_chat (link_id, tg_chat_id) VALUES (:linkId, :chatId) ON CONFLICT DO NOTHING", nativeQuery = true)
    void insertLinkChatRelation(Long linkId, Long chatId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM link_tg_chat WHERE link_id = :linkId AND tg_chat_id = :chatId", nativeQuery = true)
    void deleteLinkChatRelation(Long linkId, Long chatId);
}
