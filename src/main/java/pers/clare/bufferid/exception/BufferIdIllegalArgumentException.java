package pers.clare.bufferid.exception;

public class BufferIdIllegalArgumentException extends IllegalArgumentException{
    public BufferIdIllegalArgumentException(String message, Object... args) {
        super(String.format(message, args));
    }
}
