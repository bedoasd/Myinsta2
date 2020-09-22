package com.example.myinsta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.myinsta.Adapter.UserAdapter;
import com.example.myinsta.Model.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FollowersActivity extends AppCompatActivity {


    String id;
    String title;

    List<String>idlist;

    RecyclerView recyclerView1;
    UserAdapter userAdapter;
    List<Users>usersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers);

        Intent  intent=getIntent();
        id=intent.getStringExtra("id");
        title=intent.getStringExtra("title");

        Toolbar toolbar=findViewById(R.id.toolbarr);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        recyclerView1=findViewById(R.id.recycler_vi);
        recyclerView1.setHasFixedSize(true);
        recyclerView1.setLayoutManager(new LinearLayoutManager(this));
        usersList=new ArrayList<>();
        userAdapter  =new UserAdapter(this,usersList,false);
        recyclerView1.setAdapter(userAdapter);

        idlist=new ArrayList<>();

        switch (title) {
            case "likes" :
                getLikes();
                break;
            case "following" :
                getFollowing();
                break;

            case "followers" :
                getFollowers();
                 break;

        }



    }


    private void getLikes() {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("likes")
                .child(id);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                idlist.clear();
                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    idlist.add(snapshot1.getKey());
                }
                ShowUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void getFollowing() {

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Follow")
                .child(id).child("following");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                idlist.clear();
                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    idlist.add(snapshot1.getKey());
                }
                ShowUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void getFollowers(){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Follow")
                .child(id).child("followers");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                idlist.clear();
                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    idlist.add(snapshot1.getKey());
                }
                ShowUsers();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

   /* private void getFollowers(){

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Follow")
                .child(id).child("followers");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                idlist.clear();
                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    idlist.add(snapshot1.getKey());
                }
                ShowUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }*/


    private void ShowUsers(){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList.clear();
                for(DataSnapshot snapshot1:snapshot.getChildren()){

                    Users users=snapshot1.getValue(Users.class);

                    for(String id : idlist)
                    {
                        if(users.getId().equals(id)){
                            usersList.add(users);
                        }

                    }

                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}