package pt.unl.fct.di.apdc.firstwebapp.util.data;

public class ChangePasswordData {
    public String password;
    public String confirmation;
	
	public ChangePasswordData() {

	}
	
	public ChangePasswordData(String password, String confirmation) {
		this.password = password;
        this.confirmation = confirmation;
	}

    private static boolean validPassword(String password) {
		
		boolean hasNumber = false;
		boolean hasLowerCase = false;
		boolean hasUppercase = false;

		for(char c : password.toCharArray()) {

			if(!hasLowerCase) hasLowerCase = Character.isLowerCase(c);

			if(!hasNumber) hasNumber = Character.isDigit(c);

			if(!hasUppercase) hasUppercase = Character.isUpperCase(c);
			
			if(hasLowerCase && hasNumber && hasUppercase) return true;
		}
		return false;
	}

    public boolean verified(){
        return validPassword(password) && password.equals(confirmation);
    }
}
