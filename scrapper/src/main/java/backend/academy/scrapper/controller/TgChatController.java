package backend.academy.scrapper.controller;

import backend.academy.scrapper.dto.ApiErrorResponse;
import backend.academy.scrapper.service.TgChatService;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ApiErrorResponse registerChat(@PathVariable("id") @NotNull Long id) {
        if (tgChatService.isChatsRegistered(id)) {
            return ApiErrorResponse.builder()
                .description("Некорректные параметры запроса")
                .code("400")
                .exceptionName("Bad Request")
                .exceptionMessage("Чат уже зарегистрирован или введён некорректный id")
                .build();
        }
        return ApiErrorResponse.builder()
            .code("200")
            .description("Чат зарегистрирован")
            .build();
    }


    @DeleteMapping("/{id}")
    public ApiErrorResponse deleteChat(@PathVariable("id") @NotNull Long id) {
        if (id <= 0) {
            return ApiErrorResponse.builder()
                .description("Некорректные параметры запроса")
                .code("400")
                .exceptionName("Bad Request")
                .build();
        }
        if (tgChatService.isChatsRegistered(id)) {
            tgChatService.deleteChat(id);
            return ApiErrorResponse.builder()
                .code("200")
                .description("Чат успешно удалён")
                .build();
        } else {
            return ApiErrorResponse.builder()
                .description("Чат не существует")
                .code("404")
                .exceptionName("Not Found")
                .build();
        }
    }
}
