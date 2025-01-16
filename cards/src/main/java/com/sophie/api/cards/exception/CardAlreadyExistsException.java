package com.sophie.api.cards.exception;

public class CardAlreadyExistsException extends RuntimeException{

    public CardAlreadyExistsException(String message) {
        super(message);
    }
}
