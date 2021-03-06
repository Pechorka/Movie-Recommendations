package ru.surf.course.movierecommendations.interactor.common.network.error;


public class HttpCodes {
    public static final int CODE_200 = 200; //успех
    public static final int CODE_304 = 304; //нет обновленных данных
    public static final int CODE_401 = 401; //недоступное действие для пользователя
    public static final int CODE_400 = 400; //Bad request, возможно передан невалидный токен
    public static final int CODE_500 = 500; //ошибка сервера
}
