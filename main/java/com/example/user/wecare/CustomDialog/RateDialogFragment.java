package com.example.user.wecare.CustomDialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.user.wecare.Fragment.LearnFragment;
import com.example.user.wecare.R;

public class RateDialogFragment extends DialogFragment {

    private NavigationView navigationView;

    TextView rateTitle, rateMessage;
    Button closeRateDialog, proceedToRating;

    public RateDialogFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_rate_dialog, container, false);

        navigationView = view.findViewById(R.id.nav_view);

        rateTitle = view.findViewById(R.id.rate_title);
        rateMessage = view.findViewById(R.id.rate_message);
        closeRateDialog = view.findViewById(R.id.close_rate_dialog);
        proceedToRating = view.findViewById(R.id.proceed_to_rating);

        closeRateDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                proceedToLearnFragment();
            }
        });

        proceedToRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                proceedToRatingFragment();
            }
        });

        return view;

    }

    private void proceedToRatingFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        RatingDialogFragment ratingDialogFragment = new RatingDialogFragment();
        ratingDialogFragment.setCancelable(false);
        assert fragmentManager != null;
        Bundle bundle = new Bundle();
        bundle.putBoolean("fromRateDialog", true);
        ratingDialogFragment.setArguments(bundle);
        ratingDialogFragment.show(fragmentManager, "fragment_rating_dialog");
    }

    private void proceedToLearnFragment() {
        LearnFragment learnFragment = new LearnFragment();
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, learnFragment);
        fragmentTransaction.commit();
    }
}
