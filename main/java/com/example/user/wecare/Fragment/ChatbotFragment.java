package com.example.user.wecare.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.user.wecare.ChatBot.ChatMessage;
import com.example.user.wecare.Interface.DrawerLocker;
import com.example.user.wecare.R;
import com.example.user.wecare.ChatBot.chat_rec;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ai.api.AIListener;
import ai.api.AIServiceException;
import ai.api.android.AIDataService;
import ai.api.android.AIConfiguration;
import ai.api.android.AIService;
import ai.api.model.AIError;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ai.api.model.Result;

public class ChatbotFragment extends Fragment implements AIListener {

    public android.support.v7.widget.Toolbar toolbar;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    RecyclerView chatbotRecyclerView;
    EditText chatbotMessage;
    ImageButton chatbotSend;
    DatabaseReference chatbotRef;
    FirebaseRecyclerAdapter<ChatMessage,chat_rec> firebaseRecyclerAdapter;

    private AIService aiService;

    String facebookUserId = "";
    String clientAccessToken = "74ce7da16ca744febdaa1fc1a39f8063";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ((DrawerLocker) getActivity()).setDrawerEnabled(true);

        toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Doctor Bot");
        //toolbar.setSubtitle("Replies instantly");

        View view = inflater.inflate(R.layout.fragment_chatbot, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        for (UserInfo profile : firebaseUser.getProviderData()) {
            if (FacebookAuthProvider.PROVIDER_ID.equals(profile.getProviderId())) {
                facebookUserId = profile.getUid();
            }
        }

        chatbotRecyclerView = view.findViewById(R.id.chatbot_recycler_view);
        chatbotMessage = view.findViewById(R.id.chatbot_message);
        chatbotSend = view.findViewById(R.id.chatbot_send);

        chatbotRecyclerView.setHasFixedSize(true);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        chatbotRecyclerView.setLayoutManager(linearLayoutManager);

        chatbotRef = FirebaseDatabase.getInstance().getReference().child("Chat").child(facebookUserId);
        chatbotRef.keepSynced(true);

        final AIConfiguration aiConfiguration = new AIConfiguration(clientAccessToken,
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);

        aiService = AIService.getService(getContext(), aiConfiguration);
        aiService.setListener(this);

        final AIDataService aiDataService = new AIDataService(getContext(), aiConfiguration);
        final AIRequest aiRequest = new AIRequest();


        chatbotSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String message = chatbotMessage.getText().toString().trim();
                if (!message.equals("")){
                    final ChatMessage chatMessage = new ChatMessage(message, "user");
                    chatbotRef.push().setValue(chatMessage);

                    aiRequest.setQuery(message);

                    @SuppressLint("StaticFieldLeak") AsyncTask<AIRequest, Void, AIResponse> asyncTask = new AsyncTask<AIRequest, Void, AIResponse>() {

                        @Override
                        protected AIResponse doInBackground(AIRequest... aiRequests) {
                            final AIRequest request = aiRequests[0];
                            try  {
                                final AIResponse response = aiDataService.request(aiRequest);
                                return response;
                            } catch (AIServiceException e){}
                            return null;
                        }

                        @Override
                        protected void onPostExecute(AIResponse aiResponse) {
                            //super.onPostExecute(aiResponse);
                            if (aiResponse != null){
                                Result result = aiResponse.getResult();
                                String reply = result.getFulfillment().getSpeech();
                                ChatMessage chatMessage1 = new ChatMessage(reply, "bot");
                                chatbotRef.push().setValue(chatMessage1);
                            }
                        }
                    }.execute(aiRequest);
                } else {
                    aiService.startListening();
                }

                chatbotMessage.setText("");
            }
        });

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<ChatMessage, chat_rec>(
                ChatMessage.class,
                R.layout.msglist,
                chat_rec.class,
                chatbotRef) {
            @Override
            protected void populateViewHolder(chat_rec viewHolder, ChatMessage model, int position) {
                if (model.getMsgUser().equals("user")){
                    viewHolder.rightText.setText(model.getMsgText());
                    viewHolder.rightText.setVisibility(View.VISIBLE);
                    viewHolder.leftText.setVisibility(View.GONE);
                }
                else {
                    viewHolder.leftText.setText(model.getMsgText());
                    viewHolder.rightText.setVisibility(View.GONE);
                    viewHolder.leftText.setVisibility(View.VISIBLE);
                }
            }
        };

        firebaseRecyclerAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);

                int msgCount = firebaseRecyclerAdapter.getItemCount();
                int lastVisiblePosition = linearLayoutManager.findLastVisibleItemPosition();

                if (lastVisiblePosition == -1
                        ||(positionStart >= (msgCount - 1) &&
                        lastVisiblePosition == (positionStart - 1))) {
                    chatbotRecyclerView.scrollToPosition(positionStart);
                }
            }

        });

        chatbotRecyclerView.setAdapter(firebaseRecyclerAdapter);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        chatbotRef.removeValue();
    }

    @Override
    public void onResult(AIResponse response) {
        Result result1 = response.getResult();

        String message = result1.getResolvedQuery();
        ChatMessage chatMessage = new ChatMessage(message, "user");
        chatbotRef.push().setValue(chatMessage);

        String reply = result1.getFulfillment().getSpeech();
        ChatMessage chatMessage1 = new ChatMessage(reply, "bot");
        chatbotRef.push().setValue(chatMessage1);
    }

    @Override
    public void onError(AIError error) {

    }

    @Override
    public void onAudioLevel(float level) {

    }

    @Override
    public void onListeningStarted() {

    }

    @Override
    public void onListeningCanceled() {

    }

    @Override
    public void onListeningFinished() {

    }

}
