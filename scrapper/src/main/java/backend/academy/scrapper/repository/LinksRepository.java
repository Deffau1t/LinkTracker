package backend.academy.scrapper.repository;

import backend.academy.scrapper.entity.LinkUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * LinksRepository - Репозиторий
 * для работы с таблицей links и связанными таблицами.
 */

public interface LinksRepository extends JpaRepository<LinkUpdate, Long> {
    /**
     * Метод для добавления ссылки в таблицу links и связанных таблиц.
     * @param chatId - идентификатор чата
     * @param url - ссылка на сайт
     * @param tags - список тегов
     * @param filters - список фильтров
     */
    @Modifying
    @Transactional
    @Query(value = """
        INSERT INTO links (url, tags, filters)
        VALUES (:url, array[:tags], array[:filters])
        ON CONFLICT (url) DO NOTHING;

        INSERT INTO link_tg_chat (link_id, tg_chat_id)
        SELECT id, :chatId FROM links WHERE url = :url
        ON CONFLICT (link_id, tg_chat_id) DO NOTHING
        """, nativeQuery = true)
    void addLink(
        @Param("chatId") Long chatId,
        @Param("url") String url,
        @Param("tags") List<String> tags,
        @Param("filters") List<String> filters
    );

    /**
     * Метод для удаления ссылки из таблицы links и связанных таблиц.
     * @param chatId - идентификатор чата
     * @param url - ссылка на сайт
     */
    @Modifying
    @Transactional
    @Query(value =
        "DELETE FROM link_tg_chat WHERE tg_chat_id = ?1 AND"
            + " link_id = (SELECT id FROM links WHERE url = ?2)",
        nativeQuery = true
    )
    void removeLink(Long chatId, String url);

    /**
     * Метод для получения ссылки из таблицы links и связанных таблиц.
     * @param url - ссылка на сайт
     * @return ссылка из таблицы links и связанных таблиц.
     */
    @Query("SELECT l FROM LinkUpdate l WHERE l.url = :url")
    Optional<LinkUpdate> findByUrl(String url);
}
