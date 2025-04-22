package backend.academy.scrapper.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * LinkUpdate - модель базы данных для хранения ссылок и их обновлений.
 */

@Entity
@Table(name = "links")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LinkUpdate {
    /**
     * Id - уникальный идентификатор ссылки.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Url - ссылка на сайт.
     */
    @Column(nullable = false, unique = true)
    private String url;

    /**
     * Tags - теги для ссылки.
     */
    @Column
    private List<String> tags;

    /**
     * Filters - фильтры для ссылки.
     */
    @Column
    private List<String> filters;

    /**
     * tgChatIds - список ID чатов для уведомлений.
     */
    @Transient
    private List<Long> tgChatIds;
}
