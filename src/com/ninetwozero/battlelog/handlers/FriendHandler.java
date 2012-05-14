package com.ninetwozero.battlelog.handlers;

import com.ninetwozero.battlelog.datatypes.PostData;
import com.ninetwozero.battlelog.datatypes.WebsiteHandlerException;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.RequestHandler;

public class FriendHandler {



    public static boolean sendFriendRequest(long profileId, String checksum)
            throws WebsiteHandlerException {

        try {

            // Let's login everybody!
            RequestHandler wh = new RequestHandler();
            String httpContent = wh.post(

                    Constants.URL_FRIEND_REQUESTS.replace(

                            "{UID}", profileId + ""

                            ), new PostData[] {

                        new PostData(

                                Constants.FIELD_NAMES_CHECKSUM[0], checksum

                        )
                    }, 1

                    );

            // Did we manage?
            if (httpContent != null) {

                return true;

            } else {

                return false;

            }

        } catch (Exception ex) {

            ex.printStackTrace();
            throw new WebsiteHandlerException(ex.getMessage());

        }

    }

    public static boolean removeFriend(long profileId)
            throws WebsiteHandlerException {

        try {

            // Let's login everybody!
            RequestHandler wh = new RequestHandler();
            String httpContent = wh.get(

                    Constants.URL_FRIEND_DELETE.replace("{UID}", profileId + ""), 1

                    );

            // Did we manage?
            if (httpContent != null) {

                return true;

            } else {

                return false;

            }

        } catch (Exception ex) {

            ex.printStackTrace();
            throw new WebsiteHandlerException(ex.getMessage());

        }

    }
}
