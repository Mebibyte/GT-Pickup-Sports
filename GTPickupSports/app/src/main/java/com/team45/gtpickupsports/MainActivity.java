package com.team45.gtpickupsports;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.team45.gtpickupsports.models.Location;
import com.team45.gtpickupsports.models.SportEvent;
import com.team45.gtpickupsports.models.User;
import com.team45.gtpickupsports.networking.FirebaseWrapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Main app page
 *
 * Created by feliperoriz on 9/12/14.
 */
public class MainActivity extends FragmentActivity {
    private ArrayList<String> testList = new ArrayList<String>();
    public static ArrayList<SportEvent> eventList = new ArrayList<SportEvent>();
    public static ArrayList<SportEvent> shownEventList = new ArrayList<SportEvent>();
    private ArrayAdapter<String> adapter;
    private FloatingActionButton fab;
    public static boolean filtered;

    public static ArrayList<Location> locations = new ArrayList<Location>();
    public static ArrayList<String> sportTypes = new ArrayList<String>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String username = User.username;

        if (username == null) {
            Intent logout = new Intent(this, LoginActivity.class);
            logout.putExtra("LOGOUT", true);
            startActivity(logout);
            finish();
        }
        setContentView(R.layout.activity_main);

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, testList);
        ListView list = (ListView) findViewById(R.id.eventsList);
        list.setAdapter(adapter);
        filtered = false;

        /**
         * Listener for getting a Detailed Event.
         */
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(adapterView.getContext(), EventDetailActivity.class);
                intent.putExtra("event", i);

                if (isRegisteredForEvent(i))
                    intent.putExtra("Attending", true);
                else
                    intent.putExtra("Attending", false);
                startActivity(intent);
            }
        });

        ((TextView) findViewById(R.id.userLoggedIn)).setText("Hello, " + username + "!");
        FirebaseWrapper.myFirebase.child("Events").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                eventList.clear();
                @SuppressWarnings("unchecked")
                Map<String, HashMap<String, Object>> newData = (Map<String, HashMap<String, Object>>) dataSnapshot.getValue();
                if (newData != null) {
                    for (Map.Entry<String, HashMap<String, Object>> entry : newData.entrySet()) {
                        SportEvent se = SportEvent.JSONtoObject(entry.getValue());
                        se.setId(entry.getKey());
                        eventList.add(se);
                    }
                    Collections.sort(eventList);
                    if (!filtered) updateShownEvents(eventList);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        // Get Locations
        FirebaseWrapper.myFirebase.child("Locations").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                @SuppressWarnings("unchecked")
                Map<String, HashMap<String, Object>> newData = (Map<String, HashMap<String, Object>>) dataSnapshot.getValue();
                if (newData != null) {
                    locations.clear();
                    for (Map.Entry<String, HashMap<String, Object>> entry : newData.entrySet()) {
                        locations.add(Location.JSONtoObject(entry.getKey(), entry.getValue()));
                    }
                    if (sportTypes.size() != 0 && locations.size() != 0) {
                        fab.setVisibility(View.VISIBLE);
                    } else {
                        fab.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        // Get Locations and Sport Types
        FirebaseWrapper.myFirebase.child("Sports").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                @SuppressWarnings("unchecked")
                Map<String, HashMap<String, Object>> newData = (Map<String, HashMap<String, Object>>) dataSnapshot.getValue();
                if (newData != null) {
                    sportTypes.clear();
                    for (Map.Entry<String, HashMap<String, Object>> entry : newData.entrySet()) {
                        sportTypes.add(entry.getKey());
                    }
                    if (sportTypes.size() != 0 && locations.size() != 0) {
                        fab.setVisibility(View.VISIBLE);
                    } else {
                        fab.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        fab = (FloatingActionButton) findViewById(R.id.fabbutton);
        fab.setColor(Color.BLUE);
        fab.setDrawable(getResources().getDrawable(R.drawable.ic_content_new));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new SubmitDialog();

                FragmentTransaction ft = getFragmentManager().beginTransaction();
                Fragment prev = getFragmentManager().findFragmentByTag("add");

                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);

                // Create and show the dialog.
                newFragment.show(ft, "add");
            }
        });
        fab.setVisibility(View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_logout:
                Intent logout = new Intent(this, LoginActivity.class);
                logout.putExtra("LOGOUT", true);
                startActivity(logout);
                finish();
                break;
            case R.id.action_filter:
                DialogFragment newFragment = new FilterDialog();

                FragmentTransaction ft = getFragmentManager().beginTransaction();
                Fragment prev = getFragmentManager().findFragmentByTag("add");

                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);

                // Create and show the dialog.
                newFragment.show(ft, "add");
                break;
            case R.id.action_myEvents:
                ArrayList<SportEvent> filtered = new ArrayList<SportEvent>(MainActivity.eventList);
                Iterator<SportEvent> itr = filtered.iterator();
                while(itr.hasNext()) {
                    SportEvent se = itr.next();
                    if (!(isRegisteredForEvent(eventList.indexOf(se)))) itr.remove();
                }
                showFullListButton();
                updateShownEvents(filtered);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public synchronized void updateShownEvents(ArrayList<SportEvent> events) {
        adapter.clear();
        shownEventList.clear();
        shownEventList.addAll(events);
        for (SportEvent event: shownEventList) {
            adapter.add(event.toString());
        }
    }

    public void showFullListButton() {
        Button button = (Button) findViewById(R.id.fullEventListButton);
        button.setClickable(true);
        button.setVisibility(View.VISIBLE);
        filtered = true;
    }

    public void showFullEventList(View view) {
        Button b = (Button) view;
        b.setVisibility(View.INVISIBLE);
        b.setClickable(false);
        updateShownEvents(eventList);
        filtered = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();

        View empty = findViewById(R.id.empty);
        ListView list = (ListView) findViewById(R.id.eventsList);
        list.setEmptyView(empty);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    /**
     * Checks to see if the User's event list contains the one clicked on in the Main Event List.
     * @param index Index for the Shown Event List.
     * @return true, if the event clicked is contained in the User's list.
     */
    public boolean isRegisteredForEvent(int index) {
        return User.eventList.contains(shownEventList.get(index).getId());
    }
}
