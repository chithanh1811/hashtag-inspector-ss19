package com.uni.bremen.hastag_inspektor;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.uni.bremen.hastag_inspektor.MicrosoftSentimentAnalyseTool.Documents;
import com.uni.bremen.hastag_inspektor.MicrosoftSentimentAnalyseTool.GetSentiment;
import com.uni.bremen.hastag_inspektor.MicrosoftSentimentAnalyserParser.Example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import twitter4j.GeoLocation;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Trends;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class MainActivity extends AppCompatActivity {
    private final String ACCESS_KEY = "66aa412852304b7697ad098c256da91f";
    private final String HOST = "https://westcentralus.api.cognitive.microsoft.com";
    private final String PATH = "/text/analytics/v2.1/sentiment";

    // define private variable
    // userInput as a String
    private String userInput;
    // Button
    private Button searchButton;
    // The EditText view
    private EditText mSearchQueryEditText;

    private SearchQueryAdapter searchQueryAdapter;

    private SQLiteDatabase database;

    private ArrayList<String> listOfAllHashtags = new ArrayList<>();

    private static ArrayList<HashtagAndOccurrences> occurrencesArrayList = new ArrayList<>();

    public static boolean clearHistory = false;
    private Button mClearHistoryButton;

    private FusedLocationProviderClient client;

    private double longitude;
    private double latitude;

    private ArrayList<TweetsWithSentimentValue> tweetsWithSentimentValueList = new ArrayList<>();

    private ArrayList<String> trendsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final String CONSUMER_KEY = "bQJQwFuUGy7B9uPuuxJtgp7Q8";
        final String CONSUMER_SECRET = "6VMslMNhonTvkz3HR5uzE1x1kozltaPXxhZtPzRzJAIoYaOBLt";
        final String ACCESS_TOKEN = "799690696970092544-AvEdEbkruF9YDOwT28yqNZ5CxC4p6yR";
        final String ACCESS_TOKEN_SECRET = "YutFlDv8mP7GDiycmSNlvQ7wQYWCafphEjK6j6cmT4bNU";

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);


        // TODO: @Sajjad: have to change it later, it's a bad use-case
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        // TODO: @sajjad we have to fix this line later, here we check if the user internet connection
        // TODO: but apparently it doesn't work!
        if (!isNetworkAvailable()) {
            Toast.makeText(this, "No Internet Connection Available, hence the app will not work!", Toast.LENGTH_LONG).show();
            finish();
        }


        // Twitter authentication
        // for Twitter4j
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        configurationBuilder.setDebugEnabled(true)
                .setOAuthConsumerKey(CONSUMER_KEY)
                .setOAuthConsumerSecret(CONSUMER_SECRET)
                .setOAuthAccessToken(ACCESS_TOKEN)
                .setOAuthAccessTokenSecret(ACCESS_TOKEN_SECRET);

        TwitterFactory tf = new TwitterFactory(configurationBuilder.build());
        Twitter twitter = tf.getInstance();

        // location part
        client = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        client.getLastLocation().addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    longitude = location.getLongitude();
                    latitude = location.getLatitude();
                    trendsList = new ArrayList<>();

                    mSearchQueryEditText = findViewById(R.id.searchQueryEditText);
                    searchButton = findViewById(R.id.button);

                    try {
                        ResponseList<twitter4j.Location> locations;
                        locations = twitter.getClosestTrends(new GeoLocation(latitude, longitude));
                        int woeid = locations.get(0).getWoeid();
                        Trends trends = twitter.getPlaceTrends(woeid);
                        for (int i = 0; i < trends.getTrends().length; i++) {
                            trendsList.add(trends.getTrends()[i].getName());
                        }

                        // print all the trends
                        // we can use this list later, to show the user all the trends
                        for (String s : trendsList) {
                            System.out.println(s);
                        }

                    } catch (TwitterException ex) {
                        System.out.println(ex.getMessage());
                        // exit from the app, if you couldn't get the location from the user
                        System.exit(1);
                    }

                }
            }
        });


        SearchQueryDBHelper dbHelper = new SearchQueryDBHelper(this);
        database = dbHelper.getWritableDatabase();
        dbHelper.onCreate(database);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchQueryAdapter = new SearchQueryAdapter(this, getAllItems());
        recyclerView.setAdapter(searchQueryAdapter);


        mSearchQueryEditText = findViewById(R.id.searchQueryEditText);
        searchButton = findViewById(R.id.button);

        searchButton.setEnabled(false);

        mSearchQueryEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                String hashTag = mSearchQueryEditText.getText().toString().trim();
                searchButton.setEnabled(hashTag.isEmpty());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String hashTag = mSearchQueryEditText.getText().toString().trim();
                searchButton.setEnabled(!hashTag.isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {
                String hashTag = mSearchQueryEditText.getText().toString().trim();
                searchButton.setEnabled(!hashTag.isEmpty());
            }
        });

        // when the user clicks on the search button
        searchButton.setOnClickListener(v -> {
            userInput = mSearchQueryEditText.getText().toString();

            // every time the user clicks on the search button, we must clear the list
            tweetsWithSentimentValueList.clear();

            // always search for a hashtag, so if an input doesn't have a hashtag
            // add a hashtag add position 0
            if (userInput.charAt(0) != '#') {
                userInput = "#" + userInput;
            }
            addToDb();

            /*
            TODO: delete later
            System.out.println("The user has entered the following test: " + userInput);
            System.out.println("mTextMessage.getText() is = " + mSearchQueryEditText.getText().toString());
            */


            // TODO: add for the user a checkbox, if the user wants to see re-tweets or not
            Query query = new Query(userInput + " -filter:retweets");

            // TODO: we can also add an option to set the language and local and also count
            query.setLocale("en");
            query.setLang("en");
            query.setCount(50);

            // create a list of documents to store the response from Microsoft
            Documents sentimentToolResponseFromMicrosoftDocuments = new Documents();
            try {
                QueryResult result = twitter.search(query);
                for (Status status : result.getTweets()) {
                    // random ID to make sure that each tweet gets the right sentiment score (later)
                    String randomUUID = UUID.randomUUID().toString();

                    // extract all the hashtags from a tweet
                    extractHashtagsFromAString(status.getText());

                    // add the id, language, and the tweet text to the list of document that are going
                    // to be sent to Microsoft
                    sentimentToolResponseFromMicrosoftDocuments.add(randomUUID, "en", status.getText());

                    // even though the TweetsWithSentimentValue has 6 fields
                    // on the constructor we call only 5 of them
                    // because the sentiment score we get later
                    tweetsWithSentimentValueList.add(
                            new TweetsWithSentimentValue(status.getUser().getScreenName(),
                                    status.getUser().getName(),
                                    status.getId(),
                                    status.getText(),
                                    randomUUID)
                    );
                }
            } catch (TwitterException te) {
                te.printStackTrace();
            }

            // TODO: delete later, only for debugging purposes
            for (int i = 0; i < tweetsWithSentimentValueList.size(); i++) {
                System.out.println(tweetsWithSentimentValueList.get(i));
            }

            // call the Get Sentiment to send the request to microsoft
            GetSentiment getSentiment = new GetSentiment(ACCESS_KEY, HOST, PATH);
            try {
                // storing the response from Microsoft in a String
                String response = getSentiment.getTheSentiment(sentimentToolResponseFromMicrosoftDocuments);
                // and we prettify the response from Microsoft
                response = getSentiment.prettify(response);
                // in this part, we are going to call the parse Sentiment Response from Microsoft
                parseSentimentResponse(response);
                System.out.println("Total Sentiment Value is: " + calculateSentimentValue(tweetsWithSentimentValueList));
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
            countNumberOfOccurrences();
            goToActivity2();
        });

        mClearHistoryButton = findViewById(R.id.clear_history_button);
        mClearHistoryButton.setOnClickListener(v -> {
            // to clear database entries, uncomment the following line
            dbHelper.onUpgrade(database, 1, 1);
            clearHistory = true;
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            searchQueryAdapter = new SearchQueryAdapter(this, getAllItems());
            recyclerView.setAdapter(searchQueryAdapter);
        });


    }


    private void goToActivity2() {
        Intent intent = new Intent(this, Main2Activity.class);
        startActivity(intent);
    }

    private void addToDb() {
        String name = mSearchQueryEditText.getText().toString();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SearchQueriesDatabaseTables.SearchQueryEntry.COLUMN_NAME, name);
        database.insert(SearchQueriesDatabaseTables.SearchQueryEntry.TABLE_NAME, null, contentValues);
        searchQueryAdapter.swapCursor(getAllItems());
        mSearchQueryEditText.getText().clear();
    }


    /**
     * Extracts all hashtags from a sample text
     *
     * @param someSampleText gets some sample text from the user
     */
    public void extractHashtagsFromAString(String someSampleText) {
        // definition of custom pattern to extract all hashtags
        Pattern MY_PATTERN = Pattern.compile("#(\\w+)");
        // run the pattern on the sample text from the user
        Matcher mat = MY_PATTERN.matcher(someSampleText);
        // list to store all the hashtags in the string
        List<String> listOfHashtagsInTheString = new ArrayList<>();
        // until you find a match iterate through the pattern
        while (mat.find()) {
            // add the founded hashtags to the list
            listOfHashtagsInTheString.add(mat.group(1).toLowerCase());

        }
        // all all the hashtags to the field variable
        listOfAllHashtags.addAll(listOfHashtagsInTheString);
    }

    // TODO: fixed this method later
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private Cursor getAllItems() {
        return database.query(SearchQueriesDatabaseTables.SearchQueryEntry.TABLE_NAME, null, null, null, null, null, null);
    }


    public void countNumberOfOccurrences() {
        occurrencesArrayList = new ArrayList<>();
        Set<String> listOfAllHashtagsWithoutDuplicates = new TreeSet<>(listOfAllHashtags);

        for (String s : listOfAllHashtagsWithoutDuplicates) {
            occurrencesArrayList.add(new HashtagAndOccurrences(s, Collections.frequency(listOfAllHashtags, s)));
        }
        Collections.sort(occurrencesArrayList, Comparator.comparing(HashtagAndOccurrences::getNumberOfOccurrences)
                .thenComparing(HashtagAndOccurrences::getHashtagName).reversed());
    }

    public static ArrayList<HashtagAndOccurrences> getOccurrencesArrayList() {
        return occurrencesArrayList;
    }

    @Override
    protected void onResume() {
        super.onResume();
        listOfAllHashtags.clear();
        occurrencesArrayList.clear();
    }

    public void parseSentimentResponse(String response) {
        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonArray json = (JsonArray) parser.parse(response).getAsJsonObject().get("documents");
        System.out.println(json);
        Example[] data = gson.fromJson(json, Example[].class);
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < tweetsWithSentimentValueList.size(); j++) {
                if (tweetsWithSentimentValueList.get(j).getRandomUUID().equals(data[j].getId())) {
                    tweetsWithSentimentValueList.get(i).setSentimentValue(
                            String.valueOf(data[i].getScore())
                    );
                    break;
                }
            }
        }
        for (TweetsWithSentimentValue tweetsWithSentimentValue : tweetsWithSentimentValueList) {
            System.out.println(
                    "Sentiment value for UUID: "
                            + tweetsWithSentimentValue.getRandomUUID()
                            + "is: "
                            + tweetsWithSentimentValue.getSentimentValue());
        }
    }

    private double calculateSentimentValue(List<TweetsWithSentimentValue> tweetsWithSentimentValueList) {
        double totalSentimentValue = 0.0;
        for (int i = 0; i < tweetsWithSentimentValueList.size(); i++) {
            if (Double.parseDouble(tweetsWithSentimentValueList.get(i).getSentimentValue()) != 0.5) {
                totalSentimentValue += Double.parseDouble(tweetsWithSentimentValueList.get(i).getSentimentValue());
            }
        }
        return totalSentimentValue / tweetsWithSentimentValueList.size();
    }
}
