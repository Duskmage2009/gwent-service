package com.github.duskmage2009.controller;



import com.github.duskmage2009.dto.request.CardListRequest;
import com.github.duskmage2009.dto.request.CreateCardRequest;
import com.github.duskmage2009.dto.request.UpdateCardRequest;

import com.github.duskmage2009.dto.response.CardListResponse;
import com.github.duskmage2009.dto.response.CardResponse;
import com.github.duskmage2009.dto.response.UploadResponse;
import com.github.duskmage2009.entity.Card;

import com.github.duskmage2009.service.CardService;
import com.github.duskmage2009.service.ReportService;
import com.github.duskmage2009.service.UploadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
@Slf4j
public class CardController {

    private final CardService cardService;
    private final ReportService reportService;
    private final UploadService uploadService;

    @GetMapping("/{id}")
    public ResponseEntity<CardResponse> getCardById(@PathVariable Long id) {
        log.debug("GET /api/cards/{} - Get card by id", id);
        CardResponse card = cardService.getCardById(id);
        return ResponseEntity.ok(card);
    }


    @PostMapping
    public ResponseEntity<CardResponse> createCard(@Valid @RequestBody CreateCardRequest request) {
        log.debug("POST /api/cards - Create new card: {}", request.getName());
        CardResponse card = cardService.createCard(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(card);
    }


    @PutMapping("/{id}")
    public ResponseEntity<CardResponse> updateCard(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCardRequest request) {
        log.debug("PUT /api/cards/{} - Update card", id);
        CardResponse card = cardService.updateCard(id, request);
        return ResponseEntity.ok(card);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCard(@PathVariable Long id) {
        log.debug("DELETE /api/cards/{} - Delete card", id);
        cardService.deleteCard(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/_list")
    public ResponseEntity<CardListResponse> getCardList(@Valid @RequestBody CardListRequest request) {
        log.debug("POST /api/cards/_list - Get card list with filters");
        CardListResponse response = cardService.getCardList(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/_report")
    public ResponseEntity<byte[]> generateReport(
            @RequestParam(required = false) Long deckId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer minPower) throws IOException {

        log.debug("POST /api/cards/_report - Generate Excel report");

        List<Card> cards = cardService.getCardsForReport(deckId, type, minPower);

        byte[] excelBytes = reportService.generateCardsExcelReport(cards);

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String filename = "cards_report_" + timestamp + ".xlsx";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", filename);
        headers.setContentLength(excelBytes.length);

        log.info("Generated Excel report with {} cards", cards.size());

        return ResponseEntity.ok()
                .headers(headers)
                .body(excelBytes);
    }

    @PostMapping("/upload")
    public ResponseEntity<UploadResponse> uploadCards(@RequestParam("file") MultipartFile file) throws IOException {
        log.debug("POST /api/cards/upload - Upload JSON file: {}", file.getOriginalFilename());

        if (!file.getContentType().equals("application/json")) {
            throw new IllegalArgumentException("File must be JSON format");
        }

        UploadResponse response = uploadService.uploadCards(file);

        log.info("Upload completed - Success: {}, Failure: {}",
                response.getSuccessCount(),
                response.getFailureCount());

        return ResponseEntity.ok(response);
    }
}