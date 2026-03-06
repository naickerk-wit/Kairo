package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Page implements Identifiable {
	
	private final UUID id;
	private String name;
	private final List<Task> tasks;
	private final List<Note> notes;
	
	public Page(String name) {
		
		this.id = UUID.randomUUID();
		this.name = name;
		this.tasks = new ArrayList<>();
		this.notes = new ArrayList<>();
		
	}
	
	@Override
	public UUID getId() {
		
		return id;
		
	}
	
	public String getName() {
		
		return name;
		
	}
	
	public List<Task> getTasks() {
		
		return Collections.unmodifiableList(tasks);
		
	}
	
	public List<Note> getNotes() {
		
		return Collections.unmodifiableList(notes);
		
	}
	
	public void rename(String newName) {
		
		this.name = newName;
		
	}
	
	public void addTask(Task t) {
		
		if(t != null) {
			
			tasks.add(t);
			
		}
		
	}
	
	public void removeTask(UUID taskId) {
		
		tasks.removeIf(t -> t.getId().equals(taskId));
		
	}
	
	public void addNote(Note n) {
		
		if(n != null) {
			
			notes.add(n);
			
		}
		
	}
	
	public void removeNote(UUID noteId) {
		
		tasks.removeIf(n -> n.getId().equals(noteId));
		
	}
	
	@Override
    public String toString() {
		
        return "Page{" + "id=" + id + ", name='" + name + '\'' + ", tasks=" + tasks.size() + ", notes=" + notes.size() + '}';
        
    }

}
