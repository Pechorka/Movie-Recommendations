package ru.surf.course.movierecommendations.ui.common.error;


import java.util.List;

import ru.surf.course.movierecommendations.app.log.Logger;
import ru.surf.course.movierecommendations.interactor.common.network.error.ConversionException;
import ru.surf.course.movierecommendations.interactor.common.network.error.HttpError;
import ru.surf.course.movierecommendations.interactor.common.network.error.NetworkException;
import ru.surf.course.movierecommendations.interactor.common.network.error.NoInternetException;
import rx.exceptions.CompositeException;


public abstract class ErrorHandler {

    public void handleError(Throwable t) {
        Logger.i(t, "ErrorHandler handle error");
        if (t instanceof CompositeException) {
            handleCompositeException((CompositeException) t);
        } else if (t instanceof HttpError) {
            handleHttpError((HttpError) t);
        } else if (t instanceof NoInternetException) {
            handleNoInternetError((NoInternetException) t);
        } else if (t instanceof ConversionException) {
            handleConversionError((ConversionException) t);
        } else {
            handleOtherError(t);
        }
    }

    /**
     * @param err - CompositeException может возникать при комбинировании Observable
     */
    private void handleCompositeException(CompositeException err) {
        List<Throwable> exceptions = err.getExceptions();
        NetworkException networkException = null;
        Throwable otherException = null;
        for (Throwable e : exceptions) {
            if (e instanceof NetworkException) {
                if (networkException == null) {
                    networkException = (NetworkException) e;
                }
            } else if (otherException == null) {
                otherException = e;
            }
        }
        if (networkException != null) {
            handleError(networkException);
        }
        if (otherException != null) {
            handleOtherError(otherException);
        }
    }


    protected abstract void handleHttpError(HttpError e);

    protected abstract void handleNoInternetError(NoInternetException e);

    protected abstract void handleConversionError(ConversionException e);

    protected abstract void handleOtherError(Throwable e);
}
