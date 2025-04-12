package pt.unl.fct.di.apdc.firstwebapp.util;


public class QOLfunctions {

    public static boolean isEnduser(String role){
        return role.equalsIgnoreCase("enduser");
    }

    public static boolean isPartner(String role){
        return role.equalsIgnoreCase("partner");
    }

    public static boolean isBackOffice(String role){
        return role.equalsIgnoreCase("backoffice");
    }

    public static boolean isAdmin(String role){
        return role.equalsIgnoreCase("admin");
    }
}