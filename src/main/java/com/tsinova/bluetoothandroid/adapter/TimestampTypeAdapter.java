package com.tsinova.bluetoothandroid.adapter;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class TimestampTypeAdapter implements JsonSerializer<Timestamp>, JsonDeserializer<Timestamp> {
    private final DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    @Override
    public Timestamp deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        if(!(jsonElement instanceof JsonPrimitive)) {
            throw new JsonParseException("The date should be a string value");
        }
        try {
            String jsonDateString = jsonElement.getAsString();
            Date date = format.parse(jsonDateString);
            Timestamp timestamp = new Timestamp(date.getTime());
            return timestamp;
        }catch(ParseException e) {
            throw new JsonParseException(e);
        }
    }

    @Override
    public JsonElement serialize(Timestamp src, Type type, JsonSerializationContext context) {
        Date date = new Date(src.getTime());
        String dateFormatAsString = format.format(date);
        return new JsonPrimitive(dateFormatAsString);
    }

}
