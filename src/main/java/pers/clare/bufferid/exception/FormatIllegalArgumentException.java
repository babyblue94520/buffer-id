package pers.clare.bufferid.exception;

public class FormatIllegalArgumentException extends IllegalArgumentException{
    public FormatIllegalArgumentException(String message, Object... args) {
        super(String.format(message, args));
    }
}
