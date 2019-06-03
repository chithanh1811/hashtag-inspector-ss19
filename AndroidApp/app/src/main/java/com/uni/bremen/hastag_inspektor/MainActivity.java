package com.uni.bremen.hastag_inspektor;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    // define private variable
/*    // userInput as a String
    private String userInput;
    // Button
    private Button searchButton;
    // The EditText view
    private EditText mSearchQueryEditText;*/
    // for Twitter4j
    private ConfigurationBuilder configurationBuilder;

    private SQLiteDatabase database;

    private ArrayList<String> listOfAllHashtags = new ArrayList<>();

    // muss das hier deklariert werden, denn wir brauchen dies hier spaeter in unserer second-activity
    private static ArrayList<HashtagAndOccurences> occurrencesArrayList = new ArrayList<>();

    public static boolean clearHistory = false;

    private SearchView searchView;

    // Search Query Adapter
    public SearchQueryAdapter searchQueryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadFragment(new ExploreFragment());

        BottomNavigationView navigation = findViewById(R.id.nav_view);
        navigation.setOnNavigationItemSelectedListener(MainActivity.this);

        SearchQueryDBHelper dbHelper = new SearchQueryDBHelper(this);
        database = dbHelper.getWritableDatabase();
        dbHelper.onCreate(database);

        searchQueryAdapter = new SearchQueryAdapter(this, getAllItems());

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

        // @Thanh: Search
        searchView = findViewById(R.id.searchView);
        searchView.setQueryHint("Enter a Hashtag to search");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.charAt(0) != '#') {
                    query = "#" + query;
                }
                Toast.makeText(getBaseContext(), query, Toast.LENGTH_LONG).show();
                addToDb(query);
                searchQueryAdapter.swapCursor(getAllItems());
                searchQueryAdapter.notifyDataSetChanged();
                startSearch(query);
                Intent myIntent = new Intent(MainActivity.this, SearchResultsActivity.class);
                myIntent.putExtra("title", query);
                startActivity(myIntent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        // @Thanh: Search button
        FloatingActionButton searchButton = findViewById(R.id.search_button);
        searchButton.setOnClickListener(v -> {
                searchView.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
        });

        // TODO @Thanh: tap anywhere to hide the keyboard
        //InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        //imm.hideSoftInputFromWindow(view.getWindowToken(),0);

        // @Thanh: Trending Hashtags
        /*trendRecyclerView = (RecyclerView) findViewById(R.id.trending_recyclerView_exploreFragment);
        trendLayoutManager = new LinearLayoutManager(this);
        trendRecyclerView.setHasFixedSize(true);
        trendAdapter = new MyAdapter(getOccurrencesArrayList());

        trendRecyclerView.setLayoutManager(trendLayoutManager);
        trendRecyclerView.setAdapter(trendAdapter);*/


       /* mSearchQueryEditText = findViewById(R.id.searchQueryEditText);
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


        searchButton.setOnClickListener(v -> {
            userInput = mSearchQueryEditText.getText().toString();

            if (userInput.charAt(0) != '#') {
                userInput = "#" + userInput;
            }
            addToDb();
            System.out.println("The user has entered the following test: " + userInput);
            System.out.println("mTextMessage.getText() is = " + mSearchQueryEditText.getText().toString());
            Query query = new Query(userInput + " -filter:retweets");
//            query.setLang("de");
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
            countNumberOfOccurrences();
            goToActivity2();
        });*/


    }

    private void addToDb(String name) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(SearchQueriesDatabaseTables.SearchQueryEntry.COLUMN_NAME, name);
        database.insert(SearchQueriesDatabaseTables.SearchQueryEntry.TABLE_NAME, null, contentValues);
    }

    public void extractHashtagsFromAString(String someSampleText) {
        Pattern MY_PATTERN = Pattern.compile("#(\\w+)");
        Matcher mat = MY_PATTERN.matcher(someSampleText);
        List<String> listOfHashtagsInTheString = new ArrayList<>();
        while (mat.find()) {
            listOfHashtagsInTheString.add(mat.group(1).toLowerCase());

        }
        for (String s : listOfHashtagsInTheString) {
            System.out.println("Hashtag: " + s);
        }
        listOfAllHashtags.addAll(listOfHashtagsInTheString);
        System.out.println("++++++---------------------------------------------------++++++++");
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public Cursor getAllItems() {
        Cursor newCursor = database.query(SearchQueriesDatabaseTables.SearchQueryEntry.TABLE_NAME, null, null, null, null, null, null);
        return newCursor;
    }

    public List<HashtagAndOccurences> countNumberOfOccurrences() {
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
            System.out.println("Hashtag: " + hashtagAndOccurences.getHashtagName() + " || number of occurs: " + hashtagAndOccurences.getNumberOfOccurrences());
        }
        return occurrencesArrayList;
    }

    public static ArrayList<HashtagAndOccurences> getOccurrencesArrayList() {
        return occurrencesArrayList;
    }

    @Override
    protected void onResume() {
        super.onResume();
        listOfAllHashtags.clear();
        occurrencesArrayList.clear();

    }

    private boolean loadFragment(Fragment fragment) {
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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

    public void clearHistory() {
        new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.DeleteDialog))
                .setMessage("Do you really want to clear search history? This cannot be undone!")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        SearchQueryDBHelper dbHelper = new SearchQueryDBHelper(MainActivity.this);
                        dbHelper.onUpgrade(database, 1, 1);
                        clearHistory = true;
                        searchQueryAdapter.swapCursor(getAllItems());
                        searchQueryAdapter.notifyDataSetChanged();
                        Toast.makeText(MainActivity.this, "Search History Cleared!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();

    }

    public void startSearch(String query){
        configurationBuilder = new ConfigurationBuilder();
        configurationBuilder.setDebugEnabled(true)
                .setOAuthConsumerKey("bQJQwFuUGy7B9uPuuxJtgp7Q8")
                .setOAuthConsumerSecret("6VMslMNhonTvkz3HR5uzE1x1kozltaPXxhZtPzRzJAIoYaOBLt")
                .setOAuthAccessToken("799690696970092544-AvEdEbkruF9YDOwT28yqNZ5CxC4p6yR")
                .setOAuthAccessTokenSecret("YutFlDv8mP7GDiycmSNlvQ7wQYWCafphEjK6j6cmT4bNU");

        TwitterFactory tf = new TwitterFactory(configurationBuilder.build());
        Twitter twitter = tf.getInstance();
        Query q = new Query(query + " -filter:retweets");
        q.setCount(50);
        int countNumberOfTweets = 0;
        try {
            QueryResult result = twitter.search(q);
            for (Status status : result.getTweets()) {
                System.out.println("@" + status.getUser().getScreenName() + " - " + status.getText());
                extractHashtagsFromAString(status.getText());
                countNumberOfTweets++;
            }
        } catch (TwitterException te) {
            Toast.makeText(getBaseContext(), te.getMessage(), Toast.LENGTH_LONG).show();
        }
        countNumberOfOccurrences();
        Toast.makeText(getBaseContext(), "Number of Tweets: " + countNumberOfTweets, Toast.LENGTH_LONG).show();
        Toast.makeText(getBaseContext(), query, Toast.LENGTH_LONG).show();
    }
}
