package com.alexstyl.specialdates.events;

import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.alexstyl.specialdates.ErrorTracker;
import com.alexstyl.specialdates.events.database.EventSQLiteOpenHelper;
import com.alexstyl.specialdates.events.database.EventsDBContract.AnnualEvents;
import com.alexstyl.specialdates.events.database.EventsDBContract.DynamicEvents;

public class PeopleEventsPersister {

    private final EventSQLiteOpenHelper helper;

    public PeopleEventsPersister(EventSQLiteOpenHelper helper) {
        this.helper = helper;
    }

    public void deleteAllNamedays() {
        deleteAllEventsOfType(EventColumns.TYPE_NAMEDAY);
    }

    public void deleteAllBirthdays() {
        deleteAllEventsOfType(EventColumns.TYPE_BIRTHDAY);
    }

    private void deleteAllEventsOfType(int type) {
        SQLiteDatabase database = helper.getWritableDatabase();
        database.delete(AnnualEvents.TABLE_NAME, AnnualEvents.EVENT_TYPE + "=" + type, null);
        database.delete(DynamicEvents.TABLE_NAME, DynamicEvents.EVENT_TYPE + "=" + type, null);
    }

    public void insertDynamicEvents(ContentValues[] values) {
        insertEventsInTable(values, DynamicEvents.TABLE_NAME);
    }

    public void insertAnnualEvents(ContentValues[] values) {
        insertEventsInTable(values, AnnualEvents.TABLE_NAME);
    }

    private void insertEventsInTable(ContentValues[] values, String tableName) {
        SQLiteDatabase database = helper.getWritableDatabase();
        try {
            database.beginTransaction();
            for (ContentValues value : values) {
                database.insert(tableName, null, value);
            }
            database.setTransactionSuccessful();
        } catch (SQLException ex) {
            ErrorTracker.track(ex);
        } finally {
            database.endTransaction();
        }
    }
}
