package backend.academy.bot.controller;

import backend.academy.bot.model.ApiErrorResponse;
import backend.academy.bot.model.LinkUpdate;
import backend.academy.bot.service.LinkTrackerBot;
import backend.academy.bot.service.LinkUpdateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/updates")
@RequiredArgsConstructor
public class UpdateController {
    private final LinkUpdateService linkUpdateService;
    private final LinkTrackerBot linkTrackerBot;

    @PostMapping
    public ResponseEntity<?> postUpdate(@RequestBody LinkUpdate linkUpdate) {
        try {
            log.info("Получено обновление: {}", linkUpdate);
            linkUpdateService.updateLink(linkUpdate, linkTrackerBot);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Ошибка при обработке обновления: {}", e.getMessage());
            ApiErrorResponse apiErrorResponse = ApiErrorResponse.builder()
                    .code("400")
                    .description("Некорректные параметры запроса")
                    .exceptionName(e.getClass().getSimpleName())
                    .build();
            return ResponseEntity.badRequest().body(apiErrorResponse);
        }
    }
}
