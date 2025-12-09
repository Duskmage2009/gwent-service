package com.github.duskmage2009.repository;



import com.github.duskmage2009.entity.Deck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeckRepository extends JpaRepository<Deck, Long> {

    /**
     * Проверяет существование колоды по имени (игнорируя регистр)
     */
    boolean existsByNameIgnoreCase(String name);

    /**
     * Находит колоду по имени (игнорируя регистр)
     */
    Optional<Deck> findByNameIgnoreCase(String name);

    /**
     * Проверяет существование колоды с таким именем, исключая текущую колоду
     * (для проверки при UPDATE)
     */
    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
}