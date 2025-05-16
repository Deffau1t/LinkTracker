package backend.academy.bot.dto;

/**
 * Bot state enum - определяет текущее состояние бота.
 */

public enum BotState {
    /**
     * Бот находится в состоянии ожидания.
     */
    IDLE,

    /**
     * Бот ожидает ссылку.
     */
    WAITING_FOR_LINK,

    /**
     * Бот ожидает теги.
     */
    WAITING_FOR_TAGS,

    /**
     * Бот ожидает фильтры.
     */
    WAITING_FOR_FILTERS,

    /**
     * Бот ожидает отписку.
     */
    WAITING_FOR_UNTRACK;
}
