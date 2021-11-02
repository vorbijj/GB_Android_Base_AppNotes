package com.example.gb_android_base_appnotes.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class CardNote implements Parcelable {
    private String id;
    private String title;
    private Date date;
    private String description;
    private boolean like;

    public CardNote(String title, Date date, String description, boolean like) {
        this.title = title;
        this.date = date;
        this.description = description;
        this.like = like;
    }

    protected CardNote(Parcel in) {
        title = in.readString();
        date = new Date(in.readLong());
        description = in.readString();
        like = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeLong(date.getTime());
        dest.writeString(description);
        dest.writeByte((byte) (like ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CardNote> CREATOR = new Creator<CardNote>() {
        @Override
        public CardNote createFromParcel(Parcel in) {
            return new CardNote(in);
        }

        @Override
        public CardNote[] newArray(int size) {
            return new CardNote[size];
        }
    };

    public boolean isLike() {
        return like;
    }

    public String getTitle() {
        return title;
    }

    public Date getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
