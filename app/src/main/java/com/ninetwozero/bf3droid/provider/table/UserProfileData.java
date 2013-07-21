package com.ninetwozero.bf3droid.provider.table;

public class UserProfileData {

    private final long userId;
    private final String username;
    private final String name;
    private final String age;
    private final String enlisted;
    private final String lastSeen;
    private final String presentation;
    private final String country;
    private final int veteranStatus;
    private final String statusMessage;
    private final String statusMessageDate;

    public UserProfileData(long userId, String username, String name, String age, String enlisted, String lastSeen,
                           String presentation, String country, int veteranStatus, String statusMessage, String statusMessageDate) {
        this.userId = userId;
        this.username = username;
        this.name = name;
        this.age = age;
        this.enlisted = enlisted;
        this.lastSeen = lastSeen;
        this.presentation = presentation;
        this.country = country;
        this.veteranStatus = veteranStatus;
        this.statusMessage = statusMessage;
        this.statusMessageDate = statusMessageDate;
    }

    public long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public String getAge() {
        return age;
    }

    public String getEnlisted() {
        return enlisted;
    }

    public String getLastSeen() {
        return lastSeen;
    }

    public String getPresentation() {
        return presentation;
    }

    public String getCountry() {
        return country;
    }

    public int getVeteranStatus() {
        return veteranStatus;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public String getStatusMessageDate() {
        return statusMessageDate;
    }
}
