package com.ninetwozero.battlelog.jsonmodel;

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

	public PersonaStatsOverview(int rank, KitScores kitScores,
			VehicleScores vehicleScores, long vehicleScore, long totalScore,
			long score, long timePlayed, long gameWon, long gameLost,
			double wnRatio, long awardScore, long unlockScore, long kills,
			long deaths, double kdRatio, long killAssists, long scoreMin,
			double quitPercentage, long vehiclesDestroyed,
			long vehiclesDestroyedAssists, double accuracy,
			double longestHeadshot, int longestKillStreak, double skill,
			long squadScoreBonus, long repairs, long revives, long heals,
			long resupplies, long combatScore) {
		this.rank = rank;
		this.kitScores = kitScores;
		this.vehicleScores = vehicleScores;
		this.vehicleScore = vehicleScore;
		this.totalScore = totalScore;
		this.score = score;
		this.timePlayed = timePlayed;
		this.gameWon = gameWon;
		this.gameLost = gameLost;
		this.wlRatio = wlRatio;
		this.awardScore = awardScore;
		this.unlockScore = unlockScore;
		this.kills = kills;
		this.deaths = deaths;
		this.kdRatio = kdRatio;
		this.killAssists = killAssists;
		this.scoreMin = scoreMin;
		this.quitPercentage = quitPercentage;
		this.vehiclesDestroyed = vehiclesDestroyed;
		this.vehiclesDestroyedAssists = vehiclesDestroyedAssists;
		this.accuracy = accuracy;
		this.longestHeadshot = longestHeadshot;
		this.longestKillStreak = longestKillStreak;
		this.skill = skill;
		this.squadScoreBonus = squadScoreBonus;
		this.repairs = repairs;
		this.revives = revives;
		this.heals = heals;
		this.resupplies = resupplies;
		this.combatScore = combatScore;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public KitScores getKitScores() {
		return kitScores;
	}

	public void setKitScores(KitScores kitScores) {
		this.kitScores = kitScores;
	}

	public VehicleScores getVehicleScores() {
		return vehicleScores;
	}

	public void setVehicleScores(VehicleScores vehicleScores) {
		this.vehicleScores = vehicleScores;
	}

	public long getVehicleScore() {
		return vehicleScore;
	}

	public void setVehicleScore(long vehicleScore) {
		this.vehicleScore = vehicleScore;
	}

	public long getTotalScore() {
		return totalScore;
	}

	public void setTotalScore(long totalScore) {
		this.totalScore = totalScore;
	}

	public long getScore() {
		return score;
	}

	public void setScore(long score) {
		this.score = score;
	}

	public long getTimePlayed() {
		return timePlayed;
	}

	public void setTimePlayed(long timePlayed) {
		this.timePlayed = timePlayed;
	}

	public long getGameWon() {
		return gameWon;
	}

	public void setGameWon(long gameWon) {
		this.gameWon = gameWon;
	}

	public long getGameLost() {
		return gameLost;
	}

	public void setGameLost(long gameLost) {
		this.gameLost = gameLost;
	}

	public double getWlRatio() {
		return wlRatio;
	}

	public void setWlRatio(double wlRatio) {
		this.wlRatio = wlRatio;
	}

	public long getAwardScore() {
		return awardScore;
	}

	public void setAwardScore(long awardScore) {
		this.awardScore = awardScore;
	}

	public long getUnlockScore() {
		return unlockScore;
	}

	public void setUnlockScore(long unlockScore) {
		this.unlockScore = unlockScore;
	}

	public long getKills() {
		return kills;
	}

	public void setKills(long kills) {
		this.kills = kills;
	}

	public long getDeaths() {
		return deaths;
	}

	public void setDeaths(long deaths) {
		this.deaths = deaths;
	}

	public double getKdRatio() {
		return kdRatio;
	}

	public void setKdRatio(double kdRatio) {
		this.kdRatio = kdRatio;
	}

	public long getKillAssists() {
		return killAssists;
	}

	public void setKillAssists(long killAssists) {
		this.killAssists = killAssists;
	}

	public double getScoreMin() {
		return scoreMin;
	}

	public void setScoreMin(double scoreMin) {
		this.scoreMin = scoreMin;
	}

	public double getQuitPercentage() {
		return quitPercentage;
	}

	public void setQuitPercentage(double quitPercentage) {
		this.quitPercentage = quitPercentage;
	}

	public long getVehiclesDestroyed() {
		return vehiclesDestroyed;
	}

	public void setVehiclesDestroyed(long vehiclesDestroyed) {
		this.vehiclesDestroyed = vehiclesDestroyed;
	}

	public long getVehiclesDestroyedAssists() {
		return vehiclesDestroyedAssists;
	}

	public void setVehiclesDestroyedAssists(long vehiclesDestroyedAssists) {
		this.vehiclesDestroyedAssists = vehiclesDestroyedAssists;
	}

	public double getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(double accuracy) {
		this.accuracy = accuracy;
	}

	public double getLongestHeadshot() {
		return longestHeadshot;
	}

	public void setLongestHeadshot(double longestHeadshot) {
		this.longestHeadshot = longestHeadshot;
	}

	public int getLongestKillStreak() {
		return longestKillStreak;
	}

	public void setLongestKillStreak(int longestKillStreak) {
		this.longestKillStreak = longestKillStreak;
	}

	public double getSkill() {
		return skill;
	}

	public void setSkill(double skill) {
		this.skill = skill;
	}

	public long getSquadScoreBonus() {
		return squadScoreBonus;
	}

	public void setSquadScoreBonus(long squadScoreBonus) {
		this.squadScoreBonus = squadScoreBonus;
	}

	public long getRepairs() {
		return repairs;
	}

	public void setRepairs(long repairs) {
		this.repairs = repairs;
	}

	public long getRevives() {
		return revives;
	}

	public void setRevives(long revives) {
		this.revives = revives;
	}

	public long getHeals() {
		return heals;
	}

	public void setHeals(long heals) {
		this.heals = heals;
	}

	public long getResupplies() {
		return resupplies;
	}

	public void setResupplies(long resupplies) {
		this.resupplies = resupplies;
	}

	public long getCombatScore() {
		return combatScore;
	}

	public void setCombatScore(long combatScore) {
		this.combatScore = combatScore;
	}
}
