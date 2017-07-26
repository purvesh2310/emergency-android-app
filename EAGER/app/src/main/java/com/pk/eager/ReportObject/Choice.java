package com.pk.eager.ReportObject;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by kimpham on 7/9/17.
 */

public class Choice implements Parcelable {
    String content;
    ArrayList<String> subItems = new ArrayList<>();
    final String TAG = "Choice";
    //Constructor
    public Choice(String content, ArrayList<String> subItems){
        this.content = content;
        if(subItems!=null)
            this.subItems = subItems;
    }

    public String toString(){
        if(subItems.size() == 0)
            return content;
        String sub = "";
        for(String s : subItems){
            sub+=s+", ";
        }
        Log.d(TAG, sub.substring(0, sub.length()-2));
        return content+": " + sub.substring(0, sub.length()-2);
    }

    public Choice(String content){this.content = content;}

    public boolean equals(Object obj){
        if(this == obj) return true;
        if(obj == null) return false;
        if(obj.getClass() != this.getClass()) return false;

        Choice c = (Choice) obj;
        return c.content.equals(this.content);
    }

    //class methods
    public String getContent(){
        return content;
    }

    public boolean hasSubItems(){
        if(subItems == null) return false;
        return true;
    }

    public void addSubItem(String item){
        subItems.add(item);
    }

    public int numSubItems(){
        return subItems.size();
    }

    public ArrayList<String> getSubItems(){
        return subItems;
    }

    //Parceble implementation
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(content);
        dest.writeList(subItems);
    }

    public static final Parcelable.Creator<Choice> CREATOR
            = new Parcelable.Creator<Choice>() {
        public Choice createFromParcel(Parcel in) {
            return new Choice(in);
        }

        public Choice[] newArray(int size) {
            return new Choice[size];
        }
    };

    private Choice(Parcel in) {
        content = in.readString();
        subItems = in.readArrayList(String.class.getClassLoader());
    }


}
