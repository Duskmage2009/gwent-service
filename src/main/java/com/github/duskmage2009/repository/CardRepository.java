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

    List<Card> findByDeckId(Long deckId);

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

    @Query("SELECT c FROM Card c WHERE " +
            "(:deckId IS NULL OR c.deck.id = :deckId) AND " +
            "(:type IS NULL OR c.type = :type) AND " +
            "(:minPower IS NULL OR c.power >= :minPower)")
    List<Card> findAllByFilters(
            @Param("deckId") Long deckId,
            @Param("type") CardType type,
            @Param("minPower") Integer minPower
    );


    void deleteByDeckId(Long deckId);

    long countByDeckId(Long deckId);
}