package com.example.mridul_xpetize.supervisor;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLite {

    public static final String KEY_ROWID = "_id";
    public static final String KEY_DETAILS = "TaskDetailsId";
    public static final String KEY_TASK = "TaskId";
    public static final String KEY_TO = "AssignToId";
    public static final String KEY_START = "StartDateStr";
    public static final String KEY_END = "EndDateStr";
    public static final String KEY_MODIFIED = "ModifiedDateStr";
    public static final String KEY_BY = "AssignedById";
    public static final String KEY_STATUS = "StatusId";
    public static final String KEY_SUB = "IsSubTask";
    public static final String KEY_COMMENTS = "Comments";
    public static final String KEY_CREATED = "CreatedBy";

    public static final String KEY_DESC = "Description";
    public static final String KEY_TASKID = "TaskId";
    public static final String KEY_BY_ID = "ById";
    public static final String KEY_TO_ID = "ToId";
    public static final String KEY_CREATED_BY = "CreatedBy";

    public static final String KEY_TASKID_ASSIGNED = "TaskId";
    public static final String KEY_TOID_ASSIGNED = "AssignedToId";
    public static final String KEY_BYID_ASSIGNED = "AssignedById";
    public static final String KEY_STATUS_ASSIGNED = "StatusId";
    public static final String KEY_SUB_ASSIGNED = "IsSubTask";
    public static final String KEY_COMMENTS_ASSIGNED = "Comments";
    public static final String KEY_CREATED_ASSIGNED = "CreatedBy";

    private static final String DATABASE_NAME = "EagleXpetizeTest6";
    private static final String DATABASE_TABLE = "PostTaskTableTest3";
    private static final String DATABASE_TABLE_NOTIFICATION = "NotificationTableTest5";
    private static final String DATABASE_TABLE_ASSIGNED = "AssignedTaskTable4";
    private static final int DATABASE_VERSION = 1;
    private DbHelper ourHelper;
    private final Context ourContext;
    private SQLiteDatabase ourDatabase;

    private static class DbHelper extends SQLiteOpenHelper {

        public DbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            // TODO Auto-generated constructor stub
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL("CREATE TABLE " + DATABASE_TABLE + " (" +
                            KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            KEY_DETAILS + " TEXT NOT NULL, " +
                            KEY_TASK + " TEXT NOT NULL, " +
                            KEY_TO + " TEXT NOT NULL, " +
                            KEY_START + " TEXT NULL, " +
                            KEY_END + " TEXT NULL, " +
                            KEY_MODIFIED + " TEXT NULL, " +
                            KEY_BY + " TEXT NOT NULL, " +
                            KEY_STATUS + " TEXT NOT NULL, " +
                            KEY_SUB + " TEXT NOT NULL, " +
                            KEY_COMMENTS + " TEXT NOT NULL, " +
                            KEY_CREATED + " TEXT NOT NULL);"
            );

            db.execSQL("CREATE TABLE " + DATABASE_TABLE_NOTIFICATION + " (" +
                            KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            KEY_DESC + " TEXT NOT NULL, " +
                            KEY_TASKID + " TEXT NOT NULL, " +
                            KEY_BY_ID + " TEXT NOT NULL, " +
                            KEY_TO_ID + " TEXT NULL, " +
                            KEY_CREATED + " TEXT NOT NULL);"
            );

            db.execSQL("CREATE TABLE " + DATABASE_TABLE_ASSIGNED + " (" +
                            KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            KEY_TASKID_ASSIGNED + " TEXT NOT NULL, " +
                            KEY_TOID_ASSIGNED + " TEXT NOT NULL, " +
                            KEY_BYID_ASSIGNED + " TEXT NOT NULL, " +
                            KEY_STATUS_ASSIGNED + " TEXT NOT NULL, " +
                            KEY_SUB_ASSIGNED + " TEXT NOT NULL, " +
                            KEY_COMMENTS_ASSIGNED + " TEXT NOT NULL, " +
                            KEY_CREATED_ASSIGNED + " TEXT NOT NULL);"
            );
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // TODO Auto-generated method stub
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(db);

            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_NOTIFICATION);
            onCreate(db);

            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_ASSIGNED);
            onCreate(db);
        }
    }

    public SQLite(Context c) {
        ourContext = c;
    }

    public SQLite open() throws SQLException {
        ourHelper = new DbHelper(ourContext);
        ourDatabase = ourHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        ourHelper.close();
    }

    public long createEntryNotification(String description, String taskId, String byId, String toId, String createdBy) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_DESC, description);
        cv.put(KEY_TASKID, taskId);
        cv.put(KEY_TO_ID, toId);
        cv.put(KEY_BY_ID, byId);
        cv.put(KEY_CREATED_BY, createdBy);
        return ourDatabase.insert(DATABASE_TABLE_NOTIFICATION, null, cv);
    }

    public long createEntry(String details, String taskId, String toId, String startDate, String endDate, String modifiedDate, String byId, String status, String sub, String comments, String createdBy) {
        // TODO Auto-generated method stub
        ContentValues cv = new ContentValues();
        cv.put(KEY_DETAILS, details);
        cv.put(KEY_TASK, taskId);
        cv.put(KEY_TO, toId);
        cv.put(KEY_START, startDate);
        cv.put(KEY_END, endDate);
        cv.put(KEY_MODIFIED, modifiedDate);
        cv.put(KEY_BY, byId);
        cv.put(KEY_STATUS, status);
        cv.put(KEY_SUB, sub);
        cv.put(KEY_COMMENTS, comments);
        cv.put(KEY_CREATED, createdBy);
        return ourDatabase.insert(DATABASE_TABLE, null, cv);
    }

    public long createEntryAssigned(String taskId, String toId, String byId, String status, String sub, String comments, String createdBy) {
        // TODO Auto-generated method stub
        ContentValues cv = new ContentValues();
        cv.put(KEY_TASKID_ASSIGNED, taskId);
        cv.put(KEY_TOID_ASSIGNED, toId);
        cv.put(KEY_BYID_ASSIGNED, byId);
        cv.put(KEY_STATUS_ASSIGNED, status);
        cv.put(KEY_SUB_ASSIGNED, sub);
        cv.put(KEY_COMMENTS_ASSIGNED, comments);
        cv.put(KEY_CREATED_ASSIGNED, createdBy);
        return ourDatabase.insert(DATABASE_TABLE_ASSIGNED, null, cv);
    }

    public String getCountNotification() throws SQLException {

        String[] columns = new String[]{KEY_ROWID, KEY_DESC, KEY_TASKID, KEY_TO_ID, KEY_BY_ID, KEY_CREATED_BY};
        Cursor c = ourDatabase.query(DATABASE_TABLE_NOTIFICATION, columns, null, null, null, null, null);
        if (c != null) {
            String count = String.valueOf(c.getCount());
            return count;
        }
        return null;
    }

    public String getCount() throws SQLException {
        // TODO Auto-generated method stub
        String[] columns = new String[]{KEY_ROWID, KEY_DETAILS, KEY_TASK, KEY_TO, KEY_START, KEY_END, KEY_MODIFIED, KEY_BY, KEY_STATUS, KEY_SUB, KEY_COMMENTS, KEY_CREATED};
        Cursor c = ourDatabase.query(DATABASE_TABLE, columns, null, null, null, null, null);
        if (c != null) {
            String count = String.valueOf(c.getCount());
            return count;
        }
        return null;
    }

    public String getCountAssigned() throws SQLException {
        // TODO Auto-generated method stub
        String[] columns = new String[]{KEY_ROWID, KEY_TASKID_ASSIGNED, KEY_TOID_ASSIGNED, KEY_BYID_ASSIGNED, KEY_STATUS_ASSIGNED, KEY_SUB_ASSIGNED, KEY_COMMENTS_ASSIGNED, KEY_CREATED_ASSIGNED};
        Cursor c = ourDatabase.query(DATABASE_TABLE_ASSIGNED, columns, null, null, null, null, null);
        if (c != null) {
            String count = String.valueOf(c.getCount());
            return count;
        }
        return null;
    }

    public void deleteAssignedRow() {

        String sql = "SELECT * FROM " + DATABASE_TABLE_ASSIGNED + " ORDER BY " + KEY_ROWID + " DESC LIMIT 1";
        Cursor c = ourDatabase.rawQuery(sql, null);
        if (c.moveToFirst()) {
            String rowId = c.getString(c.getColumnIndex(KEY_ROWID));
            ourDatabase.delete(DATABASE_TABLE_ASSIGNED, KEY_ROWID + "=?", new String[]{rowId});
        }
    }

    public void deleteNotificationRow() {

        String sql = "SELECT * FROM " + DATABASE_TABLE_NOTIFICATION + " ORDER BY " + KEY_ROWID + " DESC LIMIT 1";
        Cursor c = ourDatabase.rawQuery(sql, null);
        if (c.moveToFirst()) {
            String rowId = c.getString(c.getColumnIndex(KEY_ROWID));
            ourDatabase.delete(DATABASE_TABLE_NOTIFICATION, KEY_ROWID + "=?", new String[]{rowId});
        }
    }

    public void deleteFirstRow() {

        String sql = "SELECT * FROM " + DATABASE_TABLE + " ORDER BY " + KEY_ROWID + " DESC LIMIT 1";
        Cursor c = ourDatabase.rawQuery(sql, null);
        if (c.moveToFirst()) {
            String rowId = c.getString(c.getColumnIndex(KEY_ROWID));
            ourDatabase.delete(DATABASE_TABLE, KEY_ROWID + "=?", new String[]{rowId});
        }
    }


    public String[] getNotificationRow() {

        String sql = "SELECT * FROM " + DATABASE_TABLE_NOTIFICATION + " ORDER BY " + KEY_ROWID + " DESC LIMIT 1";
        String[] columns = new String[]{KEY_ROWID, KEY_DESC, KEY_TASKID, KEY_TO_ID, KEY_BY_ID, KEY_CREATED_BY};
        Cursor c = ourDatabase.rawQuery(sql, null);
        int iRow = c.getColumnIndex(KEY_ROWID);
        int iDescription = c.getColumnIndex(KEY_DESC);
        int iTask = c.getColumnIndex(KEY_TASKID);
        int iTo = c.getColumnIndex(KEY_TO_ID);
        int iBy = c.getColumnIndex(KEY_BY_ID);
        int iCreated = c.getColumnIndex(KEY_CREATED_BY);
        String arrData[] = null;

        if (c != null) {
            if (c.moveToFirst()) {
                arrData = new String[c.getColumnCount()];
                arrData[0] = c.getString(iRow);
                arrData[1] = c.getString(iDescription);
                arrData[2] = c.getString(iTask);
                arrData[3] = c.getString(iTo);
                arrData[4] = c.getString(iBy);
                arrData[5] = c.getString(iCreated);
            }
        }
        return arrData;
    }

    public String[] getFirstRow() {

        String sql = "SELECT * FROM " + DATABASE_TABLE + " ORDER BY " + KEY_ROWID + " DESC LIMIT 1";
        String[] columns = new String[]{KEY_ROWID, KEY_DETAILS, KEY_TASK, KEY_TO, KEY_START, KEY_END, KEY_MODIFIED, KEY_BY, KEY_STATUS, KEY_SUB, KEY_COMMENTS, KEY_CREATED};
        Cursor c = ourDatabase.rawQuery(sql, null);
        int iRow = c.getColumnIndex(KEY_ROWID);
        int iDetails = c.getColumnIndex(KEY_DETAILS);
        int iTask = c.getColumnIndex(KEY_TASK);
        int iTo = c.getColumnIndex(KEY_TO);
        int iStart = c.getColumnIndex(KEY_START);
        int iEnd = c.getColumnIndex(KEY_END);
        int iModified = c.getColumnIndex(KEY_MODIFIED);
        int iBy = c.getColumnIndex(KEY_BY);
        int iStatus = c.getColumnIndex(KEY_STATUS);
        int iSub = c.getColumnIndex(KEY_SUB);
        int iComments = c.getColumnIndex(KEY_COMMENTS);
        int iCreated = c.getColumnIndex(KEY_CREATED);
        String arrData[] = null;

        if (c != null) {
            if (c.moveToFirst()) {
                arrData = new String[c.getColumnCount()];
                arrData[0] = c.getString(iRow);
                arrData[1] = c.getString(iDetails);
                arrData[2] = c.getString(iTask);
                arrData[3] = c.getString(iTo);
                arrData[4] = c.getString(iStart);
                arrData[5] = c.getString(iEnd);
                arrData[6] = c.getString(iModified);
                arrData[7] = c.getString(iBy);
                arrData[8] = c.getString(iStatus);
                arrData[9] = c.getString(iSub);
                arrData[10] = c.getString(iComments);
                arrData[11] = c.getString(iCreated);
            }
        }

        return arrData;

    }

    public String[] getFirstRowAssigned() {

        String sql = "SELECT * FROM " + DATABASE_TABLE_ASSIGNED + " ORDER BY " + KEY_ROWID + " DESC LIMIT 1";
        String[] columns = new String[]{KEY_ROWID, KEY_TASKID_ASSIGNED, KEY_TOID_ASSIGNED, KEY_BYID_ASSIGNED, KEY_STATUS_ASSIGNED, KEY_SUB_ASSIGNED, KEY_COMMENTS_ASSIGNED, KEY_CREATED_ASSIGNED};
        Cursor c = ourDatabase.rawQuery(sql, null);
        int iRow = c.getColumnIndex(KEY_ROWID);
        int iTask = c.getColumnIndex(KEY_TASKID_ASSIGNED);
        int iTo = c.getColumnIndex(KEY_TOID_ASSIGNED);
        int iBy = c.getColumnIndex(KEY_BYID_ASSIGNED);
        int iStatus = c.getColumnIndex(KEY_STATUS_ASSIGNED);
        int iSub = c.getColumnIndex(KEY_SUB_ASSIGNED);
        int iComments = c.getColumnIndex(KEY_COMMENTS_ASSIGNED);
        int iCreated = c.getColumnIndex(KEY_CREATED_ASSIGNED);
        String arrData[] = null;

        if (c != null) {
            if (c.moveToFirst()) {
                arrData = new String[c.getColumnCount()];
                arrData[0] = c.getString(iRow);
                arrData[1] = c.getString(iTask);
                arrData[2] = c.getString(iTo);
                arrData[3] = c.getString(iBy);
                arrData[4] = c.getString(iStatus);
                arrData[5] = c.getString(iSub);
                arrData[6] = c.getString(iComments);
                arrData[7] = c.getString(iCreated);
            }
        }

        return arrData;

    }
}