package com.uni.bremen.hastag_inspektor;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class MainActivity extends AppCompatActivity {
    // define private variable
    // userInput as a String
    private String userInput;
    // Button
    private Button searchButton;
    // The EditText view
    private EditText mSearchQueryEditText;
    // for Twitter4j
    private ConfigurationBuilder configurationBuilder;

    private SearchQueryAdapter searchQueryAdapter;

    private SQLiteDatabase database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SearchQueryDBHelper dbHelper = new SearchQueryDBHelper(this);
        database = dbHelper.getWritableDatabase();


        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchQueryAdapter = new SearchQueryAdapter(this, getAllItems());
        recyclerView.setAdapter(searchQueryAdapter);

        // TODO: @Sajjad: have to change it later, it's a bad use-case
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        // TODO: @sajjad we have to fix this line later, here we check if the user internet connection
        if (!isNetworkAvailable()) {
            Toast.makeText(this, "No Internet Connection Available, hence the app will not work!", Toast.LENGTH_LONG).show();
            finish();
        }


        // @sajjad: Twitter authentication
        configurationBuilder = new ConfigurationBuilder();
        configurationBuilder.setDebugEnabled(true)
                .setOAuthConsumerKey("bQJQwFuUGy7B9uPuuxJtgp7Q8")
                .setOAuthConsumerSecret("6VMslMNhonTvkz3HR5uzE1x1kozltaPXxhZtPzRzJAIoYaOBLt")
                .setOAuthAccessToken("799690696970092544-AvEdEbkruF9YDOwT28yqNZ5CxC4p6yR")
                .setOAuthAccessTokenSecret("YutFlDv8mP7GDiycmSNlvQ7wQYWCafphEjK6j6cmT4bNU");


        mSearchQueryEditText = findViewById(R.id.searchQueryEditText);
        searchButton = findViewById(R.id.button);
        TwitterFactory tf = new TwitterFactory(configurationBuilder.build());
        Twitter twitter = tf.getInstance();


        searchButton.setOnClickListener(v -> {

            userInput = mSearchQueryEditText.getText().toString();
            addToDb();
//            searchButton.setEnabled(false);
            System.out.println("The user has entered the following test: " + userInput);
            System.out.println("mTextMessage.getText() is = " + mSearchQueryEditText.getText().toString());
            Query query = new Query(userInput);
            query.setLang("de");
            query.setCount(50);
            int countNumberOfTweets = 0;


            try {
                QueryResult result = twitter.search(query);
                for (Status status : result.getTweets()) {
                    System.out.println("@" + status.getUser().getScreenName() + " - " + status.getText());
                    extractHashtagsFromAString(status.getText());
                    countNumberOfTweets++;
                }
            } catch (TwitterException te) {
                System.out.println(te.getMessage());
            }
            System.out.println("Number of Tweets: " + countNumberOfTweets);
        });

    }

    private void addToDb() {
        String name = mSearchQueryEditText.getText().toString();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SearchQueriesDatabaseTables.SearchQueryEntry.COLUMN_NAME, name);


        database.insert(SearchQueriesDatabaseTables.SearchQueryEntry.TABLE_NAME, null, contentValues);
        searchQueryAdapter.swapCursor(getAllItems());


        mSearchQueryEditText.getText().clear();
    }


    public void extractHashtagsFromAString(String someSampleText) {
        Pattern MY_PATTERN = Pattern.compile("#(\\w+)");
        Matcher mat = MY_PATTERN.matcher(someSampleText);
        List<String> listOfHashtagsInTheString = new ArrayList<>();
        while (mat.find()) {
            listOfHashtagsInTheString.add(mat.group(1));
        }
        for (String s : listOfHashtagsInTheString) {
            System.out.println("Hashtag: " + s);
        }
        System.out.println("++++++---------------------------------------------------++++++++");
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private Cursor getAllItems() {
        return database.query(SearchQueriesDatabaseTables.SearchQueryEntry.TABLE_NAME, null, null, null, null, null, null);
    }
}
