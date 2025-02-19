package backend.academy.scrapper.model;

import lombok.Getter;
import java.util.List;

@Getter
public class ApiErrorResponse {
    private String description;
    private String code;
    private String exceptionName;
    private String exceptionMessage;
    private List<String> stacktrace;
}
