package backend.academy.scrapper.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * RestTemplate configuration.
 */

@Configuration
public class RestTemplateConfig {

    /**
     * Create bean for RestTemplate.
     * для того, чтобы использовать RestTemplate в других классах
     * @return RestTemplate bean
     */

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setMessageConverters(
            List.of(
                new MappingJackson2HttpMessageConverter()
            )
        );
        return restTemplate;
    }
}
