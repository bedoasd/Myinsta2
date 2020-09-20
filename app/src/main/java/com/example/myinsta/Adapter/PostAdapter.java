package com.example.myinsta.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myinsta.CommentsActivity;
import com.example.myinsta.Fragment.PostDetailsFragment;
import com.example.myinsta.Fragment.profileFragment;
import com.example.myinsta.Model.Post;
import com.example.myinsta.Model.Users;
import com.example.myinsta.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    public Context mcontext;
    public List<Post>mPost;
    public FirebaseUser firebaseUser;

    public PostAdapter(Context mcontext, List<Post> mPost) {
        this.mcontext = mcontext;
        this.mPost = mPost;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mcontext).inflate(R.layout.post_tem,parent,false);

        return new PostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        final Post post=mPost.get(position);
        Glide.with(mcontext).load(post.getPostimage()).into(holder.post_image);



        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.like.getTag().equals("like")){
                    FirebaseDatabase.getInstance().getReference().child("likes")
                          .child(post.getPostid()).child(firebaseUser.getUid()).setValue(true);

                    addNotification(post.getPublisher(),post.getPostid());

                }
                else{
                    FirebaseDatabase.getInstance().getReference().child("likes")
                            .child(post.getPostid()).child(firebaseUser.getUid()).removeValue();

                }
            }
        });

        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mcontext, CommentsActivity.class);
                intent.putExtra("postid",post.getPostid());
                intent.putExtra("publisherid",post.getPublisher());
                mcontext.startActivity(intent);

            }
        });
        holder.comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mcontext, CommentsActivity.class);
                intent.putExtra("postid",post.getPostid());
                intent.putExtra("publisherid",post.getPublisher());
                mcontext.startActivity(intent);

            }
        });


        if ("".equalsIgnoreCase(post.getDescription()))
        {
            holder.description.setVisibility(View.GONE);
        }else {
            holder.description.setVisibility(View.VISIBLE);
            holder.description.setText(post.getDescription());
        }
        PublisherInfo(holder.image_profile,holder.username,holder.publisher,post.getPublisher());

        isliked(post.getPostid(),holder.like);
        nrlikes(holder.likes,post.getPostid());
        getcomments(post.getPostid(),holder.comments);
        IsSaved(post.getPostid(),holder.save);

        holder.image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor=mcontext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                editor.putString("profileid",post.getPublisher() );
                editor.apply();

                ((FragmentActivity)mcontext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new profileFragment()).commit();

            }
        });
        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor=mcontext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                editor.putString("profileid",post.getPublisher());
                editor.apply();

                ((FragmentActivity)mcontext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new profileFragment()).commit();

            }
        });
        holder.post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor=mcontext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                editor.putString("postid",post.getPostid());
                editor.apply();

                ((FragmentActivity)mcontext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new PostDetailsFragment()).commit();

            }
        });

        holder.publisher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor=mcontext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                editor.putString("profileid",post.getPublisher());
                editor.apply();

                ((FragmentActivity)mcontext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new profileFragment()).commit();

            }
        });


        holder.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.save.getTag().equals("save")){
                    FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid())
                        .child(post.getPostid()).setValue(true);

                }else{
                    FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid())
                            .child(post.getPostid()).removeValue() ;

                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mPost.size();
    }

    public class  ViewHolder extends RecyclerView.ViewHolder{

        public ImageView image_profile,post_image,like, comment, save;
        public TextView  username,likes,comments,publisher,description;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image_profile=itemView.findViewById(R.id.image_profile);
            post_image=itemView.findViewById(R.id.post_image);
            like=itemView.findViewById(R.id.like);
            comment=itemView.findViewById(R.id.comment);
            save=itemView.findViewById(R.id.save);
            username=itemView.findViewById(R.id.username);
            likes=itemView.findViewById(R.id.likes);
            comments=itemView.findViewById(R.id.comments);
            publisher=itemView.findViewById(R.id.publisher);
            description=itemView.findViewById(R.id.description);

        }
    }
    private void getcomments(String postid, final TextView comments){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Comments").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
           comments.setText("View All"+" "+dataSnapshot.getChildrenCount()+"comments");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void isliked(final String postid , final ImageView imageView){

        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("likes")
                .child(postid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(firebaseUser.getUid()).exists()){
                    imageView.setImageResource(R.drawable.ic_liked);
                    imageView.setTag("liked");
                }
                else {
                    imageView.setImageResource(R.drawable.ic_like);
                    imageView.setTag("like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addNotification(String userid,String postid){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Notifications").child(userid);

        HashMap<String,Object>hashMap=new HashMap<>();

        hashMap.put("userid",firebaseUser.getUid());
        hashMap.put("text","liked your photo");
        hashMap.put("postid",postid);
        hashMap.put("ispost",true);

        reference.push().setValue(hashMap);

    }

    private void nrlikes(final TextView likes, String postid){

        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("likes")
                .child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                likes.setText(dataSnapshot.getChildrenCount()+"likes");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void PublisherInfo(final ImageView image_profile, final TextView username, final TextView publisher, String userid)
    {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users users=dataSnapshot.getValue(Users.class);
                Glide.with(mcontext).load(users.getImageurl()).into(image_profile);
                username.setText(users.getUsername());
                publisher.setText(users.getUsername());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void IsSaved(final String postid, final ImageView imageView){
        FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Saves")
                .child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(postid).exists())
                {
                    imageView.setImageResource(R.drawable.ic_save_black);
                    imageView.setTag("saved");

                }
                else{
                    imageView.setImageResource(R.drawable.ic_save);
                    imageView.setTag("save");
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
