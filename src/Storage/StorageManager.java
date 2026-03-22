package Storage;

import model.Workspace;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class StorageManager {
	
	private final Path filePath;
	private final WorkspaceStorage workspaceStorage;
	
	public StorageManager(Path filePath) {
		
		this.filePath = filePath;
		this.workspaceStorage = new WorkspaceStorage();
		
	}
	
public void save(Workspace workspace) {
		
		try {
			
			// Create parent directories if they don't exist
			if (filePath.getParent() != null && !Files.exists(filePath.getParent())) {
				Files.createDirectories(filePath.getParent());
			}
			
			String json = workspaceStorage.toJson(workspace);
			
			Files.writeString(filePath, json);
			
		} catch(IOException e) {
			
			throw new RuntimeException("Workspace save failed.", e);
			
		}
		
	}
	
	public Workspace load() {
		
		try {
			
			if(!Files.exists(filePath)) {
				
				return new Workspace();
				
			}
			
			String json = Files.readString(filePath);
			
			return workspaceStorage.fromJson(json);
			
		} catch(IOException e) {
			
			throw new RuntimeException("Workspace load failed.", e);
			
		}
		
	}

}
