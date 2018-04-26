package org.brohede.marcus.sqliteapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myListView = (ListView)findViewById(R.id.myList);

        FetchData fetchData = new FetchData();
        fetchData.execute();


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
            try
            {
                JSONArray mountains = new JSONArray(o);
                MountainReaderDbHelper dbHelper = new MountainReaderDbHelper(getApplicationContext());
                SQLiteDatabase db = dbHelper.getWritableDatabase();

                for(int i = 0; i < mountains.length(); i++){
                    JSONObject mountain = mountains.getJSONObject(i);
                    ContentValues values = new ContentValues();

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

                    long newRowId = db.insert(MountainReaderContract.MountainEntry.TABLE_NAME, null, values);
                    Log.d("hiho", "Database inserted");

                    Mountain m = new Mountain(name, height, location, imageUrl, wikipediaUrl, id);
                    mountainList.add(m);
                }
            }
            catch( JSONException e) {
                e.printStackTrace();
            }

            adapter = new MountainAdapter(getApplicationContext(), mountainList);
            Log.d("hiho", mountainList.toString());
            myListView = (ListView)findViewById(R.id.myList);
            myListView.setAdapter(adapter);

        }
    }
}
