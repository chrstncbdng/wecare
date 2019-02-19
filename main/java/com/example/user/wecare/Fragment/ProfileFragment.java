package com.example.user.wecare.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.wecare.R;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


public class ProfileFragment extends Fragment {

    public android.support.v7.widget.Toolbar toolbar;

    ImageView profilePicture;
    TextView profileName, profileEmail, profileScore;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private DatabaseReference userRef, highScoreRef, nameRef, emailRef;

    private String facebookUserId = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Profile");

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        profilePicture = view.findViewById(R.id.profile_picture);
        profileName = view.findViewById(R.id.name);
        profileEmail = view.findViewById(R.id.email);
        profileScore = view.findViewById(R.id.high_score);


        for (UserInfo profile : firebaseUser.getProviderData()) {
            if (FacebookAuthProvider.PROVIDER_ID.equals(profile.getProviderId())) {
                facebookUserId = profile.getUid();
            }
        }

        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(facebookUserId);
        nameRef = userRef.child("Name");
        emailRef = userRef.child("Email");
        highScoreRef = userRef.child("High Score");

        String photoUrl = "https://graph.facebook.com/" + facebookUserId + "/picture?height=500";
        Picasso.get().load(photoUrl).into(profilePicture);

        nameRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.getValue().toString().trim();
                profileName.setText("Name: " + name);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                profileName.setText(databaseError.getDetails());
            }
        });

        emailRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String email = dataSnapshot.getValue().toString().trim();
                profileEmail.setText("Email Address: " + email);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                profileEmail.setText(databaseError.getDetails());
            }
        });

        highScoreRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String highScore = dataSnapshot.getValue().toString().trim();
                profileScore.setText("High Score: " + highScore);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                profileScore.setText(databaseError.getDetails());
            }
        });

        return view;
    }
}
