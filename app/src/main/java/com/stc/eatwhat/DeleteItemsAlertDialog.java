package com.stc.eatwhat;
import android.app.AlertDialog;
import android.content.Context;
public class DeleteItemsAlertDialog extends AlertDialog.Builder {
    public long restaurant_ID=0;
    public String restaurant_title="no title";
    public String restaurant_subtitle = "no subtitle";

    public DeleteItemsAlertDialog setValues(long restaurant_id, String restaurant_title, String restaurant_subtitle) {
        this.restaurant_title = restaurant_title;
        this.restaurant_ID = restaurant_id;
        this.restaurant_subtitle = restaurant_subtitle;
        return this;
    }

    public DeleteItemsAlertDialog(Context context) {
        super(context);
    }

}
