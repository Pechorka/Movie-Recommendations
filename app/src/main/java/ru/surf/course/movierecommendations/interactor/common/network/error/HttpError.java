package ru.surf.course.movierecommendations.interactor.common.network.error;


import ru.surf.course.movierecommendations.interactor.common.network.response.BaseResponse;


public class HttpError extends NetworkException {

    private int code;
    private BaseResponse errorResponse;

    public HttpError(String message, Throwable cause, int code, BaseResponse errorResponse) {
        super(message, cause);
        this.code = code;
        this.errorResponse = errorResponse;
    }

    public int getCode() {
        return code;
    }

    public BaseResponse getErrorResponse() {
        return errorResponse;
    }
}
