package com.ninetwozero.battlelog.datatype;

import java.util.Comparator;

public class TopStatsComparator implements Comparator<PlatoonTopStatsItem> {

	public int compare(PlatoonTopStatsItem p1, PlatoonTopStatsItem p2) {

		// Return!
		if (p1.getLabel().equals("TOP")) {

			return -1;

		} else {

			return p1.getLabel().compareToIgnoreCase(p2.getLabel());

		}

	}

}
