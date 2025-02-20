package backend.academy.scrapper.service;

import backend.academy.bot.service.LinkUpdateService;
import backend.academy.scrapper.dto.RemoveLinkRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class LinksService {

    @Autowired
    @Lazy
    private LinkUpdateService linkUpdateService;

    public void deleteLinkOfChat(Long chatId, RemoveLinkRequest removeLinkRequest) {
        List<String> links = linkUpdateService.chatSubscribes().get(chatId);
        links.remove(removeLinkRequest.link());
    }
}
