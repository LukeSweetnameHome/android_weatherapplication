package uk.ac.abertay.cmp309.project_weather;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {
    // Initialising variables
    private EditText emailTextView, passwordTextView, confirmPasswordTextView;
    private Button Btn, BtnLogin;
    private ProgressBar progressbar;
    private FirebaseAuth mAuth;
    FirebaseFirestore db;
    String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // taking FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // initialising all views
        emailTextView = findViewById(R.id.email);
        passwordTextView = findViewById(R.id.passwd);
        confirmPasswordTextView = findViewById(R.id.confirmpasswd);
        Btn = findViewById(R.id.btnregister);
        BtnLogin = findViewById(R.id.btnLogin2);
        progressbar = findViewById(R.id.progressbar);

        // Set on Click Listener on Registration button
        Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                // Assigning following methods to btn onclick listener
                validatePassword();

            }
        });
    }
    // method for validating password provided by user in the registration process
    private boolean validatePassword(){
        // declaring input in password & confirmpassword textview as a string
        // also trimming the spaces if any in both inputs
        String passwordInput = passwordTextView.getText().toString().trim();
        String confirmpasswordInput = confirmPasswordTextView.getText().toString().trim();
        // conditional for if the password input is empty
        if (passwordInput.isEmpty()){
            Toast.makeText(this, "Password needed", Toast.LENGTH_SHORT).show();
            return false;
        // conditional for if the password isn't a certain length for security
        } if (passwordInput.length()<5){
            Toast.makeText(this, "Password must be at least 5 characters", Toast.LENGTH_SHORT).show();
            return false;
        }
        // conditional checking if the input into password and confirmpassword matches
        if (!passwordInput.equals(confirmpasswordInput)){
            Toast.makeText(this, "Password does NOT match", Toast.LENGTH_SHORT).show();
            return false;
        // conditional if none of the other conditions are met
            // then the validatepassword boolean is returned as true
        } else{
            Toast.makeText(this, "Password does match", Toast.LENGTH_SHORT).show();
            // registernewuser nested within the password success conditional logic
            registerNewUser();
            return true;
        }

    }
    private void registerNewUser()
    {

        // show the visibility of progress bar to show loading
        progressbar.setVisibility(View.VISIBLE);

        // Take the value of two edit texts in Strings
        String email, password, confirmPassword;
        email = emailTextView.getText().toString();
        password = passwordTextView.getText().toString();
        confirmPassword = confirmPasswordTextView.getText().toString();

        // Validations for input email and password
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(),
                            "Please enter email!",
                            Toast.LENGTH_LONG)
                    .show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(),
                            "Please enter password!",
                            Toast.LENGTH_LONG)
                    .show();
            return;
        }
        // confirm password conditional logic
        if (TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(getApplicationContext(),
                            "Please confirm password!",
                            Toast.LENGTH_LONG)
                    .show();
            return;
        }

        // create new user or register new user
        mAuth
                .createUserWithEmailAndPassword(email, password)

                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        // successful user creation
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(),
                                            "Registration successful!",
                                            Toast.LENGTH_LONG)
                                    .show();

                            // hide the progress bar
                            progressbar.setVisibility(View.GONE);
                            //writeNewUser();
                            // if the user created intent to login activity
                            Intent intent
                                    = new Intent(RegistrationActivity.this,
                                    MainActivity.class);
                            startActivity(intent);
                            writeNewUser();
                        }
                        else {

                            // Registration failed
                            Toast.makeText(
                                            getApplicationContext(),
                                            "Registration failed!"
                                                    + " Please try again later",
                                            Toast.LENGTH_LONG)
                                    .show();

                            // hide the progress bar
                            progressbar.setVisibility(View.GONE);
                        }
                    }
                });

    }
    private void writeNewUser() {
    // method to add user to db
        // initialising variables
        emailTextView = findViewById(R.id.email);
        String Email = emailTextView.getText().toString();

        String userId = mAuth.getCurrentUser().getUid();


            // new firebase map for user object
            Map<String, Object> user = new HashMap<>();
            // putting email and password results into user collection
            user.put("Email", Email);
            user.put("User ID", userId);
        // scroll view within list view
            db.collection("users").document(userId)
                    .set(user)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        // success db write
                        public void onSuccess(Void aVoid) {

                            Toast.makeText(RegistrationActivity.this, "Successful Database Write", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        // failed db write
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(RegistrationActivity.this, "Failed Database Write", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    public void handleLogin (View v){
        // intent for home page button
        Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}