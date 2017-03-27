package com.stcsw.eatwhat;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import com.stcsw.eatwhat.EatWhatItemContract.FeedEntry;
/**
 * Created by daniel.chiou on 2016/9/19.
 */
public class RestaurantCursorAdapter extends CursorAdapter {
    public RestaurantCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    // The newView method is used to inflate a new view and return it,
    // you don't bind any data to the view at this point.
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_restaurant, parent, false);
    }

    // The bindView method is used to bind all data to a given view
    // such as setting the text on a TextView.
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find fields to populate in inflated template
        TextView tvID = (TextView) view.findViewById(R.id.tvID);
        TextView tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        //TextView tvSubtitle = (TextView) view.findViewById(R.id.tvSubtitle);
        // Extract properties from cursor
        long id = cursor.getLong(cursor.getColumnIndexOrThrow(FeedEntry._ID));
        String title = cursor.getString(cursor.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_TITLE));
        //String subtitle = cursor.getString(cursor.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_SUBTITLE));
        // Populate fields with extracted properties
        tvID.setText(String.valueOf(id));
        tvTitle.setText(title);
        //tvSubtitle.setText(subtitle);
    }
}
