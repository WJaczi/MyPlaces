package com.example.android.myplaces;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

/**
 * Created by micha on 08.06.2017.
 */

public class ExpandableListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<String> listDataHeader;
    private HashMap<String,List<String>> listHashMap;

    public ExpandableListAdapter(Context context, List<String> listDataHeader, HashMap<String, List<String>> listHashMap ) {
        this.listDataHeader = listDataHeader;
        this.listHashMap = listHashMap;
        this.context = context;
    }
    public void setExpandableLists(HashMap listHashMap,List listDataHeader){
        this.listHashMap =  listHashMap;
        this.listDataHeader = listDataHeader;
    }

    @Override
    public Object getChild(int i, int i1) {
        return this.listHashMap.get(this.listDataHeader.get(i)).get(i1);//i=group item, i1=childItem
    }
    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public View getChildView(final int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        final String childText=(String)getChild(i,i1);
        if (view ==null)
        {
            LayoutInflater inflater =(LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view=inflater.inflate(R.layout.list_item,null);
        }
        TextView txtListChild=(TextView)view.findViewById(R.id.lblListItem);
        txtListChild.setText(childText);
        return view;
    }

    @Override
    public int getChildrenCount(int i) {
        return this.listHashMap.get(this.listDataHeader.get(i)).size();
    }

    @Override
    public Object getGroup(int i) {
        return this.listDataHeader.get(i);
    }

    @Override
    public int getGroupCount() {
        return this.listDataHeader.size();
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public View getGroupView(int i, boolean b1, View view, ViewGroup viewGroup) {
        String headerTitle = (String)getGroup(i);
        if(view==null)
        {
            LayoutInflater inflater =(LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view=inflater.inflate(R.layout.list_group,null);
        }
        TextView lblListHeader = (TextView)view.findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);
        return view;
    }


    @Override
    public boolean hasStableIds() {
        return false;
    }





    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
}















