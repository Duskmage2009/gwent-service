--liquibase formatted sql

--changeset author:duskmage2009 id:create-cards-table
CREATE TABLE cards (
                       id BIGSERIAL PRIMARY KEY,
                       name VARCHAR(200) NOT NULL,
                       deck_id BIGINT NOT NULL,
                       provision INTEGER NOT NULL,
                       power INTEGER NOT NULL,
                       type VARCHAR(50) NOT NULL,
                       faction VARCHAR(50) NOT NULL,
                       description VARCHAR(500),
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Foreign Key
                       CONSTRAINT fk_cards_deck
                           FOREIGN KEY (deck_id)
                               REFERENCES decks(id)
                               ON DELETE CASCADE
);

-- Индексы для оптимизации поиска и фильтрации
CREATE INDEX idx_card_name ON cards(name);
CREATE INDEX idx_card_deck ON cards(deck_id);
CREATE INDEX idx_card_type ON cards(type);
CREATE INDEX idx_card_faction ON cards(faction);

-- Комментарии к таблице
COMMENT ON TABLE cards IS 'Gwent cards';
COMMENT ON COLUMN cards.name IS 'Card name';
COMMENT ON COLUMN cards.deck_id IS 'Reference to deck';
COMMENT ON COLUMN cards.provision IS 'Provision cost (0-20)';
COMMENT ON COLUMN cards.power IS 'Card power (0-50)';
COMMENT ON COLUMN cards.type IS 'Card type (Unit, Special, Artifact, Stratagem)';
COMMENT ON COLUMN cards.faction IS 'Card faction';