package backend.academy.bot.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TrackedLink {
    private final String url;
    private final String tags;
    private final String filters;
}
