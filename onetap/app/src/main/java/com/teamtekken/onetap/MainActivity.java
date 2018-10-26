package com.satyamapp.teamtekken;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

// will add BaseActivity later
public class MainActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "EmailPassword";

    private EditText mEmailField;
    private EditText mPasswordField;
    private TextView mStatusTextView;

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]
    private Toolbar mToolbar;
    LinearLayout kp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Views
        mEmailField = (EditText) findViewById(R.id.email);
        mPasswordField = (EditText) findViewById(R.id.pass);
        //mStatusTextView = (TextView) findViewById(R.id.textView);
        //buttons
        findViewById(R.id.Login).setOnClickListener(this);
        findViewById(R.id.Signup).setOnClickListener(this);
        kp = (LinearLayout) findViewById(R.id.kp);
        //ImageButtons
        findViewById(R.id.kp).setOnClickListener(this);
        findViewById(R.id.google).setOnClickListener(this);

        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();

    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (mAuth.getCurrentUser() != null) {
            finish();
            startActivity(new Intent(this, Important.class));
        }
        updateUI(currentUser);
    }
    // [END on_start_check_user]

    // [ Register new user ]
    private void createAccount(String email, String password)
    {
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm())
        {
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if(task.isSuccessful())
                {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    updateUI(user);

                }
                else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                    Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    updateUI(null);
                }
                // [START_EXCLUDE]
                hideProgressDialog();
                // [END_EXCLUDE]
            }
            // [END create_user_with_email]
        });

        showProgressDialog();

    }
    // [End_Login new user]


    //[validate form]
    private boolean validateForm()
    {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        return valid;

    }
    //[end validate form]

    //[update UI]
    private void updateUI(FirebaseUser user)
    {
        hideProgressDialog();
        if (user != null)
        {
            Intent i = new Intent(this ,ProfileSetup.class);
            startActivity(i);
        }
        else
        {
            CharSequence text = "Hello toast!";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(MainActivity.this, text, duration);
            toast.show();

        }
    }
    //[end update UI]
    @Override
    public void onClick(View v)
    {
        int i = v.getId();
        if (i == R.id.Signup) {
            createAccount(mEmailField.getText().toString(), mPasswordField.getText().toString());


    }
}
