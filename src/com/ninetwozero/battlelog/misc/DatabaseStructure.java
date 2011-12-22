package com.ninetwozero.battlelog.misc;


public final class DatabaseStructure {

	// Declare the Table-data for each "part"
	public static final class PersonaStatistics {

		// Table-name
		public static final String TABLE_NAME = "persona_statistics";
		/*
		 * 
			private String accountName, personaName, rankTitle;
			private long rankId, personaId, playerId, platformId, timePlayed;
			
			//EXP-section
			private long pointsThisLvl, pointsNextLvl;
			
			//STATS-section
			private int numKills, numAssists, numVehicles, numVehicleAssists;
			private int numHeals, numRevives, numRepairs, numResupplies, numDeaths, numWins, numLosses;
			private double kdRatio, accuracy, longestHS, longestKS, skill, scorePerMinute;
			
			//SCORE-section
			private long scoreAssault, scoreEngineer, scoreSupport, scoreRecon, scoreVehicle, scoreCombat, scoreAwards, scoreUnlocks, scoreTotal;
	
		*/
		// Field-names
		public static final String COLUMN_NAME_ID = "_id";
		public static final String COLUMN_NAME_TITLE = "account_name"; // String(255)
		public static final String COLUMN_NAME_TOKEN = "persona_name"; // String (7) "[a-zA-Z]{3}-[0-9]{3}" or String(9) "LOCAL-[0-9]{3}"
		public static final String COLUMN_NAME_OWNER = "rank_title"; // String(255) 
		public static final String COLUMN_NAME_USERNAME = "points_level"; // String(255)
		public static final String COLUMN_NAME_DURATION = "points_this_level"; // INTEGER (10)
		public static final String COLUMN_NAME_DISTANCE= "poinsts_next_level"; // INTEGER (10)
		public static final String COLUMN_NAME_AVGSPEED = "num_kills"; // INTEGER (3)
		public static final String COLUMN_NAME_MAXALTITUDE = "num_assists"; // INTEGER (7)
		public static final String COLUMN_NAME_DEST_LATITUDE = "num_vehicles"; // DOUBLE
		public static final String COLUMN_NAME_DEST_LONGITUDE = "longitude"; // DOUBLE
		public static final String COLUMN_NAME_DEST_ALTITUDE = "altitude"; // DOUBLE
		public static final String COLUMN_NAME_DEST_BEARING = "bearing"; // DOUBLE
		public static final String COLUMN_NAME_STATUS = "status"; // String(255)
		public static final String COLUMN_NAME_DATE = "date"; // datetime "YYYY-mm-dd HH:ii:ss"

		// Sort order
		public static final String DEFAULT_SORT_ORDER = "`date` DESC";


	}
	
	// Declare the Table-data for each "part"
		public static final class PrivateLocations {

			// Table-name
			public static final String TABLE_NAME = "privateLocations";

			// Field-names
			public static final String COLUMN_NAME_ID = "_id";
			public static final String COLUMN_NAME_TITLE = "title"; // String(255)
			public static final String COLUMN_NAME_USERNAME = "username"; // String(255)
			public static final String COLUMN_NAME_DEST_LATITUDE = "latitude"; // DOUBLE
			public static final String COLUMN_NAME_DEST_LONGITUDE = "longitude"; // DOUBLE
			public static final String COLUMN_NAME_DEST_ALTITUDE = "altitude"; // DOUBLE
			public static final String COLUMN_NAME_DATE = "date"; // datetime "YYYY-mm-dd HH:ii:ss"

			// Sort order
			public static final String DEFAULT_SORT_ORDER = "`date` DESC";

		}
		
		// Declare the Table-data for each "part"
		public static final class Invites {

			// Table-name
			public static final String TABLE_NAME = "invites";

			// Field-names
			public static final String COLUMN_NAME_ID = "_id";
			public static final String COLUMN_NAME_INVITE_ID = "inviteid";
			public static final String COLUMN_NAME_TRANSMISSION_ID = "transmissionid";
			public static final String COLUMN_NAME_TITLE = "title"; // String(255)
			public static final String COLUMN_NAME_USERNAME_FROM = "usernamefrom";
			public static final String COLUMN_NAME_USERNAME_TO = "usernameto"; // String(255)
			public static final String COLUMN_NAME_DEST_LATITUDE = "latitude"; // DOUBLE
			public static final String COLUMN_NAME_DEST_LONGITUDE = "longitude"; // DOUBLE
			public static final String COLUMN_NAME_DEST_ALTITUDE = "altitude"; // DOUBLE
			public static final String COLUMN_NAME_DATE = "date"; //LONG

			// Sort order
			public static final String DEFAULT_SORT_ORDER = "`date` DESC";

		}

	// Non-initializable
	private DatabaseStructure() {}
}