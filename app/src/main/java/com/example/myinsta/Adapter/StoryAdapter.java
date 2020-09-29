package com.example.myinsta.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myinsta.Model.Story;
import com.example.myinsta.Model.Users;
import com.example.myinsta.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.ViewHolder> {

    private Context mcontext;
    private List<Story>mStory;

    public StoryAdapter(Context mcontext, List<Story> mStory) {
        this.mcontext = mcontext;
        this.mStory = mStory;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType==0){
            View view= LayoutInflater.from(mcontext).inflate(R.layout.add_story_item,parent,false);
            return new StoryAdapter.ViewHolder(view);
        }else{
            View view= LayoutInflater.from(mcontext).inflate(R.layout.story_item,parent,false);
            return new StoryAdapter.ViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        Story story=mStory.get(position);

        userInfo(holder,story.getUserid(),position);

        if (holder.getAdapterPosition()!=0){
            seenStory(holder,story.getUserid());
        }
        if(holder.getAdapterPosition()==0){
            myStory(holder.addstory_text,holder.story_plus,false);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.getAdapterPosition()==0){
                    myStory(holder.addstory_text,holder.story_plus,true);
                }
                else{
                    //todo goto story
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return mStory.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView story_photo,story_seen,story_plus;
        public TextView  addstory_text,username_story;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            story_photo=itemView.findViewById(R.id.story_photo);
            story_seen=itemView.findViewById(R.id.story_photo_seen);
            story_plus=itemView.findViewById(R.id.story_plus);
            addstory_text=itemView.findViewById(R.id.add_story_text);
            username_story=itemView.findViewById(R.id.story_username);

        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position==0){
            return 0;
        }
        return 1;
    }

    private void userInfo(final ViewHolder viewHolder, final String userid, final int pos ){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users").child(userid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users=snapshot.getValue(Users.class);
                Glide.with(mcontext).load(users.getImageurl()).into(viewHolder.story_photo);
                if(pos!=0){
                    Glide.with(mcontext).load(users.getImageurl()).into(viewHolder.story_seen);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void myStory(final TextView textView, final ImageView imageView, final boolean click){
        DatabaseReference reference =FirebaseDatabase.getInstance().getReference("Story")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count =0;
                long timecurrent=System.currentTimeMillis();
                for (DataSnapshot snapshot1:snapshot.getChildren()){
                    Story story=snapshot1.getValue(Story.class);
                    if(timecurrent>story.getTimestart() && timecurrent<story.getTimeend())
                    {
                        count++;
                    }
                }
                if (click){
                    //todo goto dialog
                }else {
                    if(count>0){
                        textView.setText("My Story");
                        imageView.setVisibility(View.GONE);
                    }else{
                        textView.setText("AddStory");
                        imageView.setVisibility(View.VISIBLE);

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void seenStory(final ViewHolder viewHolder, String userid){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Story")
                .child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i = 0;
                for(DataSnapshot snapshot1:snapshot.getChildren()){
                   Story story= snapshot1.getValue(Story.class);
                    if(! snapshot1.child("views").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .exists() && System.currentTimeMillis()<story.getTimeend())
                    {
                        i++;
                    }
                }
                if(i>0){
                    viewHolder.story_photo.setVisibility(View.VISIBLE);
                    viewHolder.story_seen.setVisibility(View.GONE);
                }
                else{
                    viewHolder.story_photo.setVisibility(View.GONE);
                    viewHolder.story_seen.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
