package ru.surf.course.movierecommendations.interactor.common.network.parse;


import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTypeAdapter implements JsonDeserializer<Date> {

    private DateFormat df;

    public DateTypeAdapter() {
        df = new SimpleDateFormat("yyyy-MM-dd");
    }

    @Override
    public Date deserialize(final JsonElement json, final Type typeOfT,
                            final JsonDeserializationContext context)
            throws JsonParseException {
        try {
            return df.parse(json.getAsString());
        } catch (ParseException e) {
            return null;
        }
    }

}
