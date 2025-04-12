package pt.unl.fct.di.apdc.firstwebapp.util.wrappers;

import pt.unl.fct.di.apdc.firstwebapp.util.AuthToken;
import pt.unl.fct.di.apdc.firstwebapp.util.data.AccountChangeData;

public class WrapperAccountChange {
    public AccountChangeData data;
    public AuthToken token;

    public WrapperAccountChange(){}

    public WrapperAccountChange(AccountChangeData data, AuthToken token){
        this.data = data;
        this.token = token;
    }
    
}
