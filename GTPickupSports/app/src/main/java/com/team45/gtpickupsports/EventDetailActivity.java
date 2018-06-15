package com.team45.gtpickupsports;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.team45.gtpickupsports.models.SportEvent;
import com.team45.gtpickupsports.models.User;
import com.team45.gtpickupsports.networking.FirebaseWrapper;

/**
 * Detailed Event Page.
 *
 * Created by Felipe Roriz on 10/17/14.
 */
public class EventDetailActivity extends Activity {
    private TextView mAttendees;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        final SportEvent event = MainActivity.shownEventList.get(getIntent().getIntExtra("event", -1));

        // Toggle Join/Leave Event Button
        showRegisterButton(getIntent().getBooleanExtra("Attending", false));

        GoogleMap map = ((MapFragment)getFragmentManager().findFragmentById(R.id.event_map_location)).getMap();
        LatLng location = new LatLng(event.getLocation().getLatitude(), event.getLocation().getLongitude());
        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 17));
        map.addMarker(new MarkerOptions()
                .title(event.getLocation().getName())
                .position(location));

        TextView mLocationName = (TextView)findViewById(R.id.event_location);
        TextView mSportType = (TextView)findViewById(R.id.event_sport_type);
        TextView mStartTime = (TextView)findViewById(R.id.event_start_time);
        TextView mEndTime = (TextView)findViewById(R.id.event_end_time);
        mAttendees = (TextView)findViewById(R.id.event_attendees);

        Button mJoinButton = (Button)findViewById(R.id.join_event_button);
        Button mLeaveButton = (Button)findViewById(R.id.leave_event_button);

        String locationName = mLocationName.getText().toString() + " " + event.getLocation().getName();
        mLocationName.setText(locationName);

        String sportType = mSportType.getText().toString() + " " + event.getSportType();
        mSportType.setText(sportType);

        String startTime = mStartTime.getText().toString() + "\n" + event.getFormattedDate(event.getStartTime());
        mStartTime.setText(startTime);

        String endTime = mEndTime.getText().toString() + "\n" + event.getFormattedDate(event.getEndTime());
        mEndTime.setText(endTime);

        // Join this Event
        mJoinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseWrapper.addAttendee(User.username, event);
                showRegisterButton(true);
                mAttendees.setText("Number of Attendees: " + event.getAttendees());
            }
        });

        // Leave this event
        mLeaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseWrapper.removeAttendee(User.username, event);
                showRegisterButton(false);
                mAttendees.setText("Number of Attendees: " + event.getAttendees());
            }
        });

        mAttendees.setText(mAttendees.getText().toString() + " " + event.getAttendees());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.event_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        else if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Toggle the Join/Leave Button.
     * @param isRegistered True if User is attending this event.
     */
    public void showRegisterButton(boolean isRegistered) {
        if (isRegistered) {
            findViewById(R.id.join_event_button).setVisibility(View.GONE);
            findViewById(R.id.leave_event_button).setVisibility(View.VISIBLE);
        }
        else {
            findViewById(R.id.join_event_button).setVisibility(View.VISIBLE);
            findViewById(R.id.leave_event_button).setVisibility(View.GONE);
        }
    }
}
