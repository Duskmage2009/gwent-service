package com.github.duskmage2009.controller;

import com.github.duskmage2009.dto.request.CreateDeckRequest;
import com.github.duskmage2009.dto.request.UpdateDeckRequest;

import com.github.duskmage2009.dto.request.response.DeckResponse;
import com.github.duskmage2009.service.DeckService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/decks")
@RequiredArgsConstructor
@Slf4j
public class DeckController {

    private final DeckService deckService;


    @GetMapping
    public ResponseEntity<List<DeckResponse>> getAllDecks() {
        log.debug("GET /api/decks - Get all decks");
        List<DeckResponse> decks = deckService.getAllDecks();
        return ResponseEntity.ok(decks);
    }


    @GetMapping("/{id}")
    public ResponseEntity<DeckResponse> getDeckById(@PathVariable Long id) {
        log.debug("GET /api/decks/{} - Get deck by id", id);
        DeckResponse deck = deckService.getDeckById(id);
        return ResponseEntity.ok(deck);
    }


    @PostMapping
    public ResponseEntity<DeckResponse> createDeck(@Valid @RequestBody CreateDeckRequest request) {
        log.debug("POST /api/decks - Create new deck: {}", request.getName());
        DeckResponse deck = deckService.createDeck(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(deck);
    }


    @PutMapping("/{id}")
    public ResponseEntity<DeckResponse> updateDeck(
            @PathVariable Long id,
            @Valid @RequestBody UpdateDeckRequest request) {
        log.debug("PUT /api/decks/{} - Update deck", id);
        DeckResponse deck = deckService.updateDeck(id, request);
        return ResponseEntity.ok(deck);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDeck(@PathVariable Long id) {
        log.debug("DELETE /api/decks/{} - Delete deck", id);
        deckService.deleteDeck(id);
        return ResponseEntity.noContent().build();
    }
}