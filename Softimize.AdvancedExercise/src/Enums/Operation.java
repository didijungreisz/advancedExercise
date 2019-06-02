package Enums;

public enum Operation {
		ADD(" added to"), 
		REMOVE(" removed from"),
		UPDATE(" was updated");
		
		private String action;
		
		   public String getAction() 
		    { 
		        return this.action; 
		    } 
		   
		  	    private Operation(String action) 
		    { 
		        this.action = action; 
		    } 

}
