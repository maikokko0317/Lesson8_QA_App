package jp.techacademy.saito.maiko.lesson8_qa_app;

import java.io.Serializable;
import java.util.ArrayList;

public class Favorite implements Serializable {
    private String mUid;
    private String mQuestionUid;
    private ArrayList<Answer> mAnswerArrayList;

    public String getUid() {
        return mUid;
    }

    public String getQuestionUid() {
        return mQuestionUid;
    }

    //public ArrayList<Answer> getAnswers() {
    //    return mAnswerArrayList;
    //}

    public Favorite(String uid, String questionUid, ArrayList<Answer> answers) {
        mUid = uid;
        mQuestionUid = questionUid;
        //mAnswerArrayList = answers;
    }
}