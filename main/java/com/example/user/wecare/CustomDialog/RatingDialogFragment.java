package com.example.user.wecare.CustomDialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.wecare.Fragment.AboutFragment;
import com.example.user.wecare.Fragment.ChatbotFragment;
import com.example.user.wecare.Fragment.LearnFragment;
import com.example.user.wecare.Fragment.ProfileFragment;
import com.example.user.wecare.R;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RatingDialogFragment extends android.support.v4.app.DialogFragment {

    private NavigationView navigationView;

    TextView ratingText;
    RatingBar ratingBar;
    EditText ratingFeedback;
    Button closeRatingDialog, submitRating;

    private String facebookUserId = "";

    boolean fromRateDialog = false;

    public RatingDialogFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_rating_dialog, container, false);

        Bundle args = getArguments();
        if (args != null){
            fromRateDialog = args.getBoolean("fromRateDialog");
        }

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        for (UserInfo profile : firebaseUser.getProviderData()) {
            if (FacebookAuthProvider.PROVIDER_ID.equals(profile.getProviderId())) {
                facebookUserId = profile.getUid();
            }
        }

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(facebookUserId);
        final DatabaseReference rateRef = userRef.child("Rating");
        final DatabaseReference feedbackRef = userRef.child("Feedback");

        ratingText = view.findViewById(R.id.rating_title);
        ratingBar = view.findViewById(R.id.rating);
        ratingFeedback = view.findViewById(R.id.rating_feedback);
        closeRatingDialog = view.findViewById(R.id.close_rating_dialog);
        submitRating = view.findViewById(R.id.submit_rating);


        closeRatingDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "User cancelled", Toast.LENGTH_SHORT).show();
                dismiss();
                if (fromRateDialog){
                    proceedToLearnFragment();
                }
            }
        });

        submitRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rateRef.setValue(ratingBar.getRating());
                feedbackRef.setValue(ratingFeedback.getText().toString().trim());
                Toast.makeText(getActivity(), "Feedback sent. Thank you.", Toast.LENGTH_SHORT).show();
                dismiss();
                if (fromRateDialog){
                    proceedToLearnFragment();
                }
            }
        });

        return view;

    }

    private void proceedToLearnFragment() {
        LearnFragment learnFragment = new LearnFragment();
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, learnFragment);
        fragmentTransaction.commit();
    }
}