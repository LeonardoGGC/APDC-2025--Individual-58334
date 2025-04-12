package pt.unl.fct.di.apdc.firstwebapp.util.wrappers;

import pt.unl.fct.di.apdc.firstwebapp.util.AuthToken;
import pt.unl.fct.di.apdc.firstwebapp.util.data.ChangePasswordData;

public class WrapperChangePassword {

    public ChangePasswordData data;
    public AuthToken token;

    public WrapperChangePassword(){

    }

    public WrapperChangePassword(ChangePasswordData data, AuthToken token){
        this.data = data;
        this.token = token;
    }
    
}
