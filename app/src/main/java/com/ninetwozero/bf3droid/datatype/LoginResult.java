package com.ninetwozero.bf3droid.datatype;

public class LoginResult {

    private String userName;
    private String checkSum;
    private String error = "";

    public LoginResult(String error) {
        this.error = error;
    }

    public LoginResult(String userName, String checkSum) {
        this.userName = userName;
        this.checkSum = checkSum;
    }

    public String getUserName() {
        return userName;
    }

    public String getCheckSum() {
        return checkSum;
    }

    public String getError() {
        return error;
    }
}
