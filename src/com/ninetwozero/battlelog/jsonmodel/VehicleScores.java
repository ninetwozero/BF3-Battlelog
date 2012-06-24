package com.ninetwozero.battlelog.jsonmodel;

import com.google.gson.annotations.SerializedName;

public class VehicleScores {

    @SerializedName("1") private long jetScore;
    @SerializedName("2") private long tankScore;
    @SerializedName("4") private long ifvScore;
    @SerializedName("8") private long antiAirScore;
    @SerializedName("16") private long attackHeliScore;
    @SerializedName("32") private long scoutHeliScore;

    public VehicleScores(long jetScore, long tankScore, long ifvScore, long antiAirScore, long attackHeliScore, long scoutHeliScore) {
        this.jetScore = jetScore;
        this.tankScore = tankScore;
        this.ifvScore = ifvScore;
        this.antiAirScore = antiAirScore;
        this.attackHeliScore = attackHeliScore;
        this.scoutHeliScore = scoutHeliScore;
    }

    public long getJetScore() {
        return jetScore;
    }

    public void setJetScore(long jetScore) {
        this.jetScore = jetScore;
    }

    public long getTankScore() {
        return tankScore;
    }

    public void setTankScore(long tankScore) {
        this.tankScore = tankScore;
    }

    public long getIfvScore() {
        return ifvScore;
    }

    public void setIfvScore(long ifvScore) {
        this.ifvScore = ifvScore;
    }

    public long getAntiAirScore() {
        return antiAirScore;
    }

    public void setAntiAirScore(long antiAirScore) {
        this.antiAirScore = antiAirScore;
    }

    public long getAttackHeliScore() {
        return attackHeliScore;
    }

    public void setAttackHeliScore(long attackHeliScore) {
        this.attackHeliScore = attackHeliScore;
    }

    public long getScoutHeliScore() {
        return scoutHeliScore;
    }

    public void setScoutHeliScore(long scoutHeliScore) {
        this.scoutHeliScore = scoutHeliScore;
    }

    @Override
    public String toString() {
        return "VehicleScores{" +
                "jetScore=" + jetScore +
                ", tankScore=" + tankScore +
                ", ifvScore=" + ifvScore +
                ", antiAirScore=" + antiAirScore +
                ", attackHeliScore=" + attackHeliScore +
                ", scoutHeliScore=" + scoutHeliScore +
                '}';
    }
}
