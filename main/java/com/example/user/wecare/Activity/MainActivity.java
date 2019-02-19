package com.example.user.wecare.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.wecare.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private CallbackManager callbackManager;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference highScoreRef, userRef, idRef, nameRef, emailRef, policyLinkRef;
    private String name, email;
    private TextView privacyPolicy;
    private Button facebookBtn;

    private String facebookUserId = "";
    private String privacyPolicyUrl = "";
    private String privacyPolicyText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        privacyPolicy = findViewById(R.id.privacy_policy);
        privacyPolicyText = privacyPolicy.getText().toString().trim();

        policyLinkRef = FirebaseDatabase.getInstance().getReference().child("Links").child("Privacy Policy");
        policyLinkRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                privacyPolicyUrl = dataSnapshot.getValue().toString();
                SpannableString spannableString = new SpannableString(privacyPolicyText);
                ClickableSpan clickableSpan = new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(privacyPolicyUrl)));
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        super.updateDrawState(ds);
                        int color = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark);
                        ds.setColor(color);
                    }
                };
                spannableString.setSpan(clickableSpan, 37, 51, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                privacyPolicy.setText(spannableString);
                privacyPolicy.setMovementMethod(LinkMovementMethod.getInstance());
                privacyPolicy.setHighlightColor(Color.TRANSPARENT);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, databaseError.getDetails(), Toast.LENGTH_SHORT).show();
            }
        });


        if (firebaseUser == null) {
            FacebookSdk.sdkInitialize(getApplicationContext());

            facebookBtn = findViewById(R.id.facebookBtn);

            callbackManager = CallbackManager.Factory.create();

            facebookBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    facebookBtn.setEnabled(false);
                    facebookBtn.setBackgroundResource(R.color.colorPrimary);

                    LoginManager.getInstance().logInWithReadPermissions(MainActivity.this, Arrays.asList("email", "public_profile"));
                    LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult loginResult) {
                            handleFacebookToken(loginResult.getAccessToken());
                        }

                        @Override
                        public void onCancel() {
                            Toast.makeText(getApplicationContext(), "User cancelled", Toast.LENGTH_SHORT).show();
                            facebookBtn.setEnabled(true);
                            facebookBtn.setBackgroundResource(R.color.colorPrimaryDark);
                        }

                        @Override
                        public void onError(FacebookException error) {
                            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                            facebookBtn.setEnabled(true);
                            facebookBtn.setBackgroundResource(R.color.colorPrimaryDark);
                        }
                    });

                }
            });

        } else {
            Intent intent = new Intent(MainActivity.this, NavigationScreen.class);
            startActivity(intent);
            finish();
        }

    }

    private void handleFacebookToken(AccessToken accessToken) {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                            facebookBtn.setBackgroundResource(R.color.colorPrimaryDark);
                            facebookBtn.setEnabled(true);
                            updateUI(firebaseUser);
                        } else {
                            Toast.makeText(getApplicationContext(), "Could not register", Toast.LENGTH_SHORT).show();
                            facebookBtn.setBackgroundResource(R.color.colorPrimaryDark);
                            facebookBtn.setEnabled(true);
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser firebaseUser) {
        name = firebaseUser.getDisplayName();
        email = firebaseUser.getEmail();

        for (UserInfo profile : firebaseUser.getProviderData()) {
            if (FacebookAuthProvider.PROVIDER_ID.equals(profile.getProviderId())) {
                facebookUserId = profile.getUid();
            }
        }

        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!(dataSnapshot.hasChild(facebookUserId))) {

                    int initialScore = 0;

                    idRef = userRef.child(facebookUserId);
                    nameRef = idRef.child("Name");
                    emailRef = idRef.child("Email");
                    highScoreRef = idRef.child("High Score");

                    nameRef.setValue(name);
                    emailRef.setValue(email);
                    highScoreRef.setValue(initialScore);

                    Toast.makeText(getApplicationContext(), "Welcome, new user.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Welcome, existing user.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, databaseError.getDetails(), Toast.LENGTH_SHORT).show();
            }
        });

        Intent intent = new Intent(getApplicationContext(), NavigationScreen.class);
        intent.putExtra("name", name);
        intent.putExtra("email", email);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAndRemoveTask();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            finishAffinity();
        } else {
            finish();
        }
    }
}
