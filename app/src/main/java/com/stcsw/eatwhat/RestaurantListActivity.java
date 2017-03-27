package com.stcsw.eatwhat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.stcsw.eatwhat.EatWhatItemContract.FeedEntry;

import java.io.IOException;
import java.util.Random;

public class RestaurantListActivity extends AppCompatActivity {
    private static String TAG="RestaurantListActivity";
    private EatWhatItemDbHelper mDbHelper=null;
    public static final String Backup_DB_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() +"/" + EatWhatItemDbHelper.DATABASE_NAME;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 0;
    public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurantlist);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab_add = (FloatingActionButton) findViewById(R.id.fab_add);
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RestaurantListActivity.this);
                builder.setTitle("new Restaurant");
                final LinearLayout ll = (LinearLayout)LayoutInflater.from(RestaurantListActivity.this).inflate(R.layout.dialog_new_restaurant, null, false);
                builder.setView(ll);
                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //m_Text = input.getText().toString();
                        EditText etTitle = (EditText)ll.findViewById(R.id.etTitle);
                        //EditText etSubtitle = (EditText)ll.findViewById(R.id.etSubtitle);
                        addItemToDB(etTitle.getText().toString(), " ");
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });

        FloatingActionButton fab_get = (FloatingActionButton) findViewById(R.id.fab_get);
        fab_get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = getTitlebyIDFromDB(getRandomID());
                Snackbar.make(view, title , Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .show();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");

        if(null == mDbHelper) {
            mDbHelper = new EatWhatItemDbHelper(this.getApplicationContext());
        }
        updatelistcursor();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_import_db:
                Log.d(TAG, "import db from " + Backup_DB_PATH);
                if(null == mDbHelper){
                    Log.e(TAG, "mDbHelper is null, do nothing at import db");
                    return true;
                }
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        Log.e(TAG, "Should show why when ask READ_EXTERNAL_STORAGE");
                    } else {
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                        Log.d(TAG, "Ask READ_EXTERNAL_STORAGE");
                    }
                } else {
                    try {
                        mDbHelper.importDatabase(Backup_DB_PATH);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    updatelistcursor();
                }
                return true;

            case R.id.action_export_db:
                Log.d(TAG, "export db to " + Backup_DB_PATH);
                if(null == mDbHelper){
                    Log.e(TAG, "mDbHelper is null, do nothing at export db");
                    return true;
                }
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        Log.e(TAG, "Should show why when ask WRITE_EXTERNAL_STORAGE");
                    } else {
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                        Log.d(TAG, "Ask WRITE_EXTERNAL_STORAGE");
                    }
                } else {
                    try {
                        mDbHelper.exportDatabase(Backup_DB_PATH);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:{
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    try {
                        mDbHelper.importDatabase(Backup_DB_PATH);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    updatelistcursor();
                } else {
                    Log.e(TAG, "Can get permission when import db.");
                }
            }
            return;
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    try {
                        mDbHelper.exportDatabase(Backup_DB_PATH);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e(TAG, "Can get permission when export db.");
                }
                return;
            }
            default:
            return;
        }
    }

    public void addItemToDB(String title, String subtitle){
        if(null == mDbHelper){
            Log.e(TAG, "mDbHelper is null, do nothing at addItemToDB");
            return;
        }
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FeedEntry.COLUMN_NAME_TITLE, title);
        values.put(FeedEntry.COLUMN_NAME_SUBTITLE, subtitle);
        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(FeedEntry.TABLE_NAME, null, values);
        Log.d(TAG, "ADD : newRowId = "+newRowId + " title = " + title + " subtitle = " + subtitle);
        updatelistcursor();
    }

    public void deleteItemFromDB(long ID, String title){
        if(null == mDbHelper){
            Log.e(TAG, "mDbHelper is null, do nothing at deleteItemFromDB");
            return;
        }
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        // Define 'where' part of query.
        String selection = FeedEntry._ID + " LIKE ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = { String.valueOf(ID) };
        // Issue SQL statement.
        db.delete(FeedEntry.TABLE_NAME, selection, selectionArgs);
        Log.d(TAG, "DEL : ID = " + ID + " title = " + title);
        updatelistcursor();
    }

    public void updatelistcursor(){
        if(null == mDbHelper){
            Log.e(TAG, "mDbHelper is null, do nothing at updatelistcursor");
            return;
        }
        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String[] projection = {
                FeedEntry._ID,
                FeedEntry.COLUMN_NAME_TITLE,
                FeedEntry.COLUMN_NAME_SUBTITLE
        };
        String sortOrder = FeedEntry._ID + " DESC";
        Cursor c = db.query(
                FeedEntry.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );
        ListView restaurantItems = (ListView) findViewById(R.id.listView);
        RestaurantCursorAdapter ca = new RestaurantCursorAdapter(this, c);
        restaurantItems.setAdapter(ca);
        restaurantItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View view,
                                           int pos, long id) {
                Log.d(TAG,"pos: " + pos);
                TextView tvID = (TextView) view.findViewById(R.id.tvID);
                TextView tvTitle = (TextView) view.findViewById(R.id.tvTitle);
                TextView tvSubtitle = (TextView) view.findViewById(R.id.tvSubtitle);
                Log.d(TAG, "ID = " + tvID.getText()+ " title = "+tvTitle.getText() + " subtitle = " + tvSubtitle.getText());
                deleteItemFromDB(Long.parseLong(tvID.getText().toString()), tvTitle.getText().toString());
                return true;
            }
        });
    }

    public String getTitlebyIDFromDB(long ID){
        if(null == mDbHelper){
            Log.e(TAG, "mDbHelper is null, do nothing at deleteItemFromDB");
            return "Error";
        }
        Log.d(TAG, "getTitlebyIDFromDB ID = " + String.valueOf(ID));
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String sortOrder = FeedEntry._ID + " DESC";
        String[] projection = {
                FeedEntry._ID,
                FeedEntry.COLUMN_NAME_TITLE,
                FeedEntry.COLUMN_NAME_SUBTITLE
        };
        Cursor c = db.query(
                FeedEntry.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );
        if(c.getCount() > 0){
            c.moveToFirst();
            int i = 0;
            int id = Integer.parseInt(String.valueOf(ID));
            for(i=0; i<id; i++)
                c.moveToNext();
            String title = c.getString(c.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_TITLE));
            Log.d(TAG, "GET : ID = " + ID + " title = " + title);
            c.close();
            return title;
        }else{
            return "ERROR";
        }
    }

    public long getRandomID() {
        if(null == mDbHelper){
            Log.e(TAG, "mDbHelper is null, do nothing at getRandomID");
            return 0;
        }
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String sortOrder = FeedEntry._ID + " DESC";
        String[] projection = {
                FeedEntry._ID,
                FeedEntry.COLUMN_NAME_TITLE,
                FeedEntry.COLUMN_NAME_SUBTITLE
        };
        Cursor c = db.query(
                FeedEntry.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );
        final int min = 0;
        final int max = c.getCount()-1;
        if(max > 0){
            Log.d(TAG, "total items = " + c.getCount());
            Random r = new Random();
            final int random = r.nextInt((max - min) + 1) + min;
            long ret = ((long) random);
            Log.d(TAG, "getRandomID return " + ret);
            return ret;
        }else{
            return 0;
        }
    }
}
