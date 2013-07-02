package com.ninetwozero.bf3droid.jsonmodel.platoon;

import java.util.Map;

public class PlatoonScore {

    private Map<String, Long> platoonScore;

    public PlatoonScore(Map<String, Long> platoonScore) {
        this.platoonScore = platoonScore;
    }

    public Map<String, Long> getPlatoonScore() {
        return platoonScore;
    }
}
