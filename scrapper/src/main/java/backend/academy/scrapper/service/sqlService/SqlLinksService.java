package backend.academy.scrapper.service.sqlService;

import backend.academy.scrapper.dto.LinkResponse;
import backend.academy.scrapper.dto.RemoveLinkRequest;
import backend.academy.scrapper.entity.LinkUpdate;
import backend.academy.scrapper.service.LinksService;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.sql.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * SqlLinksService - реализация интерфейса LinksService
 * для работы с базой данных через JDBC.
 */

@Service
@AllArgsConstructor
@ConditionalOnProperty(name = "database.access-type", havingValue = "SQL")

public class SqlLinksService implements LinksService {
    /**
     * jdbcTemplate - объект для работы с базой данных через JDBC.
     */
    private final JdbcTemplate jdbcTemplate;

    /**
     * addLink - добавляет ссылку в базу данных.
     * @param chatId - идентификатор чата в Telegram
     * @param linkResponse - информация о ссылке
     */
    @Override
    public void addLink(final Long chatId, final LinkResponse linkResponse) {
        String insertChatSql =
            "INSERT INTO tg_chats (id) VALUES (?) ON CONFLICT DO NOTHING";
        jdbcTemplate.update(insertChatSql, chatId);

        String url = linkResponse.url();
        List<String> tags = linkResponse.tags();
        List<String> filters = linkResponse.filters();

        String insertLinkSql =
            "INSERT INTO links (url, tags, filters) VALUES (?, ?, ?)"
                + " ON CONFLICT (url) DO NOTHING";
        jdbcTemplate.update(insertLinkSql, url, tags, filters);

        String linkChatSql = "INSERT INTO link_tg_chat (link_id, tg_chat_id)"
            + " SELECT id, ? FROM links WHERE url = ? "
            + "ON CONFLICT (link_id, tg_chat_id) DO NOTHING";
        jdbcTemplate.update(linkChatSql, chatId, url);
    }

    /**
     * removeLink - удаляет ссылку из базы данных.
     * @param chatId - идентификатор чата в Telegram
     * @param removeLinkRequest - информация о ссылке для удаления
     */
    @Override
    public void removeLink(
        final Long chatId,
        final RemoveLinkRequest removeLinkRequest
    ) {
        String url = removeLinkRequest.link();
        String sql = "DELETE FROM link_tg_chat"
            + " WHERE tg_chat_id = ? AND"
            + " link_id = (SELECT id FROM links WHERE url = ?)";
        jdbcTemplate.update(sql, chatId, url);
    }

    /**
     * findByUrl - находит ссылку по url.
     * @param url - url ссылки
     * @return - объект, содержащий информацию о ссылке,
     * если она найдена, иначе пустой объект
     */
    @Override
    public Optional<LinkUpdate> findByUrl(final String url) {
        String sql = "SELECT id, url, tags, filters FROM links WHERE url = ?";
        try {
            LinkUpdate link = jdbcTemplate.queryForObject(sql, (rs, _) -> {
                Array tagsArray = rs.getArray("tags");
                Array filtersArray = rs.getArray("filters");

                List<String> tags = tagsArray != null
                    ? Arrays.asList((String[]) tagsArray.getArray())
                    : Collections.emptyList();

                List<String> filters = filtersArray != null
                    ? Arrays.asList((String[]) filtersArray.getArray())
                    : Collections.emptyList();

                return LinkUpdate.builder()
                    .id(rs.getLong("id"))
                    .url(rs.getString("url"))
                    .tags(tags)
                    .filters(filters)
                    .build();
            }, url);

            return Optional.ofNullable(link);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
