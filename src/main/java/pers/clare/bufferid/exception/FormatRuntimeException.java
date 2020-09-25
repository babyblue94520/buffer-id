package pers.clare.bufferid.exception;

public class FormatRuntimeException extends RuntimeException {

    public FormatRuntimeException(String message, Object... args) {
        super(String.format(message, args));
    }
}
