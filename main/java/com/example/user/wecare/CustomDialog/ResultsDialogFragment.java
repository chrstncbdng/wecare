package com.example.user.wecare.CustomDialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.wecare.Fragment.LearnFragment;
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

import java.util.Random;

public class ResultsDialogFragment extends DialogFragment {

    private DrawerLayout drawer;
    private NavigationView navigationView;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference userRef, highScoreRef;

    TextView resultsTitle, currentScore, highScore;
    Button closeResultsDialog;

    public int score = 0;

    private String facebookUserId = "";

    public ResultsDialogFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_results_dialog, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        for (UserInfo profile : firebaseUser.getProviderData()) {
            if (FacebookAuthProvider.PROVIDER_ID.equals(profile.getProviderId())) {
                facebookUserId = profile.getUid();
            }
        }

        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(facebookUserId);
        highScoreRef = userRef.child("High Score");

        navigationView = view.findViewById(R.id.nav_view);

        resultsTitle = view.findViewById(R.id.results_title);
        currentScore = view.findViewById(R.id.results_current_score);
        highScore = view.findViewById(R.id.results_high_score);
        closeResultsDialog = view.findViewById(R.id.close_results_dialog);
        closeResultsDialog.setEnabled(false);
        closeResultsDialog.setBackgroundResource(R.color.colorPrimary);

        drawer = view.findViewById(R.id.drawer_layout);

        Bundle args = getArguments();
        score = args.getInt("score");
        currentScore.setText("Current Score: "+score);

        highScoreRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String currentHighScore = dataSnapshot.getValue().toString().trim();
                if (score > Integer.valueOf(currentHighScore)){
                    highScore.setText("New High Score: " + score);
                    highScoreRef.setValue(score);
                } else{
                    highScore.setText("High Score: " + currentHighScore);
                }
                closeResultsDialog.setEnabled(true);
                closeResultsDialog.setBackgroundResource(R.color.colorPrimaryDark);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        closeResultsDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                navigationView = getActivity().findViewById(R.id.nav_view);
                navigationView.getMenu().findItem(R.id.nav_learn).setChecked(true);

                boolean bool = new Random().nextBoolean();
                if (bool) {
                    proceedToRateDialog();
                } else {
                    proceedToLearnFragment();
                }
            }
        });

        return view;

    }

    private void proceedToLearnFragment() {
        LearnFragment learnFragment = new LearnFragment();
        assert getFragmentManager() != null;
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, learnFragment);
        fragmentTransaction.commit();
    }

    private void proceedToRateDialog() {
        FragmentManager fragmentManager = getFragmentManager();
        RateDialogFragment rateDialogFragment = new RateDialogFragment();
        rateDialogFragment.setCancelable(false);
        assert fragmentManager != null;
        rateDialogFragment.show(fragmentManager, "fragment_rate_dialog");
    }
}
