package by.bsu.fpmi.vet.exception;

public class ReportGenerationException extends RuntimeException {
    public ReportGenerationException() {
    }

    public ReportGenerationException(String message) {
        super(message);
    }

    public ReportGenerationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReportGenerationException(Throwable cause) {
        super(cause);
    }

    protected ReportGenerationException(String message, Throwable cause, boolean enableSuppression,
                                        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
