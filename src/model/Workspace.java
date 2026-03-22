package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Workspace {
	
	private final List<Page> pages;
	private final List<Tag> tags;
	private final List<Page> archivedPages;
	private UUID selectedPageId;
	private boolean darkMode;
	
	public Workspace() {
		
		this.pages = new ArrayList<>();
		this.tags = new ArrayList<>();
		this.archivedPages = new ArrayList<>();
		this.selectedPageId = null;
		this.darkMode = false;
		
	}
	
	public List<Page> getPages() {
		
		return Collections.unmodifiableList(pages);
		
	}
	
	public UUID getSelectedPageId() {
		
		return selectedPageId;
		
	}
	
	public Page createPage(String name) {
		
		Page p = new Page(name);
		pages.add(p);
		
		if(selectedPageId == null) {
			
			selectedPageId = p.getId();
			
		}
		
		return p;
		
	}
	
	private Page findPage(UUID id) {
		
		return pages.stream().filter(p -> p.getId().equals(id)).findFirst().orElse(null);
		
	}
	
public Page getSelectedPage() {
		
		if(selectedPageId == null) return null;
		
		return findPage(selectedPageId);
		
	}
	
	public void selectPage(UUID id) {
		
		if(findPage(id) != null) {
			
			this.selectedPageId = id;
			
		}
		
	}
	
	public void renamePage(UUID id, String name) {
		
		Page p = findPage(id);
		
		if(p != null) {
			
			p.rename(name);
			
		}
		
	}
	
	public void deletePage(UUID id) {
		
		pages.removeIf(p -> p.getId().equals(id));
		
		if(id != null && id.equals(selectedPageId)) {
			
			selectedPageId = pages.isEmpty() ? null : pages.get(0).getId();
			
		}
		
	}
	
// Tag management
	public List<Tag> getTags() {
		
		return Collections.unmodifiableList(tags);
		
	}
	
	public Tag createTag(String name) {
		
		// Check if tag already exists
		Tag existing = tags.stream()
			.filter(t -> t.getName().equalsIgnoreCase(name))
			.findFirst()
			.orElse(null);
		
		if (existing != null) {
			return existing;
		}
		
		Tag tag = new Tag(name);
		tags.add(tag);
		return tag;
		
	}
	
	public Tag createTag(String name, String colorHex) {
		
		Tag existing = tags.stream()
			.filter(t -> t.getName().equalsIgnoreCase(name))
			.findFirst()
			.orElse(null);
		
		if (existing != null) {
			return existing;
		}
		
		Tag tag = new Tag(name, colorHex);
		tags.add(tag);
		return tag;
		
	}
	
	public void deleteTag(UUID tagId) {
		
		tags.removeIf(t -> t.getId().equals(tagId));
		
	}
	
	public Tag findTagByName(String name) {
		
		return tags.stream()
			.filter(t -> t.getName().equalsIgnoreCase(name))
			.findFirst()
			.orElse(null);
		
	}
	
	// Archive management
	public List<Page> getArchivedPages() {
		
		return Collections.unmodifiableList(archivedPages);
		
	}
	
	public void archivePage(UUID pageId) {
		
		Page page = findPage(pageId);
		if (page != null) {
			pages.remove(page);
			archivedPages.add(page);
			
			if (pageId.equals(selectedPageId)) {
				selectedPageId = pages.isEmpty() ? null : pages.get(0).getId();
			}
		}
		
	}
	
	public void restorePage(UUID pageId) {
		
		Page page = archivedPages.stream()
			.filter(p -> p.getId().equals(pageId))
			.findFirst()
			.orElse(null);
		
		if (page != null) {
			archivedPages.remove(page);
			pages.add(page);
		}
		
	}
	
	public void deleteArchivedPage(UUID pageId) {
		
		archivedPages.removeIf(p -> p.getId().equals(pageId));
		
	}
	
	// Dark mode
	public boolean isDarkMode() {
		
		return darkMode;
		
	}
	
	public void setDarkMode(boolean darkMode) {
		
		this.darkMode = darkMode;
		
	}
	
	public void toggleDarkMode() {
		
		this.darkMode = !this.darkMode;
		
	}
	
	@Override
	public String toString() {
		
		return "Workspace{" + "pages=" + pages.size() + ", tags=" + tags.size() + ", archived=" + archivedPages.size() + ", selectedPageId=" + selectedPageId + ", darkMode=" + darkMode + '}';
		
	}

}
