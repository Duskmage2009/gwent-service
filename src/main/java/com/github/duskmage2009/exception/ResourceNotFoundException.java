package com.github.duskmage2009.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public static ResourceNotFoundException deck(Long id) {
        return new ResourceNotFoundException("Deck not found with id: " + id);
    }

    public static ResourceNotFoundException card(Long id) {
        return new ResourceNotFoundException("Card not found with id: " + id);
    }
}