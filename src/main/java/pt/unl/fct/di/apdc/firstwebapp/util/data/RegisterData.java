package pt.unl.fct.di.apdc.firstwebapp.util.data;
 
 public class RegisterData {
 	
 	public String username;
 	public String password;
 	public String confirmation;
 	public String email;
 	public String name;
	public String phoneNumber;
	public String profileStatus;
	public String cc;
	public String nif;
	public String employer;
	public String job;
	public String address;
	public String employerNIF;
 	
 	
 	public RegisterData() {
 		
 	}
 	
 	public RegisterData(String username, String password, String confirmation,
						String email, String name, String phoneNumber, String profileStatus,
						String cc, String nif, String employer,
						String job, String address, String employerNIF) {
 		this.username = username;
 		this.password = password;
 		this.confirmation = confirmation;
 		this.email = email;
 		this.name = name;
		this.phoneNumber = phoneNumber;
		this.profileStatus = profileStatus;
		this.cc = cc;
		this.nif = nif;
		this.employer = employer;
		this.job = job;
		this.address = address;
		this.employerNIF = employerNIF;
 	}
 	
 	private boolean nonEmptyOrBlankField(String field) {
 		return field != null && !field.isBlank();
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

	public String changeIfEmpty(String field){
		if(!nonEmptyOrBlankField(field)) return "NOT DEFINED";
		return field;
	}
 	
 	public boolean validRegistration() {
 		 	
 		return nonEmptyOrBlankField(username) &&
			   validPassword(password) &&
 			   nonEmptyOrBlankField(email) &&
 			   nonEmptyOrBlankField(name) &&
			   nonEmptyOrBlankField(phoneNumber) &&
			   nonEmptyOrBlankField(profileStatus) &&
			   (profileStatus.equalsIgnoreCase("public") || profileStatus.equalsIgnoreCase("private")) &&
 			   email.contains("@") &&
 			   password.equals(confirmation);
 	}
 }