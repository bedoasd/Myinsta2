package com.example.myinsta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.myinsta.Model.Story;
import com.example.myinsta.Model.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import jp.shts.android.storiesprogressview.StoriesProgressView;

public class StoryActivity extends AppCompatActivity implements StoriesProgressView.StoriesListener {

    int counter =0;
    long presstime=0l;
    long limit=500l;

    StoriesProgressView storiesProgressView;
    ImageView img,story_photoo;
    TextView story_username;

    List<String>imagesss;
    List<String>storyids;

    String userid;

    private View.OnTouchListener touchListener=new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                    presstime=System.currentTimeMillis();
                    storiesProgressView.pause();
                    return false;
                case MotionEvent.ACTION_UP:
                    long now=System.currentTimeMillis();
                    storiesProgressView.resume();
                    return limit<now-presstime;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);

        storiesProgressView=findViewById(R.id.stories);
        img=findViewById(R.id.img);
        story_photoo=findViewById(R.id.story_photooo);
        story_username=findViewById(R.id.st_usename);
        storiesProgressView=findViewById(R.id.stories);


        userid=getIntent().getStringExtra("userid");

        getStories(userid);
        userinfo(userid);

        View reverse=findViewById(R.id.reverse);
        reverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                storiesProgressView.reverse();
            }
        });
        reverse.setOnTouchListener(touchListener);

        View skip=findViewById(R.id.skip);
        reverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                storiesProgressView.skip();
            }
        });
        skip.setOnTouchListener(touchListener);

    }

    @Override
    public void onNext() {
        Glide.with(getApplicationContext()).load(imagesss.get(++counter)).into(img);
    }

    @Override
    public void onPrev() {
        if (counter-1<0)return;

        Glide.with(getApplicationContext()).load(imagesss.get(--counter)).into(img);
    }

    @Override
    public void onComplete() {
        finish();
    }

    @Override
    protected void onDestroy() {
        storiesProgressView.destroy();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        storiesProgressView.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        storiesProgressView.resume();
        super.onResume();
    }

    private void getStories(final String userid){
        imagesss=new ArrayList<>();
        storyids=new ArrayList<>();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Story")
                .child(userid);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                imagesss.clear();
                storyids.clear();
                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    Story story=snapshot1.getValue(Story.class);
                    long timecurrent=System.currentTimeMillis();
                    if(timecurrent>story.getTimestart()&& timecurrent<story.getTimeend()){
                        imagesss.add(story.getImageurl());
                        storyids.add(story.getStoryid());
                    }
                }
                storiesProgressView.setStoriesCount(imagesss.size());
                storiesProgressView.setStoryDuration(5000l);
                storiesProgressView.setStoriesListener(StoryActivity.this);
                storiesProgressView.startStories(counter);

                Glide.with(getApplicationContext()).load(imagesss.get(counter)).into(img);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void userinfo(final String userid){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users")
                .child(userid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users= snapshot.getValue(Users.class);
                Glide.with(getApplicationContext()).load(users.getImageurl()).
                        into(story_photoo);
                story_username.setText(users.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}