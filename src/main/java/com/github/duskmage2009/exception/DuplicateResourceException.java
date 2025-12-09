package com.github.duskmage2009.exception;


public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }

    public static DuplicateResourceException deckName(String name) {
        return new DuplicateResourceException("Deck with name '" + name + "' already exists");
    }
}