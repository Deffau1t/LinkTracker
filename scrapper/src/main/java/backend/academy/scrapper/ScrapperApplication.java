package backend.academy.scrapper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * ScrapperApplication - главный класс для приложения.
 */

@SpringBootApplication
@EnableConfigurationProperties({ScrapperConfig.class})
@EnableScheduling
public class ScrapperApplication {
    /**
     * main method.
     * @param args - аргументы командной строки
     */
    public static void main(final String[] args) {
        SpringApplication.run(ScrapperApplication.class, args);
    }
}
