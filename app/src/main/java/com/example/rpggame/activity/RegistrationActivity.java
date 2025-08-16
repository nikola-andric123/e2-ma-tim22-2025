package com.example.rpggame.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.rpggame.Enums.UserTitle;
import com.example.rpggame.R;
import com.example.rpggame.domain.UserProfile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegistrationActivity extends AppCompatActivity {

    FirebaseAuth mAuth;

    private ImageView[] avatars;
    private View[] avatar_borders;
    EditText usernameField, emailField;
    TextView loginText;
    TextInputEditText confirmPasswordField, passwordField;
    ProgressBar progressBar;
    Button registerButton;
    private int selectedAvatarIndex = -1;
    String selectedAvatar;
    private FirebaseFirestore db;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            currentUser.reload().addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    mAuth.signOut();
                }
            });
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration);
        loginText = findViewById(R.id.loginWithText);
        passwordField = findViewById(R.id.passwordField);
        confirmPasswordField = findViewById(R.id.confirmPasswordField);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        EditText passwordField = findViewById(R.id.passwordField);

        loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });




        usernameField = findViewById(R.id.usernameField);
        emailField = findViewById(R.id.emailField);

        progressBar = findViewById(R.id.progressBar);
        registerButton = findViewById(R.id.registerButton);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String email, password, username;
                username = usernameField.getText().toString();
                email = emailField.getText().toString();
                password = passwordField.getText().toString();

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    if (user != null) {
                                        user.sendEmailVerification()
                                                .addOnCompleteListener(emailTask -> {
                                                    if (emailTask.isSuccessful()) {
                                                        Toast.makeText(RegistrationActivity.this,
                                                                "Verification email sent. Please check your inbox.",
                                                                Toast.LENGTH_LONG).show();
                                                        progressBar.setVisibility(View.GONE);
                                                        if (task.isSuccessful()) {
                                                            //Create user object and save it to Database
                                                            String uid = user.getUid();
                                                            UserProfile newUser = new UserProfile(
                                                                 username,
                                                                 email,
                                                                 0,0,0, 0,0,
                                                                    UserTitle.BEGGINER,
                                                                    selectedAvatar, null
                                                            );
                                                            db.collection("users").document(uid).set(newUser)
                                                                    .addOnSuccessListener(aVoid -> {
                                                                        db.collection("users").document(uid)
                                                                                .update("dateCreated", FieldValue.serverTimestamp());
                                                                        Toast.makeText(RegistrationActivity.this,
                                                                                "Verification email sent. Please verify before login.",
                                                                                Toast.LENGTH_LONG).show();

                                                            })
                                                                    .addOnFailureListener(e -> {
                                                                        Toast.makeText(RegistrationActivity.this,
                                                                                "Failed to create profile: " + e.getMessage(),
                                                                                Toast.LENGTH_LONG).show();
                                                                        registerButton.setEnabled(true);
                                                                    });

                                                            Toast.makeText(RegistrationActivity.this, "Account created.",
                                                                    Toast.LENGTH_SHORT).show();
                                                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                    } else {
                                                        Toast.makeText(RegistrationActivity.this,
                                                                "Failed to send verification email.",
                                                                Toast.LENGTH_SHORT).show();
                                                    }
                                                });


                                    }
                                }else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(RegistrationActivity.this, "Registration failed.",
                                            Toast.LENGTH_SHORT).show();

                                }

                            }
                        });
            }
        });

        int[] imageIds = {R.id.avatar1, R.id.avatar2, R.id.avatar3, R.id.avatar4};
        avatar_borders = new View[]{
                findViewById(R.id.avatar_border1),findViewById(R.id.avatar_border2),
                findViewById(R.id.avatar_border3), findViewById(R.id.avatar_border4)
        };

        // Loop through each ID
        for (int id : imageIds) {
            ImageView image = findViewById(id);

            // Set the same click listener
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectAvatar(v.getId());
                }
            });
        }
        // Listen for text changes
        TextWatcher formWatcher = new TextWatcher() {


            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkIfFormIsValid();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        usernameField.addTextChangedListener(formWatcher);
        emailField.addTextChangedListener(formWatcher);
        passwordField.addTextChangedListener(formWatcher);
        confirmPasswordField.addTextChangedListener(formWatcher);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.registration), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


    }

    private void checkIfFormIsValid() {
        String username = usernameField.getText().toString().trim();
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText() != null ? passwordField.getText().toString() : "";
        String confirmPassword = confirmPasswordField.getText() != null ? confirmPasswordField.getText().toString() : "";

        boolean isFormValid = !username.isEmpty()
                && !email.isEmpty()
                && !password.isEmpty()
                && !confirmPassword.isEmpty()
                && password.equals(confirmPassword)
                && selectedAvatarIndex != -1;

        registerButton.setEnabled(isFormValid);
    }
    private void selectAvatar(int id) {
        for (View avatar_border : avatar_borders) {
            if (avatar_border != null) avatar_border.setBackgroundResource(R.drawable.avatar_border);
        }
        if (id == R.id.avatar1) {
            Toast.makeText(this, "Image 1 clicked!", Toast.LENGTH_SHORT).show();
            View img = findViewById(R.id.avatar_border1);
            selectedAvatarIndex = id;
            selectedAvatar = "avatar_1";
            img.setBackgroundResource(R.drawable.avatar_border_selected);
        } else if (id == R.id.avatar2) {
            View img = findViewById(R.id.avatar_border2);
            selectedAvatarIndex = id;
            selectedAvatar = "avatar_2";
            img.setBackgroundResource(R.drawable.avatar_border_selected);
        } else if (id == R.id.avatar3) {
            View img = findViewById(R.id.avatar_border3);
            selectedAvatarIndex = id;
            selectedAvatar = "avatar_3";
            img.setBackgroundResource(R.drawable.avatar_border_selected);
        } else if (id == R.id.avatar4) {
            View img = findViewById(R.id.avatar_border4);
            selectedAvatarIndex = id;
            selectedAvatar = "avatar_4";
            img.setBackgroundResource(R.drawable.avatar_border_selected);
        }
        // Highlight selected one


        // Store selected avatar index
        selectedAvatarIndex = id;
    }
}