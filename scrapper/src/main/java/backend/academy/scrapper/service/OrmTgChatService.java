package backend.academy.scrapper.service;

import backend.academy.scrapper.entity.LinkUpdate;
import backend.academy.scrapper.repository.TgChatRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@AllArgsConstructor
@ConditionalOnProperty(name = "database.access-type", havingValue = "ORM", matchIfMissing = true)
public class OrmTgChatService implements TgChatService {
    private final TgChatRepository tgChatRepository;

    @Override
    public void registerChat(Long chatId) {
        tgChatRepository.registerChat(chatId);
    }

    @Override
    public void deleteChat(Long chatId) {
        tgChatRepository.deleteChat(chatId);
    }

    @Override
    public List<LinkUpdate> getAllLinks(Long chatId) {
        return tgChatRepository.getAllLinks(chatId);
    }

    @Override
    public List<Long> getAllChatIds() {
        return tgChatRepository.getAllChatIds();
    }

    @Override
    public boolean isChatRegistered(Long chatId) {
        return tgChatRepository.existsChatById(chatId);
    }
}
