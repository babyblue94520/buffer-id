package pers.clare.bufferid.exception;

public class BufferIdNumberTooLargeException extends IllegalArgumentException{
    public BufferIdNumberTooLargeException(String message, Object... args) {
        super(String.format(message, args));
    }
}
