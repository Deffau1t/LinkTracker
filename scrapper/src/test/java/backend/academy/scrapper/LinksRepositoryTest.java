package backend.academy.scrapper;

import backend.academy.scrapper.dto.LinkUpdate;
import backend.academy.scrapper.repository.LinksRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJpaTest
@SpringJUnitConfig
class LinksRepositoryTest {

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15.2");

    @DynamicPropertySource
    static void configureProperties(org.springframework.test.context.DynamicPropertyRegistry registry) {
        postgres.start();
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private LinksRepository linksRepository;

    @Test
    void testRegisterChat() {
        Long chatId = 123L;
        linksRepository.registerChat(chatId);
        List<LinkUpdate> links = linksRepository.getAllLinks(chatId);
        assertThat(links).isEmpty();
    }

    @Test
    void testAddAndGetLinks() {
        Long chatId = 456L;
        linksRepository.registerChat(chatId);
        linksRepository.addLink(chatId, "https://example.com", "Example Description");

        List<LinkUpdate> links = linksRepository.getAllLinks(chatId);
        assertThat(links).hasSize(1);
        assertThat(links.get(0).url()).isEqualTo("https://example.com");
    }

    @Test
    void testRemoveLink() {
        Long chatId = 789L;
        linksRepository.registerChat(chatId);
        linksRepository.addLink(chatId, "https://example.com", "Example Description");

        linksRepository.removeLink(chatId, "https://example.com");
        List<LinkUpdate> links = linksRepository.getAllLinks(chatId);
        assertThat(links).isEmpty();
    }
}
