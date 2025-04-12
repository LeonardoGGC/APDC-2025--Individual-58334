package pt.unl.fct.di.apdc.firstwebapp.util.data;

public class StateChangeData {
	
	public String username;
	public String state;
	
	public StateChangeData() {

	}
	
	public StateChangeData(String username, String state) {
		this.username = username;
		this.state = state;
	}

	private boolean nonEmptyOrBlankField(String field) {
        return field != null && !field.isBlank();
    }

	public boolean isValid(){
		return nonEmptyOrBlankField(state) &&
				(state.equalsIgnoreCase("activated") || state.equalsIgnoreCase("deactivated"))
				&& nonEmptyOrBlankField(username);
	}
    
}
