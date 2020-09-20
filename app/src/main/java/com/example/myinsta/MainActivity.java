package com.example.myinsta;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.myinsta.Fragment.HomeFragment;
import com.example.myinsta.Fragment.NotificationFragment;
import com.example.myinsta.Fragment.profileFragment;
import com.example.myinsta.Fragment.searchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class  MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    Fragment selectedfragment=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView=findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        Bundle intent=getIntent().getExtras();
        if (intent!=null){
            String publisherid=intent.getString("publisherid");
            SharedPreferences.Editor editor=getSharedPreferences("PREFS",MODE_PRIVATE).edit();
            editor.putString("profileid",publisherid);
            editor.apply();

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
                    ,new profileFragment()).commit();
        }
        else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
                    ,new HomeFragment()).commit();
        }


    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                    switch (menuItem.getItemId()){

                        case R.id.nav_home :
                            selectedfragment=new HomeFragment();
                            break;


                        case R.id.nav_search :
                            selectedfragment=new searchFragment();
                            break;

                        case R.id.nav_add :
                            selectedfragment=null;
                            startActivity(new Intent(MainActivity.this,PostActivity.class));
                            break;

                        case R.id.nav_heart :
                            selectedfragment=new NotificationFragment();
                            break;

                        case R.id.nav_profile :
                            SharedPreferences.Editor editor=getSharedPreferences("PREFS",MODE_PRIVATE).edit();
                            editor.putString("profileid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                            editor.apply();
                            selectedfragment=new profileFragment();
                            break;

                    }

                    if (selectedfragment!=null){
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
                        ,selectedfragment).commit();
                    }

                    return true;

                }
            };
}