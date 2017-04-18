package ru.surf.course.movierecommendations.ui.common.error;

import com.agna.ferro.mvp.component.scope.PerScreen;

import java.util.Locale;

import javax.inject.Inject;

import ru.surf.course.movierecommendations.R;
import ru.surf.course.movierecommendations.app.log.Logger;
import ru.surf.course.movierecommendations.interactor.common.network.error.ConversionException;
import ru.surf.course.movierecommendations.interactor.common.network.error.HttpCodes;
import ru.surf.course.movierecommendations.interactor.common.network.error.HttpError;
import ru.surf.course.movierecommendations.interactor.common.network.error.NoInternetException;
import ru.surf.course.movierecommendations.interactor.common.network.response.BaseResponse;
import ru.surf.course.movierecommendations.ui.common.message.MessagePresenter;


@PerScreen
public class StandardErrorHandler extends ErrorHandler {

    private final MessagePresenter messagePresenter;

    @Inject
    public StandardErrorHandler(MessagePresenter messagePresenter) {
        this.messagePresenter = messagePresenter;
    }


    @Override
    protected void handleHttpError(HttpError e) {
        if (e.getCode() >= HttpCodes.CODE_500) {
            messagePresenter.show(R.string.server_error_message);
        } else {
            BaseResponse errorResponse = e.getErrorResponse();
            messagePresenter.show(String.format(Locale.getDefault(), "%d %s", e.getCode(), e.getMessage()));
        }
    }

    @Override
    protected void handleNoInternetError(NoInternetException e) {
        messagePresenter.show(R.string.no_internet_connection_error_message);
    }

    @Override
    protected void handleConversionError(ConversionException e) {
        messagePresenter.show(R.string.bad_response_error_message);
    }

    @Override
    protected void handleOtherError(Throwable e) {
        messagePresenter.show(R.string.unexpected_error_error_message);
        Logger.e(e, "Unexpected error");
    }
}
