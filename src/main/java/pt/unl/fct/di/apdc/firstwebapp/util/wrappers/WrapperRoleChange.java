package pt.unl.fct.di.apdc.firstwebapp.util.wrappers;

import pt.unl.fct.di.apdc.firstwebapp.util.AuthToken;
import pt.unl.fct.di.apdc.firstwebapp.util.data.RoleChangeData;

public class WrapperRoleChange {

    public RoleChangeData data;
    public AuthToken token;

    public WrapperRoleChange(){

    }

    public WrapperRoleChange(RoleChangeData data, AuthToken token){
        this.data = data;
        this.token = token;
    }
    
}
