
package com.ninetwozero.battlelog.datatypes;

import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class Board {

    private Board() {
    }

    public static class Forum implements Parcelable {

        // Attributes
        private long forumId, categoryId, latestPostDate, latestPostThreadId,
                latestPostId;
        private long numPosts, numThreads, numPages;
        private String title, description;
        private String latestThreadTitle, latestPostUsername;
        private List<Board.ThreadData> threads;

        // Constructs
        public Forum(String fTitle, String fDescription, long nPosts,
                long nThreads, long nPages, List<Board.ThreadData> aThreads) {

            forumId = 0;
            categoryId = 0;
            latestPostDate = 0;
            latestPostThreadId = 0;
            latestPostId = 0;
            numPosts = nPosts;
            numThreads = nThreads;
            numPages = nPages;

            title = fTitle;
            description = fDescription;
            latestThreadTitle = null;
            latestPostUsername = null;

            threads = aThreads;

        }

        public Forum(

                long fId, long cId, long lpDate, long lpTId, long lpId, long nPosts,
                long nThreads, long nPages, String t, String d, String ltTitle,
                String lpUser

        ) {

            forumId = fId;
            categoryId = cId;
            latestPostDate = lpDate;
            latestPostThreadId = lpTId;
            latestPostId = lpId;
            numPosts = nPosts;
            numThreads = nThreads;
            numPages = nPages;

            title = t;
            description = d;
            latestThreadTitle = ltTitle;
            latestPostUsername = lpUser;

            threads = null;

        }

        public Forum(Parcel in) {

            forumId = in.readLong();
            categoryId = in.readLong();
            latestPostDate = in.readLong();
            latestPostThreadId = in.readLong();
            latestPostId = in.readLong();
            numPosts = in.readLong();
            numThreads = in.readLong();
            numPages = in.readLong();

            title = in.readString();
            description = in.readString();
            latestThreadTitle = in.readString();
            latestPostUsername = in.readString();

            in.readTypedList(threads, Board.ThreadData.CREATOR);

        }

        // Getters
        public long getForumId() {
            return forumId;
        }

        public long getCategoryId() {
            return categoryId;
        }

        public long getLatestPostDate() {
            return latestPostDate;
        }

        public long getLatestPostForumId() {
            return latestPostThreadId;
        }

        public long getLatestPostId() {
            return latestPostId;
        }

        public long getNumPosts() {
            return numPosts;
        }

        public long getNumThreads() {
            return numThreads;
        }

        public long getNumPages() {
            return numPages;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public String getLatestThreadTitle() {
            return latestThreadTitle;
        }

        public String getLatestPostUsername() {
            return latestPostUsername;
        }

        public List<Board.ThreadData> getThreads() {
            return threads;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {

            dest.writeLong(forumId);
            dest.writeLong(categoryId);
            dest.writeLong(latestPostDate);
            dest.writeLong(latestPostThreadId);
            dest.writeLong(latestPostId);
            dest.writeLong(numPosts);
            dest.writeLong(numThreads);
            dest.writeLong(numPages);

            dest.writeString(title);
            dest.writeString(description);
            dest.writeString(latestThreadTitle);
            dest.writeString(latestPostUsername);

            dest.writeTypedList(threads);

        }

        public static final Parcelable.Creator<Board.Forum> CREATOR = new Parcelable.Creator<Board.Forum>() {

            public Board.Forum createFromParcel(Parcel in) {
                return new Board.Forum(in);
            }

            public Board.Forum[] newArray(int size) {
                return new Board.Forum[size];
            }

        };

    }

    public static class ThreadData implements Parcelable {

        // Attributes
        private long threadId, date, lastPostDate;
        private int numOfficialPosts, numPosts, numCurrentPage, numTotalPages;
        private String title;
        private ProfileData owner, lastPoster;
        private boolean sticky, locked;
        private boolean censorPosts, deletePosts, editPosts, admin,
                postOfficial, viewLatestPosts, viewPostHistory;
        private List<Board.PostData> posts;

        // Construct
        public ThreadData(String t) {
            title = t;
        }

        public ThreadData(Parcel in) {

            threadId = in.readLong();
            date = in.readLong();
            lastPostDate = in.readLong();

            numOfficialPosts = in.readInt();
            numPosts = in.readInt();
            numCurrentPage = in.readInt();
            numTotalPages = in.readInt();

            title = in.readString();

            owner = in.readParcelable(ProfileData.class.getClassLoader());
            lastPoster = in.readParcelable(ProfileData.class
                    .getClassLoader());

            sticky = (in.readInt() == 1);
            locked = (in.readInt() == 1);

            censorPosts = (in.readInt() == 1);
            deletePosts = (in.readInt() == 1);
            editPosts = (in.readInt() == 1);
            admin = (in.readInt() == 1);
            ;
            postOfficial = (in.readInt() == 1);
            viewLatestPosts = (in.readInt() == 1);
            viewPostHistory = (in.readInt() == 1);

            in.readTypedList(posts, Board.PostData.CREATOR);

        }

        public ThreadData(

                long tId, long tDate, long lpDate, int nOffPosts, int nPosts, String t,
                ProfileData o, ProfileData lp, boolean st, boolean lo

        ) {

            threadId = tId;
            date = tDate;
            lastPostDate = lpDate;

            numOfficialPosts = nOffPosts;
            numPosts = nPosts;
            numCurrentPage = 0;
            numTotalPages = 0;

            title = t;

            owner = o;
            lastPoster = lp;

            sticky = st;
            locked = lo;

            censorPosts = false;
            deletePosts = false;
            editPosts = false;
            admin = false;
            postOfficial = false;
            viewLatestPosts = false;
            viewPostHistory = false;

            posts = null;

        }

        public ThreadData(

                long tId, long tDate, long lpDate, int nOffPosts, int nPosts,
                int nCurrPage, int nPages, String t, ProfileData o,
                ProfileData lp, boolean st, boolean lo, boolean cePosts,
                boolean ccPosts, boolean cdPosts, boolean cpOfficial,
                boolean cvlPosts, boolean cvpHistory, boolean ad,
                List<Board.PostData> aPosts

        ) {

            threadId = tId;
            date = tDate;
            lastPostDate = lpDate;

            numOfficialPosts = nOffPosts;
            numPosts = nPosts;
            numCurrentPage = nCurrPage;
            numTotalPages = nPages;

            title = t;

            owner = o;
            lastPoster = lp;

            sticky = st;
            locked = lo;

            censorPosts = ccPosts;
            deletePosts = cdPosts;
            editPosts = cePosts;
            admin = ad;
            postOfficial = cpOfficial;
            viewLatestPosts = cvlPosts;
            viewPostHistory = cvpHistory;

            posts = aPosts;

        }

        // Getters
        public long getThreadId() {
            return threadId;
        }

        public long getDate() {
            return date;
        }

        public long getLastPostDate() {
            return lastPostDate;
        }

        public int getNumOfficialPosts() {
            return numOfficialPosts;
        }

        public int getNumPosts() {
            return numPosts + numOfficialPosts;
        }

        public int getNumCurrentPage() {
            return numCurrentPage;
        }

        public int getNumPages() {
            return numTotalPages;
        }

        public String getTitle() {
            return title;
        }

        public ProfileData getOwner() {
            return owner;
        }

        public ProfileData getLastPoster() {
            return lastPoster;
        }

        public boolean isSticky() {
            return sticky;
        }

        public boolean isLocked() {
            return locked;
        }

        public boolean hasOfficialResponse() {
            return (numOfficialPosts > 0);
        }

        public boolean canEditPosts() {
            return editPosts;
        }

        public boolean canCensorPosts() {
            return censorPosts;
        }

        public boolean canDeletePosts() {
            return deletePosts;
        }

        public boolean canPostOfficial() {
            return postOfficial;
        }

        public boolean canViewLatestPosts() {
            return viewLatestPosts;
        }

        public boolean canViewPostHistory() {
            return viewPostHistory;
        }

        public boolean isAdmin() {
            return admin;
        }

        public List<Board.PostData> getPosts() {
            return posts;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {

            dest.writeLong(threadId);
            dest.writeLong(date);
            dest.writeLong(lastPostDate);

            dest.writeInt(numOfficialPosts);
            dest.writeInt(numPosts);
            dest.writeInt(numCurrentPage);
            dest.writeInt(numTotalPages);

            dest.writeString(title);

            dest.writeParcelable(owner, flags);
            dest.writeParcelable(lastPoster, flags);

            dest.writeInt(sticky ? 1 : 0);
            dest.writeInt(locked ? 1 : 0);

            dest.writeInt(censorPosts ? 1 : 0);
            dest.writeInt(deletePosts ? 1 : 0);
            dest.writeInt(editPosts ? 1 : 0);
            dest.writeInt(admin ? 1 : 0);
            dest.writeInt(postOfficial ? 1 : 0);
            dest.writeInt(viewLatestPosts ? 1 : 0);
            dest.writeInt(viewPostHistory ? 1 : 0);

            dest.writeTypedList(posts);

        }

        public static final Parcelable.Creator<Board.ThreadData> CREATOR = new Parcelable.Creator<Board.ThreadData>() {

            public Board.ThreadData createFromParcel(Parcel in) {
                return new Board.ThreadData(in);
            }

            public Board.ThreadData[] newArray(int size) {
                return new Board.ThreadData[size];
            }

        };

        @Override
        public String toString() {
            return owner + ":" + title;
        }

    }

    public static class PostData implements Parcelable {

        // Attributes
        private long postId, date, threadId;
        private ProfileData profileData;
        private String content;
        private int numReports;
        private boolean censored, official;

        // Construct
        public PostData(

                long pId, long d, long thId, ProfileData p, String c, int cReports,
                boolean iCensored, boolean iOfficial

        ) {

            postId = pId;
            date = d;
            threadId = thId;

            profileData = p;

            content = c;

            numReports = cReports;

            censored = iCensored;
            official = iOfficial;

        }

        public PostData(Parcel in) {

            postId = in.readLong();
            date = in.readLong();
            threadId = in.readLong();

            profileData = in.readParcelable(ProfileData.class
                    .getClassLoader());

            content = in.readString();

            numReports = in.readInt();

            censored = (in.readInt() == 1);
            official = (in.readInt() == 1);

        }

        // Getters
        public long getPostId() {
            return postId;
        }

        public long getDate() {
            return date;
        }

        public long getThreadId() {
            return threadId;
        }

        public ProfileData getProfileData() {
            return profileData;
        }

        public String getContent() {
            return content;
        }

        public int getNumReports() {
            return numReports;
        }

        public boolean isCensored() {
            return censored;
        }

        public boolean isOfficial() {
            return official;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {

            dest.writeLong(postId);
            dest.writeLong(date);
            dest.writeLong(threadId);

            dest.writeParcelable(profileData, flags);

            dest.writeString(content);

            dest.writeInt(numReports);

            dest.writeInt(censored ? 1 : 0);
            dest.writeInt(official ? 1 : 0);

        }

        public static final Parcelable.Creator<Board.PostData> CREATOR = new Parcelable.Creator<Board.PostData>() {

            public Board.PostData createFromParcel(Parcel in) {
                return new Board.PostData(in);
            }

            public Board.PostData[] newArray(int size) {
                return new Board.PostData[size];
            }

        };

        @Override
        public String toString() {
            return profileData.getUsername() + ":" + content;
        }

    }

    public static class SearchResult implements Parcelable {

        // Attributes
        private long threadId, date;
        private String title;
        private ProfileData owner;
        private boolean sticky, official;

        // Construct
        public SearchResult(String t) {
            title = t;
        }

        public SearchResult(Parcel in) {

            threadId = in.readLong();
            date = in.readLong();

            title = in.readString();

            owner = in.readParcelable(ProfileData.class.getClassLoader());

            sticky = (in.readInt() == 1);
            official = (in.readInt() == 1);

        }

        public SearchResult(

                long tId, long tDate, String t, ProfileData o, boolean st, boolean of

        ) {

            threadId = tId;
            date = tDate;

            title = t;

            owner = o;

            sticky = st;
            official = of;

        }

        // Getters
        public long getThreadId() {
            return threadId;
        }

        public long getDate() {
            return date;
        }

        public String getTitle() {
            return title;
        }

        public ProfileData getOwner() {
            return owner;
        }

        public boolean isSticky() {
            return sticky;
        }

        public boolean isOfficial() {
            return official;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {

            dest.writeLong(threadId);
            dest.writeLong(date);

            dest.writeString(title);

            dest.writeParcelable(owner, flags);

            dest.writeInt(sticky ? 1 : 0);
            dest.writeInt(official ? 1 : 0);

        }

        public static final Parcelable.Creator<Board.SearchResult> CREATOR = new Parcelable.Creator<Board.SearchResult>() {

            public Board.SearchResult createFromParcel(Parcel in) {
                return new Board.SearchResult(in);
            }

            public Board.SearchResult[] newArray(int size) {
                return new Board.SearchResult[size];
            }

        };

        @Override
        public String toString() {
            return owner + ":" + title;
        }

    }

}
