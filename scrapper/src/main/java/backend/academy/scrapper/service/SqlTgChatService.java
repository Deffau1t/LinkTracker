package backend.academy.scrapper.service;


import backend.academy.scrapper.entity.LinkUpdate;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@AllArgsConstructor
@ConditionalOnProperty(name = "database.access-type", havingValue = "SQL")
public class SqlTgChatService implements TgChatService{
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
    public List<LinkUpdate> getAllLinks(Long chatId) {
        String sql = "SELECT l.id, l.url, l.description FROM links l JOIN link_tg_chat lt ON l.id = lt.link_id WHERE lt.tg_chat_id = ?";
        return jdbcTemplate.query(sql, (rs, _) -> new LinkUpdate(
            rs.getLong("id"),
            rs.getString("url"),
            rs.getString("description"),
            List.of(chatId)
        ), chatId);
    }

    @Override
    public List<Long> getAllChatIds() {
        String sql = "SELECT id FROM tg_chats";
        return jdbcTemplate.queryForList(sql, Long.class);
    }

    @Override
    public boolean isChatRegistered(Long chatId) {
        String sql = "SELECT EXISTS(SELECT 1 FROM tg_chats WHERE id = ?)";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, chatId));
    }

}
