package backend.academy.scrapper.service;

import backend.academy.scrapper.dto.LinkResponse;
import backend.academy.scrapper.dto.RemoveLinkRequest;
import backend.academy.scrapper.entity.LinkUpdate;
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

@Service
@AllArgsConstructor
@ConditionalOnProperty(name = "database.access-type", havingValue = "SQL")
public class SqlLinksService implements LinksService {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addLink(Long chatId, LinkResponse linkResponse) {
        String insertChatSql = "INSERT INTO tg_chats (id) VALUES (?) ON CONFLICT DO NOTHING";
        jdbcTemplate.update(insertChatSql, chatId);

        String url = linkResponse.url();
        List<String> tags = linkResponse.tags();
        List<String> filters = linkResponse.filters();

        String insertLinkSql = "INSERT INTO links (url, tags, filters) VALUES (?, ?, ?) ON CONFLICT (url) DO NOTHING";
        jdbcTemplate.update(insertLinkSql, url, tags, filters);

        String linkChatSql = "INSERT INTO link_tg_chat (link_id, tg_chat_id) " +
                     "SELECT id, ? FROM links WHERE url = ? " +
                     "ON CONFLICT (link_id, tg_chat_id) DO NOTHING";
        jdbcTemplate.update(linkChatSql, chatId, url);

    }

    @Override
    public void removeLink(Long chatId, RemoveLinkRequest removeLinkRequest) {
        String url = removeLinkRequest.link();
        String sql = "DELETE FROM link_tg_chat WHERE tg_chat_id = ? AND link_id = (SELECT id FROM links WHERE url = ?)";
        jdbcTemplate.update(sql, chatId, url);
    }

    @Override
    public Optional<LinkUpdate> findByUrl(String url) {
        String sql = "SELECT id, url, tags, filters FROM links WHERE url = ?";
        try {
            LinkUpdate link = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                // Получаем массивы из ResultSet
                Array tagsArray = rs.getArray("tags");
                Array filtersArray = rs.getArray("filters");

                // Преобразуем SQL массивы в Java List<String>
                List<String> tags = tagsArray != null ?
                    Arrays.asList((String[]) tagsArray.getArray()) :
                    Collections.emptyList();

                List<String> filters = filtersArray != null ?
                    Arrays.asList((String[]) filtersArray.getArray()) :
                    Collections.emptyList();

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
