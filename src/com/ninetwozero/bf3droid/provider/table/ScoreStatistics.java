package com.ninetwozero.bf3droid.provider.table;

import android.net.Uri;
import android.provider.BaseColumns;
import com.ninetwozero.bf3droid.Battlelog;
import com.ninetwozero.bf3droid.datatype.Statistics;

public class ScoreStatistics extends Statistics {

    public static final Uri URI = Uri.parse("content://" + Battlelog.AUTHORITY + "/scoreStatistics");

    public interface Columns extends BaseColumns {
        public static final String ID = "_id";
        public static final String PERSONA_ID = "personaID";
        public static final String ASSAULT = "assault";
        public static final String ENGINEER = "engineer";
        public static final String SUPPORT = "support";
        public static final String RECON = "recon";
        public static final String JET = "jet";
        public static final String TANK = "tank";
        public static final String IFV = "ifv";
        public static final String ANTI_AIR = "antiAir";
        public static final String ATTACK_HELI = "attackHeli";
        public static final String SCOUT_HELI = "scoutHeli";
        public static final String COMBAT = "combat";
        public static final String AWARD = "award";
        public static final String UNLOCKS = "unlocks";
        public static final String TOTAL_SCORE = "totalScore";
    }

    public static final String[] SCORE_STATISTICS_PROJECTION = new String[]{Columns.ID, Columns.PERSONA_ID,
            Columns.ASSAULT, Columns.ENGINEER, Columns.SUPPORT, Columns.RECON, Columns.JET, Columns.TANK,
            Columns.IFV, Columns.ANTI_AIR, Columns.ATTACK_HELI, Columns.SCOUT_HELI, Columns.COMBAT,
            Columns.AWARD, Columns.UNLOCKS, Columns.TOTAL_SCORE
    };
}
