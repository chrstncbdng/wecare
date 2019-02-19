package com.example.user.wecare.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.user.wecare.CustomDialog.RateDialogFragment;
import com.example.user.wecare.CustomDialog.RatingDialogFragment;
import com.example.user.wecare.Model.Causes;
import com.example.user.wecare.Model.Risks;
import com.example.user.wecare.Model.Symptoms;
import com.example.user.wecare.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Random;

public class ViewDiseaseInfoFragment extends Fragment {

    RelativeLayout relativeLayout;

    public android.support.v7.widget.Toolbar toolbar;
    private String diseaseKey;
    private ViewFlipper viewFlipper;
    private TextView description, reference;
    private ImageView image;
    private Button btnPrev, btnNext;
    private ListView causesList, risksList, symptomsList;
    private Causes causes;
    private Risks risks;
    private Symptoms symptoms;

    ArrayList<String> causesArrayList;
    ArrayAdapter<String> causesArrayAdapter;

    ArrayList<String> risksArrayList;
    ArrayAdapter<String> risksArrayAdapter;

    ArrayList<String> symptomsArrayList;
    ArrayAdapter<String> symptomsArrayAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        toolbar = getActivity().findViewById(R.id.toolbar);

        View view = inflater.inflate(R.layout.fragment_viewdiseaseinfo, container, false);

        Bundle args = getArguments();
        diseaseKey = args.getString("key");

        relativeLayout = view.findViewById(R.id.view_relative);

        description = view.findViewById(R.id.description);
        image = view.findViewById(R.id.image);
        reference = view.findViewById(R.id.reference);

        btnPrev = view.findViewById(R.id.previous);
        btnNext = view.findViewById(R.id.next);
        btnPrev.setEnabled(false);
        btnNext.setEnabled(true);

        causes = new Causes();
        causesList = view.findViewById(R.id.causes);

        risks = new Risks();
        risksList = view.findViewById(R.id.risks);

        symptoms = new Symptoms();
        symptomsList = view.findViewById(R.id.symptoms);

        viewFlipper = view.findViewById(R.id.view_flipper);

        DatabaseReference diseaseRef = FirebaseDatabase.getInstance().getReference().child("Diseases").child(diseaseKey);
        DatabaseReference nameRef = diseaseRef.child("name");
        DatabaseReference descRef = diseaseRef.child("desc");
        DatabaseReference symptomsRef = diseaseRef.child("symptoms");
        DatabaseReference causesRef = diseaseRef.child("causes");
        DatabaseReference risksRef = diseaseRef.child("risks");
        DatabaseReference imageRef = diseaseRef.child("image");
        DatabaseReference refRef = diseaseRef.child("reference");

        causesArrayList = new ArrayList<>();
        causesArrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_view, R.id.info, causesArrayList);

        risksArrayList = new ArrayList<>();
        risksArrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_view, R.id.info, risksArrayList);

        symptomsArrayList = new ArrayList<>();
        symptomsArrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_view, R.id.info, symptomsArrayList);

        nameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String disease = dataSnapshot.getValue().toString();
                toolbar.setTitle(disease);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                toolbar.setTitle(databaseError.getDetails());
            }
        });

        imageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String imageUrl = dataSnapshot.getValue().toString();
                Picasso.get().load(imageUrl).into(image);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //error
            }
        });

        refRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String ref = dataSnapshot.getValue().toString();
                reference.setText("Reference: " + ref);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                reference.setText(databaseError.getDetails());
            }
        });

        descRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String descrip = dataSnapshot.getValue().toString();
                description.setText(descrip);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                description.setText(databaseError.getDetails());
            }
        });

        causesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    causes = ds.getValue(Causes.class);
                    causesArrayList.add("> " + causes.getCauses().toString());
                }
                causesList.setAdapter(causesArrayAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                causesArrayList.add("> "+databaseError.getDetails());
                causesList.setAdapter(causesArrayAdapter);
            }
        });

        risksRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    risks = ds.getValue(Risks.class);
                    risksArrayList.add("> " + risks.getRisks().toString());
                }
                risksList.setAdapter(risksArrayAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                risksArrayList.add("> "+databaseError.getDetails());
                risksList.setAdapter(risksArrayAdapter);
            }
        });

        symptomsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    symptoms = ds.getValue(Symptoms.class);
                    symptomsArrayList.add("> " + symptoms.getSymptoms().toString());
                }
                symptomsList.setAdapter(symptomsArrayAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                symptomsArrayList.add("> "+databaseError.getDetails());
                symptomsList.setAdapter(symptomsArrayAdapter);
            }
        });

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnPrev.setBackgroundResource(R.color.colorPrimary);

                if (viewFlipper.indexOfChild(viewFlipper.getCurrentView()) == 1) {

                    btnPrev.setEnabled(false);
                    btnNext.setEnabled(true);

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            btnPrev.setBackgroundResource(R.color.colorAccent);
                        }
                    }, 100);


                } else if (viewFlipper.indexOfChild(viewFlipper.getCurrentView()) == 3) {

                    btnNext.setText("Next");
                    btnPrev.setEnabled(true);

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            btnPrev.setBackgroundResource(R.color.colorPrimaryDark);
                        }
                    }, 100);
                } else {

                    btnNext.setEnabled(true);
                    btnPrev.setEnabled(true);

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            btnPrev.setBackgroundResource(R.color.colorPrimaryDark);
                        }
                    }, 100);
                }

                viewFlipper.setInAnimation(getActivity(), android.R.anim.slide_in_left);
                viewFlipper.setOutAnimation(getActivity(), android.R.anim.slide_out_right);
                viewFlipper.showPrevious();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnNext.setBackgroundResource(R.color.colorPrimary);

                if (viewFlipper.indexOfChild(viewFlipper.getCurrentView()) == 0) {

                    btnPrev.setEnabled(true);
                    btnNext.setEnabled(true);

                    btnPrev.setBackgroundResource(R.color.colorPrimaryDark);

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            btnNext.setBackgroundResource(R.color.colorPrimaryDark);
                        }
                    }, 100);
                } else if (viewFlipper.indexOfChild(viewFlipper.getCurrentView()) == 2) {
                    btnNext.setText("Done");

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            btnNext.setBackgroundResource(R.color.colorPrimaryDark);
                        }
                    }, 100);
                } else if (viewFlipper.indexOfChild(viewFlipper.getCurrentView()) == 3) {
                    relativeLayout.setVisibility(View.INVISIBLE);

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            btnNext.setBackgroundResource(R.color.colorPrimaryDark);
                        }
                    }, 100);

                    boolean bool = new Random().nextBoolean();
                    if(bool){
                        proceedToRateDialog();
                    } else{
                        proceedToLearnFragment();
                    }

                } else {
                    btnNext.setEnabled(true);
                    btnPrev.setEnabled(true);

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            btnNext.setBackgroundResource(R.color.colorPrimaryDark);
                        }
                    }, 100);
                }

                viewFlipper.setInAnimation(getActivity(), R.anim.slide_in_right);
                viewFlipper.setOutAnimation(getActivity(), R.anim.slide_out_left);
                viewFlipper.showNext();
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

    private void proceedToRateDialog() {
        FragmentManager fragmentManager = getFragmentManager();
        RateDialogFragment rateDialogFragment = new RateDialogFragment();
        rateDialogFragment.setCancelable(false);
        assert fragmentManager != null;
        rateDialogFragment.show(fragmentManager, "fragment_rate_dialog");
    }

}
