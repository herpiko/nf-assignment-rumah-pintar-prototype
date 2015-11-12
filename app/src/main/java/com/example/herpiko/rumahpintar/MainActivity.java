package com.example.herpiko.rumahpintar;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks{

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */


    private CharSequence mTitle;

    // SOCKET.IO
    private Socket mSocket;
    {
        try {
            // Initiate socket that would be connected to 128.199.95.141 port 2999
            mSocket = IO.socket("http://128.199.95.141:2999");
        } catch (URISyntaxException e) {
            // or die young like Gie
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        // Handle socket.io event
        // "whoareu" is triggered when the server is asking the client
        // is it a mobile app or a Raspberry device
        // This event is handled by onWhoareu() function
        mSocket.on("whoareu", onWhoareu);

        // "message" is triggered when the server is sending an emit
        // The arguments could contains information like sensor's data from Raspberry Pi device
        // This event is handled by onMessage() function
        mSocket.on("message", onMessage);

        // One line to rule them all, one line to find them,
        // one line to bring them all and in the darkness bind them
        mSocket.connect();
    }

    // This function is triggered when bulb lamp button touched. Touch it!
    public void hidup (View v) {

        // Send emit "ledOn" to VPS server.
        // "ledOn" is a command to switch on the LED in Raspberry Pi device
        mSocket.emit("ledOn");

        // Initiate a toast message
        Toast msg = Toast.makeText(this, "Hidupkan lampu", Toast.LENGTH_SHORT);
        msg.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
        msg.show();
        Log.d("SOCKET SWITCH LED", "ON");
    }

    // This function is triggered when "Matikan" button touched
    public void mati (View v) {
        // Send emit "ledOff" to VPS server.
        // "ledOff" is a command to switch of the LED in Raspberry Pi device
        mSocket.emit("ledOff");

        // Initiate a toast message
        Toast msg = Toast.makeText(this, "Matikan lampu", Toast.LENGTH_SHORT);
        msg.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
        msg.show();
        Log.d("SOCKET SWITCH LED", "OFF");
    }


    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    // Handle drawer menu touchs
    public void onSectionAttached(int number) {

        View nyalakan = findViewById(R.id.kendali);
        View matikan = findViewById(R.id.off);
        View temp = findViewById(R.id.temp);
        View humid = findViewById(R.id.humid);
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                // When "Kendali" selected, hide some component and show other
                nyalakan.setVisibility(View.VISIBLE);
                matikan.setVisibility(View.VISIBLE);
                humid.setVisibility(View.INVISIBLE);
                temp.setVisibility(View.INVISIBLE);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);

                // Hide some component and show other
                nyalakan.setVisibility(View.INVISIBLE);
                matikan.setVisibility(View.INVISIBLE);
                humid.setVisibility(View.VISIBLE);
                temp.setVisibility(View.VISIBLE);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);

                // Move to about activity
                Intent i=new Intent(getApplicationContext(),about.class);
                startActivity(i);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

    // Connected function to show toast message
    public void connected(){
        Toast msg = Toast.makeText(this, "Koneksi socket berhasil tersambung", Toast.LENGTH_SHORT);
        msg.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
        msg.show();

    }

    // onWhoareu function that handle "onwhoareu" socket event.
    // If it triggered, then the socket has been connected successfully. Yo~!
    private Emitter.Listener onWhoareu = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            runOnUiThread (new Thread(new Runnable() {
                public void run() {
                    Log.d("SOCKET JOIN", "Awwwwwww man! it's connected!");

                    // Socket has been connected. Call connected() function
                    connected();
                    // Join as mobile client

                    // The server ask for identity, answer her/him with "join-ng"
                    // "join-ng" is identity string that represent web or mobile app client
                    // The other one is "join-end" that represent a raspberry pi device
                    // See https://github.com/herpiko/nf-iot-end/blob/master/README.md
                    mSocket.emit("join-ng");
                }
            }));
        }
    };

    // onMessage function that handle "onmessage" socket event.
    // Since the raspberry pi only send a dht11 sensor, then make this function
    // parse these type of data
   private Emitter.Listener onMessage = new Emitter.Listener() {

        @Override
        public void call(final Object... args) {
            Log.d("SOCKET MESSAGE", "new message");
            runOnUiThread (new Thread(new Runnable() {
                public void run() {

                    // Grab the arguments! Convert it to JSON!
                    JSONObject obj = (JSONObject) args[0];

                    String temp;
                    String humid;

                    // Parse them all! Parse them!
                    try {
                        JSONObject data = (JSONObject) obj.get("data");
                        temp = data.getString("temp");
                        humid = data.getString("humid");
                        Log.d("TEMP", temp.toString());
                        Log.d("HUMID", temp.toString());
                        TextView tempText = (TextView)findViewById(R.id.temp);
                        tempText.setText(temp + " Celcius");
                        TextView humidText = (TextView)findViewById(R.id.humid);
                        humidText.setText(humid + " HpA");
                    } catch (JSONException e) {
                        // Awwwww!
                        Log.e("ERROR", e.toString());
                        return;
                    }

                }
            }));
        }
    };
}
