package com.ninetwozero.bf3droid.provider.table;

import android.net.Uri;
import android.provider.BaseColumns;

import com.ninetwozero.bf3droid.BF3Droid;
import com.ninetwozero.bf3droid.datatype.Statistics;

public class PersonaStatistics extends Statistics {

    public static final Uri URI = Uri.parse("content://" + BF3Droid.AUTHORITY + "/personaStatistics");

    public interface Columns extends BaseColumns {
        public static final String ID = "_id";
        public static final String PERSONA_ID = "personaId";
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
        public static final String MCOM_DEFENCE_KILLS = "mcomDefenceKills";
        public static final String MCOM_DESTROYED = "mcomDestroyed";
        public static final String CONQ_FLAG_CAPTURED = "conqFlagCaptured";
        public static final String CONQ_FLAG_DEFENDED = "conqFlagDefended";
        public static final String AVENGER_KILLS = "avengerKills";
        public static final String SAVIOR_KILLS = "saviorKills";
        public static final String DOGTAG_TAKEN = "dogtagTaken";
        public static final String CTF_CAPTURE_FLAG = "ctfCaptureFlag";
        public static final String SQUAD_SCORE_BONUS = "squadScoreBonus";
        public static final String SHOT_FIRED = "shotFired";
        public static final String HIG_NEMESIS_STREAK = "higNemesisStreak";
        public static final String NEMESIS_KILLS = "nemesisKills";
        public static final String SUPPRESSION_ASSISTS = "suppressionAssists";
        public static final String QUITS = "quits";
    }

    public static final String[] PERSONA_STATS_PROJECTION = new String[]{Columns.ID, Columns.PERSONA_ID, Columns.KILLS,
            Columns.KILL_ASSISTS, Columns.VEHICLE_DESTROYED, Columns.VEHICLE_ASSISTS, Columns.HEALS, Columns.REVIVES,
            Columns.REPAIRS, Columns.RESUPPLIES, Columns.DEATHS, Columns.KD_RATIO, Columns.WINS, Columns.LOSSES,
            Columns.WL_RATIO, Columns.ACCURACY, Columns.LONGEST_HEADSHOT, Columns.LONGEST_KILLSTREAK,
            Columns.SKILLRATING, Columns.TIME_PLAYED, Columns.SCORE_PER_MINUTE, Columns.MCOM_DEFENCE_KILLS,
            Columns.MCOM_DESTROYED, Columns.CONQ_FLAG_CAPTURED, Columns.CONQ_FLAG_DEFENDED, Columns.AVENGER_KILLS,
            Columns.SAVIOR_KILLS, Columns.DOGTAG_TAKEN, Columns.CTF_CAPTURE_FLAG, Columns.SQUAD_SCORE_BONUS,
            Columns.SHOT_FIRED, Columns.HIG_NEMESIS_STREAK, Columns.NEMESIS_KILLS, Columns.SUPPRESSION_ASSISTS,
            Columns.QUITS
    };

    public static final String[] PERSONA_STATISTICS = new String[]{Columns.KILLS, Columns.DEATHS, Columns.KD_RATIO,
            Columns.KILL_ASSISTS, Columns.SCORE_PER_MINUTE, Columns.QUITS, Columns.MCOM_DEFENCE_KILLS,
            Columns.MCOM_DESTROYED, Columns.CONQ_FLAG_CAPTURED, Columns.CONQ_FLAG_DEFENDED,
            Columns.VEHICLE_DESTROYED, Columns.VEHICLE_ASSISTS, Columns.ACCURACY, Columns.LONGEST_HEADSHOT,
            Columns.LONGEST_KILLSTREAK, Columns.SKILLRATING, Columns.AVENGER_KILLS, Columns.SAVIOR_KILLS,
            Columns.DOGTAG_TAKEN, Columns.CTF_CAPTURE_FLAG, Columns.SQUAD_SCORE_BONUS, Columns.REPAIRS,
            Columns.REVIVES, Columns.HEALS, Columns.RESUPPLIES, Columns.SHOT_FIRED, Columns.HIG_NEMESIS_STREAK,
            Columns.NEMESIS_KILLS, Columns.SUPPRESSION_ASSISTS, Columns.WINS, Columns.LOSSES, Columns.WL_RATIO,
            Columns.TIME_PLAYED
    };
}
