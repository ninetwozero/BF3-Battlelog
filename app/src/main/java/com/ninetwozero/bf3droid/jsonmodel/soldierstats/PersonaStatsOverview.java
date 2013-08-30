package com.ninetwozero.bf3droid.jsonmodel.soldierstats;

import com.google.gson.annotations.SerializedName;

public class PersonaStatsOverview {

    @SerializedName("rank")
    private int rank;
    /**
     * Multiplayer score
     */
    @SerializedName("kitScores")
    private KitScores kitScores;
    @SerializedName("vehicleScores")
    private VehicleScores vehicleScores;
    @SerializedName("sc_vehicle")
    private long vehicleScore;
    @SerializedName("totalScore")
    private long totalScore;
    @SerializedName("score")
    private long score;
    @SerializedName("timePlayed")
    private long timePlayed;
    @SerializedName("numWins")
    private long gameWon;
    @SerializedName("numLosses")
    private long gameLost;
    @SerializedName("wlRatio")
    private double wlRatio;
    @SerializedName("sc_award")
    private long awardScore;
    @SerializedName("sc_unlock")
    private long unlockScore;
    @SerializedName("combatScore")
    private long combatScore;

    /**
     * All time statistics
     */
    @SerializedName("kills")
    private long kills;
    @SerializedName("deaths")
    private long deaths;
    @SerializedName("kdRatio")
    private double kdRatio;
    @SerializedName("killAssists")
    private long killAssists;
    @SerializedName("scorePerMinute")
    private double scoreMin;
    @SerializedName("quitPercentage")
    private double quitPercentage;
    @SerializedName("mcomDefendKills")
    private long mcomDefendKills;
    @SerializedName("mcomDestroy")
    private long mcomDestroyed;
    @SerializedName("flagCaptures")
    private long flagCaptures;
    @SerializedName("flagDefend")
    private long flagsDefended;

    @SerializedName("vehiclesDestroyed")
    private long vehiclesDestroyed;
    @SerializedName("vehiclesDestroyedAssists")
    private long vehiclesDestroyedAssists;
    @SerializedName("accuracy")
    private double accuracy;
    @SerializedName("longestHeadshot")
    private double longestHeadshot;
    @SerializedName("killStreakBonus")
    private int longestKillStreak;
    @SerializedName("elo")
    private double skill;
    @SerializedName("avengerKills")
    private long avengerKills;
    @SerializedName("saviorKills")
    private long saviorKills;
    @SerializedName("dogtagsTaken")
    private long dogtagsTaken;
    @SerializedName("flagrunner")
    private long flagRunner;

    @SerializedName("sc_squad")
    private long squadScoreBonus;
    @SerializedName("repairs")
    private long repairs;
    @SerializedName("revives")
    private long revives;
    @SerializedName("heals")
    private long heals;
    @SerializedName("resupplies")
    private long resupplies;
    @SerializedName("shotsFired")
    private long shotsFired;
    @SerializedName("nemesisStreak")
    private int highestNemesisStreak;
    @SerializedName("nemesisKills")
    private long nemesisKills;
    @SerializedName("suppressionAssists")
    private long suppresionsAssists;

