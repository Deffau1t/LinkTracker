package backend.academy.scrapper;

import backend.academy.scrapper.dto.LinkResponse;
import backend.academy.scrapper.dto.RemoveLinkRequest;
import backend.academy.scrapper.entity.LinkUpdate;
import backend.academy.scrapper.service.LinksService;
import backend.academy.scrapper.service.TgChatService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(locations = "classpath:test.env")
@DisplayName("В зависимости от конфигурации работа идёт через разные имплементации (SQL, ORM)")
class LinkServiceTest {

    @Nested
    @ActiveProfiles("orm")
    @DisplayName("ORM реализация тестов")
    class OrmImplementationTests {
        @Autowired
        private LinksService linksService;

        @Autowired
        private TgChatService tgChatService;

        @Test
        @Transactional
        @DisplayName("Добавление и поиск ссылки")
        void shouldAddAndFindLink() {
            // Arrange
            long chatId = 1L;
            String url = "https://orm-test.com";
            tgChatService.registerChat(chatId);

            // Act
            linksService.addLink(chatId, new LinkResponse(chatId, url, List.of("tag1"), List.of("filter1")));
            Optional<LinkUpdate> found = linksService.findByUrl(url);

            // Assert
            assertThat(found).isPresent();
            assertThat(found.get().url()).isEqualTo(url);
            assertThat(found.get().tags()).containsExactly("tag1");
        }

        @Test
        @Transactional
        @DisplayName("Удаление ссылки")
        void shouldRemoveLinkRelation() {
            // Arrange
            long chatId = 2L;
            String url = "https://remove-test.com";
            tgChatService.registerChat(chatId);
            linksService.addLink(chatId, new LinkResponse(chatId, url, List.of(), List.of()));

            // Act
            linksService.removeLink(chatId, new RemoveLinkRequest(url));

            // Assert
            assertThat(tgChatService.getAllLinks(chatId)).isEmpty();
        }
    }

    @Nested
    @ActiveProfiles("sql")
    @DisplayName("SQL реализация тестов")
    class SqlImplementationTests {
        @Autowired
        private LinksService linksService;

        @Autowired
        private TgChatService tgChatService;

        @Test
        @Transactional
        @DisplayName("Добавление и поиск ссылки")
        void shouldHandleSqlOperations() {
            // Arrange
            long chatId = 3L;
            String url = "https://sql-test.com";

            // Act & Assert
            tgChatService.registerChat(chatId);
            assertThat(tgChatService.isChatRegistered(chatId)).isTrue();

            linksService.addLink(chatId, new LinkResponse(chatId, url, List.of("sql"), List.of()));
            assertThat(linksService.findByUrl(url)).isPresent();

            List<LinkUpdate> links = tgChatService.getAllLinks(chatId);
            assertThat(links).hasSize(1);
            assertThat(links.get(0).url()).isEqualTo(url);
        }
    }

    @Nested
    @DisplayName("Общие сервисные тесты")
    class CommonServiceTests {
        @Autowired
        private LinksService linksService;

        @Autowired
        private TgChatService tgChatService;

        @Test
        @Transactional
        @DisplayName("Работа с несколькими чатами для одной ссылки")
        void shouldHandleMultipleChatsForLink() {
            // Arrange
            String url = "https://shared.com";
            long chatId1 = 10L;
            long chatId2 = 11L;

            // Act
            tgChatService.registerChat(chatId1);
            tgChatService.registerChat(chatId2);
            linksService.addLink(chatId1, new LinkResponse(chatId1, url, List.of(), List.of()));
            linksService.addLink(chatId2, new LinkResponse(chatId2, url, List.of(), List.of()));

            // Assert
            assertThat(tgChatService.getAllLinks(chatId1)).hasSize(1);
            assertThat(tgChatService.getAllLinks(chatId2)).hasSize(1);

            // Remove only for one chat
            linksService.removeLink(chatId1, new RemoveLinkRequest(url));
            assertThat(tgChatService.getAllLinks(chatId1)).isEmpty();
            assertThat(tgChatService.getAllLinks(chatId2)).hasSize(1);
        }

        @Test
        @Transactional
        @DisplayName("Поиск несуществующей ссылки")
        void shouldReturnEmptyForNonExistentUrl() {
            assertThat(linksService.findByUrl("https://nonexistent.com")).isEmpty();
        }

        @Test
        @Transactional
        @DisplayName("Удаление ссылки из чата")
        void shouldHandleChatDeletion() {
            // Arrange
            long chatId = 20L;
            tgChatService.registerChat(chatId);

            // Act
            tgChatService.deleteChat(chatId);

            // Assert
            assertThat(tgChatService.isChatRegistered(chatId)).isFalse();
            assertThat(tgChatService.getAllChatIds()).doesNotContain(chatId);
        }
    }
}
