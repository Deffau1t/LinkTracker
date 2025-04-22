package backend.academy.scrapper.service;

import backend.academy.scrapper.dto.LinkResponse;
import backend.academy.scrapper.dto.RemoveLinkRequest;
import backend.academy.scrapper.entity.LinkUpdate;
import java.util.Optional;

/**
 * LinksService - интерфейс сервиса,
 * предоставляющего методы для работы с данными о ссылках в БД.
 */

public interface LinksService {
    /**
     * Добавляет ссылку в базу данных.
     * @param chatId - идентификатор чата в Telegram
     * @param linkResponse - информация о ссылке
     */
    void addLink(Long chatId, LinkResponse linkResponse);

    /**
     * Удаляет ссылку из базы данных.
     * @param chatId - идентификатор чата в Telegram
     * @param removeLinkRequest - информация о ссылке для удаления
     */
    void removeLink(Long chatId, RemoveLinkRequest removeLinkRequest);

    /**
     * Ищет ссылку по url.
     * @param url - url ссылки
     * @return - объект, содержащий информацию о ссылке, если она найдена,
     * иначе пустой объект
     */
    Optional<LinkUpdate> findByUrl(String url);
}
