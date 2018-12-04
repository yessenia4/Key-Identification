//Code based on the code by Minh Tran
//Retrived from https://dzone.com/articles/create-a-database-android-application-in-android-s

package com.rodriguez.foundmatch.DatabaseManagment;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;

import com.rodriguez.foundmatch.Keys.KeyInformation;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class MyDBHandler extends SQLiteOpenHelper {
    private static MyDBHandler dbInstance;

    //information of database
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "UserKeysDB";
    public static final String TABLE_NAME = "Keys";
    public static final String COLUMN_ID = "KeyID";
    public static final String COLUMN_NAME = "KeyName";
    public static final String COLUMN_Desc = "KeyDesc";
    public static final String COLUMN_Img = "KeyImage";
    public static final String COLUMN_Feat = "KeyFeat";

    public static synchronized MyDBHandler getDbInstance(Context context){
        if(dbInstance == null)
            dbInstance = new MyDBHandler(context.getApplicationContext());
        return dbInstance;
    }

    //initialize the database
    public MyDBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" + COLUMN_ID +
                " INTEGERPRIMARYKEY, " + COLUMN_NAME + " TEXT," + COLUMN_Desc + " TEXT," +
                COLUMN_Img + " BLOB," + COLUMN_Feat + " BLOB);";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {}

    public ArrayList<KeyInformation> loadHandler() {
        ArrayList<KeyInformation> results = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_NAME + ";";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query,null);
        while(cursor.moveToNext()){
            int keyID = cursor.getInt(0);
            String keyName = cursor.getString(1);
            String keyDesc = cursor.getString(2);
            byte[] keyImage = cursor.getBlob(3);
            Bitmap bitmap = BitmapFactory.decodeByteArray(keyImage, 0, keyImage.length);
            byte[] keyFeat = cursor.getBlob(4);
            ByteBuffer bb = ByteBuffer.wrap(keyFeat);
            Double[] features = new Double[keyFeat.length / 8];
            for(int i = 0; i < features.length; i++) {
                features[i] = bb.getDouble();
            }

            /*String stringFeat = cursor.getString(4);
            String[] tokens = stringFeat.split(",");
            Double[] keyFeat = new Double[tokens.length];
            for(int i=0; i<tokens.length; i++){
                keyFeat[i] = Array.getDouble(tokens,i);
            }*/

            KeyInformation key = new KeyInformation(keyID, keyName,keyDesc,bitmap, features);
            results.add(key);
        }
        cursor.close();
        db.close();
        return results;
    }

    public void addHandler(KeyInformation key) {
        ContentValues values = new ContentValues();

        values.put(COLUMN_ID,key.getKeyID());
        values.put(COLUMN_NAME,key.getKeyName());
        values.put(COLUMN_Desc,key.getKeyDescription());
        Bitmap bitmap = key.getKeyImage();
        ByteArrayOutputStream blob = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.PNG, 0, blob);
        byte[] bitmapdata = blob.toByteArray();
        values.put(COLUMN_Img,bitmapdata);
        Double[] feat = key.getFeatures();
        ByteBuffer bb = ByteBuffer.allocate(feat.length * 8);
        for(Double d : feat) {
            bb.putDouble(d);
        }
        byte[] bytearray = bb.array();
        values.put(COLUMN_Feat,bytearray);

        /*Double[] features = key.getFeatures();
        String featData = new String();
        for(int i=0; i<features.length; i++){
            featData += features[i].toString() + ",";
        }
        values.put(COLUMN_Feat,featData);*/

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_NAME,null,values);
        db.close();
    }

    public boolean deleteHandler(int keyID) {
        boolean result = false;
        String query = "SELECT * FROM " + TABLE_NAME + "WHERE " + COLUMN_ID + "=?" + keyID + ";";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query,null);
        KeyInformation key = new KeyInformation();
        if(cursor.moveToFirst()){
            key.setKeyID(Integer.parseInt(cursor.getString(0)));
            db.delete(TABLE_NAME,COLUMN_ID + "=?",
                    new String[]{
                            String.valueOf(key.getKeyID())
                    });
            cursor.close();
            result = true;
        }
        db.close();
        return result;
    }

    public boolean updateHandler(int ID, KeyInformation newKey) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues args = new ContentValues();
        args.put(COLUMN_ID, ID);
        args.put(COLUMN_NAME, newKey.getKeyName());
        args.put(COLUMN_Desc, newKey.getKeyDescription());
        Bitmap bitmap = newKey.getKeyImage();
        ByteArrayOutputStream blob = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.PNG, 0 /* Ignored for PNGs */, blob);
        byte[] bitmapdata = blob.toByteArray();
        args.put(COLUMN_Img,bitmapdata);
        Double[] feat = newKey.getFeatures();
        ByteBuffer bb = ByteBuffer.allocate(feat.length * 8);
        for(Double d : feat) {
            bb.putDouble(d);
        }
        byte[] bytearray = bb.array();
        args.put(COLUMN_Feat,bytearray);

        /*Double[] features = newKey.getFeatures();
        String featData = new String();
        for(int i=0; i<features.length; i++){
            featData += features[i].toString() + ",";
        }
        args.put(COLUMN_Feat,featData);*/

        return db.update(TABLE_NAME,args,COLUMN_ID + "=" + ID, null) > 0;
    }
}