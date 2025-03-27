package backend.academy.scrapper.controller;

import backend.academy.scrapper.dto.AddLinkRequest;
import backend.academy.scrapper.dto.ApiErrorResponse;
import backend.academy.scrapper.dto.LinkResponse;
import backend.academy.scrapper.dto.ListLinksResponse;
import backend.academy.scrapper.dto.RemoveLinkRequest;
import backend.academy.scrapper.service.LinksService;
import backend.academy.scrapper.service.TgChatService;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * LinksController - контроллер для работы со ссылками.
 */

@RestController
@RequestMapping(value = "/links", produces = MediaType.APPLICATION_JSON_VALUE)
public class LinksController {

    /**
     * TgChatService - сервис для работы с чатами.
     */
    @Autowired
    private TgChatService tgChatService;

    /**
     * LinksService - сервис для работы со ссылками.
     */
    @Autowired
    private LinksService linksService;

    /**
     * Метод для получения списка ссылок.
     *
     * @param chatId - идентификатор чата.
     * @return - список ссылок.
     */
    @GetMapping
    public ResponseEntity<?> getLinks(
        final @RequestHeader("Tg-Chat-Id") Long chatId) {
        if (!tgChatService.isChatsRegistered(chatId)) {
            ApiErrorResponse apiErrorResponse = ApiErrorResponse.builder()
                .code("400")
                .description("Некорректные параметры запроса")
                .exceptionName("Bad Request")
                .build();
            return ResponseEntity.badRequest().body(apiErrorResponse);
        }
        LinkResponse linkResponse = LinkResponse.builder()
            .id(chatId)
            .build();
        ListLinksResponse listLinksResponse = ListLinksResponse.builder()
            .links(List.of(linkResponse))
            .build();
        return ResponseEntity.ok(listLinksResponse);
    }

    /**
     * Метод для добавления ссылки.
     *
     * @param chatId - идентификатор чата.
     * @param addLinkRequest - запрос на добавление ссылки.
     * @return - добавленная ссылка.
     */
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> postUpdate(
        final @RequestHeader("Tg-Chat-Id") Long chatId,
        final @RequestBody AddLinkRequest addLinkRequest) {

//        if (!tgChatService.isChatsRegistered(chatId)) {
//            ApiErrorResponse apiErrorResponse = ApiErrorResponse.builder()
//                .code("400")
//                .description("Некорректные параметры запроса")
//                .exceptionName("Bad Request")
//                .build();
//            return ResponseEntity.badRequest().body(apiErrorResponse);
//        }
        linksService.registerChat(chatId);

        LinkResponse savedLink = LinkResponse.builder()
                .id(chatId)
                .url(addLinkRequest.link())
                .tags(addLinkRequest.tags() != null
                    ? addLinkRequest.tags() : Collections.emptyList())
                .filters(addLinkRequest.filters() != null
                    ? addLinkRequest.filters() : Collections.emptyList())
                .build();

        linksService.addLink(chatId, savedLink);

        return ResponseEntity.ok(savedLink);
    }

    /**
     * Метод для удаления ссылки.
     * @param chatId - идентификатор чата.
     * @param removeLinkRequest - запрос на удаление ссылки.
     * @return - удаленная ссылка.
     */
    @DeleteMapping
    public ResponseEntity<?> deleteUpdate(
        final @RequestHeader("Tg-Chat-Id") Long chatId,
        final @RequestBody RemoveLinkRequest removeLinkRequest) {
        if (chatId == null || chatId < 0) {
            ApiErrorResponse apiErrorResponse = ApiErrorResponse.builder()
                .code("400")
                .description("Некорректные параметры запроса")
                .exceptionName("Bad Request")
                .build();
            return ResponseEntity.badRequest().body(apiErrorResponse);
        }

        if (!tgChatService.isChatsRegistered(chatId)) {
            ApiErrorResponse apiErrorResponse = ApiErrorResponse.builder()
                .code("404")
                .description("Ссылка не найдена")
                .exceptionName("Not Found")
                .build();
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(apiErrorResponse);
        }

        linksService.removeLink(chatId, removeLinkRequest);
        return ResponseEntity.ok("Ссылка успешно убрана");
    }
}
