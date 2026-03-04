package model;

import java.time.LocalDate;
import java.util.UUID;

public class Task implements Identifiable {
	
	private final UUID id;
	private String title;
	private LocalDate dueDate;
	private Priority priority;
	private Status status;
	
	
	public Task(String title, LocalDate dueDate, Priority priority, Status status) {
		
		this.id = UUID.randomUUID();
		this.title = title;
		this.dueDate = dueDate;
		this.priority = priority != null ? priority : Priority.MEDIUM;
		this.status = status;
		
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
