package com.philipslight.common;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

public class HighLightArrayAdapter extends ArrayAdapter<String> {

    private int mSelectedIndex = -1;


    public void setSelection(int position) {
        mSelectedIndex =  position;
        notifyDataSetChanged();
    }

    public HighLightArrayAdapter(Context context, int resource, List<String> list) {
        super(context, resource, list);
    }


    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View itemView =  super.getDropDownView(position, convertView, parent);

        if (position == mSelectedIndex) {
            itemView.setBackgroundColor(Color.rgb(131,223,255));
        } else {
            itemView.setBackgroundColor(Color.TRANSPARENT);
        }

        return itemView;
    }
}