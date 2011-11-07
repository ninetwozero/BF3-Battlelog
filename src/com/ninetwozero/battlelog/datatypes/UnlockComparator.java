package com.ninetwozero.battlelog.datatypes;

import java.util.Comparator;

public class UnlockComparator implements Comparator<UnlockData> {
		 
    public int compare(UnlockData o1, UnlockData o2) {
        
    	//Grab the data
    	UnlockData p1 = (UnlockData) o1;
    	UnlockData p2 = (UnlockData) o2;
        
    	//Return!
    	if( p1.getUnlockPercentage() < p2.getUnlockPercentage() ) return 1;
    	if( p1.getUnlockPercentage() > p2.getUnlockPercentage() ) return -1;
    	else return 0;
    
    }				 

}