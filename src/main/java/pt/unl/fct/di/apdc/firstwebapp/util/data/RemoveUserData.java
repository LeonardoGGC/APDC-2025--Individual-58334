package pt.unl.fct.di.apdc.firstwebapp.util.data;

public class RemoveUserData {
	
	public String username;
	
	public RemoveUserData() {

	}
	
	public RemoveUserData(String username) {
		this.username = username;
	}

	private boolean nonEmptyOrBlankField(String field) {
        return field != null && !field.isBlank();
    }

	public boolean isValid(){
		return nonEmptyOrBlankField(username);
	}
}
