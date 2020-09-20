package com.example.myinsta.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myinsta.Adapter.NotificatioAdapter;
import com.example.myinsta.Model.Notification;
import com.example.myinsta.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class NotificationFragment extends Fragment {

    private RecyclerView recyclerView;
    private NotificatioAdapter notificatioAdapter;
    private List<Notification>notificationslist;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_notification, container, false);
        recyclerView=view.findViewById(R.id.recycler_view_notif);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        notificationslist=new ArrayList<>();
        notificatioAdapter=new NotificatioAdapter(getContext(),notificationslist);
        recyclerView.setAdapter(notificatioAdapter);


        readNotifications();


        return view;
    }

    private void readNotifications() {
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Notifications").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                notificationslist.clear();
                for(DataSnapshot snapshot1: snapshot.getChildren()){
                    Notification notification=snapshot1.getValue(Notification.class);
                    notificationslist.add(notification);

                }
                Collections.reverse(notificationslist);
                notificatioAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}