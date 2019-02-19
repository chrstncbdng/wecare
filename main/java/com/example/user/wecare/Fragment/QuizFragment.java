package com.example.user.wecare.Fragment;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.wecare.CustomDialog.ResultsDialogFragment;
import com.example.user.wecare.Interface.DrawerLocker;
import com.example.user.wecare.Model.Question;
import com.example.user.wecare.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;
import java.util.Objects;

public class QuizFragment extends Fragment {

    public Toolbar toolbar;

    private TextView questionNumber, questionText, timer, currentScore;
    private Button option1, option2, option3, option4;
    private ImageView correctAnswer, incorrectAnswer, timesUp;

    private DatabaseReference quizRef;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference userRef, highScoreRef;

    int count = 0, total = 20, score = 0, wrong = 0, numberOfQuestions;

    private static final long TIME_START_IN_MILLIS = 15000;
    private CountDownTimer countDownTimer;
    private long TIME_LEFT_IN_MILLIS = TIME_START_IN_MILLIS;

    int[] question_sequence = new int[20];
    int[] option_sequence = new int[4];

    String[] new_options = new String[4];

    public static int[] random_number_generator(int min, int max, int numbers_to_get) {
        int[] numbers = new int[numbers_to_get];
        boolean flag = true;
        do {
            for (int i = 0; i < numbers_to_get; i++) {
                numbers[i] = (int) (Math.random() * max + min);
            }
            again:
            for (int i = 0; i < numbers_to_get - 1; i++) {
                for (int j = i + 1; j < numbers_to_get; j++) {
                    if (numbers[i] == numbers[j]) {
                        break again;
                    }
                    if ((i == numbers_to_get - 2) && (j == numbers_to_get - 1)) {
                        flag = false;
                    }
                }
            }
        } while (flag);
        return numbers;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        quizRef = FirebaseDatabase.getInstance().getReference().child("Questions");
        quizRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                numberOfQuestions = (int) dataSnapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getDetails(), Toast.LENGTH_SHORT).show();
            }
        });

        question_sequence = random_number_generator(1, 50, total);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Take Quiz");

        View view = inflater.inflate(R.layout.fragment_quiz, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        questionNumber = view.findViewById(R.id.question_number);
        questionText = view.findViewById(R.id.question);
        currentScore = view.findViewById(R.id.score);
        timer = view.findViewById(R.id.timer);

        option1 = view.findViewById(R.id.option1);
        option2 = view.findViewById(R.id.option2);
        option3 = view.findViewById(R.id.option3);
        option4 = view.findViewById(R.id.option4);

        correctAnswer = view.findViewById(R.id.correct);
        incorrectAnswer = view.findViewById(R.id.incorrect);
        timesUp = view.findViewById(R.id.time);

        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getDisplayName());
        highScoreRef = userRef.child("High Score");

        getActivity().setTitle("Take Quiz");

        questionNumber.setVisibility(View.VISIBLE);
        questionText.setVisibility(View.VISIBLE);
        currentScore.setVisibility(View.VISIBLE);
        currentScore.setText("Score: 0");
        timer.setVisibility(View.VISIBLE);

        option1.setVisibility(View.VISIBLE);
        option2.setVisibility(View.VISIBLE);
        option3.setVisibility(View.VISIBLE);
        option4.setVisibility(View.VISIBLE);

        correctAnswer.setVisibility(View.VISIBLE);
        incorrectAnswer.setVisibility(View.VISIBLE);
        timesUp.setVisibility(View.VISIBLE);

        ((DrawerLocker) getActivity()).setDrawerEnabled(false);
        updateQuestion();

        return view;
    }

    private void updateQuestion() {
        if (count + 1 > total) {
            showResultsDialog();
        } else {
            option_sequence = random_number_generator(1, 4, 4);

            correctAnswer.setVisibility(View.INVISIBLE);
            incorrectAnswer.setVisibility(View.INVISIBLE);
            timesUp.setVisibility(View.INVISIBLE);

            final int countplus = count + 1;

            option1.setEnabled(true);
            option2.setEnabled(true);
            option3.setEnabled(true);
            option4.setEnabled(true);

            quizRef = FirebaseDatabase.getInstance().getReference().child("Questions").child(String.valueOf(question_sequence[count]));
            quizRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    questionNumber.setText("Question " + countplus + "/20");
                    startTimer();

                    final Question question = dataSnapshot.getValue(Question.class);
                    questionText.setText(question.getQuestion());

                    new_options[option_sequence[0] - 1] = question.getOption1();
                    new_options[option_sequence[1] - 1] = question.getOption2();
                    new_options[option_sequence[2] - 1] = question.getOption3();
                    new_options[option_sequence[3] - 1] = question.getOption4();

                    option1.setText(new_options[0]);
                    option2.setText(new_options[1]);
                    option3.setText(new_options[2]);
                    option4.setText(new_options[3]);

                    option1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            option1.setBackgroundResource(R.color.colorPrimary);

                            option1.setEnabled(false);
                            option2.setEnabled(false);
                            option3.setEnabled(false);
                            option4.setEnabled(false);

                            if (option1.getText().toString().equals(question.getAnswer())) {
                                correctAnswer.setVisibility(View.VISIBLE);
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        score++;
                                        count++;
                                        currentScore.setText("Score: " + score);
                                        option1.setBackgroundResource(R.color.colorPrimaryDark);
                                        updateQuestion();
                                    }
                                }, 1500);

                            } else {
                                incorrectAnswer.setVisibility(View.VISIBLE);
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        option1.setBackgroundResource(R.color.colorPrimaryDark);
                                        wrong++;
                                        count++;
                                        updateQuestion();
                                    }
                                }, 1500);
                            }
                            resetTimer();
                        }
                    });

                    option2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            option2.setBackgroundResource(R.color.colorPrimary);

                            option1.setEnabled(false);
                            option2.setEnabled(false);
                            option3.setEnabled(false);
                            option4.setEnabled(false);

                            if (option2.getText().toString().equals(question.getAnswer())) {
                                correctAnswer.setVisibility(View.VISIBLE);
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        score++;
                                        count++;
                                        currentScore.setText("Score: " + score);
                                        option2.setBackgroundResource(R.color.colorPrimaryDark);
                                        updateQuestion();
                                    }
                                }, 1500);

                            } else {
                                incorrectAnswer.setVisibility(View.VISIBLE);
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        option2.setBackgroundResource(R.color.colorPrimaryDark);
                                        wrong++;
                                        count++;
                                        updateQuestion();
                                    }
                                }, 1500);
                            }
                            resetTimer();
                        }
                    });

                    option3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            option3.setBackgroundResource(R.color.colorPrimary);

                            option1.setEnabled(false);
                            option2.setEnabled(false);
                            option3.setEnabled(false);
                            option4.setEnabled(false);

                            if (option3.getText().toString().equals(question.getAnswer())) {
                                correctAnswer.setVisibility(View.VISIBLE);
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        score++;
                                        count++;
                                        currentScore.setText("Score: " + score);
                                        option3.setBackgroundResource(R.color.colorPrimaryDark);
                                        updateQuestion();
                                    }
                                }, 1500);

                            } else {
                                incorrectAnswer.setVisibility(View.VISIBLE);
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        option3.setBackgroundResource(R.color.colorPrimaryDark);
                                        wrong++;
                                        count++;
                                        updateQuestion();
                                    }
                                }, 1500);
                            }
                            resetTimer();
                        }
                    });

                    option4.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            option4.setBackgroundResource(R.color.colorPrimary);

                            option1.setEnabled(false);
                            option2.setEnabled(false);
                            option3.setEnabled(false);
                            option4.setEnabled(false);

                            if (option4.getText().toString().equals(question.getAnswer())) {
                                correctAnswer.setVisibility(View.VISIBLE);
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        score++;
                                        count++;
                                        currentScore.setText("Score: " + score);
                                        option4.setBackgroundResource(R.color.colorPrimaryDark);
                                        updateQuestion();
                                    }
                                }, 1500);

                            } else {
                                incorrectAnswer.setVisibility(View.VISIBLE);
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        option4.setBackgroundResource(R.color.colorPrimaryDark);
                                        wrong++;
                                        count++;
                                        updateQuestion();
                                    }
                                }, 1500);
                            }
                            resetTimer();
                        }
                    });

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(getContext(), databaseError.getDetails(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void showResultsDialog() {
        FragmentManager fragmentManager = getFragmentManager();
        ResultsDialogFragment resultsDialogFragment = new ResultsDialogFragment();
        resultsDialogFragment.setCancelable(false);
        Bundle bundle = new Bundle();
        bundle.putInt("score", score);
        resultsDialogFragment.setArguments(bundle);
        assert fragmentManager != null;
        resultsDialogFragment.show(fragmentManager, "fragment_results_dialog");
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(TIME_START_IN_MILLIS, 500) {
            @Override
            public void onTick(long millisUntilFinished) {
                TIME_LEFT_IN_MILLIS = millisUntilFinished;
                updateTimer();
            }

            @Override
            public void onFinish() {

                resetTimer();

                timesUp.setVisibility(View.VISIBLE);

                option1.setBackgroundResource(R.color.colorPrimary);
                option2.setBackgroundResource(R.color.colorPrimary);
                option3.setBackgroundResource(R.color.colorPrimary);
                option4.setBackgroundResource(R.color.colorPrimary);

                option1.setClickable(false);
                option2.setClickable(false);
                option3.setClickable(false);
                option4.setClickable(false);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        count++;
                        option1.setBackgroundResource(R.color.colorPrimaryDark);
                        option2.setBackgroundResource(R.color.colorPrimaryDark);
                        option3.setBackgroundResource(R.color.colorPrimaryDark);
                        option4.setBackgroundResource(R.color.colorPrimaryDark);
                        updateQuestion();
                    }
                }, 1500);
            }
        }.start();

    }

    private void updateTimer() {
        int minutes = (int) (TIME_LEFT_IN_MILLIS / 1000) / 60;
        int seconds = (int) (TIME_LEFT_IN_MILLIS / 1000) % 60;
        String timeLeft = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        timer.setText(timeLeft);
    }

    private void resetTimer() {
        TIME_LEFT_IN_MILLIS = TIME_START_IN_MILLIS;
        countDownTimer.cancel();
        updateTimer();
    }

}
