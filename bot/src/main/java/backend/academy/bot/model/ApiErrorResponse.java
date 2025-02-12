package backend.academy.bot.model;

import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ApiErrorResponse {
    private String description;
    private String code;
    private String exceptionName;
    private List<String> stacktrace;
}
