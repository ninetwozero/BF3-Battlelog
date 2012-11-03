/*
	This file is part of BF3 Battlelog

    BF3 Battlelog is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    BF3 Battlelog is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
 */

package com.ninetwozero.battlelog.misc;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.text.TextUtils;

import com.coveragemapper.android.Map.ExternalCacheDirectory;
import com.ninetwozero.battlelog.dao.ProfileInformationDAO;
import com.ninetwozero.battlelog.datatype.ForumThreadData;
import com.ninetwozero.battlelog.datatype.PersonaData;
import com.ninetwozero.battlelog.datatype.PersonaStats;
import com.ninetwozero.battlelog.datatype.PlatoonData;
import com.ninetwozero.battlelog.datatype.PlatoonInformation;
import com.ninetwozero.battlelog.datatype.ProfileData;
import com.ninetwozero.battlelog.datatype.ProfileInformation;
import com.ninetwozero.battlelog.datatype.SavedForumThreadData;

/* 
 * Methods of this class should be loaded in AsyncTasks, as they would probably lock up the GUI
 */

public class CacheHandler {

    private CacheHandler() {
    }

    public static class Platoon {
        public static long insert(Context context, PlatoonInformation stats) {
            SQLiteManager manager = new SQLiteManager(context);
            try {
                long results = manager.insert(
                    DatabaseStructure.PlatoonProfile.TABLE_NAME,
                    DatabaseStructure.PlatoonProfile.getColumns(),
                    stats.toArray()
                );
                manager.close();
                return results;
            } catch (SQLiteConstraintException ex) { 
            	// Duplicate input, no worries!
                manager.close();
                return 0;
            } catch (Exception ex) {
                manager.close();
                ex.printStackTrace();
                return -1;
            }
        }

        public static long insert(Context context, HashMap<Long, PlatoonInformation> statsArray) {
            SQLiteManager manager = new SQLiteManager(context);
            long results = 0;
            try {
                for (long key : statsArray.keySet()) {
                    PlatoonInformation stats = statsArray.get(key);
                    results += manager.insert(
                        DatabaseStructure.PlatoonProfile.TABLE_NAME,
                        DatabaseStructure.PlatoonProfile.getColumns(),
                        stats.toArray()
                    );
                }
                manager.close();
                return results;
            } catch (SQLiteConstraintException ex) { 
            	// Duplicate input, no worries!
                manager.close();
                return 0;
            } catch (Exception ex) {
                manager.close();
                ex.printStackTrace();
                return -1;
            }
        }

        public static boolean update(Context context, PlatoonInformation stats) {
            SQLiteManager manager = new SQLiteManager(context);
            try {
                manager.update(
                    DatabaseStructure.PlatoonProfile.TABLE_NAME,
                    DatabaseStructure.PlatoonProfile.getColumns(),
                    stats.toArray(),
                    DatabaseStructure.PlatoonProfile.COLUMN_NAME_NUM_ID,
                    stats.getId()
                );
                manager.close();
                return true;
            } catch (Exception ex) {
                manager.close();
                ex.printStackTrace();
                return false;
            }
        }

        public static boolean update(Context context, HashMap<Long, PlatoonInformation> statsArray) {
            SQLiteManager manager = new SQLiteManager(context);
            int results = 0;
            try {
                for (long key : statsArray.keySet()) {
                    PlatoonInformation stats = statsArray.get(key);
                    results += manager.update(
                        DatabaseStructure.PlatoonProfile.TABLE_NAME,
                        DatabaseStructure.PlatoonProfile.getColumns(),
                        stats.toArray(),
                        DatabaseStructure.PlatoonProfile.COLUMN_NAME_NUM_ID,
                        stats.getId()
                    );
                }
                manager.close();
                return (results > 0);
            } catch (Exception ex) {
                manager.close();
                ex.printStackTrace();
                return false;
            }
        }

