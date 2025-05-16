package backend.academy.scrapper.service.ormDB;

import backend.academy.scrapper.entity.LinkUpdate;
import backend.academy.scrapper.entity.TgChat;
import backend.academy.scrapper.repository.TgChatRepository;
import backend.academy.scrapper.service.TgChatService;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@AllArgsConstructor
@ConditionalOnProperty(
    name = "database.access-type",
    havingValue = "ORM",
    matchIfMissing = true
)
public class OrmTgChatService implements TgChatService {
    /**
     * tgChatRepository - репозиторий для работы с таблицей tg_chats.
     */
    private final TgChatRepository tgChatRepository;

    /**
     * registerChat - метод для регистрации чата в базе данных.
     * @param chatId - идентификатор чата в базе данных.
     */
    @Override
    public void registerChat(final Long chatId) {
        if (!tgChatRepository.existsById(chatId)) {
            tgChatRepository.save(new TgChat(chatId));
        }
    }

    /**
     * deleteChat - метод для удаления чата из базы данных.
     * @param chatId - идентификатор чата в базе данных.
     */
    @Override
    public void deleteChat(final Long chatId) {
        tgChatRepository.deleteById(chatId);
    }

    /**
     * getAllLinks - метод для получения списка ссылок.
     * @param chatId - идентификатор чата в базе данных.
     * @return список ссылок на страницы обновлений для указанного чата.
     */
    @Override
    public List<LinkUpdate> getAllLinks(final Long chatId) {
        return tgChatRepository.findAllLinksByChatId(chatId);
    }

    /**
     * getAllChatIds - метод для получения списка идентификаторов чатов.
     * @return список идентификаторов чатов.
     */
    @Override
    public List<Long> getAllChatIds() {
        return tgChatRepository.findAllChatIds();
    }

    /**
     * isChatRegistered - метод для проверки регистрации чата.
     * @param chatId - идентификатор чата в базе данных.
     * @return true, если чат зарегистрирован, иначе false.
     */
    @Override
    public boolean isChatRegistered(final Long chatId) {
        return tgChatRepository.existsById(chatId);
    }
}
