/**
 * Пакет с конфигурациями.
 */
package backend.academy.bot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * RestTemplate configuration.
 */

@Configuration
public class AppConfig {

    /**
     * Create bean for RestTemplate.
     * для того, чтобы использовать RestTemplate в других классах
     * @return RestTemplate bean
     */

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
