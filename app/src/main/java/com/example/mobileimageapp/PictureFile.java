package com.example.mobileimageapp;

import android.os.Parcel;
import android.os.Parcelable;

public class PictureFile implements Parcelable {
    private String dosyaUriString;

    public PictureFile(String dosyaUriString) {
        this.dosyaUriString = dosyaUriString;
    }

    protected PictureFile(Parcel in) {
        dosyaUriString = in.readString();
    }

    public static final Creator<PictureFile> CREATOR = new Creator<PictureFile>() {
        @Override
        public PictureFile createFromParcel(Parcel in) {
            return new PictureFile(in);
        }

        @Override
        public PictureFile[] newArray(int size) {
            return new PictureFile[size];
        }
    };

    public String getDosyaUriString() {
        return dosyaUriString;
    }

    public void setDosyaUriString(String dosyaUriString) {
        this.dosyaUriString = dosyaUriString;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(dosyaUriString);
    }
}