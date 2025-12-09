--liquibase formatted sql

--changeset author:duskmage2009 id:insert-initial-decks
INSERT INTO decks (name, faction, leader_ability, provision_limit, categories, description)
VALUES ('Monster Swarm', 'Monsters', 'Overwhelming Hunger', 165, 'Swarm, Consume',
        'A powerful swarm deck that overwhelms opponents with numbers'),
       ('Northern Tactics', 'Northern Realms', 'Inspired Zeal', 165, 'Engines, Boost',
        'Strategic deck focused on engines and boosting units'),
       ('Nilfgaard Control', 'Nilfgaard', 'Lockdown', 160, 'Control, Poison',
        'Control-heavy deck with poison mechanics'),
       ('Scoia''tael Harmony', 'Scoia''tael', 'Precision Strike', 165, 'Harmony, Nature',
        'Nature-themed deck with harmony synergies'),
       ('Skellige Warriors', 'Skellige', 'Onslaught', 170, 'Warriors, Damage',
        'Aggressive warrior deck with direct damage'),
       ('Syndicate Coins', 'Syndicate', 'Congregate', 165, 'Coins, Crime', 'Coin-based strategy with crime synergies'),
       ('Neutral Mix', 'Neutral', 'None', 150, 'Mixed', 'Testing deck with neutral cards');
