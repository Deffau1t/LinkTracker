package backend.academy.scrapper.service;

import backend.academy.scrapper.entity.LinkUpdate;
import backend.academy.scrapper.entity.TgChat;
import backend.academy.scrapper.repository.TgChatRepository;
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
    private final TgChatRepository tgChatRepository;

    @Override
    public void registerChat(final Long chatId) {
        if (!tgChatRepository.existsById(chatId)) {
            tgChatRepository.save(new TgChat(chatId));
        }
    }

    @Override
    public void deleteChat(final Long chatId) {
        tgChatRepository.deleteById(chatId);
    }

    @Override
    public List<LinkUpdate> getAllLinks(final Long chatId) {
        return tgChatRepository.findAllLinksByChatId(chatId);
    }

    @Override
    public List<Long> getAllChatIds() {
        return tgChatRepository.findAllChatIds();
    }

    @Override
    public boolean isChatRegistered(final Long chatId) {
        return tgChatRepository.existsById(chatId);
    }
}
