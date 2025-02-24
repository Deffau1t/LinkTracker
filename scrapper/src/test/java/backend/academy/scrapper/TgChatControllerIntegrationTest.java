package backend.academy.scrapper;

import backend.academy.scrapper.dto.ApiErrorResponse;
import backend.academy.scrapper.service.TgChatService;
import java.util.Objects;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TgChatControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Mock
    private TgChatService tgChatService;

    @Test
    public void testRegisterChat() {
        // Настройка моков
        when(tgChatService.isChatsRegistered(anyLong())).thenReturn(true);

        // Выполнение запроса
        ResponseEntity<ApiErrorResponse> response = restTemplate.postForEntity(
                "/tg-chat/123", null, ApiErrorResponse.class);

        // Проверка результата
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Чат зарегистрирован", Objects.requireNonNull(response.getBody()).description());
        assertEquals("200", Objects.requireNonNull(response.getBody()).code());
    }

}
