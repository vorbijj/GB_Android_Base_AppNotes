package com.example.gb_android_base_appnotes;

import android.os.Parcel;
import android.os.Parcelable;

public class Note implements Parcelable {
    private int indexNote;
    private String title;
    private String date;
    private String description;

    public Note(int indexNote, String title, String date, String description) {
        this.indexNote = indexNote;
        this.title = title;
        this.date = date;
        this.description = description;
    }

    protected Note(Parcel in) {
        indexNote = in.readInt();
        title = in.readString();
        date = in.readString();
        description = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(indexNote);
        dest.writeString(title);
        dest.writeString(date);
        dest.writeString(description);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Note> CREATOR = new Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };

    public int getIndexNote() {
        return indexNote;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }
}
