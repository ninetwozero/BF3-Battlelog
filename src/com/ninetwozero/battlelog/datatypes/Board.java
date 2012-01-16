package com.ninetwozero.battlelog.datatypes;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;
import com.ninetwozero.battlelog.R;


public class Board {

	private Board() {}
	
	public static class Forum implements Parcelable {
		
		//Attributes
		private long forumId, categoryId, latestPostDate, latestPostThreadId, latestPostId;
		private long numPosts, numThreads;
		private String title, description;
		private String latestThreadTitle, latestPostUsername;
		private ArrayList<Board.ThreadData> threads;
		
		//Constructs
		public Forum( String fTitle, String fDescription, long nPosts, long nThreads, ArrayList<Board.ThreadData> aThreads ) {
			
			this.forumId = 0;
			this.categoryId = 0;
			this.latestPostDate = 0;
			this.latestPostThreadId = 0;
			this.latestPostId = 0;
			this.numPosts = nPosts;
			this.numThreads = nThreads;
			
			this.title = fTitle;
			this.description = fDescription;
			this.latestThreadTitle = null;
			this.latestPostUsername = null;
			
			this.threads = aThreads;
			
		}
				
		public Forum(
		
			long fId, long cId, long lpDate, long lpTId, long lpId, long nPosts, long nThreads,
			String t, String d, String ltTitle, String lpUser
				
		) {
			
			this.forumId = fId;
			this.categoryId = cId;
			this.latestPostDate = lpDate;
			this.latestPostThreadId = lpTId;
			this.latestPostId = lpId;
			this.numPosts = nPosts;
			this.numThreads = nThreads;
			
			this.title = t;
			this.description = d;
			this.latestThreadTitle = ltTitle;
			this.latestPostUsername = lpUser;
			
			this.threads = null;
			
		}
		
		public Forum( Parcel in ) {
			
			this.forumId = in.readLong();
			this.categoryId = in.readLong();
			this.latestPostDate = in.readLong();
			this.latestPostThreadId = in.readLong();
			this.latestPostId = in.readLong();
			this.numPosts = in.readLong();
			this.numThreads = in.readLong();
			
			this.title = in.readString();
			this.description = in.readString();
			this.latestThreadTitle = in.readString();
			this.latestPostUsername = in.readString();
			
			in.readTypedList(threads, Board.ThreadData.CREATOR);
			
		}
		
		//Getters
		public long getForumId() { return this.forumId; }
		public long getCategoryId() { return this.categoryId; }
		public long getLatestPostDate() { return this.latestPostDate; }
		public long getLatestPostForumId() { return this.latestPostThreadId; }
		public long getLatestPostId() { return this.latestPostId; }
		public long getNumPosts() { return this.numPosts; }
		public long getNumThreads() { return this.numThreads; }
		
		public String getTitle() { return this.title; }
		public String getDescription() { return this.description; }
		public String getLatestThreadTitle() { return this.latestThreadTitle; }
		public String getLatestPostUsername() { return this.latestPostUsername; }
		
		public ArrayList<Board.ThreadData> getThreads() { return this.threads; }

		@Override
		public int describeContents() { return 0; }

		@Override
		public void writeToParcel( Parcel dest, int flags ) {

			dest.writeLong( this.forumId );
			dest.writeLong( this.categoryId );
			dest.writeLong( this.latestPostDate );
			dest.writeLong( this.latestPostThreadId );
			dest.writeLong( this.latestPostId );
			dest.writeLong( this.numPosts );
			dest.writeLong( this.numThreads );
	
			dest.writeString( this.title );
			dest.writeString( this.description );
			dest.writeString( this.latestThreadTitle );
			dest.writeString( this.latestPostUsername );
			
			dest.writeTypedList(threads);
			
		}
		
		public static final Parcelable.Creator<Board.Forum> CREATOR = new Parcelable.Creator<Board.Forum>() {
			
			public Board.Forum createFromParcel(Parcel in) { return new Board.Forum(in); }
	        public Board.Forum[] newArray(int size) { return new Board.Forum[size]; }
		
		};
		
	}
	
	public static class ThreadData implements Parcelable {
		
		//Attributes
		private long threadId, date, lastPostDate, lastPostUserId, ownerId;
		private int numOfficialPosts, numPosts, numCurrentPage, numTotalPages;
		private String title, lastPostUsername, owner;
		private boolean sticky, locked;
		private boolean censorPosts, deletePosts, editPosts, admin, postOfficial, viewLatestPosts, viewPostHistory;
		private ArrayList<Board.PostData> posts;
		
