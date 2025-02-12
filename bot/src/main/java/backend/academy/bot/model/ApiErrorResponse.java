package backend.academy.bot.model;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiErrorResponse {
    private String description;
    private String code;
    private String exceptionName;
    private List<String> stacktrace;
}
