package com.example.mroojBE.exceptions;

/**
 * Thrown when a real call to the AI provider fails (network error, non-2xx
 * response, unparsable output) — as opposed to the "not configured yet"
 * mock state, which is not an error. Per NOTE-B1 on Booking.java, this is
 * NEVER caught-and-replaced with mock data inside the service layer; it
 * propagates to GlobalExceptionHandler like any other domain exception.
 */
public class AiDiagnosisException extends RuntimeException {
    public AiDiagnosisException(String message, Throwable cause) {
        super(message, cause);
    }
}