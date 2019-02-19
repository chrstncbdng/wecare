package com.example.user.wecare.Activity;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.user.wecare.CustomDialog.QuizDialogFragment;
import com.example.user.wecare.CustomDialog.RateDialogFragment;
import com.example.user.wecare.CustomDialog.RatingDialogFragment;
import com.example.user.wecare.Fragment.AboutFragment;
import com.example.user.wecare.Fragment.ChatbotFragment;
import com.example.user.wecare.Fragment.LearnFragment;
import com.example.user.wecare.Fragment.ProfileFragment;
import com.example.user.wecare.Fragment.QuizFragment;
import com.example.user.wecare.Interface.DrawerLocker;
import com.example.user.wecare.R;
import com.facebook.CallbackManager;
import com.facebook.login.LoginManager;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.support.v4.widget.DrawerLayout;
import android.widget.Toast;

import java.util.Random;

public class NavigationScreen extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, DrawerLocker {

    private CallbackManager callbackManager;

    private DrawerLayout drawer;
    private NavigationView navigationView;
    public Toolbar toolbar;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private DatabaseReference downloadLinkRef, userRef, rateRef, feedbackRef;
    private String link;

    public ActionBarDrawerToggle toggle;

    QuizFragment quizFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_screen);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextAppearance(this, R.style.RobotoTextAppearance);

        drawer = findViewById(R.id.drawer_layout);

        navigationView = findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this);

        toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        quizFragment = new QuizFragment();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new LearnFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_learn);
        }

        downloadLinkRef = FirebaseDatabase.getInstance().getReference().child("Links").child("Download");
        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getDisplayName());
        rateRef = userRef.child("Rating");
        feedbackRef = userRef.child("Feedback");

        downloadLinkRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                link = dataSnapshot.getValue().toString().trim();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(NavigationScreen.this, databaseError.getDetails(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int menuItemId = item.getItemId();
        switch (menuItemId) {
            case R.id.nav_learn:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new LearnFragment()).commit();
                drawer.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_quiz:
                drawer.closeDrawer(GravityCompat.START);
                showQuizDialog();
                break;
            case R.id.nav_chat:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ChatbotFragment()).commit();
                drawer.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
                drawer.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_share:
                drawer.closeDrawer(GravityCompat.START);
                shareToFacebook();
                break;
            case R.id.nav_rate:
                drawer.closeDrawer(GravityCompat.START);
                showRatingDialog();
                break;
            case R.id.nav_about:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AboutFragment()).commit();
                drawer.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_logout:
                firebaseAuth.signOut();
                LoginManager.getInstance().logOut();
                updateUI();
        }
        return true;
    }

    private void showQuizDialog() {
        String checkedItem = "";
        if (navigationView.getMenu().findItem(R.id.nav_learn).isChecked()) {
            checkedItem = "Learn";
        } else if (navigationView.getMenu().findItem(R.id.nav_chat).isChecked()) {
            checkedItem = "Chat";
        } else if (navigationView.getMenu().findItem(R.id.nav_profile).isChecked()) {
            checkedItem = "Profile";
        } else if (navigationView.getMenu().findItem(R.id.nav_about).isChecked()) {
            checkedItem = "About";
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        QuizDialogFragment quizDialogFragment = new QuizDialogFragment();
        quizDialogFragment.setCancelable(false);
        Bundle bundle = new Bundle();
        bundle.putString("checkedItem", checkedItem);
        quizDialogFragment.setArguments(bundle);
        quizDialogFragment.show(fragmentManager, "fragment_quiz_dialog");
    }

    private void showRatingDialog() {
//        String checkedItem = "";
//        if (navigationView.getMenu().findItem(R.id.nav_learn).isChecked()) {
//            checkedItem = "Learn";
//        } else if (navigationView.getMenu().findItem(R.id.nav_chat).isChecked()) {
//            checkedItem = "Chat";
//        } else if (navigationView.getMenu().findItem(R.id.nav_profile).isChecked()) {
//            checkedItem = "Profile";
//        } else if (navigationView.getMenu().findItem(R.id.nav_about).isChecked()) {
//            checkedItem = "About";
//        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        RatingDialogFragment ratingDialogFragment = new RatingDialogFragment();
        ratingDialogFragment.setCancelable(false);
//        Bundle bundle = new Bundle();
//        bundle.putString("checkedItem", checkedItem);
//        ratingDialogFragment.setArguments(bundle);
        ratingDialogFragment.show(fragmentManager, "fragment_rating_dialog");
    }

    private void shareToFacebook() {
        ShareDialog shareDialog;
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareLinkContent shareLinkContent = new ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse(link))
                    .setShareHashtag(new ShareHashtag.Builder().setHashtag("#WeCare").build())
                    .build();
            shareDialog.show(shareLinkContent);
        } else{
            //error
        }
    }

    private void updateUI() {
        Toast.makeText(getApplicationContext(), "Successfully logged out", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (navigationView.getMenu().findItem(R.id.nav_learn).isChecked()
                || navigationView.getMenu().findItem(R.id.nav_profile).isChecked()
                || navigationView.getMenu().findItem(R.id.nav_chat).isChecked()
                || navigationView.getMenu().findItem(R.id.nav_about).isChecked()) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                drawer.openDrawer(GravityCompat.START);
            }
        }
    }

    @Override
    public void setDrawerEnabled(boolean enabled) {
        int lockMode = enabled ? DrawerLayout.LOCK_MODE_UNLOCKED :
                DrawerLayout.LOCK_MODE_LOCKED_CLOSED;
        drawer.setDrawerLockMode(lockMode);
        toggle.setDrawerIndicatorEnabled(enabled);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
