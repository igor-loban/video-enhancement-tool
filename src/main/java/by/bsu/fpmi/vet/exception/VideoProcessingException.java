package by.bsu.fpmi.vet.exception;

public class VideoProcessingException extends RuntimeException {
    public VideoProcessingException() {
    }

    public VideoProcessingException(String message) {
        super(message);
    }

    public VideoProcessingException(String message, Throwable cause) {
        super(message, cause);
    }

    public VideoProcessingException(Throwable cause) {
        super(cause);
    }

    protected VideoProcessingException(String message, Throwable cause, boolean enableSuppression,
                                       boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
