package backend.academy.scrapper.service.sqlDB;


import backend.academy.scrapper.entity.LinkUpdate;
import backend.academy.scrapper.service.TgChatService;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.sql.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * SqlTgChatService - класс сервиса для работы
 * с таблицей tg_chats в базе данных.
 */

@Service
@AllArgsConstructor
@ConditionalOnProperty(name = "database.access-type", havingValue = "SQL")
public class SqlTgChatService implements TgChatService {
    /**
     * jdbcTemplate - объект для работы с базой данных.
     */
    private final JdbcTemplate jdbcTemplate;

    /**
     * registerChat - метод для регистрации чата в базе данных.
     * @param chatId - идентификатор чата в базе данных
     */
    @Override
    public void registerChat(final Long chatId) {
        String sql =
            "INSERT INTO tg_chats (id) VALUES (?) ON CONFLICT DO NOTHING";
        jdbcTemplate.update(sql, chatId);
    }

    /**
     * deleteChat - метод для удаления чата из базы данных.
     * @param chatId - идентификатор чата в базе данных
     */
    @Override
    public void deleteChat(final Long chatId) {
        String sql = "DELETE FROM tg_chats WHERE id = ?";
        jdbcTemplate.update(sql, chatId);
    }

    /**
     * getAllLinks - метод для получения списка ссылок для указанного чата.
     * @param chatId - идентификатор чата в базе данных
     * @return список ссылок для указанного чата
     */
    @Override
    public List<LinkUpdate> getAllLinks(final Long chatId) {
        String sql = "SELECT l.id, l.url, l.tags, l.filters "
            + "FROM links l JOIN link_tg_chat lt ON l.id = lt.link_id "
            + "WHERE lt.tg_chat_id = ?";

        return jdbcTemplate.query(sql, (rs, _) -> {
            // Преобразуем массивы из результатов в списки
            Array tagsArray = rs.getArray("tags");
            Array filtersArray = rs.getArray("filters");

            List<String> tagsList = tagsArray != null
                ? Arrays.asList((String[]) tagsArray.getArray())
                : Collections.emptyList();

            List<String> filtersList = filtersArray != null
                ? Arrays.asList((String[]) filtersArray.getArray())
                : Collections.emptyList();

            return LinkUpdate.builder()
                .id(rs.getLong("id"))
                .url(rs.getString("url"))
                .tags(tagsList)
                .filters(filtersList)
                .tgChatIds(List.of(chatId))
                .build();
        }, chatId);
    }

    /**
     * getAllChatIds - метод для получения списка
     * всех идентификаторов чатов в базе данных.
     * @return список всех идентификаторов чатов в базе данных
     */
    @Override
    public List<Long> getAllChatIds() {
        String sql = "SELECT id FROM tg_chats";
        return jdbcTemplate.queryForList(sql, Long.class);
    }

    /**
     * isChatRegistered - метод для проверки регистрации чата в базе данных.
     * @param chatId - идентификатор чата в базе данных
     * @return true, если чат зарегистрирован в базе данных, иначе false
     */
    @Override
    public boolean isChatRegistered(final Long chatId) {
        String sql = "SELECT EXISTS(SELECT 1 FROM tg_chats WHERE id = ?)";
        return Boolean.TRUE.equals(
            jdbcTemplate.queryForObject(
                sql,
                Boolean.class,
                chatId
            )
        );
    }
}
