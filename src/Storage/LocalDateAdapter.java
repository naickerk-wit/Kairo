package Storage;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonSerializationContext;
import java.lang.reflect.Type;
import java.time.LocalDate;

public class LocalDateAdapter implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {
	
	@Override
	public JsonElement serialize(LocalDate date, Type type, JsonSerializationContext ctx) {
		
		return new JsonPrimitive(date.toString());
		
	}
	
	@Override
	public LocalDate deserialize(JsonElement json, Type type, JsonDeserializationContext ctx) throws JsonParseException {
		
		return LocalDate.parse(json.getAsString());
		
	}

}
