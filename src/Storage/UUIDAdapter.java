package Storage;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonSerializationContext;
import java.lang.reflect.Type;
import java.util.UUID;

public class UUIDAdapter implements JsonSerializer<UUID>, JsonDeserializer<UUID> {
	
	@Override
	public JsonElement serialize(UUID uuid, Type type, JsonSerializationContext ctx) {
		
		return new JsonPrimitive(uuid.toString());
		
	}
	
	@Override
	public UUID deserialize(JsonElement json, Type type, JsonDeserializationContext ctx) throws JsonParseException {
		
		return UUID.fromString(json.getAsString());
		
	}

}
