package com.jthd.lahn;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import com.jthd.lahn.R;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class CreateAccountActivity extends AppCompatActivity{

    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private Button createAccountButton;

    private String email;
    private String password;
    private String confirmPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        emailEditText = (EditText) findViewById(R.id.emailEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        confirmPasswordEditText = (EditText) findViewById(R.id.confirmPasswordEditText);
        createAccountButton = (Button) findViewById(R.id.createAccountButton);

    }


    @Override
    public void onResume(){
        super.onResume();
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();

            }
        });
    }

    /**
     * Creates a ParseUser account with the given information. If success, log in user and launch FeedActivity. If failure, display information
     * relevant to that failure. Note: ParseUser username field is the user's provided email address.
     */
    private void createAccount(){
        email = emailEditText.getText().toString();
        password = passwordEditText.getText().toString();
        confirmPassword = confirmPasswordEditText.getText().toString();
        View focusView = checkInputs();
        if (focusView != null) {
            // There was an input error; don't attempt to create account, and focus view that is source of error
            focusView.requestFocus();
        } else {
            // Log out potential current user to prevent invalid session token issues (error 209)
            ParseUser currentUser = ParseUser.getCurrentUser();
            currentUser.logOut();
            ParseUser user = new ParseUser();
            user.setUsername(email);
            user.setPassword(password);
            user.signUpInBackground(new SignUpCallback() {
                public void done(ParseException e) {
                    if (e == null) {
                        // Log in automatically and, since account is new, travel to home selection page to select first and primary home
                        ParseUser.logInInBackground(email, password, new LogInCallback() {
                            public void done(ParseUser user, ParseException e) {
                                if (user != null) {
                                    Log.d("CreateAccountActivity", "Login success");
                                    toHouseSelect();
                                } else {
                                    // this should never happen, since a user was just created successfully with this information
                                }
                            }
                        });
                    } else {
                        // Email address or username are already taken
                        createAccountFailure(e);

                    }
                }
            });
        }

    }

    /**
     * Checks the EditText inputs for email, username, password, and confirm password for validity. If a field is invalid, displays error message and
     * returns appropriate View for focusing.
     * @return Returns the View that caused the input error.
     */
    private View checkInputs(){
        View focusView = null;

        // check email for valid input
        if (TextUtils.isEmpty(email)){
            emailEditText.setError(getString(R.string.error_field_required));
            focusView = emailEditText;
        } else if (!email.contains("@")){
            emailEditText.setError(getString(R.string.error_invalid_email));
            focusView = emailEditText;
        }

        // check password for valid input
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError(getString(R.string.error_field_required));
            focusView = passwordEditText;
        } else if (password.length() < 6){
            passwordEditText.setError(getString(R.string.error_password_too_short));
            focusView = passwordEditText;
        }

        //check confirm password for valid input
        if (TextUtils.isEmpty(confirmPassword)) {
            confirmPasswordEditText.setError(getString(R.string.error_field_required));
            focusView = confirmPasswordEditText;
        } else if (password != null && !confirmPassword.equals(password)){
            confirmPasswordEditText.setError(getString(R.string.error_password_mismatch));
            focusView = confirmPasswordEditText;
        }
        return focusView;

    }

    /**
     * If ParseUser creation fails, use associated ParseException to determine why and display this information to the user.
     * @param e ParseException associated with account creation failure.
     */
    private void createAccountFailure(ParseException e){
        if (e.getCode() == ParseException.EMAIL_TAKEN){
            emailEditText.setError("Email is taken!");
            emailEditText.requestFocus();
        } else {
            emailEditText.setError("Unknown error occurred!");
            emailEditText.requestFocus();
            Log.d("CreateAccountActivity", Integer.toString(e.getCode()));
        }
    }

    /**
     * Launches SelectHouseActivity. Flags clear backstack so as to prevent the user from visiting LoginActivity or CreateAccountActivity without signing out.
     */
    private void toHouseSelect(){
        Intent intent = new Intent(CreateAccountActivity.this, SelectHouseActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        CreateAccountActivity.this.startActivity(intent);
    }




}
