package com.uni.bremen.hastag_inspektor;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;
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
import java.util.Arrays;
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

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    // Microsoft Sentiment
    private final String ACCESS_KEY = "a860e30cb3834a23a9c4a071988df408";
    private final String HOST = "https://westcentralus.api.cognitive.microsoft.com";
    private final String PATH = "/text/analytics/v2.1/sentiment";

    // for Twitter4j
    private ConfigurationBuilder configurationBuilder;

    private SQLiteDatabase database;

    private ArrayList<String> listOfAllHashtags = new ArrayList<>();

    // muss das hier deklariert werden, denn wir brauchen dies hier spaeter in unserer second-activity
    private static ArrayList<HashtagAndOccurences> occurrencesArrayList = new ArrayList<>();

    private static ArrayList<Tweet> tweets = new ArrayList<>();

    public static boolean clearHistory = false;

    private SearchView searchView;

    // Search Query Adapter
    public SearchQueryAdapter searchQueryAdapter;
    // History Chip Groups Adapter
    public HistoryAdapter historyAdapter;
    // Trend Adapter
    public TrendAdapter trendAdapter;

    // Sentiment
    private FusedLocationProviderClient client;

    private double longitude;
    private double latitude;

    private ArrayList<TweetsWithSentimentValue> tweetsWithSentimentValueList = new ArrayList<>();
    private static ArrayList<String> trendsList;


    @Override
    protected void onCreate (Bundle savedInstanceState) {
        final String CONSUMER_KEY = "bQJQwFuUGy7B9uPuuxJtgp7Q8";
        final String CONSUMER_SECRET = "6VMslMNhonTvkz3HR5uzE1x1kozltaPXxhZtPzRzJAIoYaOBLt";
        final String ACCESS_TOKEN = "799690696970092544-AvEdEbkruF9YDOwT28yqNZ5CxC4p6yR";
        final String ACCESS_TOKEN_SECRET = "YutFlDv8mP7GDiycmSNlvQ7wQYWCafphEjK6j6cmT4bNU";

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        BottomNavigationView navigation = findViewById(R.id.nav_view);
        navigation.setOnNavigationItemSelectedListener(MainActivity.this);

        SearchQueryDBHelper dbHelper = new SearchQueryDBHelper(this);
        database = dbHelper.getWritableDatabase();
        dbHelper.onCreate(database);

        searchQueryAdapter = new SearchQueryAdapter(this, getAllItems());
        historyAdapter = new HistoryAdapter(this, getAllItems());

        trendAdapter = new TrendAdapter(this, trendsList);
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

        TwitterFactory tf = new TwitterFactory(configurationBuilder.build());
        Twitter twitter = tf.getInstance();

        // Location
        client = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        client.getLastLocation().addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess (Location location) {
                if (location != null) {
                    longitude = location.getLongitude();
                    latitude = location.getLatitude();
                    trendsList = new ArrayList<>();

                    try {
                        ResponseList<twitter4j.Location> locations;
                        locations = twitter.getClosestTrends(new GeoLocation(latitude, longitude));
                        int woeid = locations.get(0).getWoeid();
                        Trends trends = twitter.getPlaceTrends(woeid);
                        for (int i = 0; i < trends.getTrends().length; i++) {
                            if (trends.getTrends()[i].getName().charAt(0) == '#') {
                                trendsList.add(trends.getTrends()[i].getName());
                            }
                        }
                        trendAdapter.update(trendsList);
                        trendAdapter.notifyDataSetChanged();

                    } catch (TwitterException ex) {
                        System.out.println(ex.getMessage());
                        // exit from the app, if you couldn't get the location from the user
                        System.exit(1);
                    }

                }
            }
        });

        // @Thanh: Search
        searchView = findViewById(R.id.searchView);
        searchView.setQueryHint("Enter a Hashtag to search");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit (String query) {
                // every time the user clicks on the search button, we must clear the list
                tweetsWithSentimentValueList.clear();

                if (query.charAt(0) != '#') {
                    query = "#" + query;
                }
                startSearch(query);

                Intent myIntent = new Intent(MainActivity.this, SearchResultsActivity.class);
                myIntent.putExtra("title", query);
                startActivity(myIntent);
                return false;
            }

            @Override
            public boolean onQueryTextChange (String newText) {
                return false;
            }
        });

        // @Thanh: Search button
        FloatingActionButton searchButton = findViewById(R.id.search_button);
        searchButton.setOnClickListener(v -> {
            if (!checkFocusRec(searchView)) {
                searchView.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            } else if (searchView.getQuery() != null) {
                searchView.setQuery(searchView.getQuery(), true);
            } else {
                Toast.makeText(getBaseContext(), "NULL", Toast.LENGTH_LONG).show();
            }

        });

        loadFragment(new ExploreFragment());
    }

    private void addToDb (String name) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(SearchQueriesDatabaseTables.SearchQueryEntry.COLUMN_NAME, name);
        database.insert(SearchQueriesDatabaseTables.SearchQueryEntry.TABLE_NAME, null, contentValues);
        searchQueryAdapter.notifyDataSetChanged();
    }

    public void extractHashtagsFromAString (String someSampleText) {
        Pattern MY_PATTERN = Pattern.compile("#(\\w+)");
        Matcher mat = MY_PATTERN.matcher(someSampleText);
        List<String> listOfHashtagsInTheString = new ArrayList<>();
        while (mat.find()) {
            listOfHashtagsInTheString.add(mat.group(1).toLowerCase());

        }
        for (String s : listOfHashtagsInTheString) {
            //System.out.println("Hashtag: " + s);
        }
        listOfAllHashtags.addAll(listOfHashtagsInTheString);
        System.out.println("++++++---------------------------------------------------++++++++");
    }

    private boolean isNetworkAvailable ( ) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public Cursor getAllItems ( ) {
        Cursor newCursor = database.query(SearchQueriesDatabaseTables.SearchQueryEntry.TABLE_NAME, null, null, null, null, null, null);
        return newCursor;
    }

    public List<HashtagAndOccurences> countNumberOfOccurrences ( ) {
        occurrencesArrayList = new ArrayList<>();
        Set<String> listOfAllHashtagsWithoutDuplicates = new TreeSet<>(listOfAllHashtags);

        for (String s : listOfAllHashtagsWithoutDuplicates) {
            occurrencesArrayList.add(new HashtagAndOccurences(s, Collections.frequency(listOfAllHashtags, s)));
        }

        System.out.println("Alle Hhashtags in dieser Suchanfrage ist wie folgt: ");
        System.out.println(Arrays.toString(listOfAllHashtags.toArray()));
        System.out.println("Unsorted List: ");
        System.out.println("Number of Occurrences of each item is: " + Arrays.toString(occurrencesArrayList.toArray()));


        Collections.sort(occurrencesArrayList, Comparator.comparing(HashtagAndOccurences::getNumberOfOccurrences)
                .thenComparing(HashtagAndOccurences::getHashtagName).reversed());

        System.out.println("-----------------------------------------------------");
        System.out.println("Sorted List: ");
        for (HashtagAndOccurences hashtagAndOccurences : occurrencesArrayList) {
            //System.out.println("Hashtag: " + hashtagAndOccurences.getHashtagName() + " || number of occurs: " + hashtagAndOccurences.getNumberOfOccurrences());
        }
        return occurrencesArrayList;
    }

    public static ArrayList<HashtagAndOccurences> getOccurrencesArrayList ( ) {
        return occurrencesArrayList;
    }

    public static ArrayList<Tweet> getTweets () {
        System.out.println("Size " + tweets.size());
        return tweets;
    }

    public static ArrayList<String> getTrendsList ( ) {
        return trendsList;
    }

    @Override
    protected void onResume ( ) {
        super.onResume();
        listOfAllHashtags.clear();
        occurrencesArrayList.clear();

    }

    private boolean loadFragment (Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected (@NonNull MenuItem item) {
        Fragment fragment = null;

        switch (item.getItemId()) {
            case R.id.navigation_explore:
                fragment = new ExploreFragment();
                break;

            case R.id.navigation_history:
                fragment = new HistoryFragment();
                break;
        }
        return loadFragment(fragment);
    }

    public void clearHistory ( ) {
        new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.DeleteDialog))
                .setMessage("Do you really want to clear search history? This cannot be undone!")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick (DialogInterface dialog, int whichButton) {
                        SearchQueryDBHelper dbHelper = new SearchQueryDBHelper(MainActivity.this);
                        dbHelper.onUpgrade(database, 1, 1);
                        clearHistory = true;
                        searchQueryAdapter.swapCursor(getAllItems());
                        searchQueryAdapter.notifyDataSetChanged();
                        historyAdapter.swapCursor(getAllItems());
                        historyAdapter.notifyDataSetChanged();
                        Toast.makeText(MainActivity.this, "Search History Cleared!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();

    }

    public void startSearch (String query) {
        addToDb(query);
        configurationBuilder = new ConfigurationBuilder();
        configurationBuilder.setDebugEnabled(true)
                .setOAuthConsumerKey("bQJQwFuUGy7B9uPuuxJtgp7Q8")
                .setOAuthConsumerSecret("6VMslMNhonTvkz3HR5uzE1x1kozltaPXxhZtPzRzJAIoYaOBLt")
                .setOAuthAccessToken("799690696970092544-AvEdEbkruF9YDOwT28yqNZ5CxC4p6yR")
                .setOAuthAccessTokenSecret("YutFlDv8mP7GDiycmSNlvQ7wQYWCafphEjK6j6cmT4bNU");

        TwitterFactory tf = new TwitterFactory(configurationBuilder.build());
        Twitter twitter = tf.getInstance();
        Query q = new Query(query + " -filter:retweets");
        q.setLocale("en");
        q.setLang("en");
        q.setCount(50);

        searchQueryAdapter.swapCursor(getAllItems());
        searchQueryAdapter.notifyDataSetChanged();
        historyAdapter.swapCursor(getAllItems());
        historyAdapter.notifyDataSetChanged();

        int countNumberOfTweets = 0;
        tweets = new ArrayList<>();
        try {
            QueryResult result = twitter.search(q);
            for (Status status : result.getTweets()) {
                tweets.add(new Tweet(status));
                //System.out.println("@" + status.getUser().getScreenName() + " - " + status.getText());
                extractHashtagsFromAString(status.getText());
                countNumberOfTweets++;
            }
        } catch (TwitterException te) {
            Toast.makeText(getBaseContext(), te.getMessage(), Toast.LENGTH_LONG).show();
        }

        /*
        // create a list of documents to store the response from Microsoft
        Documents sentimentToolResponseFromMicrosoftDocuments = new Documents();
        try {
            QueryResult result = twitter.search(q);
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
        }*/
        countNumberOfOccurrences();
        Toast.makeText(getBaseContext(), "Number of Tweets: " + countNumberOfTweets, Toast.LENGTH_LONG).show();
        Toast.makeText(getBaseContext(), query, Toast.LENGTH_LONG).show();
    }

    private boolean checkFocusRec (View view) {
        if (view.isFocused())
            return true;

        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                if (checkFocusRec(viewGroup.getChildAt(i)))
                    return true;
            }
        }
        return false;
    }

    public void parseSentimentResponse (String response) {
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

    private double calculateSentimentValue (List<TweetsWithSentimentValue> tweetsWithSentimentValueList) {
        double totalSentimentValue = 0.0;
        for (int i = 0; i < tweetsWithSentimentValueList.size(); i++) {
            if (Double.parseDouble(tweetsWithSentimentValueList.get(i).getSentimentValue()) != 0.5) {
                totalSentimentValue += Double.parseDouble(tweetsWithSentimentValueList.get(i).getSentimentValue());
            }
        }
        return totalSentimentValue / tweetsWithSentimentValueList.size();
    }

}
