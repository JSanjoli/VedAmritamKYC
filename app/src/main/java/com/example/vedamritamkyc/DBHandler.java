package com.example.vedamritamkyc;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class DBHandler extends SQLiteOpenHelper {

    private static final String DB_NAME = "ved_amritam_kyc";

    private static final int DB_VERSION = 1;

    private static final String TABLE_NAME = "prospects";

    private static final String NAME = "name";

    private static final String EMAIL_ID = "email_id";
    private static final String CONTACT_NUMBER = "contact_number";
    private static final String ADDRESS = "address";

    // creating a constructor for our database handler.
    public DBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // below method is for creating a database by running a sqlite query
    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + " ("
                + NAME + " TEXT, "
                + EMAIL_ID + " TEXT, "
                + CONTACT_NUMBER + " INTEGER, "
                + ADDRESS + " TEXT)";
        db.execSQL(query);
    }

    public void addNewProspect(String name, String email, Long contactNumber, String address) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NAME, name);
        values.put(EMAIL_ID, email);
        values.put(CONTACT_NUMBER, contactNumber);
        values.put(ADDRESS, address);

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    boolean exportDB() {
        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());

        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            return false;
        }
        else {
            //We use the Download directory for saving our .csv file.
            File exportDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (!exportDir.exists())
            {
                exportDir.mkdirs();
            }

            File file;
            PrintWriter printWriter = null;
            try
            {
                file = new File(exportDir, "ved_amritam_prospects.csv");
                file.createNewFile();
                printWriter = new PrintWriter(new FileWriter(file));

                SQLiteDatabase db = this.getReadableDatabase(); //open the database for reading
                Cursor curCSV = db.rawQuery("select * from " + TABLE_NAME, null);
                printWriter.println(TABLE_NAME);
                printWriter.println(NAME + "," + CONTACT_NUMBER + "," + EMAIL_ID + "," + ADDRESS);
                while(curCSV.moveToNext())
                {
                    String name = curCSV.getString(curCSV.getColumnIndexOrThrow(NAME));
                    Long contactNumber = curCSV.getLong(curCSV.getColumnIndexOrThrow(CONTACT_NUMBER));
                    String email = curCSV.getString(curCSV.getColumnIndexOrThrow(EMAIL_ID));
                    String address = curCSV.getString(curCSV.getColumnIndexOrThrow(ADDRESS)).replace(",", ";");

                    String record = name + "," + contactNumber + "," + email + "," + address;
                    printWriter.println(record);
                }

                curCSV.close();
                db.close();
            }

            catch(Exception exc) {
                return false;
            }
            finally {
                if(printWriter != null) printWriter.close();
            }

            return true;
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // this method is called to check if the table exists already.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
