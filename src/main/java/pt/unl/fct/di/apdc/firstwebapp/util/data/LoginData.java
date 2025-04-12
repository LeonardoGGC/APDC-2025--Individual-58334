package pt.unl.fct.di.apdc.firstwebapp.util.data;

public class LoginData {
	
	public String username;
	public String password;
	
	public LoginData() {
		
	}
	
	public LoginData(String username, String password) {
		this.username = username;
		this.password = password;
	}

	private boolean nonEmptyOrBlankField(String field) {
        return field != null && !field.isBlank();
    }

	public boolean isValid(){
		return nonEmptyOrBlankField(username) && nonEmptyOrBlankField(password);
	}
	
}
