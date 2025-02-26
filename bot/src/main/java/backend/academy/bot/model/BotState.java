package backend.academy.bot.model;

public enum BotState {
    IDLE,
    WAITING_FOR_LINK,
    WAITING_FOR_TAGS,
    WAITING_FOR_FILTERS,
    WAITING_FOR_UNTRACK
}
