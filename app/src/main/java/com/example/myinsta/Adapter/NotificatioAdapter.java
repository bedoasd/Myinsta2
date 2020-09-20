package com.example.myinsta.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentContainer;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myinsta.EditProfileActivity;
import com.example.myinsta.Fragment.PostDetailsFragment;
import com.example.myinsta.Fragment.profileFragment;
import com.example.myinsta.Model.Notification;
import com.example.myinsta.Model.Post;
import com.example.myinsta.Model.Users;
import com.example.myinsta.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class NotificatioAdapter extends RecyclerView.Adapter<NotificatioAdapter.ViewHolder> {

    private Context mcontext;
    private List<Notification>mNotification;

    public NotificatioAdapter(Context context, List<Notification> mNotification) {
        this.mcontext = context;
        this.mNotification = mNotification;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mcontext).inflate(R.layout.notification_item,parent,false);
        return new  NotificatioAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final Notification notification=mNotification.get(position);
        holder.textt.setText(notification.getText());
        getUserinfo(holder.image_profilee,holder.usernamee,notification.getUserid());

        if (notification.isIspost()){
            holder.post_image.setVisibility(View.VISIBLE);
            getPostInfo(holder.post_image,notification.getPostid());
        }else{
            holder.post_image.setVisibility(View.GONE);

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(notification.isIspost()){
                    SharedPreferences.Editor editor=mcontext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                    editor.putString("postid",notification.getPostid());
                    editor.apply();

                    ((FragmentActivity)mcontext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new PostDetailsFragment()).commit();

                }
                else {

                    SharedPreferences.Editor editor=mcontext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                    editor.putString("profileid",notification.getUserid());
                    editor.apply();

                    ((FragmentActivity)mcontext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new profileFragment()).commit();

                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return mNotification.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView image_profilee,post_image;
        public TextView  usernamee,textt;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image_profilee=itemView.findViewById(R.id.image_profile);
            post_image=itemView.findViewById(R.id.post_image);
            usernamee=itemView.findViewById(R.id.usermamee);
            textt=itemView.findViewById(R.id.comment);
        }
    }

    private void getUserinfo(final ImageView imageView, final TextView username, String publisher) {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users").child(publisher);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users=snapshot.getValue(Users.class);
                Glide.with(mcontext).load(users.getImageurl()).into(imageView);
                username.setText(users.getUsername());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getPostInfo(final ImageView imageView, final String postid){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Posts").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Post post=snapshot.getValue(Post.class);
                Glide.with(mcontext).load(post.getPostimage()).into(imageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


}
