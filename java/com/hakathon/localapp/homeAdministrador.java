package com.hakathon.localapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class homeAdministrador extends AppCompatActivity {

    Spinner tarjetas;
    EditText cod_desc_bus;
    TextView precio_final;
    Button calc_desc_bus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_administrador);

        tarjetas = findViewById(R.id.sel_tarjetas);
        cod_desc_bus = findViewById(R.id.cod_input);
        calc_desc_bus = findViewById(R.id.btn_calc_desc);
        precio_final = findViewById(R.id.precio_final_text);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.tarjetas, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tarjetas.setAdapter(adapter);

        calc_desc_bus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cod_desc_bus.getText().toString().equals("")) {
                    Toast.makeText(homeAdministrador.this, "Introdueix un codi vàlid",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                if(tarjetas.getSelectedItem().toString().equals("")) {
                    Toast.makeText(homeAdministrador.this, "Selecciona una targeta",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                double precio;
                double desc;
                if(tarjetas.getSelectedItem().toString().equals("T-Rosa")){
                    precio = 6.6;
                } else if (tarjetas.getSelectedItem().toString().equals("T-10")){
                    precio = 8.15;
                } else {
                    precio = 1.7;
                }

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                CollectionReference docRef = db.collection("descuentos");
                docRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>(){
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for(QueryDocumentSnapshot document : task.getResult()){
                                if(document.get("codigo").toString().equalsIgnoreCase(cod_desc_bus.getText().toString())){
                                    DocumentReference ref = document.getReference();
                                    Log.e("Found", ref.getId());

                                    ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if(task.isSuccessful()){
                                                DocumentSnapshot descDoc = task.getResult();
                                                if(descDoc.exists()){
                                                    double descuento = descDoc.getDouble("descuento");

                                                    precio_final.setText(String.format("%.2f €", precio * (1 - (descuento / 100))));

                                                    int coste;
                                                    if(descuento==5){
                                                        coste = 10;
                                                    } else if(descuento==10){
                                                        coste = 20;
                                                    } else if(descuento==15){
                                                        coste = 30;
                                                    } else {
                                                        coste = 40;
                                                    }

                                                    db.collection("usuarios").document(ref.getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                            if(task.isSuccessful()){
                                                                DocumentSnapshot doc = task.getResult();
                                                                if(doc.exists()){
                                                                    Double points = doc.getDouble("puntos");
                                                                    Log.e(doc.getId(), String.valueOf(points + " " + coste));
                                                                    if(points >= coste){
                                                                        Map<String, Object> puntos_final = new HashMap<>();
                                                                        double aux = points - coste;
                                                                        puntos_final.put("puntos", aux);

                                                                        db.collection("usuarios").document(ref.getId()).update(puntos_final).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                Toast.makeText(homeAdministrador.this, "Pagament Realitzat",
                                                                                        Toast.LENGTH_LONG).show();
                                                                                ref.delete();
                                                                            }
                                                                        });

                                                                    }
                                                                } else {
                                                                    Toast.makeText(homeAdministrador.this, "Punts insuficients",
                                                                            Toast.LENGTH_LONG).show();
                                                                }
                                                            }
                                                        }
                                                    });
                                                }
                                            }
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
        });

    }
}