        public static PlatoonInformation select(final Context context, final long platoonId) {
            SQLiteManager manager = new SQLiteManager(context);
            PlatoonInformation tempPlatoon = null;
            try {
                Cursor results = manager.query(
                    DatabaseStructure.PlatoonProfile.TABLE_NAME, null,
                    DatabaseStructure.PlatoonProfile.COLUMN_NAME_NUM_ID + " = ?", 
                    new String[]{platoonId + ""},
                    null, 
                    null,
                    DatabaseStructure.PlatoonProfile.DEFAULT_SORT_ORDER
                );

                if (results.moveToFirst()) {
                    do {
                        tempPlatoon = new PlatoonInformation(
                            results.getLong(results.getColumnIndex(DatabaseStructure.PlatoonProfile.COLUMN_NAME_NUM_ID)),
                            results.getLong(results.getColumnIndex(DatabaseStructure.PlatoonProfile.COLUMN_NAME_NUM_DATE)),
                            results.getInt(results.getColumnIndex(DatabaseStructure.PlatoonProfile.COLUMN_NAME_NUM_PLATFORM_ID)),
                            results.getInt(results.getColumnIndex(DatabaseStructure.PlatoonProfile.COLUMN_NAME_NUM_GAME_ID)),
                            results.getInt(results.getColumnIndex(DatabaseStructure.PlatoonProfile.COLUMN_NAME_NUM_FANS)),
                            results.getInt(results.getColumnIndex(DatabaseStructure.PlatoonProfile.COLUMN_NAME_NUM_MEMBERS)),
                            results.getInt(results.getColumnIndex(DatabaseStructure.PlatoonProfile.COLUMN_NAME_NUM_BLAZE_ID)),
                            results.getString(results.getColumnIndex(DatabaseStructure.PlatoonProfile.COLUMN_NAME_STRING_NAME)),
                            results.getString(results.getColumnIndex(DatabaseStructure.PlatoonProfile.COLUMN_NAME_STRING_TAG)),
                            results.getString(results.getColumnIndex(DatabaseStructure.PlatoonProfile.COLUMN_NAME_STRING_INFO)),
                            results.getString(results.getColumnIndex(DatabaseStructure.PlatoonProfile.COLUMN_NAME_STRING_WEB)),
                            results.getInt(results.getColumnIndex(DatabaseStructure.PlatoonProfile.COLUMN_NAME_BOOL_VISIBLE))
                        );
                    } while (results.moveToNext());
                }
                results.close();
                manager.close();
                return tempPlatoon;
            } catch (Exception ex) {
                ex.printStackTrace();
                manager.close();
                return null;
            }
        }

        public static ArrayList<PlatoonData> selectPlatoonsForUser(final Context context, final long[] platoonId) {
            SQLiteManager manager = new SQLiteManager(context);
            try {
                StringBuilder strQuestionMarks = new StringBuilder("(?");
                String[] platoonIdArray = new String[platoonId.length];
                List<PlatoonData> stats = new ArrayList<PlatoonData>();

                for (int i = 0, max = platoonId.length; i < max; i++) {
                    if (i > 0) {
                        strQuestionMarks.append(", ?");
                    }
                    platoonIdArray[i] = String.valueOf(platoonId[i]);
                }
                strQuestionMarks.append(")");

                Cursor results = manager.query(
                    DatabaseStructure.PlatoonProfile.TABLE_NAME, 
                    null,
                    DatabaseStructure.PlatoonProfile.COLUMN_NAME_NUM_ID + " IN " + strQuestionMarks,
                    platoonIdArray, 
                    null, 
                    null,
                    DatabaseStructure.PlatoonProfile.DEFAULT_SORT_ORDER
                );

                if (results.moveToFirst()) {
                    do {
                        stats.add(
                        new PlatoonData(

                                results.getLong(results.getColumnIndex(DatabaseStructure.PlatoonProfile.COLUMN_NAME_NUM_ID)),
                                results.getInt(results.getColumnIndex(DatabaseStructure.PlatoonProfile.COLUMN_NAME_NUM_FANS)),
                                results.getInt(results.getColumnIndex(DatabaseStructure.PlatoonProfile.COLUMN_NAME_NUM_MEMBERS)),
                                results.getInt(results.getColumnIndex(DatabaseStructure.PlatoonProfile.COLUMN_NAME_NUM_PLATFORM_ID)),
                                results.getString(results.getColumnIndex(DatabaseStructure.PlatoonProfile.COLUMN_NAME_STRING_NAME)),
                                results.getString(results.getColumnIndex(DatabaseStructure.PlatoonProfile.COLUMN_NAME_STRING_TAG)),
                                null,
                                (results.getInt(results.getColumnIndex(DatabaseStructure.PlatoonProfile.COLUMN_NAME_BOOL_VISIBLE)) == 1)
                            )
                        );
                    } while (results.moveToNext());
                }
                results.close();
                manager.close();
                return (ArrayList<PlatoonData>) stats;
            } catch (Exception ex) {
                ex.printStackTrace();
                manager.close();
                return null;
            }
        }

