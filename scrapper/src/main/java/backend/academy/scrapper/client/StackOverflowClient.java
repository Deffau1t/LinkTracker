package backend.academy.scrapper.client;


import backend.academy.scrapper.ScrapperConfig;
import backend.academy.scrapper.dto.StackOverflowQuestionResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class StackOverflowClient {

    private final WebClient webClient;

    public StackOverflowClient(ScrapperConfig scrapperConfig) {
        this.webClient = WebClient.builder()
                .baseUrl(scrapperConfig.stackoverflowApiUrl())
                .defaultHeader("Accept", "application/json")
                .build();
    }

    public Mono<StackOverflowQuestionResponse> fetchQuestionInfo(Long questionId) {
        return webClient.get()
                .uri("/questions/{questionId}?site=stackoverflow", questionId)
                .retrieve()
                .bodyToMono(StackOverflowQuestionResponse.class);
    }
}
