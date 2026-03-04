package model;

public enum Status {
	
	NOT_STARTED, IN_PROGRESS, COMPLETED, CANCELLED;
	
	public boolean isActive() {
		
		return this == NOT_STARTED || this == IN_PROGRESS;
		
	}
	
	public boolean isCompleted() {
		
		return this == COMPLETED;
		
	}
	
	public boolean isCancelled() {
		
		return this == CANCELLED;
		
	}
	
	@Override
	public String toString() {
		
		String lower = name().toLowerCase().replace("_", " ");
		return Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
	}

}
