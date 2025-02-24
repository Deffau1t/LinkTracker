//package backend.academy.bot;
//
//import backend.academy.bot.model.LinkUpdate;
//import backend.academy.bot.service.LinkUpdateService;
//import backend.academy.scrapper.client.GitHubClient;
//import backend.academy.scrapper.client.StackOverflowClient;
//import lombok.AllArgsConstructor;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//@Component
//@AllArgsConstructor
//public class LinkCheckScheduler {
//
//    private final LinkUpdateService linkUpdateService;
//
//    @Scheduled(fixedRate = 60000)
//    public void checkForUpdates() {
//         LinkUpdate dummyUpdate = LinkUpdate.builder()
//                .url("https://github.com/spring-projects/spring-boot")
//                .description("Обнаружено новое обновление репозитория.")
//                .tgChatIds(linkUpdateService.chatSubscribes().keySet().stream().toList())
//                .build();
//
//        linkUpdateService.updateLink(dummyUpdate);
//    }
//}
