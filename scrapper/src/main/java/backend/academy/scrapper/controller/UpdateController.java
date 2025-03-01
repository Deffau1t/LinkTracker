package backend.academy.scrapper.controller;

import backend.academy.scrapper.dto.LinkUpdate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/updates")
public class UpdateController {

    @GetMapping
    public ResponseEntity<List<LinkUpdate>> getUpdates() {
        log.info("Отправка обновлений в бот...");

        // Здесь должен быть код, который получает реальные обновления
        LinkUpdate dummyUpdate = LinkUpdate.builder()
                .url("https://example.com")
                .description("Обнаружено обновление!")
                .tgChatIds(List.of(123456L))  // Здесь должны быть реальные ID чатов
                .build();

        return ResponseEntity.ok(Collections.singletonList(dummyUpdate));
    }
}
