package backend.academy.scrapper.client;

import backend.academy.scrapper.config.ScrapperConfig;
import backend.academy.scrapper.dto.StackOverflowAnswerResponse;
import backend.academy.scrapper.dto.StackOverflowCommentResponse;
import backend.academy.scrapper.dto.StackOverflowQuestionResponse;
import backend.academy.scrapper.dto.StackOverflowResponseWrapper;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
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
     * webClient - объект для работы с Stack Overflow API.
     */
    private final WebClient webClient;

    /**
     * apiKey - ключ для доступа к Stack Overflow API.
     */
    @Value("${app.stackoverflow.key}")
    private String apiKey;

    /**
     * Конструктор класса StackOverflowClient.
     *
     * @param scrapperConfig - объект конфигурации для доступа
     *                      к Stack Overflow API.
     */
    public StackOverflowClient(final ScrapperConfig scrapperConfig) {
        this.webClient = WebClient.builder()
                .baseUrl(scrapperConfig.stackoverflowApiUrl())
                .defaultHeader("Accept", "application/json")
                .build();
    }

    /**
     * Метод для получения информации о вопросе по его идентификатору.
     *
     * @param questionId - идентификатор вопроса.
     * @return Mono с объектом StackOverflowQuestionResponse.
     */
    public Mono<StackOverflowQuestionResponse> fetchQuestionInfo(
        final Long questionId) {
        return webClient.get()
            .uri("/questions/{questionId}?site=stackoverflow", questionId)
            .retrieve()
            .bodyToMono(StackOverflowQuestionResponse.class)
            .doOnSuccess(response -> log.info(
                "Получен вопрос {}: {}", questionId, response)
            )
            .doOnError(
                error -> log.error("Ошибка при запросе вопроса {}: {}",
                    questionId,
                    error.getMessage())
            );
    }

    /**
     * Метод для получения комментариев к вопросу по его идентификатору.
     * @param questionId - идентификатор вопроса.
     * @return - Mono с списком объектов StackOverflowCommentResponse.
     */
    public Mono<List<StackOverflowCommentResponse>> fetchComments(
        final Long questionId) {
        return webClient.get()
            .uri(
                "/questions/{questionId}/comments?site=stackoverflow",
                questionId
            )
            .retrieve()
            .bodyToMono(
                new ParameterizedTypeReference<
                    StackOverflowResponseWrapper<StackOverflowCommentResponse>
                    >() {

                }
            )
            .map(StackOverflowResponseWrapper::items)
            .doOnError(error -> log.error(
                "Ошибка при запросе комментариев {}: {}",
                questionId,
                error.getMessage())
            );
    }

    /**
     * Метод для получения ответов на вопрос по его идентификатору.
     * @param questionId - идентификатор вопроса.
     * @return - Mono с списком объектов StackOverflowAnswerResponse.
     */
    public Mono<List<StackOverflowAnswerResponse>> fetchAnswers(
        final Long questionId) {
        return webClient.get()
            .uri(
                "/questions/{questionId}/answers?site=stackoverflow",
                questionId
            )
            .retrieve()
            .bodyToMono(
                new ParameterizedTypeReference<
                    StackOverflowResponseWrapper<StackOverflowAnswerResponse>
                    >() {

                }
            )
            .map(StackOverflowResponseWrapper::items)
            .doOnError(error -> log.error(
                "Ошибка при запросе ответов {}: {}",
                questionId,
                error.getMessage())
            );
    }
}
