
package com.ninetwozero.battlelog.datatype;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.misc.Constants;

public class NotificationData implements Parcelable {

    // Attributes
    private long itemId, ownerId, commenterId, date;
    private int typeId;
    private String owner, commenter, type, extra;

    // Constructs
    public NotificationData(

            long iId, long oId, long cId, long d, int tId, String oName, String cName,
            String t, String x

    ) {

        itemId = iId;
        ownerId = oId;
        commenterId = cId;
        date = d;
        typeId = tId;
        owner = oName;
        commenter = cName;
        type = t;
        extra = x;

    }

    public NotificationData(Parcel in) {

        itemId = in.readLong();
        ownerId = in.readLong();
        commenterId = in.readLong();
        date = in.readLong();
        typeId = in.readInt();
        owner = in.readString();
        commenter = in.readString();
        type = in.readString();
        extra = in.readString();

    }

    // Getters
    public long getItemId() {
        return itemId;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public long getCommenterId() {
        return commenterId;
    }

    public long getDate() {
        return date;
    }

    public int getTypeId() {
        return typeId;
    }

    public String getOwner() {
        return owner;
    }

    public String getCommenter() {
        return commenter;
    }

    public String getType() {
        return type;
    }

    public String getExtra() {
        return extra;
    }

    // Generate a message
    public String getMessage(final Context c, final long userId) {

        // What have we here?
        String message;
        if ("feedcomment".equals(type) ) {

            if (0 == ownerId || userId == ownerId) {

                message = c.getString(R.string.info_feed_comment_own).replace(

                        "{action}", resolveActionFromId(c, typeId)

                        );

            } else {

                if (owner.endsWith("s") ) {

                    message = c.getString(R.string.info_feed_comment_other)
                            .replace(

                                    "{action}", resolveActionFromId(c, typeId)

                            );

                } else {

                    message = c.getString(R.string.info_feed_comment_other_v2)
                            .replace(

                                    "{action}", resolveActionFromId(c, typeId)

                            );

                }

            }

        } else if ("feedcommentreply".equals(type) ) {

            if (0 == ownerId || userId == ownerId) {

                message = c.getString(R.string.info_feed_comment_own_2)
                        .replace(

                                "{action}", resolveActionFromId(c, typeId)

                        );

            } else {

                if (owner.endsWith("s") ) {

                    message = c.getString(R.string.info_feed_comment_other_2)
                            .replace(

                                    "{action}", resolveActionFromId(c, typeId)

                            );

                } else {

                    message = c
                            .getString(R.string.info_feed_comment_other_v2_2)
                            .replace(

                                    "{action}", resolveActionFromId(c, typeId)

                            );

                }

            }

        } else if ("wallpostcreated".equals(type) ) {

            message = c.getString(R.string.info_feed_post_own);

        } else if ("feedlike".equals(type) ) {

            if (0 == ownerId || userId == ownerId) {

                message = c.getString(R.string.info_feed_hooah_own).replace(

                        "{action}", resolveActionFromId(c, typeId)

                        );

            } else {

                if (owner.endsWith("s") ) {

                    message = c.getString(R.string.info_feed_hooah_other)
                            .replace(

                                    "{action}", resolveActionFromId(c, typeId)

                            );

                } else {

                    message = c.getString(R.string.info_feed_hooah_other_2)
                            .replace(

                                    "{action}", resolveActionFromId(c, typeId)

                            );

                }

            }

        } else if ("platoonjoinrequestaccepted".equals(type) ) {

            message = c.getString(R.string.info_platoon_join_ok).replace(

                    "{platoon}", extra

                    );

        } else if ("platoonjoinrequestdeclined".equals(type) ) {

            message = c.getString(R.string.info_platoon_join_no).replace(

                    "{platoon}", extra

                    );

        } else if ("platooninviterequest".equals(type) ) {

            message = c.getString(R.string.info_platoon_invite).replace(

                    "{platoon}", extra

                    );

        } else if ("platoonpromoted".equals(type) ) {

            message = c.getString(R.string.info_platoon_promote).replace(

                    "{platoon}", extra

                    );

        } else if ("platoonkicked".equals(type) ) {

            message = c.getString(R.string.info_platoon_kick).replace(

                    "{platoon}", extra

                    );
        } else if ("platoondemoted".equals(type) ) {

            message = c.getString(R.string.info_platoon_demote).replace(

                    "{platoon}", extra

                    );

        } else if ("platoonjoinrequest".equals(type) ) {

            message = c.getString(R.string.info_platoon_join_request).replace(

                    "{platoon}", extra

                    );

        } else if ("platoonmemberjoined".equals(type) ) {

            message = c.getString(R.string.info_platoon_join_request_ok)
                    .replace(

                            "{platoon}", extra

                    );

        } else if ("platoonleft".equals(type) ) {

            message = c.getString(R.string.info_platoon_left).replace(

                    "{platoon}", extra

                    );

        } else if ("friendrequestaccepted".equals(type) ) {

            message = c.getString(R.string.info_feed_friend_accept);

        } else {

            message = c.getString(R.string.info_unknown_notification);
            Log.d(Constants.DEBUG_TAG, "noticationType => " + type);
        }

        return message.replace("{username}", commenter).replace("{owner}",
                owner);

    }

    // Misc
    public String resolveActionFromId(Context c, int id) {

        switch (typeId) {

            case 1: // Friendship
                return c.getString(R.string.info_friendship);

            case 2: // Forum post
                return c.getString(R.string.info_forum_post);

            case 3: // Blog post
                return c.getString(R.string.info_blog_comment);

            case 4: // Game report
                return c.getString(R.string.info_game_report);

            case 6: // Wall post
                return c.getString(R.string.info_wallpost);

            case 7: // Status message
                return c.getString(R.string.info_status_message);

            case 8: // Award
                return c.getString(R.string.info_award);

            case 9: // Battle report?
                return c.getString(R.string.info_battle_report);

            case 11: // Rank
                return c.getString(R.string.info_new_rank);

            case 13: // Platoon activity
            case 15:
            case 16: // Platoon activity
            case 17: // New platoon
                return c.getString(R.string.info_platoon_activity);

            case 20: // Platoon wall post
                return c.getString(R.string.info_platoon_wall_post);

            case 21: // Assignment completed
                return c.getString(R.string.info_assignment_completed);

            case 22: // Got game
                return c.getString(R.string.info_p_activated_expansion);

            default:
                Log.d(Constants.DEBUG_TAG, "type => " + type + ":" + typeId);
                return "{unknown action}";
        }

    }

    // TO STRING
    @Override
    public String toString() {

        return (

        "(" + itemId + ")" + " " + commenter + "(" + commenterId
                + ") <" + type + "> " + owner + "(" + ownerId + ")"

        );
    }

    @Override
    public int describeContents() {

        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeLong(itemId);
        dest.writeLong(ownerId);
        dest.writeLong(commenterId);
        dest.writeLong(date);
        dest.writeInt(typeId);
        dest.writeString(owner);
        dest.writeString(commenter);
        dest.writeString(type);
        dest.writeString(extra);

    }

    public static final Parcelable.Creator<NotificationData> CREATOR = new Parcelable.Creator<NotificationData>() {

        public NotificationData createFromParcel(Parcel in) {
            return new NotificationData(in);
        }

        public NotificationData[] newArray(int size) {
            return new NotificationData[size];
        }

    };

}
