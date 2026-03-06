package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Workspace {
	
	private final List<Page> pages;
	private UUID selectedPageId;
	
	public Workspace() {
		
		this.pages = new ArrayList<>();
		this.selectedPageId = null;
		
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
	
	public Page getSelectedPage(UUID id) {
		
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
	
	@Override
	public String toString() {
		
		return "Workspace{" + "pages=" + pages.size() + ", selectedPageId=" + selectedPageId + '}';
		
	}

}
