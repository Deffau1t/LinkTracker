package backend.academy.scrapper.controller;

import backend.academy.scrapper.dto.ApiErrorResponse;
import backend.academy.scrapper.service.TgChatService;
import java.util.Map;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tg-chat")
public class TgChatController {

    @Autowired
    private TgChatService tgChatService;

    @PostMapping("/{id}")
    public ResponseEntity<?> registerChat(@PathVariable("id") Long id) {
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().body(
                ApiErrorResponse.builder()
                    .description("Некорректные параметры запроса")
                    .code("400")
                    .exceptionName("Bad Request")
                    .exceptionMessage("ID не может быть null или <= 0")
                    .build()
            );
        }

        if (tgChatService.isChatsRegistered(id)) {
            return ResponseEntity.badRequest().body(
                ApiErrorResponse.builder()
                    .description("Чат уже зарегистрирован")
                    .code("400")
                    .exceptionName("Bad Request")
                    .exceptionMessage("Чат уже зарегистрирован или введён некорректный id")
                    .build()
            );
        }

        tgChatService.registerChat(id);

        return ResponseEntity.ok(Map.of("code", "200", "description", "Чат зарегистрирован"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteChat(@PathVariable("id") Long id) {
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().body(
                ApiErrorResponse.builder()
                    .description("Некорректные параметры запроса")
                    .code("400")
                    .exceptionName("Bad Request")
                    .build()
            );
        }

        if (tgChatService.isChatsRegistered(id)) {
            tgChatService.deleteChat(id);
            return ResponseEntity.ok(Map.of("code", "200", "description", "Чат успешно удалён"));
        } else {
            return ResponseEntity.status(404).body(
                ApiErrorResponse.builder()
                    .description("Чат не существует")
                    .code("404")
                    .exceptionName("Not Found")
                    .build()
            );
        }
    }
}
