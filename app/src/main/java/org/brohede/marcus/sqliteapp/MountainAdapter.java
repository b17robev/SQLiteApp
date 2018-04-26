package org.brohede.marcus.sqliteapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Roberth on 4/25/2018.
 */

public class MountainAdapter extends ArrayAdapter {
    private Context context;
    private List<Mountain> mtnList = new ArrayList<>();

    public MountainAdapter(Context c, ArrayList<Mountain> list){
        super(c, 0, list);
        context = c;
        mtnList = list;
    }

    @Override
    public @NonNull
    View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        View listItem = convertView;

        if(listItem == null){
            listItem = LayoutInflater.from(context).inflate(R.layout.list_item_textview, parent, false);
        }

        Mountain currentMountain = mtnList.get(position);

        TextView name = listItem.findViewById(R.id.mtn_name);
        name.setText(currentMountain.getName());


        return listItem;
    }

}
