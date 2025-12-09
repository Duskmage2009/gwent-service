package com.github.duskmage2009.service;



import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.duskmage2009.dto.request.response.UploadResponse;
import com.github.duskmage2009.entity.Card;
import com.github.duskmage2009.entity.CardType;
import com.github.duskmage2009.entity.Deck;
import com.github.duskmage2009.entity.Faction;
import com.github.duskmage2009.exception.ResourceNotFoundException;
import com.github.duskmage2009.repository.CardRepository;
import com.github.duskmage2009.repository.DeckRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UploadService {

    private final CardRepository cardRepository;
    private final DeckRepository deckRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public UploadResponse uploadCards(MultipartFile file) throws IOException {
        log.debug("Starting upload of file: {}", file.getOriginalFilename());

        CardUploadDto[] cardsData = objectMapper.readValue(
                file.getInputStream(),
                CardUploadDto[].class
        );

        int successCount = 0;
        int failureCount = 0;
        List<String> errors = new ArrayList<>();

        for (int i = 0; i < cardsData.length; i++) {
            CardUploadDto cardData = cardsData[i];
            try {
                importCard(cardData);
                successCount++;
            } catch (Exception e) {
                failureCount++;
                String errorMsg = String.format("Row %d (%s): %s",
                        i + 1,
                        cardData.getName(),
                        e.getMessage()
                );
                errors.add(errorMsg);
                log.warn("Failed to import card: {}", errorMsg);
            }
        }

        log.info("Upload completed - Success: {}, Failure: {}", successCount, failureCount);

        return UploadResponse.builder()
                .successCount(successCount)
                .failureCount(failureCount)
                .errors(errors)
                .build();
    }

    private void importCard(CardUploadDto cardData) {
        Deck deck = deckRepository.findByNameIgnoreCase(cardData.getDeckName())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Deck not found: " + cardData.getDeckName()
                ));

        Card card = Card.builder()
                .name(cardData.getName())
                .deck(deck)
                .provision(cardData.getProvision())
                .power(cardData.getPower())
                .type(cardData.getType())
                .faction(cardData.getFaction())
                .description(cardData.getDescription())
                .build();

        cardRepository.save(card);
    }

    @lombok.Data
    public static class CardUploadDto {
        private String name;
        private String deckName;  // ✅ Ищем колоду по имени
        private Integer provision;
        private Integer power;
        private CardType type;
        private Faction faction;
        private String description;
    }
}