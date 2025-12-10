package com.github.duskmage2009.service;


import com.github.duskmage2009.dto.request.CreateDeckRequest;
import com.github.duskmage2009.dto.request.UpdateDeckRequest;

import com.github.duskmage2009.dto.response.DeckResponse;
import com.github.duskmage2009.entity.Deck;
import com.github.duskmage2009.exception.DuplicateResourceException;
import com.github.duskmage2009.exception.ResourceNotFoundException;
import com.github.duskmage2009.repository.DeckRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DeckService {

    private final DeckRepository deckRepository;

    public List<DeckResponse> getAllDecks() {
        log.debug("Getting all decks");
        return deckRepository.findAll().stream()
                .map(DeckResponse::from)
                .collect(Collectors.toList());
    }


    public DeckResponse getDeckById(Long id) {
        log.debug("Getting deck by id: {}", id);
        Deck deck = findDeckByIdOrThrow(id);
        return DeckResponse.from(deck);
    }

    @Transactional
    public DeckResponse createDeck(CreateDeckRequest request) {
        log.debug("Creating deck with name: {}", request.getName());

        if (deckRepository.existsByNameIgnoreCase(request.getName())) {
            throw DuplicateResourceException.deckName(request.getName());
        }

        Deck deck = Deck.builder()
                .name(request.getName())
                .faction(request.getFaction())
                .leaderAbility(request.getLeaderAbility())
                .provisionLimit(request.getProvisionLimit())
                .categories(request.getCategories())
                .description(request.getDescription())
                .build();

        Deck savedDeck = deckRepository.save(deck);
        log.info("Created deck with id: {}", savedDeck.getId());

        return DeckResponse.from(savedDeck);
    }

    @Transactional
    public DeckResponse updateDeck(Long id, UpdateDeckRequest request) {
        log.debug("Updating deck with id: {}", id);

        Deck deck = findDeckByIdOrThrow(id);

        if (deckRepository.existsByNameIgnoreCaseAndIdNot(request.getName(), id)) {
            throw DuplicateResourceException.deckName(request.getName());
        }

        deck.setName(request.getName());
        deck.setFaction(request.getFaction());
        deck.setLeaderAbility(request.getLeaderAbility());
        deck.setProvisionLimit(request.getProvisionLimit());
        deck.setCategories(request.getCategories());
        deck.setDescription(request.getDescription());

        Deck updatedDeck = deckRepository.save(deck);
        log.info("Updated deck with id: {}", updatedDeck.getId());

        return DeckResponse.from(updatedDeck);
    }

    @Transactional
    public void deleteDeck(Long id) {
        log.debug("Deleting deck with id: {}", id);

        Deck deck = findDeckByIdOrThrow(id);
        deckRepository.delete(deck);

        log.info("Deleted deck with id: {}", id);
    }

    public Deck findDeckByIdOrThrow(Long id) {
        return deckRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.deck(id));
    }
}