package model;

public class Tag {
	
	private String name;
	
	public Tag(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@Override 
	public boolean equals(Object obj) { 
		
		if (this == obj) return true; 
		if (!(obj instanceof Tag)) return false; 
		
		Tag other = (Tag) obj; 
		
		return name.equalsIgnoreCase(other.name); 
		
	} 
	
	@Override 
	public int hashCode() { 
		
		return name.toLowerCase().hashCode(); 
		
	} 
	
	@Override public String toString() { 
		
		return "#" + name; 
		
	}

}
