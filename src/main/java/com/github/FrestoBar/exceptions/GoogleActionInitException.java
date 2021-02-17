package com.github.FrestoBar.exceptions;

/**
 * This exception is thrown when initialization of google intent failed.
 * It could be caused from different cases such Intent Class not found.
 */
public class GoogleActionInitException extends RuntimeException {

    public GoogleActionInitException(Exception exception){
        super(exception);
    }
    public GoogleActionInitException(String message){
        super(message);
    }
}
