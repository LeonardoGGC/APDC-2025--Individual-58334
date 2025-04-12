package pt.unl.fct.di.apdc.firstwebapp.util.wrappers;

import pt.unl.fct.di.apdc.firstwebapp.util.AuthToken;
import pt.unl.fct.di.apdc.firstwebapp.util.data.RemoveUserData;

public class WrapperRemoveUser {

    public RemoveUserData data;
    public AuthToken token;

    public WrapperRemoveUser(){

    }

    public WrapperRemoveUser(RemoveUserData data, AuthToken token){
        this.data = data;
        this.token = token;
    }
    
}