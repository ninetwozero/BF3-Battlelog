package com.ninetwozero.bf3droid.provider.table;

import android.net.Uri;
import android.provider.BaseColumns;
import com.ninetwozero.bf3droid.BF3Droid;

public class Personas {

    public static final Uri URI = Uri.parse("content://" + BF3Droid.AUTHORITY + "/personas/");

    public interface Columns extends BaseColumns {
        public static final String PERSONA_ID = "personaId";
        public static final String USER_ID = "userId";
        public static final String NAME = "name";
        public static final String PLATFORM = "platform";
        public static final String IMAGE = "image";
    }

    public static final String[] PERSONAS_PROJECTION = new String[]{Columns.PERSONA_ID, Columns.USER_ID,
            Columns.NAME, Columns.PLATFORM, Columns.IMAGE};

    private long personaId;
    private long userId;
    private String name;
    private String platform;
    private String image;

    public long getPersonaId() {
        return personaId;
    }

    public void setPersonaId(long personaId) {
        this.personaId = personaId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}