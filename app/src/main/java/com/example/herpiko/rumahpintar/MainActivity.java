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
            mSocket = IO.socket("http://128.199.95.141:2999");
        } catch (URISyntaxException e) {}
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

        mSocket.on("whoareu", onWhoareu);
        mSocket.on("message", onMessage);

        mSocket.connect();
    }
    public void hidup (View v) {
        mSocket.emit("ledOn");
        Toast msg = Toast.makeText(this, "Hidupkan lampu", Toast.LENGTH_SHORT);
        msg.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
        msg.show();
        Log.d("SOCKET SWITCH LED", "ON");
    }

    public void mati (View v) {
        mSocket.emit("ledOff");
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

    public void onSectionAttached(int number) {
        //Globals g = (Globals)getApplication();
        View nyalakan = findViewById(R.id.kendali);
        View matikan = findViewById(R.id.off);
        View temp = findViewById(R.id.temp);
        View humid = findViewById(R.id.humid);
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);

                //g.setTitle("Kendali");

                nyalakan.setVisibility(View.VISIBLE);
                matikan.setVisibility(View.VISIBLE);
                humid.setVisibility(View.INVISIBLE);
                temp.setVisibility(View.INVISIBLE);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                //g.setTitle("Pantau");

                nyalakan.setVisibility(View.INVISIBLE);
                matikan.setVisibility(View.INVISIBLE);
                humid.setVisibility(View.VISIBLE);
                temp.setVisibility(View.VISIBLE);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
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
    public void connected(){
        Toast msg = Toast.makeText(this, "Koneksi socket berhasil tersambung", Toast.LENGTH_SHORT);
        msg.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
        msg.show();

    }
    private Emitter.Listener onWhoareu = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            runOnUiThread (new Thread(new Runnable() {
                public void run() {

                    Log.d("SOCKET JOIN", "awwwwwww man! it's connected!");
                    connected();
                    // Join as mobile client
                    // See https://github.com/herpiko/nf-iot-end/blob/master/README.md
                    mSocket.emit("join-ng");
                }
            }));
        }
    };
   private Emitter.Listener onMessage = new Emitter.Listener() {

        @Override
        public void call(final Object... args) {
            Log.d("SOCKET MESSAGE", "new message");
            runOnUiThread (new Thread(new Runnable() {
                public void run() {
                    JSONObject obj = (JSONObject) args[0];

                    String temp;
                    String humid;
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
                        Log.e("ERROR", e.toString());
                        return;
                    }

                }
            }));
        }
    };




}
