package lms.exceptions;

public class BedRequestException extends RuntimeException {
    public BedRequestException() {
    }

    public BedRequestException(String message) {
        super(message);
    }
}
