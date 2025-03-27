package backend.academy.scrapper.service;

import backend.academy.scrapper.dto.LinkResponse;
import backend.academy.scrapper.dto.LinkUpdate;
import backend.academy.scrapper.dto.RemoveLinkRequest;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@ConditionalOnProperty(name = "database.access-type", havingValue = "SQL")
public class SqlLinksService implements LinksService {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void registerChat(Long chatId) {
        String sql = "INSERT INTO tg_chats (id) VALUES (?) ON CONFLICT DO NOTHING";
        jdbcTemplate.update(sql, chatId);
    }

    @Override
    public void deleteChat(Long chatId) {
        String sql = "DELETE FROM tg_chats WHERE id = ?";
        jdbcTemplate.update(sql, chatId);
    }

    @Override
    public void addLink(Long chatId, LinkResponse linkResponse) {
        String insertChatSql = "INSERT INTO tg_chats (id) VALUES (?) ON CONFLICT DO NOTHING";
        jdbcTemplate.update(insertChatSql, chatId);

        String url = linkResponse.url();
        String description = String.join(", ", linkResponse.tags());

        String insertLinkSql = "INSERT INTO links (url, description) VALUES (?, ?) ON CONFLICT (url) DO NOTHING";
        jdbcTemplate.update(insertLinkSql, url, description);

        String linkChatSql = "INSERT INTO link_tg_chat (link_id, tg_chat_id) SELECT id, ? FROM links WHERE url = ?";
        jdbcTemplate.update(linkChatSql, chatId, url);
    }

    @Override
    public void removeLink(Long chatId, RemoveLinkRequest removeLinkRequest) {
        String url = removeLinkRequest.link();
        String sql = "DELETE FROM link_tg_chat WHERE tg_chat_id = ? AND link_id = (SELECT id FROM links WHERE url = ?)";
        jdbcTemplate.update(sql, chatId, url);
    }

    @Override
    public List<LinkUpdate> getAllLinks(Long chatId) {
        String sql = "SELECT l.id, l.url, l.description FROM links l JOIN link_tg_chat lt ON l.id = lt.link_id WHERE lt.tg_chat_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new LinkUpdate(
            rs.getLong("id"),
            rs.getString("url"),
            rs.getString("description"),
            List.of(chatId)
        ), chatId);
    }
}
