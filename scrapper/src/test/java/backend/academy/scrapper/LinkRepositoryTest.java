package backend.academy.scrapper;

import backend.academy.scrapper.dto.LinkResponse;
import backend.academy.scrapper.dto.RemoveLinkRequest;
import backend.academy.scrapper.entity.LinkUpdate;
import backend.academy.scrapper.entity.TgChat;
import backend.academy.scrapper.repository.LinksRepository;
import backend.academy.scrapper.repository.TgChatRepository;
import java.sql.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import backend.academy.scrapper.service.LinksService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
@ActiveProfiles("test")
@Sql(scripts = "/db/changelog/0001_create_tables.sql")
@DisplayName("Запросы к БД работают ожидаемым образом: вставка, удаление, обновление")
public class LinkRepositoryTest {

    @Autowired
    private LinksRepository linksRepository;

    @Autowired
    private LinksService linksService;

    @Autowired
    private TgChatRepository tgChatRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test")
            .withInitScript("db/changelog/0001_create_tables.sql");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Test
    @Transactional
    @DisplayName("Тест сохранения и получения ссылки")
    void shouldSaveAndRetrieveLink() {
        // Arrange
        List<String> tags = List.of("news", "tech");
        List<String> filters = List.of("python", "java");
        long chatId = 123L;
        String url = "https://example.com";

        // Act
        tgChatRepository.save(new TgChat(chatId));
        LinkResponse response = new LinkResponse(0L, url, tags, filters);
        linksService.addLink(chatId, response);
        Optional<LinkUpdate> found = linksRepository.findByUrl(url);

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().url()).isEqualTo(url);
    }

    @Test
    @Transactional
    @DisplayName("Тест сохранения и получения нескольких ссылок c неопределенными тегами и фильтрами")
    void shouldHandleEmptyArrays() {
        // Arrange
        long chatId = 123L;
        String url = "https://empty.com";
        List<String> emptyList = Collections.emptyList();

        // Act
        tgChatRepository.save(new TgChat(chatId));
        LinkResponse response = new LinkResponse(0L, url, emptyList, emptyList);
        linksService.addLink(chatId, response);

        // Получаем данные через JdbcTemplate
        List<String> tags = jdbcTemplate.queryForObject(
            "SELECT tags FROM links WHERE url = ?",
            (rs, rowNum) -> {
                Array array = rs.getArray("tags");
                return array != null ? Arrays.asList((String[]) array.getArray()) : Collections.emptyList();
            },
            url
        );

        // Assert
        assertThat(tags).isNotNull();
    }

    @Test
    @Transactional
    @DisplayName("Тест удаления связи между чатом и ссылкой")
    void shouldRemoveRelationBetweenChatAndLink() {
        // Arrange
        long chatId = 125L;
        String url = "https://example.com";
        tgChatRepository.save(new TgChat(chatId));
        LinkResponse response = new LinkResponse(0L, url, List.of("test"), List.of("filter"));
        linksService.addLink(chatId, response);

        Integer initialCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM link_tg_chat WHERE tg_chat_id = ? AND link_id = " +
            "(SELECT id FROM links WHERE url = ?)",
            Integer.class,
            chatId, url
        );
        assertThat(initialCount).isEqualTo(1);

        // Act
        RemoveLinkRequest removeLinkRequest = new RemoveLinkRequest(url);
        linksService.removeLink(chatId, removeLinkRequest);

        // Assert
        Integer relationCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM link_tg_chat WHERE tg_chat_id = ? AND link_id = " +
            "(SELECT id FROM links WHERE url = ?)",
            Integer.class,
            chatId, url
        );
        assertThat(relationCount).isZero();
    }
}

