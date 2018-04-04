package com.example.mircea.instaapp.Activities;

import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.mircea.instaapp.Adapters.FollowsPagerAdapter;
import com.example.mircea.instaapp.HelperClasses.EmailRefactor;
import com.example.mircea.instaapp.R;
import com.example.mircea.instaapp.Raw.Friend;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class FriendsListActivity extends AppCompatActivity {

    private ViewPager followsPager;
    private FirebaseAuth mAuth;
    private FollowsPagerAdapter followAdp;
    private TabLayout followsTab;
    private Bundle bundle;

    private byte[] profilePictureBytes;

    private ArrayList<Friend> followers;
    private ArrayList<Friend> following;

    private EmailRefactor emailRefactor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);

        setupUi();
    }

    private void setupUi() {

        final String crrUserEmail = getCrrUserEmail();

        followsPager = findViewById(R.id.followsPager);

        bundle = new Bundle();
        following = new ArrayList<>();
        followers = new ArrayList<>();
        emailRefactor = new EmailRefactor();

        displayFollows(crrUserEmail);

    }

    private void displayFollows(String crrUserEmail) {

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users/" + crrUserEmail);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                populateArrayLists(dataSnapshot);

                followAdp = new FollowsPagerAdapter(getApplicationContext(), getSupportFragmentManager(), bundle);
                followsPager.setAdapter(followAdp);

                followsTab = findViewById(R.id.pagerTabs);
                followsTab.setupWithViewPager(followsPager);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    //TODO THIS IS UGLY AS SHIT AND PRONE TO DIE

    private void populateArrayLists(DataSnapshot dataSnapshot){

        //final EmailRefactor emailRefactor = new EmailRefactor();

        for(DataSnapshot data: dataSnapshot.child("Following").getChildren()){

            //profilePictureBytes = getProfilePictureInBytes(data.getKey());

            getProfilePictureInBytes(data.getKey(), following);

            //following.add(new Friend(emailRefactor.decodeEmail(data.getKey()), null));
        }

        bundle.putParcelableArrayList("following", following);

        for(DataSnapshot data:dataSnapshot.child("Followers").getChildren()){

            getProfilePictureInBytes(data.getKey(), followers);

            //followers.add(new Friend(emailRefactor.decodeEmail(data.getKey()), null));
        }

        bundle.putParcelableArrayList("followers", followers);
    }

    private void getProfilePictureInBytes(String key, ArrayList<Friend> fArr) {

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users/" + key);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                getPictureFromFirebase(dataSnapshot.child("profilePicture").getValue(String.class), fArr, key);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getPictureFromFirebase(String picture, ArrayList<Friend> fArr, String key) {

        final StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(picture);

        final long ONE_MEGABYTE = 1024*1024*5;

        storageReference.getBytes(ONE_MEGABYTE).addOnCompleteListener(new OnCompleteListener<byte[]>() {
            @Override
            public void onComplete(@NonNull Task<byte[]> task) {

                if(task.isSuccessful()){

                    fArr.add(new Friend(emailRefactor.decodeEmail(key), task.getResult()));
                    followAdp = new FollowsPagerAdapter(getApplicationContext(), getSupportFragmentManager(), bundle);
                    followsPager.setAdapter(followAdp);

                }
            }
        });
    }

    public String getCrrUserEmail() {

        mAuth = FirebaseAuth.getInstance();
        EmailRefactor emailRefactor = new EmailRefactor();
        return emailRefactor.refactorEmail(mAuth.getCurrentUser().getEmail());
    }
}
