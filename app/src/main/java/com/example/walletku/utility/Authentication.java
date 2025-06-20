package com.example.walletku.utility;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.Toast;

import com.example.walletku.view.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

public class Authentication {
    private FirebaseAuth mAuth;
    private FirebaseUser currUser;

    public Authentication() {
        if(mAuth == null) {
            mAuth = FirebaseAuth.getInstance();
        }
    }

    public void register(Activity activity, EditText etEmail, EditText etPassword) {
        if(isFieldValid(etEmail, etPassword)) {
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(activity, task -> {
                if (task.isSuccessful()) {
                    currUser = mAuth.getCurrentUser();
                    if (currUser != null) {
                        Intent homeIntent = new Intent(activity, MainActivity.class);
                        homeIntent.putExtra("USER_ACCOUNT", email);
                        activity.startActivity(homeIntent);
                        activity.finish();
                    }
                }
                else {
                    try {
                        throw task.getException();
                    }
                    catch (FirebaseAuthUserCollisionException e) {
                        etEmail.setError("Email already exist");
                        etEmail.requestFocus();
                    }
                    catch (Exception e) {
                        Log.e("error", e.getLocalizedMessage());
//                        Toast.makeText(activity, "Sign Up Failed " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public void login(Activity activity, EditText etEmail, EditText etPassword) {
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        if(email.isEmpty() || password.isEmpty()) {
            etEmail.setError("All field must be filled");
            etEmail.requestFocus();
            etPassword.setError("All field must be filled");
            etPassword.requestFocus();
            return;
        }
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(activity, task -> {
            if(task.isSuccessful()) {
                currUser = mAuth.getCurrentUser();
                if (currUser != null) {
                    Intent homeIntent = new Intent(activity, MainActivity.class);
                    homeIntent.putExtra("USER_ACCOUNT", email);
                    homeIntent.putExtra("USER", currUser);
                    activity.startActivity(homeIntent);
                    activity.finish();
                }
            }
            else {
                try {
                    throw task.getException();
                }
                catch (Exception e) {
                    etEmail.setError("Check your credentials");
                    etEmail.requestFocus();
                    etPassword.setError("Check your credentials");
                    etPassword.requestFocus();
                    Log.e("error", e.getLocalizedMessage());
                }
            }
        });
    }

    public boolean isFieldValid(EditText etEmail, EditText etPassword) {
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        if (email.isEmpty()) {
            etEmail.setError("Email cannot be empty");
            etEmail.requestFocus();
            return false;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter valid email address");
            etEmail.requestFocus();
            return false;
        }

        if (password.isEmpty()) {
            etPassword.setError("Password cannot be empty");
            etEmail.requestFocus();
            return false;
        }

        if(password.length() < 6 || password.length() > 20) {
            etPassword.setError("Password must be 6 - 20 characters long");
            etEmail.requestFocus();
            return false;
        }

        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$";
        if (!password.matches(passwordRegex)) {
            etPassword.setError("Password must contain at least one uppercase, lowercase, and digit");
            etEmail.requestFocus();
            return false;
        }
        return true;
    }

    public void signout() {
        mAuth.signOut();
    }
}
