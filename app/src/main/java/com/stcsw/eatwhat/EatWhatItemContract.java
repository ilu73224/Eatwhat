package com.stcsw.eatwhat;

import android.provider.BaseColumns;

/**
 * Created by daniel.chiou on 2016/9/19.
 */
public final class EatWhatItemContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private EatWhatItemContract() {}

    /* Inner class that defines the table contents */
    public static class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "entry";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_SUBTITLE = "subtitle";
    }
}
