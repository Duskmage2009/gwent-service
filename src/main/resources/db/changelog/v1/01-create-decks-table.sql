--liquibase formatted sql

--changeset author:duskmage2009 id:create-decks-table
CREATE TABLE decks (
                       id BIGSERIAL PRIMARY KEY,
                       name VARCHAR(200) NOT NULL UNIQUE,
                       faction VARCHAR(50) NOT NULL,
                       leader_ability VARCHAR(200) NOT NULL,
                       provision_limit INTEGER NOT NULL,
                       categories VARCHAR(500),
                       description VARCHAR(1000),
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Индексы для оптимизации поиска
CREATE INDEX idx_deck_name ON decks(name);
CREATE INDEX idx_deck_faction ON decks(faction);

-- Комментарии к таблице
COMMENT ON TABLE decks IS 'Gwent card decks';
COMMENT ON COLUMN decks.name IS 'Unique deck name';
COMMENT ON COLUMN decks.faction IS 'Deck faction (Northern Realms, Nilfgaard, etc.)';
COMMENT ON COLUMN decks.leader_ability IS 'Leader ability name';
COMMENT ON COLUMN decks.provision_limit IS 'Maximum provision points (100-200)';