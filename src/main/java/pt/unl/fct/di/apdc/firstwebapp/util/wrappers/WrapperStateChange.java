package pt.unl.fct.di.apdc.firstwebapp.util.wrappers;

import pt.unl.fct.di.apdc.firstwebapp.util.AuthToken;
import pt.unl.fct.di.apdc.firstwebapp.util.data.StateChangeData;

public class WrapperStateChange {
    public StateChangeData data;
    public AuthToken token;

    public WrapperStateChange(){

    }

    public WrapperStateChange(StateChangeData data, AuthToken token){
        this.data = data;
        this.token = token;
    }
    
}
