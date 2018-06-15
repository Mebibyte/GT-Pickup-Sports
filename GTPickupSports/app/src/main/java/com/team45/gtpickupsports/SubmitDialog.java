package com.team45.gtpickupsports;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.team45.gtpickupsports.models.Location;
import com.team45.gtpickupsports.models.SportEvent;
import com.team45.gtpickupsports.networking.FirebaseWrapper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Page to submit a new sports game.
 *
 * Created by Glenn on 9/18/2014.
 */
public class SubmitDialog extends DialogFragment {
    private Spinner mSportTypeSpinner, mLocationSpinner;
    private TextView mStartDateTime, mEndDateTime;
    private GregorianCalendar startCal, endCal;
    private Dialog picker;
    private static final SimpleDateFormat date_format = new SimpleDateFormat("MMM dd, yyyy HH:mm a");

    /**
     * Called when creating a submit dialog.
     * Makes the layout and sets the starting time to be the current time.
     * @param savedInstanceState Bundle, unused.
     * @return Created dialog
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View fragView = getActivity().getLayoutInflater().inflate(R.layout.dialog_submit, (ViewGroup) getView());
        builder.setView(fragView)
                // Add action buttons
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        FirebaseWrapper.addEvent(new SportEvent(startCal, endCal,
                                mSportTypeSpinner.getSelectedItem().toString(),
                                mLocationSpinner.getSelectedItem().toString(),
                                ((Location) mLocationSpinner.getSelectedItem()).getLatitude(),
                                ((Location) mLocationSpinner.getSelectedItem()).getLongitude()));
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SubmitDialog.this.getDialog().cancel();
                    }
                });

        mSportTypeSpinner = (Spinner) fragView.findViewById(R.id.sportTypeSpinner);
        ArrayAdapter<String> sa = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, MainActivity.sportTypes);
        sa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSportTypeSpinner.setAdapter(sa);

        mLocationSpinner = (Spinner) fragView.findViewById(R.id.locationSpinner);
        ArrayAdapter<Location> sl
                = new ArrayAdapter<com.team45.gtpickupsports.models.Location>(getActivity(),
                android.R.layout.simple_list_item_1, MainActivity.locations);
        sl.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mLocationSpinner.setAdapter(sl);

        mStartDateTime = (TextView) fragView.findViewById(R.id.startDateTimeText);
        mEndDateTime = (TextView) fragView.findViewById(R.id.endDateTimeText);
        startCal = new GregorianCalendar();
        endCal = new GregorianCalendar();
        Date creationDate = startCal.getTime();
        mStartDateTime.setText(date_format.format(creationDate));
        mEndDateTime.setText(date_format.format(creationDate));

        Button mStartTimeButton = (Button) fragView.findViewById(R.id.startTimeButton);
        Button mEndTimeButton = (Button) fragView.findViewById(R.id.endTimeButton);
        Button mStartDateButton = (Button) fragView.findViewById(R.id.startDateButton);
        Button mEndDateButton = (Button) fragView.findViewById(R.id.endDateButton);

        mStartDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                picker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int newYear, int newMonth, int newDay) {
                        startCal = new GregorianCalendar(newYear, newMonth, newDay, startCal.get(Calendar.HOUR), startCal.get(Calendar.MINUTE));
                        Date creationDate = startCal.getTime();
                        mStartDateTime.setText(date_format.format(creationDate));
                    }
                }, startCal.get(Calendar.YEAR), startCal.get(Calendar.MONTH), startCal.get(Calendar.DAY_OF_MONTH));
                picker.show();
            }
        });

        mEndDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                picker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int newYear, int newMonth, int newDay) {
                        endCal = new GregorianCalendar(newYear, newMonth, newDay, endCal.get(Calendar.HOUR), endCal.get(Calendar.MINUTE));
                        Date creationDate = startCal.getTime();
                        mEndDateTime.setText(date_format.format(creationDate));
                    }
                }, endCal.get(Calendar.YEAR), endCal.get(Calendar.MONTH), endCal.get(Calendar.DAY_OF_MONTH));
                picker.show();
            }
        });

        mStartTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                picker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int newHour, int newMinute) {
                        startCal = new GregorianCalendar(startCal.get(Calendar.YEAR),
                                startCal.get(Calendar.MONTH), startCal.get(Calendar.DAY_OF_MONTH),
                                newHour, newMinute);
                        Date creationDate = startCal.getTime();
                        mStartDateTime.setText(date_format.format(creationDate));
                    }
                }, startCal.get(Calendar.HOUR), startCal.get(Calendar.MINUTE), false);
                picker.show();
            }
        });

        mEndTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                picker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int newHour, int newMinute) {
                        endCal = new GregorianCalendar(endCal.get(Calendar.YEAR),
                                endCal.get(Calendar.MONTH), endCal.get(Calendar.DAY_OF_MONTH),
                                newHour, newMinute);
                        Date creationDate = endCal.getTime();
                        mEndDateTime.setText(date_format.format(creationDate));
                    }
                }, endCal.get(Calendar.HOUR), endCal.get(Calendar.MINUTE), false);
                picker.show();
            }
        });

        return builder.create();
    }
}
