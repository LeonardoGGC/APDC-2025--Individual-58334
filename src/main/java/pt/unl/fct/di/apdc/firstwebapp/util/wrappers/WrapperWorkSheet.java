package pt.unl.fct.di.apdc.firstwebapp.util.wrappers;

import pt.unl.fct.di.apdc.firstwebapp.util.AuthToken;
import pt.unl.fct.di.apdc.firstwebapp.util.data.WorkSheetData;

public class WrapperWorkSheet {

    public WorkSheetData data;
    public AuthToken token;

    public WrapperWorkSheet(){

    }

    public WrapperWorkSheet(WorkSheetData data, AuthToken token){
        this.data = data;
        this.token = token;
    }
    
}
