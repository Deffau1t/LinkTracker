package backend.academy.scrapper.client;

import backend.academy.scrapper.ScrapperConfig;
import backend.academy.scrapper.model.StackOverflowQuestionList;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class StackOverflowClient {

    private final String baseUrl;
    private final RestTemplate restTemplate;

    @Autowired
    public StackOverflowClient(ScrapperConfig config) {
        this.baseUrl = config.stackoverflowApiUrl();
        this.restTemplate = new RestTemplate();
    }

    /**
     * Запрашивает список вопросов с указанным тегом.
     */
    public StackOverflowQuestionList getQuestions(String tag) {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .pathSegment("questions")
                .queryParam("order", "desc")
                .queryParam("sort", "activity")
                .queryParam("tagged", tag)
                .queryParam("site", "stackoverflow")
                .build().toUriString();
        return restTemplate.getForObject(url, StackOverflowQuestionList.class);
    }
}
