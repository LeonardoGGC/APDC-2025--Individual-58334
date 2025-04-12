package pt.unl.fct.di.apdc.firstwebapp.util.data;
 
 public class AccountChangeData {
 	
    public String originalUsername;
 	public String username;
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
    public String role;
    public String state;
 	
 	
 	public AccountChangeData() {
 		
 	}
 	
 	public AccountChangeData(String originalUsername, String username, String email, String name, String phoneNumber, String profileStatus,
						String cc, String nif, String employer, String job, String address, String employerNIF, String state, String role) {
        this.originalUsername = originalUsername;
        this.username = username;
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
        this.state = state;
        this.role = role;
 	}

    public boolean nonEmptyOrBlankField(String field) {
        return field != null && !field.isBlank();
    }
 	
 	public boolean validChange() { 		 	
 		return ((profileStatus.equalsIgnoreCase("private") || profileStatus.equalsIgnoreCase("public") || !nonEmptyOrBlankField(profileStatus)) &&
				(state.equalsIgnoreCase("activated") || state.equalsIgnoreCase("suspended") || state.equalsIgnoreCase("deactivated") || !nonEmptyOrBlankField(state)) &&
 			   	(email.contains("@") || !nonEmptyOrBlankField(email))) && nonEmptyOrBlankField(originalUsername) ;
 	}

    public String update(String field, String originalField){
        String result = originalField;
        if(nonEmptyOrBlankField(field)) result = field;
        return result;
    }

    public boolean invalidChangeForEnduser(){
        return nonEmptyOrBlankField(username) || nonEmptyOrBlankField(email) 
                || nonEmptyOrBlankField(name) || nonEmptyOrBlankField(role) || nonEmptyOrBlankField(state);
    }

    public boolean invalidChangeForBackoffice(){
        return nonEmptyOrBlankField(username) || nonEmptyOrBlankField(email);
    }
 }
