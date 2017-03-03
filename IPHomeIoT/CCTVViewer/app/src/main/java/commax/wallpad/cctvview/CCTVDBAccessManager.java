package commax.wallpad.cctvview;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;

public class CCTVDBAccessManager extends SQLiteOpenHelper{

    private static final String FAVORITE_TABLE_NAME = "list_favorite";

    public CCTVDBAccessManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + FAVORITE_TABLE_NAME +" (_id INTEGER PRIMARY KEY AUTOINCREMENT, cameraId INTEGER, ip TEXT, name TEXT, id TEXT, password TEXT, rtspUrl TEXT, streamNo INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insert(int cameraId, String ip, String name, String id, String password, String rtspUrl, int streamNo ) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("insert into " + FAVORITE_TABLE_NAME + " values (null, " + cameraId +",'" + ip + "','" + name +"','" + id + "','"+ password + "','" + rtspUrl + "'," + streamNo +");");
        db.close();
    }

    public void delete(String ip) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("delete from " + FAVORITE_TABLE_NAME + " where ip = " + "'" + ip + "';");
        db.close();
    }

    public ArrayList<CameraInfo> getFavoritesList() {
        ArrayList<CameraInfo> resultDB = new ArrayList<CameraInfo>();

        SQLiteDatabase db = getWritableDatabase();
        String query = "select * from " + FAVORITE_TABLE_NAME + ";";
        Cursor cursor = db.rawQuery(query, null);

         while (cursor.moveToNext()) {
             int cameraId = cursor.getInt(1);
             String ip = cursor.getString(2);
             String name = cursor.getString(3);
             String id = cursor.getString(4);
             String password = cursor.getString(5);
             String rtspUrl = cursor.getString(6);
             int streamNo = cursor.getInt(7);
             CameraInfo cameraInfo = new CameraInfo(null, cameraId, ip, name, id, password, rtspUrl, streamNo);
             resultDB.add(cameraInfo);
         }
        cursor.close();
        db.close();
        return resultDB;
    }

    public boolean isFavoriteCamera(String ip) {
        SQLiteDatabase db = getWritableDatabase();
        String query = "select rtspUrl from " + FAVORITE_TABLE_NAME + " where ip = " + "'" + ip + "';";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.getCount() == 0) {
            cursor.close();
            db.close();
            return false;
        }

        cursor.close();
        db.close();
        return true;
    }

    public void onUpdateData(String ip, int cameraId, String name, String id, String password, String rtspUrl, int streamNo) {

        SQLiteDatabase db = getWritableDatabase();
        String query = String.format("update " + FAVORITE_TABLE_NAME + " set cameraId = %d, name = '%s', id = '%s', password = '%s', rtspUrl = '%s', streamNo = %d  where ip = '%s'",
                cameraId, name, id, password, rtspUrl, streamNo, ip);
        db.execSQL(query);
        db.close();
    }
}
