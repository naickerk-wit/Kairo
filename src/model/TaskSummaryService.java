package model;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class TaskSummaryService {
	
	private final Workspace workspace;
	
	public TaskSummaryService(Workspace workspace) {
		
		this.workspace = workspace;
		
	}
	
	private List<Task> allTasks() {
		
		return workspace.getPages().stream().flatMap(p -> p.getTasks().stream()).collect(Collectors.toList());
		
	}
	
	public List<Task> getUpcoming(LocalDate today) {
		
		return allTasks().stream().filter(t -> t.getStatus().isActive()).filter(t -> t.getDueDate() != null && t.getDueDate().isAfter(today)).sorted(Comparator.comparing(Task::getDueDate)).collect(Collectors.toList());
		
	}
	
	public List<Task> getOverdue(LocalDate today) {
		
		return allTasks().stream().filter(t -> t.isOverdue(today)).sorted(Comparator.comparing(Task::getDueDate)).collect(Collectors.toList());

	}
	
	public List<Task> getPriority() {
		
		return allTasks().stream().filter(t -> t.getStatus().isActive()).sorted(Comparator.comparing(Task::getPriority).thenComparing(Task::getDueDate, Comparator.nullsLast(Comparator.naturalOrder()))).collect(Collectors.toList());
		
	}
	
}
