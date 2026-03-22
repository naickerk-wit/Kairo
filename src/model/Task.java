package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Task implements Identifiable {
	
	private final UUID id;
	private String title;
	private LocalDate dueDate;
	private Priority priority;
	private Status status;
	private List<Tag> tags;
	
	
	public Task(String title, LocalDate dueDate, Priority priority, Status status) {
		
		this.id = UUID.randomUUID();
		this.title = title;
		this.dueDate = dueDate;
		this.priority = priority != null ? priority : Priority.MEDIUM;
		this.status = status;
		this.tags = new ArrayList<>();
		
	}
	
	@Override
	public UUID getId() { 
		
		return id; 
		
	} 
	
	public String getTitle() { 
		
		return title; 
		
	} 
	
	public LocalDate getDueDate() { 
		
		return dueDate; 
		
	} 
	
	public Priority getPriority() { 
		
		return priority; 
		
	} 
	
	public Status getStatus() { 
		
		return status; 
		
	} 
	
	public void setTitle(String title) { 
		
		this.title = title; 
		
	} 
	
	public void setDueDate(LocalDate dueDate) { 
		
		this.dueDate = dueDate; 
		
	} 
	
	public void setPriority(Priority priority) { 
		
		this.priority = priority; 
		
	} 
	
public void setStatus(Status status) { 
		
		this.status = status; 
		
	}
	
	public List<Tag> getTags() {
		
		if (tags == null) {
			tags = new ArrayList<>();
		}
		return Collections.unmodifiableList(tags);
		
	}
	
	public void addTag(Tag tag) {
		
		if (tags == null) {
			tags = new ArrayList<>();
		}
		if (tag != null && !tags.contains(tag)) {
			tags.add(tag);
		}
		
	}
	
	public void removeTag(Tag tag) {
		
		if (tags != null) {
			tags.remove(tag);
		}
		
	}
	
	public void removeTagByName(String tagName) {
		
		if (tags != null) {
			tags.removeIf(t -> t.getName().equalsIgnoreCase(tagName));
		}
		
	}
	
	public boolean hasTag(String tagName) {
		
		if (tags == null) return false;
		return tags.stream().anyMatch(t -> t.getName().equalsIgnoreCase(tagName));
		
	}
	
	public void clearTags() {
		
		if (tags != null) {
			tags.clear();
		}
		
	}
	
	public void markComplete() {
		
		this.status = Status.COMPLETED;
		
	}
	
	public boolean isCompleted() {
		
		return status.isCompleted();
		
	}
	
	public boolean isOverdue(LocalDate today) {
		
		return status.isActive() && dueDate != null && dueDate.isBefore(today);
		
	}
	
	@Override
    public String toString() {
    	
		return "Task{" + "id='" + id + '\'' + ", title='" + title + '\'' + ", dueDate=" + dueDate + ", priority=" + priority + ", status=" + status + '}';
		
    }

}
