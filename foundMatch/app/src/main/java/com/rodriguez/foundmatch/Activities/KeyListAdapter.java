package com.rodriguez.foundmatch.Activities;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ArrayAdapter;

import com.rodriguez.foundmatch.Keys.KeyInformation;
import com.rodriguez.foundmatch.R;

import java.util.ArrayList;

public class KeyListAdapter extends ArrayAdapter<KeyInformation> {

    private ArrayList<KeyInformation> dataSet;
    Context mContext;

    private static class ViewHolder{
        ImageView kimage;
        TextView txtname, txtdesc;
    }

    public KeyListAdapter(ArrayList<KeyInformation> data, Context context) {
        super(context, R.layout.activity_keyinfo, data);
        this.dataSet = data;
        this.mContext=context;
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.activity_keyinfo, null);
            viewHolder = new ViewHolder();
            viewHolder.kimage = (ImageView) convertView.findViewById(R.id.key_image);
            viewHolder.txtname = (TextView) convertView.findViewById(R.id.key_name);
            viewHolder.txtdesc = (TextView) convertView.findViewById(R.id.key_description);

            result=convertView;

            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        lastPosition = position;

        viewHolder.kimage.setImageBitmap(dataSet.get(position).getKeyImage());
        viewHolder.txtname.setText(dataSet.get(position).getKeyName());
        viewHolder.txtdesc.setText(dataSet.get(position).getKeyDescription());

        // Return the completed view to render on screen
        return convertView;
    }
}