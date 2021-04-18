package com.hakathon.localapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    ImageView backButton;
    Button registerButton;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    EditText rg_mail;
    EditText rg_pass;
    EditText rg_pass2;
    EditText rg_dni;

    public static boolean isValidEmail(EditText edit_text) {
        String target = edit_text.getText().toString().trim();
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public void registerUser(FirebaseAuth mAuth, String email, String password, String dni) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("", "createUserWithEmail:success");
                        } else {
                            Toast.makeText(RegisterActivity.this, "Error al crear la conta - Prova de posar majúscules, minúscules i números en la contrasenya",
                                    Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                });

        DocumentReference docRef = db.collection("usuarios").document(email);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>(){
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if(!document.exists()){
                        Map<String, Object> user = new HashMap<>();
                        user.put("puntos", 0);
                        user.put("dni", dni);
                        user.put("tipo", "cliente");

                        docRef.set(user)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.e("Success", "DocumentSnapshot successfully written!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e("Error", "Error writing document", e);
                                    }
                                });
                    } else {
                        Toast.makeText(RegisterActivity.this, "L'usuari ja existeix",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("ExistingUsers", "Error getting documents.", task.getException());
                }

            }
        });
    }

    public void addUser(FirebaseAuth mAuth, String email, String password, String pass2, String dni) {
        email = email.toLowerCase().trim();
        if (!password.equals(pass2)) {
            Toast.makeText(RegisterActivity.this, "Les contrasenyes no coincideixen",
                    Toast.LENGTH_LONG).show();
            return;
        } else {
            registerUser(mAuth, email, password, dni);

            mAuth.signOut();
            Toast.makeText(RegisterActivity.this, "Usuari creat correctament",
                    Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        rg_mail = findViewById(R.id.rig_email);
        rg_dni = findViewById(R.id.rig_DNI_NIF);
        rg_pass = findViewById(R.id.rig_pass);
        rg_pass2 = findViewById(R.id.rig_pass2);

        backButton = findViewById(R.id.BackBtn);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        registerButton = findViewById(R.id.registerbtn);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addUser(mAuth, rg_mail.getText().toString(), rg_pass.getText().toString(), rg_pass2.getText().toString(), rg_dni.getText().toString());
            }
        });

    }
}

