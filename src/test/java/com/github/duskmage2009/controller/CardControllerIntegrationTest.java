package com.github.duskmage2009.controller;



import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.duskmage2009.BaseIntegrationTest;
import com.github.duskmage2009.dto.request.CardListRequest;
import com.github.duskmage2009.dto.request.CreateCardRequest;
import com.github.duskmage2009.dto.request.UpdateCardRequest;
import com.github.duskmage2009.entity.CardType;
import com.github.duskmage2009.entity.Faction;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
class CardControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateCard() throws Exception {
        CreateCardRequest request = new CreateCardRequest();
        request.setName("Test Card");
        request.setDeckId(1L);
        request.setProvision(5);
        request.setPower(4);
        request.setType(CardType.UNIT);
        request.setFaction(Faction.MONSTERS);
        request.setDescription("Test card description");

        mockMvc.perform(post("/api/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Test Card"))
                .andExpect(jsonPath("$.deck.id").value(1))
                .andExpect(jsonPath("$.deck.name").exists())
                .andExpect(jsonPath("$.provision").value(5))
                .andExpect(jsonPath("$.power").value(4))
                .andExpect(jsonPath("$.type").value("Unit"))
                .andExpect(jsonPath("$.faction").value("Monsters"));
    }

    @Test
    void shouldReturnNotFoundForInvalidDeckId() throws Exception {
        CreateCardRequest request = new CreateCardRequest();
        request.setName("Test Card");
        request.setDeckId(999L); // Non-existent deck
        request.setProvision(5);
        request.setPower(4);
        request.setType(CardType.UNIT);
        request.setFaction(Faction.MONSTERS);

        mockMvc.perform(post("/api/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Deck not found with id: 999"));
    }

    @Test
    void shouldGetCardById() throws Exception {
        // Создаем карту
        CreateCardRequest createRequest = new CreateCardRequest();
        createRequest.setName("Card To Get");
        createRequest.setDeckId(1L);
        createRequest.setProvision(6);
        createRequest.setPower(5);
        createRequest.setType(CardType.UNIT);
        createRequest.setFaction(Faction.MONSTERS);

        String response = mockMvc.perform(post("/api/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long cardId = objectMapper.readTree(response).get("id").asLong();

        // Получаем карту
        mockMvc.perform(get("/api/cards/" + cardId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(cardId))
                .andExpect(jsonPath("$.name").value("Card To Get"))
                .andExpect(jsonPath("$.deck.id").value(1))
                .andExpect(jsonPath("$.deck.name").exists());
    }

    @Test
    void shouldUpdateCard() throws Exception {
        // Создаем карту
        CreateCardRequest createRequest = new CreateCardRequest();
        createRequest.setName("Card To Update");
        createRequest.setDeckId(1L);
        createRequest.setProvision(5);
        createRequest.setPower(4);
        createRequest.setType(CardType.UNIT);
        createRequest.setFaction(Faction.MONSTERS);

        String response = mockMvc.perform(post("/api/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long cardId = objectMapper.readTree(response).get("id").asLong();

        // Обновляем карту
        UpdateCardRequest updateRequest = new UpdateCardRequest();
        updateRequest.setName("Updated Card");
        updateRequest.setDeckId(2L); // Меняем колоду
        updateRequest.setProvision(7);
        updateRequest.setPower(6);
        updateRequest.setType(CardType.SPECIAL);
        updateRequest.setFaction(Faction.NORTHERN_REALMS);
        updateRequest.setDescription("Updated description");

        mockMvc.perform(put("/api/cards/" + cardId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(cardId))
                .andExpect(jsonPath("$.name").value("Updated Card"))
                .andExpect(jsonPath("$.deck.id").value(2))
                .andExpect(jsonPath("$.provision").value(7))
                .andExpect(jsonPath("$.type").value("Special"));
    }

    @Test
    void shouldDeleteCard() throws Exception {
        // Создаем карту
        CreateCardRequest createRequest = new CreateCardRequest();
        createRequest.setName("Card To Delete");
        createRequest.setDeckId(1L);
        createRequest.setProvision(5);
        createRequest.setPower(4);
        createRequest.setType(CardType.UNIT);
        createRequest.setFaction(Faction.MONSTERS);

        String response = mockMvc.perform(post("/api/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long cardId = objectMapper.readTree(response).get("id").asLong();

        // Удаляем
        mockMvc.perform(delete("/api/cards/" + cardId))
                .andExpect(status().isNoContent());

        // Проверяем что удалилась
        mockMvc.perform(get("/api/cards/" + cardId))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetCardListWithPagination() throws Exception {
        CardListRequest request = new CardListRequest();
        request.setPage(1);
        request.setSize(10);

        mockMvc.perform(post("/api/cards/_list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.list").isArray())
                .andExpect(jsonPath("$.totalPages").exists())
                .andExpect(jsonPath("$.totalElements").exists())
                .andExpect(jsonPath("$.currentPage").value(1))
                .andExpect(jsonPath("$.pageSize").value(10));
    }

    @Test
    void shouldGetCardListWithFilters() throws Exception {
        CardListRequest request = new CardListRequest();
        request.setDeckId(1L);
        request.setType(CardType.UNIT);
        request.setMinPower(3);
        request.setPage(1);
        request.setSize(20);

        mockMvc.perform(post("/api/cards/_list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.list").isArray());
    }

    @Test
    void shouldGenerateExcelReport() throws Exception {
        mockMvc.perform(post("/api/cards/_report")
                        .param("deckId", "1"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/octet-stream"))
                .andExpect(header().exists("Content-Disposition"))
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM));
    }

    @Test
    void shouldUploadCardsFromJson() throws Exception {
        String jsonContent = """
                [
                  {
                    "name": "Imported Card 1",
                    "deckName": "Monster Swarm",
                    "provision": 5,
                    "power": 4,
                    "type": "Unit",
                    "faction": "Monsters",
                    "description": "Test import"
                  },
                  {
                    "name": "Imported Card 2",
                    "deckName": "Northern Tactics",
                    "provision": 6,
                    "power": 5,
                    "type": "Special",
                    "faction": "Northern Realms",
                    "description": "Test import 2"
                  }
                ]
                """;

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "cards.json",
                "application/json",
                jsonContent.getBytes()
        );

        mockMvc.perform(multipart("/api/cards/upload").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.successCount").value(2))
                .andExpect(jsonPath("$.failureCount").value(0))
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    @Test
    void shouldHandleUploadErrorsForInvalidDeck() throws Exception {
        String jsonContent = """
                [
                  {
                    "name": "Valid Card",
                    "deckName": "Monster Swarm",
                    "provision": 5,
                    "power": 4,
                    "type": "Unit",
                    "faction": "Monsters"
                  },
                  {
                    "name": "Invalid Card",
                    "deckName": "Non Existent Deck",
                    "provision": 5,
                    "power": 4,
                    "type": "Unit",
                    "faction": "Monsters"
                  }
                ]
                """;

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "cards.json",
                "application/json",
                jsonContent.getBytes()
        );

        mockMvc.perform(multipart("/api/cards/upload").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.successCount").value(1))
                .andExpect(jsonPath("$.failureCount").value(1))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors", hasSize(1)));
    }
}