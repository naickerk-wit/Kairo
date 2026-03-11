package controller;

import java.util.List;
import java.util.UUID;

import exceptions.InvalidNameException;
import exceptions.PageNotFoundException;
import model.Page;
import model.Workspace;

public class PageController {

	private Workspace workspace;

	public PageController(Workspace workspace) {
		this.workspace = workspace;
	}

	private void validateName(String name) {
		if (name == null || name.trim().isEmpty()) {
			throw new InvalidNameException("Page name cannot be blank.");
		}
	}

	private Page findPageById(UUID pageId) {
		if (pageId == null) {
			return null;
		}

		List<Page> pages = workspace.getPages();
		for (Page page : pages) {
			if (page.getId().equals(pageId)) {
				return page;
			}
		}

		return null;
	}

	public Page createPage(String name) {
		validateName(name);
		return workspace.createPage(name);
	}

	public void renamePage(UUID pageId, String newName) {
		validateName(newName);

		Page page = findPageById(pageId);
		if (page == null) {
			throw new PageNotFoundException("Page not found.");
		}

		workspace.renamePage(pageId, newName);
	}

	public void deletePage(UUID pageId) {
		Page page = findPageById(pageId);
		if (page == null) {
			throw new PageNotFoundException("Page not found.");
		}

		workspace.deletePage(pageId);
	}

	public void selectPage(UUID pageId) {
		Page page = findPageById(pageId);
		if (page == null) {
			throw new PageNotFoundException("Page not found.");
		}

		workspace.selectPage(pageId);
	}
}