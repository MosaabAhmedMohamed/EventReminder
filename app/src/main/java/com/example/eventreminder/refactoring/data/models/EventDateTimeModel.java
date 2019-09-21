package com.example.eventreminder.refactoring.data.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.api.services.calendar.model.Event;

public class EventDateTimeModel implements Parcelable {
    private String Day;
    private int startTime, endTime;
    private Event event;
    private boolean discard;

    public EventDateTimeModel(String day, int startTime, int endTime,Event event) {
        Day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.event = event;
        discard = false;

    }

    protected EventDateTimeModel(Parcel in) {
        Day = in.readString();
        startTime = in.readInt();
        endTime = in.readInt();
    }

    public static final Creator<EventDateTimeModel> CREATOR = new Creator<EventDateTimeModel>() {
        @Override
        public EventDateTimeModel createFromParcel(Parcel in) {
            return new EventDateTimeModel(in);
        }

        @Override
        public EventDateTimeModel[] newArray(int size) {
            return new EventDateTimeModel[size];
        }
    };

    public String getDay() {
        return Day;
    }

    public int getStartTime() {
        return startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public Event getEvent() {
        return event;
    }

    public boolean isDiscard() {
        return discard;
    }

    public void setDiscard(boolean discard) {
        this.discard = discard;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Day);
        dest.writeInt(startTime);
        dest.writeInt(endTime);
    }
}
