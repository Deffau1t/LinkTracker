package backend.academy.scrapper.service;

import backend.academy.scrapper.entity.LinkUpdate;
import backend.academy.scrapper.repository.TgChatRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * OrmTgChatService - класс, реализующий интерфейс TgChatService
 * и предоставляющий реализацию работы с базой данных на основе ORM (Hibernate).
 */

@Service
@AllArgsConstructor
@ConditionalOnProperty(
    name = "database.access-type",
    havingValue = "ORM",
    matchIfMissing = true
)
public class OrmTgChatService implements TgChatService {
    /**
     * tgChatRepository - объект для работы с таблицей чатов в базе данных.
     */
    private final TgChatRepository tgChatRepository;

    /**
     * registerChat - метод для регистрации чата в базе данных.
     * @param chatId - идентификатор чата
     */
    @Override
    public void registerChat(final Long chatId) {
        tgChatRepository.registerChat(chatId);
    }

    /**
     * deleteChat - метод для удаления чата из базы данных.
     * @param chatId - идентификатор чата
     */
    @Override
    public void deleteChat(final Long chatId) {
        tgChatRepository.deleteChat(chatId);
    }

    /**
     * getAllLinks - метод для получения всех ссылок для заданного чата.
     * @param chatId - идентификатор чата
     * @return список ссылок для заданного чата
     */
    @Override
    public List<LinkUpdate> getAllLinks(final Long chatId) {
        return tgChatRepository.getAllLinks(chatId);
    }

    /**
     * getAllChatIds - метод для получения списка всех идентификаторов чатов.
     * @return список всех идентификаторов чатов
     */
    @Override
    public List<Long> getAllChatIds() {
        return tgChatRepository.getAllChatIds();
    }

    /**
     * isChatRegistered - метод для проверки регистрации чата.
     * @param chatId - идентификатор чата
     * @return true, если чат зарегистрирован, иначе false
     */
    @Override
    public boolean isChatRegistered(final Long chatId) {
        return tgChatRepository.existsChatById(chatId);
    }
}