        public static boolean delete(final Context context, final long[] PlatoonId) {
            SQLiteManager manager = new SQLiteManager(context);
            try {
                String[] platoonIdArray = new String[PlatoonId.length];
                for (int i = 0, max = PlatoonId.length; i < max; i++) {
                    platoonIdArray[i] = String.valueOf(PlatoonId[i]);
                }
                
                int results = manager.delete(
                    DatabaseStructure.PlatoonProfile.TABLE_NAME,
                    DatabaseStructure.PlatoonProfile.COLUMN_NAME_NUM_ID,
                    platoonIdArray
                );
                manager.close();
                return (results > 0);
            } catch (Exception ex) {
                ex.printStackTrace();
                manager.close();
                return false;
            }
        }
    }

    public static class Forum {
        public static long insert(Context context, ForumThreadData thread, long uid) {
            SQLiteManager manager = new SQLiteManager(context);
            try {
                Object[] originArray = thread.toArray();
                String[] valueArray = new String[originArray.length + 1];
                for (int i = 0, max = originArray.length; i < max; i++) {
                    valueArray[i] = String.valueOf(originArray[i]);
                    if (i == (max - 1)) {
                        valueArray[i + 1] = String.valueOf(uid);
                        break;
                    }
                }

                long results = manager.insert(
	                DatabaseStructure.ForumThreads.TABLE_NAME,
	                DatabaseStructure.ForumThreads.getColumns(),
                    valueArray
                );
                manager.close();
                return results;
            } catch (SQLiteConstraintException ex) {
                // Duplicate input, no worries!
                manager.close();
                return 0;
            } catch (Exception ex) {
                manager.close();
                ex.printStackTrace();
                return -1;
            }
        }

        public static long insert(Context context, HashMap<Long, ForumThreadData> threadArray) {
            SQLiteManager manager = new SQLiteManager(context);
            long results = 0;
            try {
                for (long key : threadArray.keySet()) {
                    ForumThreadData stats = threadArray.get(key);
                    results += manager.insert(
                        DatabaseStructure.ForumThreads.TABLE_NAME,
                        DatabaseStructure.ForumThreads.getColumns(),
                        stats.toArray()
                    );
                }
                manager.close();
                return results;
            } catch (SQLiteConstraintException ex) { 
            	// Duplicate input, no worries!
                manager.close();
                return 0;
            } catch (Exception ex) {
                manager.close();
                ex.printStackTrace();
                return -1;
            }
        }

        public static boolean update(Context context, SavedForumThreadData thread) {
            SQLiteManager manager = new SQLiteManager(context);
            try {
                manager.update(
                    DatabaseStructure.ForumThreads.TABLE_NAME,
                    DatabaseStructure.ForumThreads.getColumns(),
                    thread.toArray(),
                    DatabaseStructure.ForumThreads.COLUMN_NAME_NUM_ID,
                    thread.getId()
                );
                manager.close();
                return true;
            } catch (Exception ex) {
                manager.close();
                ex.printStackTrace();
                return false;
            }
        }

        public static boolean updateAfterRefresh(Context context, SavedForumThreadData thread) {
            SQLiteManager manager = new SQLiteManager(context);
            try {
            	manager.update(
                    DatabaseStructure.ForumThreads.TABLE_NAME,
                    new String[]{
                        DatabaseStructure.ForumThreads.COLUMN_NAME_NUM_HAS_UNREAD,
                        DatabaseStructure.ForumThreads.COLUMN_NAME_NUM_DATE_LAST_POST,
                        DatabaseStructure.ForumThreads.COLUMN_NAME_STRING_LAST_AUTHOR,
                        DatabaseStructure.ForumThreads.COLUMN_NAME_NUM_LAST_AUTHOR_ID,
                        DatabaseStructure.ForumThreads.COLUMN_NAME_NUM_DATE_CHECKED
                    },
                    new Object[]{
                        thread.hasUnread() ? 1 : 0,
                        thread.getDateLastPost(),
                        thread.getLastPoster().getUsername(),
                        thread.getLastPoster().getId(),
                        thread.getDateLastChecked()
                    },
                    DatabaseStructure.ForumThreads.COLUMN_NAME_NUM_ID,
                    thread.getId()
                );
                manager.close();
                return true;
            } catch (Exception ex) {
                manager.close();
                ex.printStackTrace();
                return false;
            }
        }

