package backend.academy.scrapper.client;

import backend.academy.scrapper.ScrapperConfig;
import backend.academy.scrapper.dto.StackOverflowQuestionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * Client для работы с API Stack Overflow.
 */

@Slf4j
@Component
public class StackOverflowClient {

    /**
     * webClient - объект для работы с API Stack Overflow.
     */
    private final WebClient webClient;

    /**
     * Конструктор класса StackOverflowClient.
     *
     * @param scrapperConfig - конфигурация для доступа к API Stack Overflow.
     */
    public StackOverflowClient(final ScrapperConfig scrapperConfig) {
        this.webClient = WebClient.builder()
                .baseUrl(scrapperConfig.stackoverflowApiUrl())
                .defaultHeader("Accept", "application/json")
                .build();
    }

    /**
     * Метод для получения информации о вопросе на Stack Overflow.
     *
     * @param questionId - идентификатор вопроса на Stack Overflow.
     * @return Mono<StackOverflowQuestionResponse>
     * - Информация о вопросе на Stack Overflow.
     */
    public Mono<StackOverflowQuestionResponse> fetchQuestionInfo(
        final Long questionId) {
        return webClient.get()
                .uri("/questions/{questionId}?site=stackoverflow", questionId)
                .retrieve()
                .bodyToMono(StackOverflowQuestionResponse.class)
                .doOnError(
                    e -> log.error(
                        "Ошибка при запросе к StackOverflow API: {}",
                        e.getMessage()
                    )
                );
    }
}
