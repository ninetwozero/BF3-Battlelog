package com.ninetwozero.battlelog.provider.table;

import android.net.Uri;
import android.provider.BaseColumns;
import com.ninetwozero.battlelog.Battlelog;

public class PersonaStatistics {

    public static final Uri URI = Uri.parse("content://" + Battlelog.AUTHORITY + "/personaStatistics");

    public interface Columns extends BaseColumns {
        public static final String ID = "_id";
        public static final String PERSONA_ID = "personaID";
        public static final String KILLS = "kills";
        public static final String KILL_ASSISTS = "killAssists";
        public static final String VEHICLE_DESTROYED = "vehicleDestroyed";
        public static final String VEHICLE_ASSISTS = "vehicleAssists";
        public static final String HEALS = "heals";
        public static final String REVIVES = "revives";
        public static final String REPAIRS = "repairs";
        public static final String RESUPPLIES = "resupplies";
        public static final String DEATHS = "deaths";
        public static final String KD_RATIO = "kdRatio";
        public static final String WINS = "wins";
        public static final String LOSSES = "losses";
        public static final String WL_RATIO = "wlRatio";
        public static final String ACCURACY = "accuracy";
        public static final String LONGEST_HEADSHOT = "longestHeadshot";
        public static final String LONGEST_KILLSTREAK = "longestKillstreak";
        public static final String SKILLRATING = "skillrating";
        public static final String TIME_PLAYED = "timePlayed";
        public static final String SCORE_PER_MINUTE = "scorePerMinute";
    }

    public static final String[] PERSONA_STATS_PROJECTION = new String[]{Columns.ID, Columns.PERSONA_ID, Columns.KILLS,
            Columns.KILL_ASSISTS, Columns.VEHICLE_DESTROYED, Columns.VEHICLE_ASSISTS, Columns.HEALS, Columns.REVIVES,
            Columns.REPAIRS, Columns.RESUPPLIES, Columns.DEATHS, Columns.KD_RATIO, Columns.WINS, Columns.LOSSES,
            Columns.WL_RATIO, Columns.ACCURACY, Columns.LONGEST_HEADSHOT, Columns.LONGEST_KILLSTREAK,
            Columns.SKILLRATING, Columns.TIME_PLAYED, Columns.SCORE_PER_MINUTE
    };


    private int title;
    private String value;
    private int style;

    public PersonaStatistics(int title, String value, int style){
        this.title = title;
        this.value = value;
        this.style = style;
    }

    public int getTitle() {
        return title;
    }

    public void setTitle(int title) {
        this.title = title;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getStyle() {
        return style;
    }

    public void setStyle(int style) {
        this.style = style;
    }
}
