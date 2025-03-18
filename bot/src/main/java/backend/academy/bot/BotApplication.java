/**
 * Пакет backend.academy.bot содержит классы, связанные с ботом.
 */
package backend.academy.bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * BotApplication - the main class for bot.
 */
@SpringBootApplication
@EnableConfigurationProperties({BotConfig.class})
@EnableScheduling
public class BotApplication {
    /**
     * main method.
     * @param args - аргументы командной строки
     */
    public static void main(final String[] args) {
        SpringApplication.run(BotApplication.class, args);
    }
}
