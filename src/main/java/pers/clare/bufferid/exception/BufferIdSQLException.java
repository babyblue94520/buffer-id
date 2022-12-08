package pers.clare.bufferid.exception;

import java.sql.SQLException;

public class BufferIdSQLException extends RuntimeException {
    private SQLException sqlException;

    public BufferIdSQLException(String message, Object... args) {
        super(String.format(message, args));
    }

    public BufferIdSQLException(SQLException sqlException, String message, Object... args) {
        super(String.format(message, args));
        this.sqlException = sqlException;
    }

    public SQLException getSqlException() {
        return sqlException;
    }
}
