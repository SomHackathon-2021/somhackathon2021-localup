package com.hakathon.localapp;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class DescuentoActivity extends AppCompatActivity {

    CodeGenerator codes = new CodeGenerator();
    LinearLayout desc_5_btn;
    LinearLayout desc_10_btn;
    LinearLayout desc_15_btn;
    LinearLayout desc_20_btn;
    TextView desc_code;

    void createDesc(int desc) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference ref = db.collection("descuentos").document(user.getEmail());

        Map<String, Object> descuento = new HashMap<>();
        descuento.put("codigo", codes.genCode(5, 2));
        descuento.put("descuento", desc);
        ref.set(descuento).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                desc_code.setText(descuento.get("codigo").toString());
            }
        });
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_descuento);

        desc_code = findViewById(R.id.text_code_desc_view);
        desc_5_btn = findViewById(R.id.btn_5_proc);
        desc_5_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDesc(5);
            }
        });

        desc_10_btn = findViewById(R.id.btn_10_proc);
        desc_10_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDesc(10);
            }
        });

        desc_15_btn = findViewById(R.id.btn_15_proc);
        desc_15_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDesc(15);
            }
        });

        desc_20_btn = findViewById(R.id.btn_20_proc);
        desc_20_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDesc(20);
            }
        });

    }
}
