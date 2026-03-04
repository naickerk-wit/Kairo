package model;

public enum Priority {
	
	LOW, MEDIUM, HIGH;
	
	@Override
	public String toString() {
		
		String lower = name().toLowerCase();
		return Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
	}
	

}
