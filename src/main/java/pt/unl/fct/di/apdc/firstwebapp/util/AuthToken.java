package pt.unl.fct.di.apdc.firstwebapp.util;


import org.apache.commons.codec.digest.DigestUtils;

public class AuthToken {

	public static final long EXPIRATION_TIME = 1000*60*60*2;
	
	public String username;
	public String role;
	public long creationData;
	public long expirationData;
	public String magicNumber;
	
	public AuthToken() {

	}
	
	public AuthToken(String username, String role) {
		this.username = username;
		this.role = role;
		this.creationData = System.currentTimeMillis();
		this.expirationData = this.creationData + EXPIRATION_TIME;
		this.magicNumber = DigestUtils.sha512Hex(username + creationData);
	}

	public AuthToken(String username, String role, long creationData, long expirationData, String magicNumber) {
		this.username = username;
		this.role = role;
		this.creationData = creationData;
		this.expirationData = expirationData;
		this.magicNumber = magicNumber;
	}
	
}
