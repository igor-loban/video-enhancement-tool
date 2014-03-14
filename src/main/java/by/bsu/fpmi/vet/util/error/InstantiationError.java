package by.bsu.fpmi.vet.util.error;

/**
 * This error means that object of pseudo abstract class tries to be created.
 */
class InstantiationError extends Error {
    public InstantiationError() {
    }

    public InstantiationError(String message) {
        super(message);
    }

    public InstantiationError(String message, Throwable cause) {
        super(message, cause);
    }

    public InstantiationError(Throwable cause) {
        super(cause);
    }

    public InstantiationError(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
