package pt.unl.fct.di.apdc.firstwebapp.util.data;

import pt.unl.fct.di.apdc.firstwebapp.util.QOLfunctions;

public class RoleChangeData{
	
	public String username;
	public String role;
	
	public RoleChangeData() {

	}
	
	public RoleChangeData(String username, String role) {
		this.username = username;
		this.role = role;
	}

	private boolean nonEmptyOrBlankField(String field) {
		return field != null && !field.isBlank();
	}

	public boolean validChange(){
		return nonEmptyOrBlankField(username) &&
			(QOLfunctions.isEnduser(role) || QOLfunctions.isPartner(role) || QOLfunctions.isBackOffice(role) || QOLfunctions.isAdmin(role));
	}
	
}