		//Construct
		public ThreadData( String t ) { this.title = t; }
		public ThreadData( Parcel in ) { 
			
			this.threadId = in.readLong();
			this.date = in.readLong();
			this.lastPostDate = in.readLong();
			this.lastPostUserId = in.readLong();
			this.ownerId = in.readLong();
			
			this.numOfficialPosts = in.readInt();
			this.numPosts = in.readInt();
			this.numCurrentPage = in.readInt();
			this.numTotalPages = in.readInt();
			
			this.title = in.readString();
			this.lastPostUsername = in.readString();
			this.owner = in.readString();
		
			this.sticky = ( in.readInt() == 1 );
			this.locked = ( in.readInt() == 1 );
			
			this.censorPosts = ( in.readInt() == 1 );
			this.deletePosts = ( in.readInt() == 1 );
			this.editPosts = ( in.readInt() == 1 );
			this.admin = ( in.readInt() == 1 );;
			this.postOfficial = ( in.readInt() == 1 );
			this.viewLatestPosts = ( in.readInt() == 1 );
			this.viewPostHistory = ( in.readInt() == 1 );
			
			in.readTypedList( posts, Board.PostData.CREATOR );
			
		}
		
		public ThreadData(
		
			long tId, long tDate, long lpDate, long lpUId, long oId,
			int nOffPosts, int nPosts,
			String t, String lpUser, String oName,
			boolean st, boolean lo
		
		) {
				
			this.threadId = tId;
			this.date = tDate;
			this.lastPostDate = lpDate;
			this.lastPostUserId = lpUId;
			this.ownerId = oId;
			
			this.numOfficialPosts = nOffPosts;
			this.numPosts = nPosts;
			this.numCurrentPage = 0;
			this.numTotalPages = 0;
			
			this.title = t;
			this.lastPostUsername = lpUser;
			this.owner = oName;
		
			this.sticky = st;
			this.locked = lo;
			
			this.censorPosts = false;
			this.deletePosts = false;
			this.editPosts = false;
			this.admin = false;
			this.postOfficial = false;
			this.viewLatestPosts = false;
			this.viewPostHistory = false;
			
			this.posts = null;
			
		}
		
		public ThreadData(
				
			long tId, long tDate, long lpDate, long lpUId, long oId,
			int nOffPosts, int nPosts, int nCurrPage, int nPages,
			String t, String lpUser, String oName,
			boolean st, boolean lo,
			boolean cePosts, boolean ccPosts, boolean cdPosts, boolean cpOfficial, 
			boolean cvlPosts, boolean cvpHistory, boolean ad, 
			ArrayList<Board.PostData> aPosts
			
		) {
			
			this.threadId = tId;
			this.date = tDate;
			this.lastPostDate = lpDate;
			this.lastPostUserId = lpUId;
			this.ownerId = oId;
			
			this.numOfficialPosts = nOffPosts;
			this.numPosts = nPosts;
			this.numCurrentPage = nCurrPage;
			this.numTotalPages = nPages;
			
			this.title = t;
			this.lastPostUsername = lpUser;
			this.owner = oName;
		
			this.sticky = st;
			this.locked = lo;
			
			this.censorPosts = ccPosts;
			this.deletePosts = cdPosts;
			this.editPosts = cePosts;
			this.admin = ad;
			this.postOfficial = cpOfficial;
			this.viewLatestPosts = cvlPosts;
			this.viewPostHistory = cvpHistory;
			
			this.posts = aPosts;
			
		}

		//Getters
		public long getThreadId() { return this.threadId; }
		public long getDate() { return this.date; }
		public long getLastPostDate() { return this.lastPostDate; }
		public long getLastPostUserId() { return this.lastPostUserId; }
		public long getOwnerId() { return this.ownerId; }
		
		public int getNumOfficialPosts() { return this.numOfficialPosts; }
		public int getNumPosts() { return this.numPosts + this.numOfficialPosts; }

		public String getTitle() { return this.title; }
		public String getLastPostUsername() { return this.lastPostUsername; }
		public String getOwner() { return this.owner; }
	
		public boolean isSticky() { return this.sticky; }
		public boolean isLocked() { return this.locked; }
		public boolean hasOfficialResponse() { return (this.numOfficialPosts > 0); }
		
