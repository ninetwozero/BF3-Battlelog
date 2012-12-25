package com.ninetwozero.bf3droid.datatype;

public class LoginResult {

    private String userName;
    private long userId;
    private String checkSum;
    private String error = "";

    public LoginResult(String error) {
        this.error = error;
    }

    public LoginResult(String userName, long userId, String checkSum) {
        this.userName = userName;
        this.userId = userId;
        this.checkSum = checkSum;
    }

    public String getUserName() {
        return userName;
    }

    public long getUserId() {
        return userId;
    }

    public String getCheckSum() {
        return checkSum;
    }

    public String getError() {
        return error;
    }
}
