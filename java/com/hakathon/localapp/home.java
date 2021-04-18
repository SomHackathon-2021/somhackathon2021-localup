package com.hakathon.localapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import java.util.HashMap;
import java.util.Map;

public class home extends AppCompatActivity {

    TextView puntos_view;
    Button addPnts;
    TextView code_puntos;
    LinearLayout btn_desc;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser curUser = FirebaseAuth.getInstance().getCurrentUser();

        puntos_view = findViewById(R.id.textView2);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DocumentReference docRef = db.collection("usuarios").document(user.getEmail());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        puntos_view.setText(String.format("%.2f", document.getDouble("puntos")));
                    } else {
                        Log.d("Error", "get failed with ", task.getException());
                    }
                }
            }
        });

        btn_desc = findViewById(R.id.descuento_btn);
        btn_desc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(home.this, DescuentoActivity.class);
                startActivity(intent);
            }
        });

        addPnts = findViewById(R.id.add_points_btn);
        code_puntos = findViewById(R.id.add_points_code);
        addPnts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(code_puntos.getText().toString().equals("")) {
                        Toast.makeText(home.this, "Introdueix un codi",
                                Toast.LENGTH_SHORT).show();
                } else {
                    CollectionReference docRef = db.collection("codigos");
                    docRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>(){
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for(QueryDocumentSnapshot document : task.getResult()){
                                    if(document.get("codigo").toString().equalsIgnoreCase(code_puntos.getText().toString())){
                                        DocumentReference ref = document.getReference();
                                        Log.e("Found", ref.getId());

                                        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                DocumentSnapshot refDoc = task.getResult();
                                                Double add = refDoc.getDouble("puntos");
                                                Log.e("Puntos: ", String.valueOf(add));

                                                db.collection("usuarios").document(curUser.getEmail()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        DocumentSnapshot usrDoc = task.getResult();
                                                        Double puntos = usrDoc.getDouble("puntos");
                                                        Log.e("Puntos " + usrDoc.getId(), String.valueOf(puntos));

                                                        Map<String, Object> new_puntos = new HashMap<>();
                                                        new_puntos.put("puntos", add + puntos);
                                                        db.collection("usuarios").document(usrDoc.getId()).update(new_puntos).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                Log.e("Puntos " + usrDoc.getId(), String.valueOf(puntos));
                                                                puntos_view.setText(String.format("%.2f", new_puntos.get("puntos")));
                                                                ref.delete();
                                                            }
                                                        });

                                                    }
                                                });

                                            }
                                        });

                                        break;
                                    }
                                }

                            } else {
                                Log.e("CodeReader", "Error getting documents.", task.getException());
                            }

                        }
                    });
                }
            }
        });
    }

}