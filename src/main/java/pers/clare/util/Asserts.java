package pers.clare.util;

import java.math.BigDecimal;

public interface Asserts {
    public final static String InvalidRequest = "Invalid request";
    /**
     * " existed"
     */
    public static final String Existed = " existed";
    /**
     * " does not exist"
     */
    public static final String DoesNotExist = " does not exist";
    /**
     * " can't be empty"
     */
    public static final String NotEmptyMessage = " can't be empty";
    /**
     * " can't be null"
     */
    public static final String NotNullMessage = " can't be null";
    /**
     * " can't be greater than "
     */
    public static final String NotGreaterMessage = " can't be greater than ";
    /**
     * " can't be less than "
     */
    public static final String NotLessMessage = " can't be less than ";
    /**
     * " can't be same"
     */
    public static final String NotSame = " can't be same";


    /**
     * notEmpty
     * 不能為Empty
     *
     * @param str
     * @param message
     * @throws
     */
    public static void notEmpty(String str, String message) {
        if (str == null || "".equals(str)) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * notNull
     * 不能為Null
     *
     * @param object
     * @param message
     * @throws
     */
    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * notEquals
     * 不能等於
     *
     * @param value
     * @param target
     * @param message
     * @throws
     */
    public static void notEquals(String value, String target, String message) {
        if (value == null) {
            if (value == target) {
                throw new IllegalArgumentException(message);
            }
        } else {
            if (value.equals(target)) {
                throw new IllegalArgumentException(message);
            }
        }
    }

    /**
     * notEquals
     * 不能等於
     *
     * @param value
     * @param target
     * @param message
     * @throws
     */
    public static void notEquals(int value, int target, String message) {
        if (value == target) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * notEquals
     * 不能等於
     *
     * @param value
     * @param target
     * @param message
     * @throws
     */
    public static void notEquals(BigDecimal value, BigDecimal target, String message) {
        if (value.compareTo(target) == 0) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * isEquals
     * 等於
     *
     * @param value
     * @param target
     * @param message
     * @throws
     */
    public static void isEquals(String value, String target, String message) {
        if (value == null) {
            if (value != target) {
                throw new IllegalArgumentException(message);
            }
        } else {
            if (!value.equals(target)) {
                throw new IllegalArgumentException(message);
            }
        }
    }

    /**
     * isTrue
     * 為真
     *
     * @param expression
     * @param message
     * @throws
     */
    public static void isTrue(boolean expression, String message) {
        if (!expression) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * isFalse
     * 為假
     *
     * @param expression
     * @param message
     * @throws
     */
    public static void isFalse(boolean expression, String message) {
        if (expression) {
            throw new IllegalArgumentException(message);
        }
    }
}
