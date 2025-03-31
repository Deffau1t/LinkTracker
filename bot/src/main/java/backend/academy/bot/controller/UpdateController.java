package backend.academy.bot.controller;

import backend.academy.bot.model.ApiErrorResponse;
import backend.academy.bot.model.LinkUpdate;
import backend.academy.bot.service.LinkTrackerBot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller для обработки обновлений ссылок.
 */

@Slf4j
@RestController
@RequestMapping("/updates")
@RequiredArgsConstructor
public class UpdateController {

    /**
     * LinkTrackerBot - объект бота для обработки обновлений ссылок.
     */
    private final LinkTrackerBot linkTrackerBot;

    /**
     * Обрабатывает POST-запрос на адрес /updates
     * с телом в виде объекта LinkUpdate.
     * Вызывает метод updateLink из сервиса LinkUpdateService
     * для обработки обновления ссылки.
     *
     * @param linkUpdate Объект LinkUpdate,
     *                  содержащий информацию о ссылке и ее обновлении.
     * @return ResponseEntity с кодом 200 в случае успешной обработки,
     * или ResponseEntity с кодом 400 и ApiErrorResponse в случае ошибки.
     */

    @PostMapping
    public ResponseEntity<?> postUpdate(
        final @RequestBody LinkUpdate linkUpdate
    ) {
        try {
            log.info("Получено обновление: {}", linkUpdate);

            for (Long chatId : linkUpdate.tgChatIds()) {
                linkTrackerBot.sendMessage(chatId,
                    "\uD83D\uDD14 Обновление на "
                        + linkUpdate.url()
                        + "\n"
                        + linkUpdate.description());
            }

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
