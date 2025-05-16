package backend.academy.bot.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * API error response model.
 */

@Getter
@Setter
@Builder
public class ApiErrorResponse {
    /**
     * Описание ошибки.
     */
    private String description;

    /**
     * Код ошибки.
     */
    private String code;

    /**
     * Имя исключения.
     */
    private String exceptionName;

    /**
     * Стек трассировки ошибки.
     */
    private List<String> stacktrace;
}
