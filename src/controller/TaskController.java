package controller;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import exceptions.InvalidNameException;
import exceptions.PageNotFoundException;
import exceptions.TaskNotFoundException;
import model.Page;
import model.Priority;
import model.Status;
import model.Task;
import model.Workspace;

public class TaskController {

	private Workspace workspace;

	public TaskController(Workspace workspace) {
		this.workspace = workspace;
	}

	private Page getSelectedPageOrThrow() {
		Page page = workspace.getSelectedPage();

		if (page == null) {
			throw new PageNotFoundException("No page is currently selected.");
		}

		return page;
	}

	private void validateTitle(String title) {
		if (title == null || title.trim().isEmpty()) {
			throw new InvalidNameException("Task title cannot be blank.");
		}
	}

	private Task findTaskById(Page page, UUID taskId) {
		if (taskId == null) {
			return null;
		}

		List<Task> tasks = page.getTasks();
		for (Task task : tasks) {
			if (task.getId().equals(taskId)) {
				return task;
			}
		}

		return null;
	}

	public Task addTask(String title, LocalDate dueDate, Priority priority, Status status) {
		Page page = getSelectedPageOrThrow();
		validateTitle(title);

		if (priority == null) {
			priority = Priority.MEDIUM;
		}

		if (status == null) {
			status = Status.NOT_STARTED;
		}

		Task task = new Task(title, dueDate, priority, status);
		page.addTask(task);

		return task;
	}

	public void editTask(UUID taskId, String newTitle, LocalDate newDueDate, Priority newPriority, Status newStatus) {
		Page page = getSelectedPageOrThrow();

		Task task = findTaskById(page, taskId);
		if (task == null) {
			throw new TaskNotFoundException("Task not found.");
		}

		validateTitle(newTitle);

		task.setTitle(newTitle);
		task.setDueDate(newDueDate);

		if (newPriority != null) {
			task.setPriority(newPriority);
		}

		if (newStatus != null) {
			task.setStatus(newStatus);
		}
	}

	public void deleteTask(UUID taskId) {
		Page page = getSelectedPageOrThrow();

		Task task = findTaskById(page, taskId);
		if (task == null) {
			throw new TaskNotFoundException("Task not found.");
		}

		page.removeTask(taskId);
	}

	public void toggleComplete(UUID taskId) {
		Page page = getSelectedPageOrThrow();

		Task task = findTaskById(page, taskId);
		if (task == null) {
			throw new TaskNotFoundException("Task not found.");
		}

		if (task.getStatus() == Status.COMPLETED) {
			task.setStatus(Status.NOT_STARTED);
		} else {
			task.markComplete();
		}
	}
}