        public static boolean updateBeforeView(Context context, SavedForumThreadData thread) {
            SQLiteManager manager = new SQLiteManager(context);
            try {
                manager.update(
            		DatabaseStructure.ForumThreads.TABLE_NAME,
                    new String[]{
        				DatabaseStructure.ForumThreads.COLUMN_NAME_NUM_HAS_UNREAD,
                        DatabaseStructure.ForumThreads.COLUMN_NAME_NUM_DATE_READ,
                        DatabaseStructure.ForumThreads.COLUMN_NAME_NUM_DATE_CHECKED
                    },
                    new Object[]{
                        0,
                        thread.getDateLastRead(),
                        thread.getDateLastChecked()
                    },
                    DatabaseStructure.ForumThreads.COLUMN_NAME_NUM_ID,
                    thread.getId()
                );
                manager.close();
                return true;
            } catch (Exception ex) {
                manager.close();
                ex.printStackTrace();
                return false;
            }
        }

        public static boolean updateAfterView(Context context, long threadId, int numPosts) {
            SQLiteManager manager = new SQLiteManager(context);
            try {
                manager.update(
                	DatabaseStructure.ForumThreads.TABLE_NAME,
                    new String[]{
            			DatabaseStructure.ForumThreads.COLUMN_NAME_NUM_POSTS
                    },
                    new Object[]{
                		numPosts
                	},
                    DatabaseStructure.ForumThreads.COLUMN_NAME_NUM_ID,
                    threadId
                );
                manager.close();
                return true;
            } catch (Exception ex) {
                manager.close();
                ex.printStackTrace();
                return false;
            }
        }

        public static boolean update(Context context, HashMap<Long, SavedForumThreadData> threadArray) {
            SQLiteManager manager = new SQLiteManager(context);
            int results = 0;
            try {
                for (long key : threadArray.keySet()) {
                    SavedForumThreadData thread = threadArray.get(key);
                    results += manager.update(
                        DatabaseStructure.ForumThreads.TABLE_NAME,
                        DatabaseStructure.ForumThreads.getColumns(),
                        thread.toArray(),
                        DatabaseStructure.ForumThreads.COLUMN_NAME_NUM_ID,
                        thread.getId()
                    );
                }
                manager.close();
                return (results > 0);
            } catch (Exception ex) {
                manager.close();
                ex.printStackTrace();
                return false;
            }
        }

        public static SavedForumThreadData select(final Context context, final long threadId) {
            SQLiteManager manager = new SQLiteManager(context);
            SavedForumThreadData tempSavedForumThread = null;
            try {
                Cursor results = manager.query(
                    DatabaseStructure.ForumThreads.TABLE_NAME, 
                    null,
                    DatabaseStructure.ForumThreads.COLUMN_NAME_NUM_ID + " = ?", 
                    new String[]{threadId + ""},
                    null, 
                    null,
                    DatabaseStructure.ForumThreads.DEFAULT_SORT_ORDER
        		);

                if (results.moveToFirst()) {
                    do {
                        tempSavedForumThread = new SavedForumThreadData(
                            results.getLong(results.getColumnIndex(DatabaseStructure.ForumThreads.COLUMN_NAME_NUM_ID)),
                            results.getString(results.getColumnIndex(DatabaseStructure.ForumThreads.COLUMN_NAME_STRING_TITLE)),
                            results.getLong(results.getColumnIndex(DatabaseStructure.ForumThreads.COLUMN_NAME_NUM_FORUM_ID)),
                            results.getLong(results.getColumnIndex(DatabaseStructure.ForumThreads.COLUMN_NAME_NUM_DATE_LAST_POST)),
                            new ProfileData.Builder(
                                results.getLong(results.getColumnIndex(DatabaseStructure.ForumThreads.COLUMN_NAME_NUM_ID)),
                                results.getString(results.getColumnIndex(DatabaseStructure.ForumThreads.COLUMN_NAME_STRING_LAST_AUTHOR))
                    		).build(),
                            results.getLong(results.getColumnIndex(DatabaseStructure.ForumThreads.COLUMN_NAME_NUM_DATE_CHECKED)),
                            results.getLong(results.getColumnIndex(DatabaseStructure.ForumThreads.COLUMN_NAME_NUM_DATE_READ)),
                            results.getInt(results.getColumnIndex(DatabaseStructure.ForumThreads.COLUMN_NAME_NUM_LAST_PAGE_ID)),
                            results.getInt(results.getColumnIndex(DatabaseStructure.ForumThreads.COLUMN_NAME_NUM_POSTS)),
                            results.getInt(results.getColumnIndex(DatabaseStructure.ForumThreads.COLUMN_NAME_NUM_HAS_UNREAD)) == 1,
                            results.getLong(results.getColumnIndex(DatabaseStructure.ForumThreads.COLUMN_NAME_NUM_PROFILE_ID))
                        );
                    } while (results.moveToNext());
                }
                results.close();
                manager.close();
                return tempSavedForumThread;
            } catch (Exception ex) {
                ex.printStackTrace();
                manager.close();
                return null;
            }
        }

