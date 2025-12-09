package com.github.duskmage2009.repository;


import com.github.duskmage2009.entity.Card;
import com.github.duskmage2009.entity.CardType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    /**
     * Находит все карты для конкретной колоды
     */
    List<Card> findByDeckId(Long deckId);

    /**
     * Пагинация с фильтрацией по deck, type, minPower
     * ✅ Фильтрация на уровне БД для производительности
     */
    @Query("SELECT c FROM Card c WHERE " +
            "(:deckId IS NULL OR c.deck.id = :deckId) AND " +
            "(:type IS NULL OR c.type = :type) AND " +
            "(:minPower IS NULL OR c.power >= :minPower)")
    Page<Card> findByFilters(
            @Param("deckId") Long deckId,
            @Param("type") CardType type,
            @Param("minPower") Integer minPower,
            Pageable pageable
    );

    /**
     * Для генерации отчета - возвращает все карты без пагинации
     */
    @Query("SELECT c FROM Card c WHERE " +
            "(:deckId IS NULL OR c.deck.id = :deckId) AND " +
            "(:type IS NULL OR c.type = :type) AND " +
            "(:minPower IS NULL OR c.power >= :minPower)")
    List<Card> findAllByFilters(
            @Param("deckId") Long deckId,
            @Param("type") CardType type,
            @Param("minPower") Integer minPower
    );

    /**
     * Удаляет все карты для конкретной колоды
     * (полезно если нужно каскадное удаление)
     */
    void deleteByDeckId(Long deckId);

    /**
     * Подсчитывает количество карт в колоде
     */
    long countByDeckId(Long deckId);
}