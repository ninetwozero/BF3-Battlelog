package com.ninetwozero.bf3droid.datatype;

import java.util.Comparator;

public class SearchComparator implements Comparator<GeneralSearchResult> {

    public int compare(GeneralSearchResult o1, GeneralSearchResult o2) {

        return o1.getName().compareToIgnoreCase(o2.getName());

    }

}
