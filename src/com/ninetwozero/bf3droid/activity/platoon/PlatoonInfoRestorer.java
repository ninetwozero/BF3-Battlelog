package com.ninetwozero.bf3droid.activity.platoon;

import com.ninetwozero.bf3droid.jsonmodel.platoon.PlatoonDossier;
import com.ninetwozero.bf3droid.service.Restorer;

public class PlatoonInfoRestorer extends Restorer<PlatoonDossier> {

    @Override
    public PlatoonDossier fetch() {
        return null;
    }

    @Override
    public void save(PlatoonDossier platoons) {
    }

    /*public ArrayList<PlatoonData> getPlatoons(final Context context) throws WebsiteHandlerException {
        List<PlatoonData> platoons = new ArrayList<PlatoonData>();
        try {
            String httpContent = mRequestHandler.get(
                RequestHandler.generateUrl(ProfileClient.URL_INFO, mProfileData.getUsername()),
                RequestHandler.HEADER_AJAX
            );
            if ("".equals(httpContent)) {
                return null;
            } else {
                JSONArray platoonArray = new JSONObject(httpContent)
                    .getJSONObject("context")
                    .getJSONObject("profileCommon")
                    .getJSONArray("platoons");
                if (platoonArray != null && platoonArray.length() > 0) {
                    for (int i = 0, max = platoonArray.length(); i < max; i++) {
                        JSONObject currItem = platoonArray.getJSONObject(i);

                        String title = currItem.getString("id") + ".jpeg";
                        if (!CacheHandler.isCached(context, title)) {
                            PlatoonClient.cacheBadge(
                                context,
                                currItem.getString("badgePath"),
                                title,
                                Constants.DEFAULT_BADGE_SIZE
                            );
                        }
                        platoons.add(
                            new PlatoonData(
                                Long.parseLong(currItem.getString("id")),
                                currItem.getString("name"),
                                currItem.getString("tag"),
                                currItem.getInt("platform"),
                                currItem.getInt("fanCounter"),
                                currItem.getInt("memberCounter"),
                                !currItem.getBoolean("hidden")
                            )
                        );
                    }
                }
                return (ArrayList<PlatoonData>) platoons;
            }
        } catch (Exception ex) {
            throw new WebsiteHandlerException(ex.getMessage());
        }
    }*/
}
