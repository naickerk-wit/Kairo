package Storage;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.Workspace;

public class WorkspaceStorage {
	
	private final Gson gson;
	
	public WorkspaceStorage() {
		
		this.gson = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).registerTypeAdapter(Instant.class, new InstantAdapter()).registerTypeAdapter(UUID.class, new UUIDAdapter()).create();			
		
	}
	
	public String toJson(Workspace workspace) {
		
		return gson.toJson(workspace);
		
	}
	
	public Workspace fromJson(String json) {
		
		return gson.fromJson(json, Workspace.class);
		
	}

}
