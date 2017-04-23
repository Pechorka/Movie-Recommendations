package ru.surf.course.movierecommendations.interactor.common.network.error;


public class NoInternetException extends NetworkException {

    public NoInternetException(Throwable cause) {
        super(cause);
    }
}
