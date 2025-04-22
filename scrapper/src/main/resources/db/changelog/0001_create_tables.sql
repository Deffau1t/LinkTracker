-- Таблица Telegram чатов
CREATE TABLE IF NOT EXISTS tg_chats (
    id BIGSERIAL PRIMARY KEY
);

-- Таблица отслеживаемых ссылок
CREATE TABLE IF NOT EXISTS links (
    id BIGSERIAL PRIMARY KEY,
    url TEXT NOT NULL UNIQUE,
    tags TEXT[],
    filters TEXT[]
);

-- Таблица для связи ссылок и чатов
CREATE TABLE IF NOT EXISTS link_tg_chat (
    link_id BIGINT NOT NULL REFERENCES links(id) ON DELETE CASCADE,
    tg_chat_id BIGINT NOT NULL REFERENCES tg_chats(id) ON DELETE CASCADE,
    PRIMARY KEY (link_id, tg_chat_id)
);

-- Индексы для ускорения запросов
CREATE INDEX IF NOT EXISTS idx_links_url ON links(url);
CREATE INDEX IF NOT EXISTS idx_link_tg_chat_link ON link_tg_chat(link_id);
CREATE INDEX IF NOT EXISTS idx_link_tg_chat_chat ON link_tg_chat(tg_chat_id);
