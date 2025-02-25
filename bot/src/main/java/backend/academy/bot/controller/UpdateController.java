package backend.academy.bot.controller;

import backend.academy.bot.model.ApiErrorResponse;
import backend.academy.bot.model.LinkUpdate;
import backend.academy.bot.service.LinkTrackerBot;
import backend.academy.bot.service.LinkUpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/updates")
public class UpdateController {
    @Autowired
    private LinkUpdateService linkUpdateService;

    @Autowired
    private LinkTrackerBot linkTrackerBot;

    private ApiErrorResponse apiErrorResponse;

    @PostMapping
    public ResponseEntity<?> postUpdate(@RequestBody LinkUpdate linkUpdate) {
        try {
            linkUpdateService.updateLink(linkUpdate, linkTrackerBot);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            ApiErrorResponse apiErrorResponse = ApiErrorResponse.builder()
                .code("400")
                .description("Некорректные параметры запроса")
                .exceptionName("Bad Request")
                .build();
            return ResponseEntity.badRequest().body(apiErrorResponse);
        }
    }
}
