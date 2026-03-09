package Storage;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonSerializationContext;
import java.lang.reflect.Type;
import java.time.Instant;


public class InstantAdapter implements JsonSerializer<Instant>, JsonDeserializer<Instant> {
	
	@Override
	public JsonElement serialize(Instant instant, Type type, JsonSerializationContext ctx) {
		
		return new JsonPrimitive(instant.toString());
		
	}
	
	@Override
	public Instant deserialize(JsonElement json, Type type, JsonDeserializationContext ctx) throws JsonParseException {
		
		return Instant.parse(json.getAsString());
		
	}

}
