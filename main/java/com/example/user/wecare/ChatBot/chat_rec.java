package com.example.user.wecare.ChatBot;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.user.wecare.R;

public class chat_rec extends RecyclerView.ViewHolder {
    public TextView leftText, rightText;

    public chat_rec(View itemView) {
        super(itemView);

        leftText =  itemView.findViewById(R.id.leftText);
        rightText = itemView.findViewById(R.id.rightText);
    }
}
