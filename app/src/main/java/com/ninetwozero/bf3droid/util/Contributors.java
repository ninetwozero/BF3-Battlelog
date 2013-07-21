package com.ninetwozero.bf3droid.util;

import com.ninetwozero.bf3droid.R;
import com.ninetwozero.bf3droid.datatype.AppContributorData;

import java.util.ArrayList;
import java.util.List;

public class Contributors {

    private Contributors(){}

    private static List<AppContributorData> APP_CONTRIBUTORS = new ArrayList<AppContributorData>(){{
        add(new AppContributorData(R.string.info_credits_code));
        add(new AppContributorData("Lukas Larson", "mailto:lukaslarson@gmail.com"));
        add(new AppContributorData("Martin Nuc", "http://www.nuc.cz/"));
        add(new AppContributorData("Peter Miklo\u0161ko", "https://github.com/peter-budo"));

        add(new AppContributorData(R.string.info_credits_graphics));
        add(new AppContributorData("Marcus Januszewski", ""));
        add(new AppContributorData("Stephen 'Dbagjones' Beaudet", "http://djonesradio.com"));

        add(new AppContributorData(R.string.info_credits_translations));
        add(new AppContributorData("Alexander Katsero", "http://vk.com/crybot"));
        add(new AppContributorData("Angelo Zangarini", ""));
        add(new AppContributorData("Arnaud Ligny", ""));
        add(new AppContributorData("artsiputsi", ""));
        add(new AppContributorData("bagione", ""));
        add(new AppContributorData("basr", ""));
        add(new AppContributorData("capalex", ""));
        add(new AppContributorData("Coval Delanight", ""));
        add(new AppContributorData("Cuprax", ""));
        add(new AppContributorData("cyrq", ""));
        add(new AppContributorData("DarkoKukovec", ""));
        add(new AppContributorData("federico", ""));
        add(new AppContributorData("fysme", ""));
        add(new AppContributorData("Judit Tur", ""));
        add(new AppContributorData("Klaus Thenmayer", ""));
        add(new AppContributorData("Mirella Lindmark", ""));
        add(new AppContributorData("MMario1989", "http://www.facebook.com/people/M%C3%A1ri%C3%B3-Morvai/100001683668440"));
        add(new AppContributorData("neurokirurgi", ""));
        add(new AppContributorData("pauldegroot", ""));
        add(new AppContributorData("pingus", ""));
        add(new AppContributorData("Ricket008", ""));
        add(new AppContributorData("sangr1aman", ""));
        add(new AppContributorData("waldzias", ""));
        add(new AppContributorData("zAo82", "mailto:sbkg0002@gmail.com"));
        add(new AppContributorData("zauriel", ""));
    }};

    public static List<AppContributorData> getContributors() {
        return APP_CONTRIBUTORS;
    }
}
