package model;

import java.awt.Color;
import java.util.UUID;

public class Tag implements Identifiable {
	
	private final UUID id;
	private String name;
	private String colorHex;
	
	// Predefined tag colors
	public static final String[] TAG_COLORS = {
		"#9E5C58", // Terracotta
		"#A88A54", // Gold
		"#5C7C60", // Sage
		"#827869", // Taupe
		"#6B8E9F", // Steel Blue
		"#8B7B96"  // Lavender
	};
	
	public Tag(String name) {
		this.id = UUID.randomUUID();
		this.name = name;
		this.colorHex = TAG_COLORS[Math.abs(name.hashCode()) % TAG_COLORS.length];
	}
	
	public Tag(String name, String colorHex) {
		this.id = UUID.randomUUID();
		this.name = name;
		this.colorHex = colorHex;
	}
	
	@Override
	public UUID getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getColorHex() {
		return colorHex;
	}
	
	public void setColorHex(String colorHex) {
		this.colorHex = colorHex;
	}
	
	public Color getColor() {
		return Color.decode(colorHex);
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
	
	@Override 
	public String toString() { 
		
		return "#" + name; 
		
	}

}
