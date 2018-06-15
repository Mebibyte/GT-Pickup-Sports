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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.team45.gtpickupsports.models.Location;
import com.team45.gtpickupsports.models.SportEvent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

/**
 * @author Andrew on 11/4/2014.
 */
public class FilterDialog extends DialogFragment {
    private MultiSelectionSpinner<String> mSportTypeSpinner;
    private MultiSelectionSpinner<Location> mLocationSpinner;
    private TextView mStartDateTime, mEndDateTime, mStartTitle, mEndTitle;
    private GregorianCalendar startCal, endCal;
    private Dialog picker;
    private static final SimpleDateFormat
            date_format = new SimpleDateFormat("MMM dd, yyyy HH:mm a"),
            day_format = new SimpleDateFormat("MMM dd, yyyy"),
            time_format = new SimpleDateFormat("HH:mm");
    private boolean startTimeChanged = false, endTimeChanged = false;
    private boolean startDateChanged = false, endDateChanged = false;

    @Override
    @SuppressWarnings("unchecked")
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View fragView = getActivity().getLayoutInflater().inflate(R.layout.dialog_filter, (ViewGroup) getView());
        builder.setView(fragView) // Add action buttons
            .setPositiveButton("Filter", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    filterEvents(startCal, endCal, mSportTypeSpinner.getSelected(),
                            mLocationSpinner.getSelected());

                }
            })
            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    FilterDialog.this.getDialog().cancel();
                }
            });

        mSportTypeSpinner = (MultiSelectionSpinner<String>) fragView.findViewById(R.id.sportTypeMultiSpinner);
        mSportTypeSpinner.setNoneSelectedText("Any Sport");
        mSportTypeSpinner.setItems(MainActivity.sportTypes);

        mLocationSpinner = (MultiSelectionSpinner<Location>) fragView.findViewById(R.id.locationMultiSpinner);
        mLocationSpinner.setNoneSelectedText("Any Location");
        mLocationSpinner.setItems(MainActivity.locations);

        mStartTitle = (TextView) fragView.findViewById(R.id.startDateTimeTitle);
        mEndTitle = (TextView) fragView.findViewById(R.id.endDateTimeTitle);
        mStartDateTime = (TextView) fragView.findViewById(R.id.startDateTimeText);
        mEndDateTime = (TextView) fragView.findViewById(R.id.endDateTimeText);
        startCal = new GregorianCalendar();
        endCal = new GregorianCalendar();
        mStartDateTime.setText("Not Specified");
        mEndDateTime.setText("Not Specified");

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
                        startDateChanged = true;
                        if (!startTimeChanged) {
                            mStartTitle.setText("Start Date");
                            mStartDateTime.setText(day_format.format(creationDate));
                        } else {
                            mStartTitle.setText("Start Date & Time");
                            mStartDateTime.setText(date_format.format(creationDate));
                        }
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
                        endDateChanged = true;
                        if (!endTimeChanged) {
                            mEndTitle.setText("End Date");
                            mEndDateTime.setText(day_format.format(creationDate));
                        } else {
                            mEndTitle.setText("End Date & Time");
                            mEndDateTime.setText(date_format.format(creationDate));
                        }
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
                        startTimeChanged = true;
                        if (!startDateChanged) {
                            mStartTitle.setText("Start Time");
                            mStartDateTime.setText(time_format.format(creationDate));
                        } else {
                            mStartTitle.setText("Start Date & Time");
                            mStartDateTime.setText(date_format.format(creationDate));
                        }
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
                        endTimeChanged = true;
                        if (!endDateChanged) {
                            mEndTitle.setText("End Time");
                            mEndDateTime.setText(time_format.format(creationDate));
                        } else {
                            mEndTitle.setText("End Date & Time");
                            mEndDateTime.setText(date_format.format(creationDate));
                        }
                    }
                }, endCal.get(Calendar.HOUR), endCal.get(Calendar.MINUTE), false);
                picker.show();
            }
        });

        return builder.create();
    }

    /**
     * filters events based on any criteria that was selected. Iterates through the list several times
     * because as soon as an element doesn't match a given criteria, it can be tossed out and checks against
     * other criteria are useless
     *
     * @param startCal Starting calendar
     * @param endCal Ending calendar
     * @param sportsList Sports
     * @param locationList Locations
     */
    private void filterEvents(GregorianCalendar startCal, GregorianCalendar endCal, List<String> sportsList, List<Location> locationList) {
        ArrayList<SportEvent> filtered = new ArrayList<SportEvent>(MainActivity.eventList);

        Iterator<SportEvent> itr = filtered.iterator();
        while(itr.hasNext()) {
            SportEvent se = itr.next();

            // Filter by sport (if any were selected)
            if (sportsList.size() > 0) {
                if (!(sportsList.contains(se.getSportType()))) {
                    itr.remove();
                    continue;
                }
            }

            // Filter by location (if any were selected)
            if (locationList.size() > 0) {
                if (!(locationList.contains(se.getLocation()))) {
                    itr.remove();
                    continue;
                }
            }

            // The user selected a specific start date lower bound
            if (startDateChanged) {
                //either the event has a higher year or higher/equal day_of year value (1-365)
                if (!(se.getEndTime().get(Calendar.YEAR) > startCal.get(Calendar.YEAR)
                        || se.getEndTime().get(Calendar.DAY_OF_YEAR) >= startCal.get(Calendar.DAY_OF_YEAR))) {
                    itr.remove();
                    continue;
                }
            }

            // The user selected a specific end date upper bound
            if (endDateChanged) {
                //either the event has a lower year or lower/equal day_of year value (1-365)
                if (!(se.getStartTime().get(Calendar.YEAR) < endCal.get(Calendar.YEAR)
                        || se.getStartTime().get(Calendar.DAY_OF_YEAR) >= endCal.get(Calendar.DAY_OF_YEAR))) {
                    itr.remove();
                    continue;
                }
            }

            // The user selected a specific start time lower bound
            if (startTimeChanged) {
                if (!(se.getEndTime().getTimeInMillis() % 86400000 >= startCal.getTimeInMillis() % 86400000))
                    itr.remove();
                    continue;
            }

            // The user selected a specific start time lower bound
            if (endTimeChanged) {
                if (!(se.getStartTime().getTimeInMillis() % 86400000 <= endCal.getTimeInMillis() % 86400000))
                    itr.remove();
            }
        }

        MainActivity main = (MainActivity) getActivity();
        main.showFullListButton();
        main.updateShownEvents(filtered);
    }
}
