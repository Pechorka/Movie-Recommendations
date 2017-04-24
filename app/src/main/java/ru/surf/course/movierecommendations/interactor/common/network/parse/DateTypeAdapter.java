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
import java.util.Locale;
import ru.surf.course.movierecommendations.util.Utilities;

public class DateTypeAdapter implements JsonDeserializer<Date> {

    private DateFormat df;

    public DateTypeAdapter() {
      df = new SimpleDateFormat("yyyy-MM-dd", new Locale(Utilities.getSystemLanguage()));
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
