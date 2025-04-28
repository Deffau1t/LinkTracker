package backend.academy.scrapper.service.ormService;

import backend.academy.scrapper.dto.LinkResponse;
import backend.academy.scrapper.dto.RemoveLinkRequest;
import backend.academy.scrapper.entity.LinkUpdate;
import backend.academy.scrapper.repository.LinksRepository;
import backend.academy.scrapper.service.LinksService;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

/**
 * OrmLinksService - класс для работы с таблицей ссылок в БД.
 */

@Service
@AllArgsConstructor
@ConditionalOnProperty(
    name = "database.access-type",
    havingValue = "ORM",
    matchIfMissing = true
)
public class OrmLinksService implements LinksService {
    /**
     * linksRepository - объект для работы с таблицей ссылок в БД.
     */
    private final LinksRepository linksRepository;

    /**
     * addLink - добавляет ссылку в таблицу ссылок в БД.
     * @param chatId - идентификатор чата в Telegram
     * @param linkResponse - информация о ссылке
     */
    @Override
    public void addLink(final Long chatId, final LinkResponse linkResponse) {
        String url = linkResponse.url();
        List<String> tags = linkResponse.tags();
        List<String> filters = linkResponse.filters();
        linksRepository.addLink(chatId, url, tags, filters);
    }

    /**
     * removeLink - удаляет ссылку из таблицы ссылок в БД.
     * @param chatId - идентификатор чата в Telegram
     * @param removeLinkRequest - информация о ссылке для удаления
     */
    @Override
    public void removeLink(
        final Long chatId,
        final RemoveLinkRequest removeLinkRequest
    ) {
        String url = removeLinkRequest.link();
        linksRepository.removeLink(chatId, url);
    }

    /**
     * findByUrl - находит ссылку по url.
     * @param url - url ссылки
     * @return - объект, содержащий информацию о ссылке, если она найдена,
     * иначе пустой объект
     */
    @Override
    public Optional<LinkUpdate> findByUrl(final String url) {
        return linksRepository.findByUrl(url);
    }

}
