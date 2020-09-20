package com.example.myinsta.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myinsta.Adapter.PostAdapter;
import com.example.myinsta.Model.Post;
import com.example.myinsta.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class PostDetailsFragment extends Fragment {

    String postid;
    private RecyclerView recyclerView0;
    private List<Post>postlist;
    private PostAdapter postAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=  inflater.inflate(R.layout.fragment_post_details, container, false);

        SharedPreferences preferences =getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        postid=preferences.getString("postid","none");

        recyclerView0=view.findViewById(R.id.recycler_view0);
        recyclerView0.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        recyclerView0.setLayoutManager(linearLayoutManager);

        postlist=new ArrayList<>();
        postAdapter=new PostAdapter(getContext(),postlist);
        recyclerView0.setAdapter(postAdapter);

        readPost();

        return view;

    }
    public void readPost(){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Posts").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postlist.clear();
                Post post=snapshot.getValue(Post.class);
                postlist.add(post);

                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}