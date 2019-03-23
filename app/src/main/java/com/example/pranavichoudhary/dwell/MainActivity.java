package com.example.pranavichoudhary.dwell;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;


import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 0;
    private FirebaseAuth auth;
    //private DatabaseReference db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);

        AuthMethodPickerLayout customLayout = new AuthMethodPickerLayout
                .Builder(R.layout.signin_ui)
                .setGoogleButtonId(R.id.google)
                //.setFacebookButtonId(R.id.facebook)
                .build();

        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            Toast.makeText(getApplicationContext(), "Welcome User" + auth.getCurrentUser().getDisplayName(), Toast.LENGTH_SHORT).show();
        } else {
            startActivityForResult(AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setIsSmartLockEnabled(false)
                    .setTheme(R.style.AppTheme)
                    .setAuthMethodPickerLayout(customLayout)
                    .setAvailableProviders(Arrays.asList(
                            new AuthUI.IdpConfig.GoogleBuilder().build(),
                            new AuthUI.IdpConfig.FacebookBuilder().build()))
                    .build(), RC_SIGN_IN);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            if(resultCode == RESULT_OK){
                Toast.makeText(this, "sign In Success", Toast.LENGTH_SHORT).show();
                Intent igeo = new Intent(this, ChooseActivity.class);
                startActivity(igeo);
            }else{
                Toast.makeText(this, "Please Try Again", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    public void signOut(View v){
        AuthUI.getInstance().signOut(this);
        finish();
    }
}
