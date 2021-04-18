package com.hakathon.localapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.concurrent.FutureTask;

public class LoginActivity extends AppCompatActivity {

    ImageView backButton;
    Button loginButton;
    TextView registerButton;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    EditText mail;
    EditText pass;

    public void updateUI(FirebaseUser user){
        DocumentReference docRef = db.collection("usuarios").document(user.getEmail());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String tipo = document.getString("tipo");
                        if(tipo.equals("cliente")){
                            Intent intent = new Intent(LoginActivity.this, home.class);
                            startActivity(intent);
                        } else if (tipo.equals("comercio")) {
                            Intent intent = new Intent(LoginActivity.this, homeAdminTienda.class);
                            startActivity(intent);
                        } else if (tipo.equals("bus")){
                            Intent intent = new Intent(LoginActivity.this, homeAdministrador.class);
                            startActivity(intent);
                        };
                    } else {
                        Log.d("Error", "No such document");
                    }
                } else {
                    Log.d("Error", "get failed with ", task.getException());
                }
            }
        });
    }

    public static boolean isValidEmail(EditText edit_text) {
        String target = edit_text.getText().toString().trim();
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public void loginMailPass(FirebaseAuth mAuth, String email, String password){
        email = email.toLowerCase().trim();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("LocalApp", "signInWithEmail:success");
                            updateUI(mAuth.getCurrentUser());
                        } else {
                            Log.w("LocalApp", "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Inici de sessió incorrecte",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

        mail = findViewById(R.id.editTextTextPersonName);
        mail.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!isValidEmail(mail)){
                    mail.setBackground(ContextCompat.getDrawable(LoginActivity.this, R.drawable.incorrect_input_file));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(isValidEmail(mail)){
                    {
                        mail.setBackground(ContextCompat.getDrawable(LoginActivity.this, R.drawable.input_file));
                    }
                }
            }
        });

        pass = findViewById(R.id.editTextTextPassword);

        backButton = findViewById(R.id.BackBtn);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        registerButton = findViewById(R.id.lgnRgsBtn);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);

            }
        });

        loginButton = findViewById(R.id.loginbtn);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isValidEmail(mail)){
                    Toast.makeText(LoginActivity.this, "Usuari invàlid",
                            Toast.LENGTH_SHORT).show();
                } else if(pass.getText().toString().equals("")){
                    Toast.makeText(LoginActivity.this, "Contrasenya invàlida",
                            Toast.LENGTH_SHORT).show();
                } else {
                    loginMailPass(mAuth, mail.getText().toString(), pass.getText().toString());
                }
            }
        });

    }
}