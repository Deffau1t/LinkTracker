package backend.academy.scrapper.service;

import backend.academy.scrapper.dto.LinkResponse;
import backend.academy.scrapper.dto.RemoveLinkRequest;
import backend.academy.scrapper.entity.LinkUpdate;
import backend.academy.scrapper.entity.TgChat;
import backend.academy.scrapper.repository.LinksRepository;
import java.util.Optional;
import backend.academy.scrapper.repository.TgChatRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@AllArgsConstructor
@ConditionalOnProperty(
    name = "database.access-type",
    havingValue = "ORM",
    matchIfMissing = true
)
public class OrmLinksService implements LinksService {
    private final LinksRepository linksRepository;
    private final TgChatRepository tgChatRepository;

    @Override
    @Transactional
    public void addLink(final Long chatId, final LinkResponse linkResponse) {
        LinkUpdate link = new LinkUpdate();
        link.url(linkResponse.url());
        link.tags(linkResponse.tags());
        link.filters(linkResponse.filters());

        Optional<LinkUpdate> existing = linksRepository.findByUrl(link.url());
        LinkUpdate saved = existing.orElseGet(() -> linksRepository.save(link));

        if (!tgChatRepository.existsById(chatId)) {
            tgChatRepository.save(new TgChat(chatId));
        }

        tgChatRepository.insertLinkChatRelation(saved.id(), chatId);
    }

    @Override
    @Transactional
    public void removeLink(final Long chatId, final RemoveLinkRequest removeLinkRequest) {
        linksRepository.findByUrl(removeLinkRequest.link())
            .ifPresent(link -> tgChatRepository.deleteLinkChatRelation(link.id(), chatId));
    }

    @Override
    public Optional<LinkUpdate> findByUrl(final String url) {
        return linksRepository.findByUrl(url);
    }
}
