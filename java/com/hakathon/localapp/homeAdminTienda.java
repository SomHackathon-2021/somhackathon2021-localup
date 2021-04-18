package com.hakathon.localapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class homeAdminTienda extends AppCompatActivity {

    CodeGenerator codes = new CodeGenerator();
    TextView precio;
    TextView code_view;
    Button calc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_admin_tienda);
        FirebaseUser curUser = FirebaseAuth.getInstance().getCurrentUser();

        double precio_puntos = 0.3;

        precio = findViewById(R.id.precio_text);
        calc = findViewById(R.id.calc_button);
        code_view = findViewById(R.id.code_view);

        calc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(precio.getText().equals("")){
                    Toast.makeText(homeAdminTienda.this, "Introdueix un preu",
                            Toast.LENGTH_SHORT).show();
                    precio.setText("");
                } else {
                    try {
                        double price = Double.parseDouble(precio.getText().toString());
                    } catch (Exception e){
                        Toast.makeText(homeAdminTienda.this, "Introdueix un valor vàlit",
                                Toast.LENGTH_SHORT).show();
                        precio.setText("");
                        return;
                    }
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    DocumentReference docRef = db.collection("codigos").document(curUser.getEmail());
                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>(){
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if(document.exists()){
                                    docRef.delete();
                                }
                                Map<String, Object> code = new HashMap<>();
                                code.put("codigo", codes.genCode(4,3));
                                code.put("puntos", Double.parseDouble(precio.getText().toString()) * precio_puntos);
                                docRef.set(code).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.i("CodeGenerator", "Code generatod");
                                        code_view.setText(code.get("codigo").toString());
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e("CodeGenerator", "Code not generated", e);
                                    }
                                    });
                            } else {
                                Log.e("ExistingUsers", "Error getting documents.", task.getException());
                            }

                        }
                    });

                }
            }
        });


    }
}