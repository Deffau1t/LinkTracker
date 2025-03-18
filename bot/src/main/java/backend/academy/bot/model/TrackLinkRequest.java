package backend.academy.bot.model;

import java.util.List;

public record TrackLinkRequest(String link,
                               List<String> tags,
                               List<String> filters) {}