		public boolean canEditPosts() { return this.editPosts; }
		public boolean canCensorPosts() { return this.censorPosts; }
		public boolean canDeletePosts() { return this.deletePosts; }
		public boolean canPostOfficial() { return this.postOfficial; }
		public boolean canViewLatestPosts() { return this.viewLatestPosts; }
		public boolean canViewPostHistory() { return this.viewPostHistory; }
		public boolean isAdmin() { return this.admin; }
		
		public ArrayList<Board.PostData> getPosts() { return this.posts; }
		

		@Override
		public int describeContents() { return 0; }

		@Override
		public void writeToParcel( Parcel dest, int flags ) {

			dest.writeLong( this.threadId );
			dest.writeLong( this.date );
			dest.writeLong( this.lastPostDate );
			dest.writeLong( this.lastPostUserId );
			dest.writeLong( this.ownerId );
		
			dest.writeInt( this.numOfficialPosts );
			dest.writeInt( this.numPosts );
			dest.writeInt( this.numCurrentPage );
			dest.writeInt( this.numTotalPages );
			
			dest.writeString( this.title );
			dest.writeString( this.lastPostUsername );
			dest.writeString( this.owner );
		
			dest.writeInt( this.sticky ? 1 : 0 );
			dest.writeInt( this.locked ? 1 : 0 );
			
			dest.writeInt( this.censorPosts ? 1 : 0 );
			dest.writeInt( this.deletePosts ? 1 : 0 );
			dest.writeInt( this.editPosts ? 1 : 0 );
			dest.writeInt( this.admin ? 1 : 0 );
			dest.writeInt( this.postOfficial ? 1 : 0 );
			dest.writeInt( this.viewLatestPosts ? 1 : 0 );
			dest.writeInt( this.viewPostHistory ? 1 : 0 );
			
			dest.writeTypedList( this.posts );
			
		}
		
		public static final Parcelable.Creator<Board.ThreadData> CREATOR = new Parcelable.Creator<Board.ThreadData>() {
			
			public Board.ThreadData createFromParcel(Parcel in) { return new Board.ThreadData(in); }
	        public Board.ThreadData[] newArray(int size) { return new Board.ThreadData[size]; }
		
		};
		
		@Override
		public String toString() { return this.owner + ":" + this.title; }
	
	}

	public static class PostData implements Parcelable {
		
		//Attributes
		private long postId, date, userId, threadId;
		private String username, content;
		private int numReports;
		private boolean censored, official;
		
		//Construct
		public PostData( 
				
			long pId, long d, long uId, long thId,
			String uName, String c,
			int cReports,
			boolean iCensored, boolean iOfficial
			
		) {
			
			this.postId = pId;
			this.date = d;
			this.userId = uId;
			this.threadId = thId;
			
			this.username = uName;
			this.content = c;
			
			this.numReports = cReports;
			
			this.censored = iCensored;
			this.official = iOfficial;
		
		}
		
		public PostData(Parcel in) {
			
			this.postId = in.readLong();
			this.date = in.readLong();
			this.userId = in.readLong();
			this.threadId = in.readLong();
			
			this.username = in.readString();
			this.content = in.readString();
			
			this.numReports = in.readInt();
			
			this.censored = ( in.readInt() == 1 );
			this.official = ( in.readInt() == 1 );			
			
		}
		
		//Getters
		public long getPostId() { return this.postId; }
		public long getDate() { return this.date; }
		public long getUserId() { return this.userId; }
		public long getThreadId() { return this.threadId; }
		
		public String getUsername() { return this.username; }
		public String getContent() { return this.content; }
		
		public int getNumReports() { return this.numReports; }
		
		public boolean isCensored() { return this.censored; }
		public boolean isOfficial() { return this.official; }

		@Override
		public int describeContents() { return 0; }

		@Override
		public void writeToParcel( Parcel dest, int flags ) {

			dest.writeLong( this.postId );
			dest.writeLong( this.date );
			dest.writeLong( this.userId );
			dest.writeLong( this.threadId );
			
			dest.writeString( this.username );
			dest.writeString( this.content );
			
			dest.writeInt( this.numReports );
			
			dest.writeInt( this.censored ? 1 : 0 );
			dest.writeInt( this.official ? 1 : 0 );	
			
		}
		
		public static final Parcelable.Creator<Board.PostData> CREATOR = new Parcelable.Creator<Board.PostData>() {
			
			public Board.PostData createFromParcel(Parcel in) { return new Board.PostData(in); }
	        public Board.PostData[] newArray(int size) { return new Board.PostData[size]; }
		
		};
		
		@Override
		public String toString() { return this.username + ":" + this.content; }
		
	}	
	
}