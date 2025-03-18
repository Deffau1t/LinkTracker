package backend.academy.scrapper.dto;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * ApiErrorResponse - ДTO для ответа на ошибку.
 */

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiErrorResponse {
    /**
     * description - описание ошибки.
     */
    private String description;

    /**
     * code - код ошибки.
     */
    private String code;

    /**
     * exceptionName - имя исключения.
     */
    private String exceptionName;

    /**
     * exceptionMessage - сообщение исключения.
     */
    private String exceptionMessage;

    /**
     * Stacktrace - список стек трассировки.
     */
    private List<String> stacktrace;
}
