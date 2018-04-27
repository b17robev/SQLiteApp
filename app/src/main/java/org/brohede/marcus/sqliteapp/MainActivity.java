package org.brohede.marcus.sqliteapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.provider.BaseColumns;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<Mountain> mountainList = new ArrayList<>();
    MountainAdapter adapter;
    ListView myListView;

    MountainReaderDbHelper dbHelper;

    String[] projection = {
            BaseColumns._ID,
            MountainReaderContract.MountainEntry.COLUMN_NAME_NAME,
            MountainReaderContract.MountainEntry.COLUMN_NAME_HEIGHT,
            MountainReaderContract.MountainEntry.COLUMN_NAME_LOCATION,
            MountainReaderContract.MountainEntry.COLUMN_NAME_IMG_URL,
            MountainReaderContract.MountainEntry.COLUMN_NAME_INFO_URL,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        dbHelper = new MountainReaderDbHelper(this);
        adapter = new MountainAdapter(getApplicationContext(), mountainList);
        myListView = (ListView)findViewById(R.id.myList);

        FetchData fetchData = new FetchData();
        fetchData.execute();

        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                /*No need to open new readable database since mountainList contains the data fetched from the database*/

                String toastText = mountainList.get(i).infoText();
                Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT).show();

            }
        });


    }

    /*
        TODO: Create an App that stores Mountain data in SQLite database

        TODO: Schema for the database must include columns for all member variables in Mountain class
              See: https://developer.android.com/training/data-storage/sqlite.html

        TODO: The Main Activity must have a ListView that displays the names of all the Mountains
              currently in the local SQLite database.

        TODO: In the details activity an ImageView should display the img_url
              See: https://developer.android.com/reference/android/widget/ImageView.html

        TODO: The main activity must have an Options Menu with the following options:
              * "Fetch mountains" - Which fetches mountains from the same Internet service as in
                "Use JSON data over Internet" assignment. Re-use code.
              * "Drop database" - Which drops the local SQLite database

        TODO: All fields in the details activity should be EditText elements

        TODO: The details activity must have a button "Update" that updates the current mountain
              in the local SQLite database with the values from the EditText boxes.
              See: https://developer.android.com/training/data-storage/sqlite.html

        TODO: The details activity must have a button "Delete" that removes the
              current mountain from the local SQLite database
              See: https://developer.android.com/training/data-storage/sqlite.html

        TODO: The SQLite database must not contain any duplicate mountain names

     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id == R.id.action_refresh)
        {
            mountainList.clear();
            new FetchData().execute();
            Toast refreshed = Toast.makeText(this, "List have been refreshed", Toast.LENGTH_SHORT);
            refreshed.show();

            return true;
        }

        if(id == R.id.action_sortHeight)
        {
            mountainList.clear();

            Cursor c = sortByColumn(projection, "height", "DESC" );
            updateList(c);
            Toast.makeText(this, "Sorted by height", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

    private class FetchData extends AsyncTask<Void,Void,String> {
        @Override
        protected String doInBackground(Void... params) {
            // These two variables need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a Java string.
            String jsonStr = null;

            try {
                // Construct the URL for the Internet service
                URL url = new URL("http://wwwlab.iit.his.se/brom/kurser/mobilprog/dbservice/admin/getdataasjson.php?type=brom");

                // Create the request to the PHP-service, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                jsonStr = buffer.toString();
                return jsonStr;
            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in
                // attempting to parse it.
                return null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("Network error", "Error closing stream", e);
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(String o) {
            super.onPostExecute(o);
            // This code executes after we have received our data. The String object o holds
            // the un-parsed JSON string or is null if we had an IOException during the fetch.

            // Implement a parsing code that loops through the entire JSON and creates objects
            // of our newly created Mountain class

            deleteDatabase(MountainReaderDbHelper.DATABASE_NAME);
            dbHelper = new MountainReaderDbHelper(MainActivity.this);

            try
            {
                JSONArray mountains = new JSONArray(o);

                for(int i = 0; i < mountains.length(); i++){
                    JSONObject mountain = mountains.getJSONObject(i);
                    ContentValues values = new ContentValues();
                    SQLiteDatabase db = dbHelper.getWritableDatabase();

                    int id = mountain.getInt("ID");
                    String name = mountain.getString("name");
                    String location = mountain.getString("location");
                    int height = mountain.getInt("size");

                    String auxData = mountain.getString("auxdata");
                    JSONObject aux = new JSONObject(auxData);
                    String imageUrl = aux.getString("img");
                    String wikipediaUrl = aux.getString("url");


                    values.put(MountainReaderContract.MountainEntry.COLUMN_NAME_NAME, name);
                    values.put(MountainReaderContract.MountainEntry.COLUMN_NAME_LOCATION, location);
                    values.put(MountainReaderContract.MountainEntry.COLUMN_NAME_HEIGHT, height);
                    values.put(MountainReaderContract.MountainEntry.COLUMN_NAME_IMG_URL, imageUrl);
                    values.put(MountainReaderContract.MountainEntry.COLUMN_NAME_INFO_URL, wikipediaUrl);


                    db.insert(MountainReaderContract.MountainEntry.TABLE_NAME, null, values);
                }
                Cursor c = sortByColumn(projection, "name", "DESC" );
                updateList(c);
            }
            catch( JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void updateList(Cursor cursor)
    {
        adapter.clear();

        while(cursor.moveToNext()){
            int id = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID));
            String name = cursor.getString(cursor.getColumnIndex(MountainReaderContract.MountainEntry.COLUMN_NAME_NAME));
            int height = cursor.getInt(cursor.getColumnIndex(MountainReaderContract.MountainEntry.COLUMN_NAME_HEIGHT));
            String location = cursor.getString(cursor.getColumnIndex(MountainReaderContract.MountainEntry.COLUMN_NAME_LOCATION));
            String imageUrl = cursor.getString(cursor.getColumnIndex(MountainReaderContract.MountainEntry.COLUMN_NAME_IMG_URL));
            String infoUrl = cursor.getString(cursor.getColumnIndex(MountainReaderContract.MountainEntry.COLUMN_NAME_INFO_URL));


            Mountain mountain = new Mountain(name, height, location, imageUrl, infoUrl, id);
            mountainList.add(mountain);
        }
        Log.d("hiho", "Mountains added");
        cursor.close();

        myListView = (ListView)findViewById(R.id.myList);
        myListView.setAdapter(adapter);

    }

    public Cursor sortByColumn(String[] projections, String column, String sortBy){
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String order = column + " " + sortBy;
        Cursor cursor = db.query(MountainReaderContract.MountainEntry.TABLE_NAME, projections ,null, null, null, null, order);

        return cursor;
    };
}
