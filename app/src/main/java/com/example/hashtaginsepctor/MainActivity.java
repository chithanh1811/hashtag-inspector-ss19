package com.example.hashtaginsepctor;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.view.MenuItem;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        mTextMessage = findViewById(R.id.message);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        AndroidNetworking.post("https://api.twitter.com/oauth2/token")
                .addHeaders("User-Agent", "My Twitter App v1.0.23")
                .addHeaders("Authorization", "Basic YlFKUXdGdVVHeTdCOXVQdXV4SnRncDdRODo2Vk1zbE1OaG9uVHZrejNIUjV1ekUxeDFrb3psdGFQWHhoWnRQelJ6SkFJb1lhT0JMdA==")
                .addHeaders("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8")
                .addBodyParameter("grant_type", "client_credentials")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("Response is: ");
                        System.out.println(response);
                    }

                    @Override
                    public void onError(ANError error) {
                        System.out.println("Error: ");
                        System.out.println(error.getErrorCode());
                        System.out.println(error.getErrorBody());
                    }
                });


    }

}
