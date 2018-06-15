package com.team45.gtpickupsports;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Andrew on 11/4/2014.
 */
public class MultiSelectionSpinner<T> extends Spinner implements
        OnMultiChoiceClickListener {
    String[] _items = null;
    ArrayList<T> realItemList = null;
    boolean[] mSelection = null;
    String noneSelectedText = "";

    ArrayAdapter<String> simple_adapter;

    public MultiSelectionSpinner(Context context) {
        super(context);
        simple_adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item);
        super.setAdapter(simple_adapter);
    }

    public MultiSelectionSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        simple_adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item);
        super.setAdapter(simple_adapter);
    }

    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
        if (mSelection != null && which < mSelection.length) {
            mSelection[which] = isChecked;
            simple_adapter.clear();
            simple_adapter.add(buildSelectedItemString());
        } else {
            throw new IllegalArgumentException("Argument 'which' is out of bounds.");
        }
    }

    @Override
    public boolean performClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMultiChoiceItems(_items, mSelection, this);
        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        builder.show();
        return true;
    }

    @Override
    public void setAdapter(SpinnerAdapter adapter) {
        throw new RuntimeException("setAdapter is not supported by MultiSelectSpinner.");
    }

    public void setNoneSelectedText(String text) {
        noneSelectedText = text;
    }

    public void setItems(List<T> items) {
        _items = new String[items.size()];
        for (int i = 0; i < items.size(); i++) {
            _items[i] = items.get(i).toString();
        }
        mSelection = new boolean[_items.length];
        simple_adapter.clear();
        simple_adapter.add(noneSelectedText);
        Arrays.fill(mSelection, false);
        realItemList = new ArrayList<T>(items);
    }

    public void setSelection(int index) {
        for (int i = 0; i < mSelection.length; i++) {
            mSelection[i] = false;
        }
        if (index >= 0 && index < mSelection.length) {
            mSelection[index] = true;
        } else {
            throw new IllegalArgumentException("Index " + index + " is out of bounds.");
        }
        simple_adapter.clear();
        simple_adapter.add(buildSelectedItemString());
    }

    public List<T> getSelected() {
        List<T> selection = new LinkedList<T>();
        for (int i = 0; i < _items.length; ++i) {
            if (mSelection[i]) {
                selection.add(realItemList.get(i));
            }
        }
        return selection;
    }

    private String buildSelectedItemString() {
        StringBuilder sb = new StringBuilder();
        boolean foundOne = false;

        for (int i = 0; i < _items.length; ++i) {
            if (mSelection[i]) {
                if (foundOne) {
                    sb.append(", ");
                }
                foundOne = true;

                sb.append(_items[i]);
            }
        }
        String ret = sb.toString();
        if (ret.equals("")) return noneSelectedText;
        else return ret;
    }
}
