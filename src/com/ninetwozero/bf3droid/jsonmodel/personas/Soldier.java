/*
    This file is part of BF3 Droid

    BF3 Droid is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    BF3 Droid is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
 */

package com.ninetwozero.bf3droid.jsonmodel.personas;

import com.google.gson.annotations.SerializedName;

public class Soldier {

	@SerializedName("timePlayed")
	private int timePlayed;
    @SerializedName("persona")
    private Persona persona;
	@SerializedName("kills")
	private int kills;
	@SerializedName("deaths")
	private int deaths;
    @SerializedName("rank")
    private int rank;
	@SerializedName("numWins")
	private int wins;
	@SerializedName("numLosses")
	private int losses;
	@SerializedName("score")
	private int score;
    @SerializedName("game")
    private int game;

    public Soldier(int timePlayed, Persona persona, int kills, int deaths, int rank, int wins, int losses, int score, int game) {
        this.timePlayed = timePlayed;
        this.persona = persona;
        this.kills = kills;
        this.deaths = deaths;
        this.rank = rank;
        this.wins = wins;
        this.losses = losses;
        this.score = score;
        this.game = game;
    }

    public int getTimePlayed() {
        return timePlayed;
    }

    public Persona getPersona() {
        return persona;
    }

    public int getKills() {
        return kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public int getRank() {
        return rank;
    }

    public int getWins() {
        return wins;
    }

    public int getLosses() {
        return losses;
    }

    public int getScore() {
        return score;
    }

    public int getGame() {
        return game;
    }
}
