package com.github.duskmage2009.service;

import com.github.duskmage2009.dto.request.CardListRequest;
import com.github.duskmage2009.dto.request.CreateCardRequest;
import com.github.duskmage2009.dto.request.UpdateCardRequest;
import com.github.duskmage2009.dto.response.CardListItemResponse;
import com.github.duskmage2009.dto.response.CardListResponse;
import com.github.duskmage2009.dto.response.CardResponse;
import com.github.duskmage2009.entity.Card;
import com.github.duskmage2009.entity.CardType;
import com.github.duskmage2009.entity.Deck;
import com.github.duskmage2009.exception.ResourceNotFoundException;
import com.github.duskmage2009.dto.messaging.EmailNotificationProducer;
import com.github.duskmage2009.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CardService {

    private final CardRepository cardRepository;
    private final DeckService deckService;
    private final EmailNotificationProducer emailNotificationProducer;

    public CardResponse getCardById(Long id) {
        log.debug("Getting card by id: {}", id);
        Card card = findCardByIdOrThrow(id);
        return CardResponse.from(card);
    }

    @Transactional
    public CardResponse createCard(CreateCardRequest request) {
        log.debug("Creating card with name: {}", request.getName());

        Deck deck = deckService.findDeckByIdOrThrow(request.getDeckId());

        Card card = Card.builder()
                .name(request.getName())
                .deck(deck)
                .provision(request.getProvision())
                .power(request.getPower())
                .type(request.getType())
                .faction(request.getFaction())
                .description(request.getDescription())
                .build();

        Card savedCard = cardRepository.save(card);
        log.info("Created card with id: {}", savedCard.getId());

        try {
            emailNotificationProducer.sendCardCreatedNotification(
                    savedCard.getName(),
                    deck.getName(),
                    savedCard.getPower(),
                    savedCard.getFaction().getDisplayName(),
                    savedCard.getType().getDisplayName()
            );
            log.info("Email notification queued for card: {}", savedCard.getName());
        } catch (Exception e) {
            log.error("Failed to send email notification for card: {}", savedCard.getName(), e);
        }

        return CardResponse.from(savedCard);
    }

    @Transactional
    public CardResponse updateCard(Long id, UpdateCardRequest request) {
        log.debug("Updating card with id: {}", id);

        Card card = findCardByIdOrThrow(id);

        if (!card.getDeck().getId().equals(request.getDeckId())) {
            Deck newDeck = deckService.findDeckByIdOrThrow(request.getDeckId());
            card.setDeck(newDeck);
        }

        card.setName(request.getName());
        card.setProvision(request.getProvision());
        card.setPower(request.getPower());
        card.setType(request.getType());
        card.setFaction(request.getFaction());
        card.setDescription(request.getDescription());

        Card updatedCard = cardRepository.save(card);
        log.info("Updated card with id: {}", updatedCard.getId());

        return CardResponse.from(updatedCard);
    }

    @Transactional
    public void deleteCard(Long id) {
        log.debug("Deleting card with id: {}", id);

        Card card = findCardByIdOrThrow(id);
        cardRepository.delete(card);

        log.info("Deleted card with id: {}", id);
    }

    public CardListResponse getCardList(CardListRequest request) {
        log.debug("Getting card list with filters - deckId: {}, type: {}, minPower: {}",
                request.getDeckId(), request.getType(), request.getMinPower());

        Pageable pageable = PageRequest.of(
                request.getPage() - 1,
                request.getSize(),
                Sort.by("id").ascending()
        );

        Page<Card> page = cardRepository.findByFilters(
                request.getDeckId(),
                request.getType(),
                request.getMinPower(),
                pageable
        );

        List<CardListItemResponse> items = page.getContent().stream()
                .map(CardListItemResponse::from)
                .collect(Collectors.toList());

        return CardListResponse.builder()
                .list(items)
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .currentPage(request.getPage())
                .pageSize(request.getSize())
                .build();
    }

    public List<Card> getCardsForReport(Long deckId, String type, Integer minPower) {
        log.debug("Getting cards for report - deckId: {}, type: {}, minPower: {}",
                deckId, type, minPower);

        var cardType = (type != null && !type.isBlank())
                ? CardType.fromString(type)
                : null;

        return cardRepository.findAllByFilters(deckId, cardType, minPower);
    }

    private Card findCardByIdOrThrow(Long id) {
        return cardRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.card(id));
    }
}