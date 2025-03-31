package backend.academy.scrapper.repository;

import backend.academy.scrapper.entity.LinkUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface TgChatRepository extends JpaRepository<LinkUpdate, Long> {
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO tg_chats (id) VALUES (?1) ON CONFLICT DO NOTHING", nativeQuery = true)
    void registerChat(Long chatId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM tg_chats WHERE id = ?1", nativeQuery = true)
    void deleteChat(Long chatId);

    @Query(value = "SELECT l.id, l.url, l.description FROM links l JOIN link_tg_chat lt ON l.id = lt.link_id WHERE lt.tg_chat_id = ?1", nativeQuery = true)
    List<LinkUpdate> getAllLinks(Long chatId);

    @Query(value = "SELECT id from tg_chats", nativeQuery = true)
    List<Long> getAllChatIds();

    @Query(value = "SELECT EXISTS(SELECT 1 FROM tg_chats WHERE id = ?1)", nativeQuery = true)
    boolean existsChatById(Long chatId);
}
