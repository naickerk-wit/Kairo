package model;

import java.time.Instant;
import java.util.UUID;

public class Note implements Identifiable {
	
	private final UUID id;
	private String content;
	private final Instant createdAt;
	
	public Note(String content) {
		
		this.id = UUID.randomUUID();
		this.content = content;
		this.createdAt = Instant.now();
		
	}
	
	@Override
	public UUID getId() {
		
		return id;
		
	}
	
	public String getContent() {
		
		return content;
		
	}
	
	public Instant getCreatedAt() {
		
		return createdAt;
		
	}
	
	public void setContent(String text) {
		
		this.content = text;
		
	}
	
	public String getPreview(int nChars) {
		
		if(content == null) return "";
		if(content.length() <= nChars) return content;
		
		return content.substring(0, nChars) + "...";
		
	}
	
	@Override
	public String toString() {
		
		return "Note{" + "id=" + id + ", createdAt=" + createdAt + ", content='" + getPreview(30) + '\'' + '}';
		
	}

}
