package com.example.ktuweather;

import android.os.Parcel;
import android.os.Parcelable;

public class WeatherDataClass implements Parcelable {
    double temperature;
    double feels_like;
    int humidity;
    String city;

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getFeels_like() {
        return feels_like;
    }

    public void setFeels_like(double feels_like) {
        this.feels_like = feels_like;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.temperature);
        dest.writeDouble(this.feels_like);
        dest.writeInt(this.humidity);
        dest.writeString(this.city);
    }

    public WeatherDataClass() {
    }

    protected WeatherDataClass(Parcel in) {
        this.temperature = in.readDouble();
        this.feels_like = in.readDouble();
        this.humidity = in.readInt();
        this.city = in.readString();
    }

    public static final Parcelable.Creator<WeatherDataClass> CREATOR = new Parcelable.Creator<WeatherDataClass>() {
        @Override
        public WeatherDataClass createFromParcel(Parcel source) {
            return new WeatherDataClass(source);
        }

        @Override
        public WeatherDataClass[] newArray(int size) {
            return new WeatherDataClass[size];
        }
    };
}
