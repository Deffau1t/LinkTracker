package backend.academy.scrapper.repository;

import backend.academy.scrapper.dto.LinkUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface LinksRepository extends JpaRepository<LinkUpdate, Long> {
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO tg_chats (id) VALUES (?1) ON CONFLICT DO NOTHING", nativeQuery = true)
    void registerChat(Long chatId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM tg_chats WHERE id = ?1", nativeQuery = true)
    void deleteChat(Long chatId);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO links (url, description) VALUES (?2, ?3) ON CONFLICT (url) DO NOTHING; INSERT INTO link_tg_chat (link_id, tg_chat_id) SELECT id, ?1 FROM links WHERE url = ?2", nativeQuery = true)
    void addLink(Long chatId, String url, String description);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM link_tg_chat WHERE tg_chat_id = ?1 AND link_id = (SELECT id FROM links WHERE url = ?2)", nativeQuery = true)
    void removeLink(Long chatId, String url);

    @Query(value = "SELECT l.id, l.url, l.description FROM links l JOIN link_tg_chat lt ON l.id = lt.link_id WHERE lt.tg_chat_id = ?1", nativeQuery = true)
    List<LinkUpdate> getAllLinks(Long chatId);
}
