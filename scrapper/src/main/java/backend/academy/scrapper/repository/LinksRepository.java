package backend.academy.scrapper.repository;

import backend.academy.scrapper.entity.LinkUpdate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * LinksRepository - Репозиторий для работы с таблицей links.
 */
public interface LinksRepository extends JpaRepository<LinkUpdate, Long> {
    Optional<LinkUpdate> findByUrl(String url);
}
