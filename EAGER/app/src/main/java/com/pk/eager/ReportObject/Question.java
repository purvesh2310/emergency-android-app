package com.pk.eager.ReportObject;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by kimpham on 7/9/17.
 */


public class Question implements Parcelable {
    String question;
    ArrayList<Choice> choice = new ArrayList<>();

    //constructors
    public Question(String question, ArrayList<Choice> choice){
        this.question = question;
        this.choice = choice;
    }

    public Question(String question){this.question = question;}


    public Question(){
        choice = new ArrayList<Choice>();
    }


    //class methods
    public String toString(){
        if(choice.size()==0)
            return "";
        String s = "";
        for(Choice c: choice){
            s+=c.toString()+"\n";
        }
        return question+": Yes" + "\n" + s;
    }

    public String getQuestion(){
        return question;
    }

    public ArrayList<Choice> getChoice(){
        return choice;
    }

    public int numAnswer(){
        return choice.size();
    }


    //Parceble implementation
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(question);
        dest.writeList(choice);
    }

    public static final Parcelable.Creator<Question> CREATOR
            = new Parcelable.Creator<Question>() {
        public Question createFromParcel(Parcel in) {
            return new Question(in);
        }

        public Question[] newArray(int size) {
            return new Question[size];
        }
    };

    private Question(Parcel in) {
        question = in.readString();
        choice = in.readArrayList(Choice.class.getClassLoader());
    }


}
