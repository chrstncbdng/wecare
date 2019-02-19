package com.example.user.wecare.CustomDialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.user.wecare.Fragment.QuizFragment;
import com.example.user.wecare.R;

public class QuizDialogFragment extends DialogFragment {

    private NavigationView navigationView;

    TextView quizTitle, quizMessage;
    Button closeQuizDialog, proceedToQuiz;

    public QuizDialogFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_quiz_dialog, container, false);

        navigationView = view.findViewById(R.id.nav_view);

        quizTitle = view.findViewById(R.id.quiz_title);
        quizMessage = view.findViewById(R.id.quiz_message);
        closeQuizDialog = view.findViewById(R.id.close_quiz_dialog);
        proceedToQuiz = view.findViewById(R.id.proceed_to_quiz);

        closeQuizDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                navigationView = getActivity().findViewById(R.id.nav_view);
                Bundle args = getArguments();
                String checkedItem = args.getString("checkedItem");
                if (checkedItem.equals("Learn")){
                    navigationView.getMenu().findItem(R.id.nav_learn).setChecked(true);
                } else if (checkedItem.equals("Chat")){
                    navigationView.getMenu().findItem(R.id.nav_chat).setChecked(true);
                } else if (checkedItem.equals("Profile")){
                    navigationView.getMenu().findItem(R.id.nav_profile).setChecked(true);
                } else if (checkedItem.equals("About")){
                    navigationView.getMenu().findItem(R.id.nav_about).setChecked(true);
                }
            }
        });

        proceedToQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();

                QuizFragment quizFragment = new QuizFragment();
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, quizFragment);
                fragmentTransaction.commit();
            }
        });

        return view;

    }
}
