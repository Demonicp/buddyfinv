package com.es.backendbuddyfinv.dto;

public class PasswordResetRequest {
    private String usernameOrEmail;

    public String getUsernameOrEmail(){
        return usernameOrEmail;
    }
     public void setUsernameOrEmail(String usernameOrEmail){
        this.usernameOrEmail= usernameOrEmail;
     }
    
}
