package backend.academy.bot.model;

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
    private String description;
    private String code;
    private String exceptionName;
    private List<String> stacktrace;
}
