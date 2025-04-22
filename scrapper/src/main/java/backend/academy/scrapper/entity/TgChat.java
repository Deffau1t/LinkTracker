package backend.academy.scrapper.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tg_chats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TgChat {
    /**
     * ChatId - id чата.
     */
    @Id
    private Long id;
}
