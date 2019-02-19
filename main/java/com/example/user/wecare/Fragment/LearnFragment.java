package com.example.user.wecare.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.user.wecare.Interface.DrawerLocker;
import com.example.user.wecare.Model.Disease;
import com.example.user.wecare.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LearnFragment extends Fragment {

    RecyclerView recyclerView;
    DatabaseReference databaseReference;

    public Toolbar toolbar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Learn");

        final View view = inflater.inflate(R.layout.fragment_learn, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);

        ((DrawerLocker) getActivity()).setDrawerEnabled(true);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Diseases");
        databaseReference.keepSynced(true);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        ((DrawerLocker) getActivity()).setDrawerEnabled(true);

        FirebaseRecyclerAdapter<Disease, DiseaseViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Disease, DiseaseViewHolder>
                (Disease.class, R.layout.disease, DiseaseViewHolder.class, databaseReference) {
            @Override
            protected void populateViewHolder(DiseaseViewHolder viewHolder, Disease model, final int position) {
                viewHolder.setName(model.getName());
                viewHolder.setShortdesc(model.getShortdesc());
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String view_disease_info = getRef(position).getKey();
                        ViewDiseaseInfoFragment viewDiseaseInfoFragment = new ViewDiseaseInfoFragment();
                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        Bundle args = new Bundle();
                        args.putString("key", view_disease_info);
                        viewDiseaseInfoFragment.setArguments(args);
                        transaction.replace(R.id.fragment_container, viewDiseaseInfoFragment);
                        transaction.commit();
                    }
                });
            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);

        return view;
    }

    public static class DiseaseViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public DiseaseViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setName(String name) {
            TextView disease_name = mView.findViewById(R.id.name);
            disease_name.setText(name);
        }

        public void setShortdesc(String shortDesc){
            TextView short_desc = mView.findViewById(R.id.short_desc);
            short_desc.setText(shortDesc);
        }

    }

}