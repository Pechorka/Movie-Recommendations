package ru.surf.course.movierecommendations.interactor.common.network.error;


public abstract class NetworkException extends RuntimeException {

    NetworkException() {
    }

    NetworkException(String message) {
        super(message);
    }

    NetworkException(Throwable cause) {
        super(cause);
    }

    NetworkException(String message, Throwable cause) {
        super(message, cause);
    }
}
