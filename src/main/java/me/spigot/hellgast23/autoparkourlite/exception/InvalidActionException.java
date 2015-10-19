package me.spigot.hellgast23.autoparkourlite.exception;

/**
 * Created on: 19/10/2015 at 14:58
 */
public class InvalidActionException extends Exception {

    public InvalidActionException() {
        this("You did something that you may not do.");
    }

    public InvalidActionException(String message) {
        super(message);
    }

    public InvalidActionException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidActionException(Throwable cause) {
        super(cause);
    }

    public InvalidActionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