    public PersonaStatsOverview(int rank, KitScores kitScores,
                                VehicleScores vehicleScores, long vehicleScore, long totalScore,
                                long score, long timePlayed, long gameWon, long gameLost,
                                double wnRatio, long awardScore, long unlockScore, long kills,
                                long deaths, double kdRatio, long killAssists, long scoreMin,
                                double quitPercentage, long mcomDefendKills, long mcomDestroyed,
                                long flagCaptures, long flagsDefended, long vehiclesDestroyed,
                                long vehiclesDestroyedAssists, double accuracy,
                                double longestHeadshot, int longestKillStreak, double skill,
                                long avengerKills, long saviorKills, long dogtagsTaken, long flagRunner,
                                long squadScoreBonus, long repairs, long revives, long heals,
                                long resupplies, long combatScore, long shotsFired, int highestNemesisStreak,
                                long nemesisKills, long suppresionsAssists) {
        this.rank = rank;
        this.kitScores = kitScores;
        this.vehicleScores = vehicleScores;
        this.vehicleScore = vehicleScore;
        this.totalScore = totalScore;
        this.score = score;
        this.timePlayed = timePlayed;
        this.gameWon = gameWon;
        this.gameLost = gameLost;
        this.wlRatio = wnRatio;
        this.awardScore = awardScore;
        this.unlockScore = unlockScore;
        this.kills = kills;
        this.deaths = deaths;
        this.kdRatio = kdRatio;
        this.killAssists = killAssists;
        this.scoreMin = scoreMin;
        this.quitPercentage = quitPercentage;
        this.mcomDefendKills = mcomDefendKills;
        this.mcomDestroyed = mcomDestroyed;
        this.flagCaptures = flagCaptures;
        this.flagsDefended = flagsDefended;
        this.vehiclesDestroyed = vehiclesDestroyed;
        this.vehiclesDestroyedAssists = vehiclesDestroyedAssists;
        this.accuracy = accuracy;
        this.longestHeadshot = longestHeadshot;
        this.longestKillStreak = longestKillStreak;
        this.skill = skill;
        this.avengerKills = avengerKills;
        this.saviorKills = saviorKills;
        this.dogtagsTaken = dogtagsTaken;
        this.flagRunner = flagRunner;
        this.squadScoreBonus = squadScoreBonus;
        this.repairs = repairs;
        this.revives = revives;
        this.heals = heals;
        this.resupplies = resupplies;
        this.combatScore = combatScore;
        this.shotsFired = shotsFired;
        this.highestNemesisStreak = highestNemesisStreak;
        this.nemesisKills = nemesisKills;
        this.suppresionsAssists = suppresionsAssists;
    }

    public int getRank() {
        return rank;
    }

    public KitScores getKitScores() {
        return kitScores;
    }

    public VehicleScores getVehicleScores() {
        return vehicleScores;
    }

    public long getVehicleScore() {
        return vehicleScore;
    }

    public long getTotalScore() {
        return totalScore;
    }

    public long getScore() {
        return score;
    }

    public long getTimePlayed() {
        return timePlayed;
    }

    public long getGameWon() {
        return gameWon;
    }

    public long getGameLost() {
        return gameLost;
    }

    public double getWlRatio() {
        return wlRatio;
    }

    public long getAwardScore() {
        return awardScore;
    }

    public long getUnlockScore() {
        return unlockScore;
    }

    public long getCombatScore() {
        return combatScore;
    }

    public long getKills() {
        return kills;
    }

    public long getDeaths() {
        return deaths;
    }

    public double getKdRatio() {
        return kdRatio;
    }

    public long getKillAssists() {
        return killAssists;
    }

    public double getScoreMin() {
        return scoreMin;
    }

    public double getQuitPercentage() {
        return quitPercentage;
    }

    public long getMcomDefendKills() {
        return mcomDefendKills;
    }

    public long getMcomDestroyed() {
        return mcomDestroyed;
    }

    public long getFlagCaptures() {
        return flagCaptures;
    }

    public long getFlagsDefended() {
        return flagsDefended;
    }

    public long getVehiclesDestroyed() {
        return vehiclesDestroyed;
    }

    public long getVehiclesDestroyedAssists() {
        return vehiclesDestroyedAssists;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public double getLongestHeadshot() {
        return longestHeadshot;
    }

    public int getLongestKillStreak() {
        return longestKillStreak;
    }

    public double getSkill() {
        return skill;
    }

    public long getAvengerKills() {
        return avengerKills;
    }

    public long getSaviorKills() {
        return saviorKills;
    }

    public long getDogtagsTaken() {
        return dogtagsTaken;
    }

    public long getFlagRunner() {
        return flagRunner;
    }

    public long getSquadScoreBonus() {
        return squadScoreBonus;
    }

    public long getRepairs() {
        return repairs;
    }

    public long getRevives() {
        return revives;
    }

    public long getHeals() {
        return heals;
    }

    public long getResupplies() {
        return resupplies;
    }

    public long getShotsFired() {
        return shotsFired;
    }

    public int getHighestNemesisStreak() {
        return highestNemesisStreak;
    }

    public long getNemesisKills() {
        return nemesisKills;
    }

    public long getSuppresionsAssists() {
        return suppresionsAssists;
    }
}
