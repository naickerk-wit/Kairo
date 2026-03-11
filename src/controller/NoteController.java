package controller;

import java.util.List;
import java.util.UUID;

import exceptions.InvalidNameException;
import exceptions.NoteNotFoundException;
import exceptions.PageNotFoundException;
import model.Note;
import model.Page;
import model.Workspace;

public class NoteController {

	private Workspace workspace;

	public NoteController(Workspace workspace) {
		this.workspace = workspace;
	}

	private Page getSelectedPageOrThrow() {
		Page page = workspace.getSelectedPage();

		if (page == null) {
			throw new PageNotFoundException("No page is currently selected.");
		}

		return page;
	}

	private void validateContent(String content) {
		if (content == null || content.trim().isEmpty()) {
			throw new InvalidNameException("Note content cannot be blank.");
		}
	}

	private Note findNoteById(Page page, UUID noteId) {
		if (noteId == null) {
			return null;
		}

		List<Note> notes = page.getNotes();
		for (Note note : notes) {
			if (note.getId().equals(noteId)) {
				return note;
			}
		}

		return null;
	}

	public Note addNote(String content) {
		Page page = getSelectedPageOrThrow();
		validateContent(content);

		Note note = new Note(content);
		page.addNote(note);

		return note;
	}

	public void editNote(UUID noteId, String newContent) {
		Page page = getSelectedPageOrThrow();

		Note note = findNoteById(page, noteId);
		if (note == null) {
			throw new NoteNotFoundException("Note not found.");
		}

		validateContent(newContent);
		note.setContent(newContent);
	}

	public void deleteNote(UUID noteId) {
		Page page = getSelectedPageOrThrow();

		Note note = findNoteById(page, noteId);
		if (note == null) {
			throw new NoteNotFoundException("Note not found.");
		}

		page.removeNote(noteId);
	}
}

