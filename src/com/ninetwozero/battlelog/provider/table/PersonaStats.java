package com.ninetwozero.battlelog.provider.table;

import android.net.Uri;
import android.provider.BaseColumns;
import com.ninetwozero.battlelog.Battlelog;

public class PersonaStats {

    public static final Uri URI = Uri.parse("content://" + Battlelog.AUTHORITY + "/personaStats");

    public interface Columns extends BaseColumns {
        public static final String ID = "_id";
        public static final String PERSONA_ID = "personaID";
        public static final String KEY = "key";
        public static final String VALUE = "value";
        public static final String STYLE = "style";
    }

    public static final String[] PERSONA_STATS_PROJECTION = new String[]{Columns.ID, Columns.PERSONA_ID,
            Columns.KEY, Columns.VALUE, Columns.STYLE
    };

    private int id;
    private long personaId;
    private String key;
    private String value;
    private String style;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getPersonaId() {
        return personaId;
    }

    public void setPersonaId(long personaId) {
        this.personaId = personaId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }
}
