package comnilssonharnertjerhamre.httpsgithub.parrot_android;

import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;

import java.io.Console;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView mBottomNav;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initiate current user to -1, AKA none.
        DataHolder.setData(-1);

        queue = Volley.newRequestQueue(this);

        // Creating logic for displaying correct fragment after click on bottom navigation bar.
        mBottomNav = (BottomNavigationView) findViewById(R.id.navigation);
        mBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment selectedFragment = null;
                        switch (item.getItemId()) {
                            case R.id.menu_feed:
                                selectedFragment = FragmentFeed.newInstance();
                                break;
                            case R.id.menu_record:
                                selectedFragment = FragmentRecord.newInstance();
                                break;
                            case R.id.menu_about_us:
                                selectedFragment = FragmentAbout.newInstance();
                                break;
                        }

                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.frame_layout, selectedFragment);
                        transaction.commit();
                        return true;
                    }
                });

        //Manually displaying the first fragment - one time only
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, FragmentFeed.newInstance());
        transaction.commit();

        showUI();

    }

    /*
    Toggling between login screen and fragment view if a user is signed in.
     */

    private void showUI() {
        if(DataHolder.getData() == -1) {
            findViewById(R.id.login).setVisibility(View.VISIBLE);
            findViewById(R.id.frame_layout).setVisibility(View.INVISIBLE);
            findViewById(R.id.navigation).setVisibility(View.INVISIBLE);
        } else {
            findViewById(R.id.login).setVisibility(View.INVISIBLE);
            findViewById(R.id.frame_layout).setVisibility(View.VISIBLE);
            findViewById(R.id.navigation).setVisibility(View.VISIBLE);
        }
    }

    /*
    Sign in of user.
     */
    public void onWake(View v) {

        // HTTP POST request.
        String url = "http://ec2-34-210-104-209.us-west-2.compute.amazonaws.com:45678/wake";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Success", response);

                int res = Integer.parseInt(response);

                if(res > 0) {

                    // OK, user signed in.

                    Log.d("Wake", "Awaken with id " + res);

                    // Set id of user so that the app can user it.
                    DataHolder.setData(res);

                    // Toggle UI.
                    showUI();

                } else {
                    Log.d("Wake", "Its not time to wake yet");
                    if(res == -4) {
                        Toast.makeText(getApplication(), "The sun isn't even up..!\n(Bad login)", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("URLError", "Getting chirps didn't work");
            }
        }){
            @Override
            protected Map<String, String> getParams()
            {
                // Adding data to the POST request. Username and password.
                Map<String, String> params = new HashMap<String, String>();
                params.put("uname", ((EditText) findViewById(R.id.uname)).getText().toString());
                params.put("pw", ((EditText) findViewById(R.id.pw)).getText().toString());

                return params;
            }
        };
        // Sending HTTP POST request.
        queue.add(stringRequest);
    }

    /*
    Register of new user.
     */

    public void onBirth(View v) {

        // HTTP POST request.
        String url = "http://ec2-34-210-104-209.us-west-2.compute.amazonaws.com:45678/birth";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Success", response);

                int res = Integer.parseInt(response);

                if(res > 0) {
                    // OK, user registered

                    Log.d("Birth", "Mother gave birth to you.");

                    // Set id of user so the rest of the app can access id.
                    DataHolder.setData(res);

                    // Toggle UI.
                    showUI();

                } else {
                    Log.d("Wake", "Its not time to wake yet");
                    if(res == -5) {
                        Toast.makeText(getApplication(), "Too long username or password\nMax is 48 symbols", Toast.LENGTH_SHORT).show();
                    }
                    if(res == -2) {
                        Toast.makeText(getApplication(), "Parrot name is already taken! :(", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("URLError", "Getting chirps didn't work");
            }
        }){
            @Override
            protected Map<String, String> getParams()
            {
                // Adding data to the POST request. Username and password.
                Map<String, String> params = new HashMap<String, String>();
                params.put("uname", ((EditText) findViewById(R.id.uname)).getText().toString());
                params.put("pw", ((EditText) findViewById(R.id.pw)).getText().toString());

                return params;
            }
        };
        // Send HTTP POST request.
        queue.add(stringRequest);


    }


}