        public static List<SavedForumThreadData> selectAll(final Context context, final long uid) {
            SQLiteManager manager = new SQLiteManager(context);
            List<SavedForumThreadData> tempSavedForumThread = new ArrayList<SavedForumThreadData>();
            try {
                Cursor results = manager.query(
                    DatabaseStructure.ForumThreads.TABLE_NAME, 
                    null,
                    DatabaseStructure.ForumThreads.COLUMN_NAME_NUM_PROFILE_ID + " = ?", 
                    new String[]{uid + ""},
                    null, 
                    null,
                    DatabaseStructure.ForumThreads.DEFAULT_SORT_ORDER
                );

                if (results.moveToFirst()) {
                    do {
                        tempSavedForumThread.add(
                        	new SavedForumThreadData(
                                results.getLong(results.getColumnIndex(DatabaseStructure.ForumThreads.COLUMN_NAME_NUM_ID)),
                                results.getString(results.getColumnIndex(DatabaseStructure.ForumThreads.COLUMN_NAME_STRING_TITLE)),
                                results.getLong(results.getColumnIndex(DatabaseStructure.ForumThreads.COLUMN_NAME_NUM_FORUM_ID)),
                                results.getLong(results.getColumnIndex(DatabaseStructure.ForumThreads.COLUMN_NAME_NUM_DATE_LAST_POST)),
                                new ProfileData.Builder(
                                    results.getLong(results.getColumnIndex(DatabaseStructure.ForumThreads.COLUMN_NAME_NUM_ID)),
                                    results.getString(results.getColumnIndex(DatabaseStructure.ForumThreads.COLUMN_NAME_STRING_LAST_AUTHOR))
                                ).build(),
                                results.getLong(results.getColumnIndex(DatabaseStructure.ForumThreads.COLUMN_NAME_NUM_DATE_CHECKED)),
                                results.getLong(results.getColumnIndex(DatabaseStructure.ForumThreads.COLUMN_NAME_NUM_DATE_READ)),
                                results.getInt(results.getColumnIndex(DatabaseStructure.ForumThreads.COLUMN_NAME_NUM_LAST_PAGE_ID)),
                                results.getInt(results.getColumnIndex(DatabaseStructure.ForumThreads.COLUMN_NAME_NUM_POSTS)),
                                results.getInt(results.getColumnIndex(DatabaseStructure.ForumThreads.COLUMN_NAME_NUM_HAS_UNREAD)) == 1,
                                uid
                			)
                		);
                    } while (results.moveToNext());
                }
                results.close();
                manager.close();
                return (ArrayList<SavedForumThreadData>) tempSavedForumThread;
            } catch (Exception ex) {
                ex.printStackTrace();
                manager.close();
                return null;
            }
        }

        public static boolean delete(final Context context, final long[] threadId) {
            SQLiteManager manager = new SQLiteManager(context);
            try {
                String[] threadIdArray = new String[threadId.length];
                for (int i = 0, max = threadId.length; i < max; i++) {
                    threadIdArray[i] = String.valueOf(threadId[i]);
                }

                int results = manager.delete(
                    DatabaseStructure.ForumThreads.TABLE_NAME,
                    DatabaseStructure.ForumThreads.COLUMN_NAME_NUM_ID,
                    threadIdArray
                );
                manager.close();
                return (results > 0);
            } catch (Exception ex) {
                ex.printStackTrace();
                manager.close();
                return false;
            }
        }
    }

    public static boolean isCached(Context c, String f) {
        return new File(ExternalCacheDirectory.getInstance(c).getExternalCacheDirectory(), f).exists();
    }
}
