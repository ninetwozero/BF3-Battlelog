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

import com.coveragemapper.android.Map.ExternalCacheDirectory;
import com.ninetwozero.battlelog.datatype.ForumThreadData;
import com.ninetwozero.battlelog.datatype.ProfileData;
import com.ninetwozero.battlelog.datatype.SavedForumThreadData;

/* 
 * Methods of this class should be loaded in AsyncTasks, as they would probably lock up the GUI
 */

public class CacheHandler {

    private CacheHandler() {
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
