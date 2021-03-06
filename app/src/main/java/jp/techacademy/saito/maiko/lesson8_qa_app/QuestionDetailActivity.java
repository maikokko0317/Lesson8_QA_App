package jp.techacademy.saito.maiko.lesson8_qa_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.Button;
import android.widget.ImageButton;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import com.google.firebase.database.ValueEventListener;

public class QuestionDetailActivity extends AppCompatActivity {

    private ListView mListView;
    private Question mQuestion;
    private QuestionDetailListAdapter mAdapter;

    private DatabaseReference mAnswerRef;

    // ★課題追記ここから★
    private FirebaseUser user;
    private DatabaseReference mfavoriteRef;
    private Favorite mFavorite;
    boolean mIsFavorite = false;
    private ValueEventListener mFavoriteListener;
    private ImageButton favoriteOnButton;
    private ImageButton favoriteOffButton;
    // ★課題追記ここまで★

    private ChildEventListener mEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            HashMap map = (HashMap) dataSnapshot.getValue();

            String answerUid = dataSnapshot.getKey();

            for(Answer answer : mQuestion.getAnswers()) {
                // 同じAnswerUidのものが存在しているときは何もしない
                if (answerUid.equals(answer.getAnswerUid())) {
                    return;
                }
            }

            String body = (String) map.get("body");
            String name = (String) map.get("name");
            String uid = (String) map.get("uid");

            Answer answer = new Answer(body, name, uid, answerUid);
            mQuestion.getAnswers().add(answer);
            mAdapter.notifyDataSetChanged();

        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };


    // ★課題追記ここから★
    private ChildEventListener mEventListener2 = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            HashMap map = (HashMap) dataSnapshot.getValue();

            String favoriteUid = dataSnapshot.getKey();
            Log.d("MAIKO_LOG", "画面上のQuestionUid : " + mQuestion.getQuestionUid());
            Log.d("MAIKO_LOG", "favoriteUid : " + favoriteUid);

            if (favoriteUid.equals(mQuestion.getQuestionUid())) {
                Log.d("MAIKO_LOG", "画面上のQuestionUid : " + mQuestion.getQuestionUid());
                Log.d("MAIKO_LOG", "firebase上のQuestionUid  : " + favoriteUid);
                mIsFavorite = true;
                Log.d("MAIKO_LOG", "イベントリスナmIsFavorite  : " + mIsFavorite);
                favoriteOnButton.setVisibility(View.INVISIBLE); //offボタンのみ表示
                favoriteOffButton.setVisibility(View.VISIBLE); //offボタンのみ表示
            }
            //else {
             //   mIsFavorite = false;
             //   Log.d("MAIKO_LOG", "イベントリスナmIsFavorite  : " + mIsFavorite);
             //   favoriteOffButton.setVisibility(View.INVISIBLE); //onボタンのみ表示
            //}

        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            //HashMap map = (HashMap) dataSnapshot.getValue();

            //String favoriteUid = dataSnapshot.getKey();

            //if (favoriteUid.equals(mQuestion.getQuestionUid())) {
             //   return;
            //} else {
                mIsFavorite = false;
                Log.d("MAIKO_LOG", "イベントリスナmIsFavorite  : " + mIsFavorite);
                favoriteOffButton.setVisibility(View.INVISIBLE); //onボタンのみ表示
                favoriteOnButton.setVisibility(View.VISIBLE); //onボタンのみ表示
            //}

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };
    // ★課題追記ここまで★



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_detail);

        // ★課題追記ここから★
        // ログイン済みのユーザーを取得する
        favoriteOnButton = (ImageButton) findViewById(R.id.favoriteOnButton);
        favoriteOffButton = (ImageButton) findViewById(R.id.favoriteOffButton);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            favoriteOnButton.setVisibility(View.INVISIBLE);
            favoriteOffButton.setVisibility(View.INVISIBLE);
        } else {
            Log.d("MAIKO_LOG", "--------質問詳細画面START-------- ");
            Log.d("MAIKO_LOG", "mIsFavorite : " + mIsFavorite);
            Log.d("MAIKO_LOG", "userID : " + user.getUid());


            // Databaseへの参照
            mfavoriteRef = FirebaseDatabase.getInstance().getReference().child("favorites").child(user.getUid());
            mfavoriteRef.addChildEventListener(mEventListener2);

            favoriteOffButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //firebaseから削除し
                    Log.d("MAIKO_LOG", "お気に入りボタン押下");
                    Log.d("MAIKO_LOG", "mIsFavorite : true->false");
                    DatabaseReference dataBaseReference = FirebaseDatabase.getInstance().getReference();
                    mfavoriteRef = dataBaseReference.child(Const.FavoritesPATH).child(user.getUid()).child(mQuestion.getQuestionUid());
                    mfavoriteRef.removeValue();
                }
            });

            favoriteOnButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //firebaseに登録
                    Log.d("MAIKO_LOG", "お気に入りボタン押下");
                    Log.d("MAIKO_LOG", "mIsFavorite : false->true");
                    DatabaseReference dataBaseReference = FirebaseDatabase.getInstance().getReference();
                    //mfavoriteRef = dataBaseReference.child(Const.FavoritesPATH).child(user.getUid()).child(mQuestion.getQuestionUid());
                    mfavoriteRef = dataBaseReference.child(Const.FavoritesPATH).child(user.getUid()).child(mQuestion.getQuestionUid());

                    Map<String, String> data = new HashMap<String, String>();
                    //data.put(mQuestion.getQuestionUid(), String.valueOf(mQuestion.getGenre()));
                    //data.put("qid", mQuestion.getQuestionUid());
                    data.put("genre", String.valueOf(mQuestion.getGenre()));
                    mfavoriteRef.setValue(data);
                    //mfavoriteRef.push().setValue(mQuestion.getQuestionUid());
                }
            });
            // ★課題追記ここまで★
        }


        // 渡ってきたQuestionのオブジェクトを保持する
        Bundle extras = getIntent().getExtras();
        mQuestion = (Question) extras.get("question");

        setTitle(mQuestion.getTitle());

        // ListViewの準備
        mListView = (ListView) findViewById(R.id.listView);
        mAdapter = new QuestionDetailListAdapter(this, mQuestion);
        mListView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // ログイン済みのユーザーを取得する
                //FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if (user == null) {
                    // ログインしていなければログイン画面に遷移させる
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                } else {
                    // Questionを渡して回答作成画面を起動する
                    // --- ここから ---
                    Intent intent = new Intent(getApplicationContext(), AnswerSendActivity.class);
                    intent.putExtra("question", mQuestion);
                    startActivity(intent);
                    // --- ここまで ---
                }
            }
        });

        DatabaseReference dataBaseReference = FirebaseDatabase.getInstance().getReference();
        mAnswerRef = dataBaseReference.child(Const.ContentsPATH).child(String.valueOf(mQuestion.getGenre())).child(mQuestion.getQuestionUid()).child(Const.AnswersPATH);
        mAnswerRef.addChildEventListener(mEventListener);

    }
}