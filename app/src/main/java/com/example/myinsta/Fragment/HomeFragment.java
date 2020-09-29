package com.example.myinsta.Fragment;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myinsta.Adapter.PostAdapter;
import com.example.myinsta.Adapter.StoryAdapter;
import com.example.myinsta.Model.Post;
import com.example.myinsta.Model.Story;
import com.example.myinsta.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {


    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List <Post>postLists;

    private RecyclerView recyclerView_story;
    private StoryAdapter storyAdapter;
    private List<Story> storyList;

    private List<String> followinglist;

    ProgressBar progressBar ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=   inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView=view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        progressBar=view.findViewById(R.id.progress_cicularr);

        postLists=new ArrayList<>();

        postAdapter=new PostAdapter(getContext(),postLists);
        recyclerView.setAdapter(postAdapter);

        recyclerView_story=view.findViewById(R.id.recycler_view_story);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayout=new LinearLayoutManager(getContext(),linearLayoutManager.HORIZONTAL,false);
        recyclerView_story.setLayoutManager(linearLayout);
        storyList=new ArrayList<>();
        storyAdapter=new StoryAdapter(getContext(),storyList);
        recyclerView_story.setAdapter(storyAdapter);

        checkfollowig();

        return view;


    }


    private void checkfollowig(){

        followinglist=new ArrayList<>();

        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Follow")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                followinglist.clear();
                for (DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    followinglist.add(snapshot.getKey());

                }

                ReadPosts();
                readStory();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void ReadPosts()
    {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postLists.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                 Post post=snapshot.getValue(Post.class);

                 for (String id:followinglist)
                 {
                     if (post.getPublisher().equals(id))
                     {
                         postLists.add(post);
                     }
                 }
                }
                postAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void readStory(){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Story");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long timecurrent=System.currentTimeMillis();
                storyList.clear();
                storyList.add(new Story("",0,0,"",
                        FirebaseAuth.getInstance().getCurrentUser().getUid()));
                for (String id : followinglist){
                        int countstory=0;
                        Story story=null;
                        for(DataSnapshot snapshot1:snapshot.child(id).getChildren()){
                             story=snapshot1.getValue(Story.class);

                             if(timecurrent>story.getTimestart() && timecurrent<story.getTimeend()){
                                 countstory++;
                             }
                        }
                        if (countstory>0){
                            storyList.add(story);
                        }
                }
                storyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}

