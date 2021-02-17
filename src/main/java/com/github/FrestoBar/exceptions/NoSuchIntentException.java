package com.github.FrestoBar.exceptions;

public class NoSuchIntentException extends RuntimeException{

    private static final String COULD_NOT_FIND_INTENT_SUCH_INTENT = "Could not find Intent such Intent %s";

    public NoSuchIntentException(String intent){
        super(String.format(COULD_NOT_FIND_INTENT_SUCH_INTENT, intent));
    }
}
