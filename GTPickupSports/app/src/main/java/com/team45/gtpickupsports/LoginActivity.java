package com.team45.gtpickupsports;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.team45.gtpickupsports.models.User;
import com.team45.gtpickupsports.networking.CASAuthenticator;
import com.team45.gtpickupsports.networking.FirebaseWrapper;

import java.util.Map;

/**
 * Main Login Activity for the GT Pickup Sports App. User will login with GT Username and password.
 * @author Glenn on 9/29/2014.
 */
public class LoginActivity extends Activity {
    private EditText mUsernameField, mPasswordField;
    private Intent mainScreenIntent;
    private AuthData authData;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        preferences = getSharedPreferences("GTPST", 0);
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);

        if (getIntent().hasExtra("LOGOUT")) {
            logout();
        }

        FirebaseWrapper.addAuthStateListener(new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {
                setAuthenticatedUser(authData);
            }
        });

        if(!preferences.getString("username", "").equals("")) {
            User.createUser(preferences.getString("username", ""));
            FirebaseWrapper.authWithUsername(preferences.getString("username", ""));
        }

        mainScreenIntent = new Intent(this, MainActivity.class);

        if (User.username != null) {
            startActivity(mainScreenIntent);
            finish();
        }

        setContentView(R.layout.activity_login);

        mUsernameField = (EditText) findViewById(R.id.loginUsernameText);
        mPasswordField = (EditText) findViewById(R.id.loginPasswordText);

        mPasswordField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_GO) {
                    login();
                }
                return false;
            }
        });

        findViewById(R.id.loginButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
    }

    /**
     * Attempts to log the user into the GT CAS Login system.
     */
    private void login() {
        boolean failed = false;

        if (mUsernameField.getText().toString().equals("")) {
            mUsernameField.setError("Invalid Username");
            failed = true;
        } else {
            mUsernameField.setError(null);
        }

        if (mPasswordField.getText().toString().equals("")) {
            mPasswordField.setError("Invalid Password");
            failed = true;
        } else {
            mPasswordField.setError(null);
        }

        if (!failed) {
            new LoginRequest().execute(mUsernameField.getText().toString(), mPasswordField.getText().toString());
        }
    }

    private void logout() {
        User.username = null;
        SharedPreferences.Editor e = preferences.edit();
        e.remove("username");
        e.apply();
        if (this.authData != null) {
            FirebaseWrapper.unauth(); // Logs out of Firebase
            setAuthenticatedUser(null); // Update authenticated user and show login buttons
        }
    }

    /**
     * Once a user is logged in, take the authData provided from Firebase and "use" it.
     */
    private void setAuthenticatedUser(AuthData authData) {
        this.authData = authData;
    }

    private class LoginRequest extends AsyncTask<String, Void, Boolean> {

        private ProgressDialog loadingSpinner = null;

        @Override
        protected void onPreExecute() {
            loadingSpinner = new ProgressDialog(LoginActivity.this);
            loadingSpinner.setMessage("Logging into Buzzport");
            loadingSpinner.show();
        }

        @Override
        protected Boolean doInBackground(String... info) {
            Map<String, String> cookies = new CASAuthenticator(info[0], info[1]).connect();
            return cookies != null;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                SharedPreferences.Editor e = preferences.edit();
                e.putString("username", mUsernameField.getText().toString());
                e.apply();
                FirebaseWrapper.authWithUsername(mUsernameField.getText().toString());
                User.createUser(mUsernameField.getText().toString());
                startActivity(mainScreenIntent);
                finish();
            } else {
                Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_LONG).show();
            }
            loadingSpinner.dismiss();
        }
    }
}
