package com.abdiel.firebasenotasfinal.Notas;

import android.content.Intent;
import android.os.Bundle;

import com.abdiel.firebasenotasfinal.Actividades.SplashActivity;
import com.abdiel.firebasenotasfinal.Auth.Login;
import com.abdiel.firebasenotasfinal.MainActivity;
import com.abdiel.firebasenotasfinal.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class AddNotes extends AppCompatActivity {

    FirebaseFirestore fStore;
    EditText noteTitle;
    EditText noteContent;
    ProgressBar progressBarLoading;
    FirebaseUser user;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notes);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fStore = FirebaseFirestore.getInstance();
        noteContent =  findViewById(R.id.addNoteContent);
        noteTitle = findViewById(R.id.addNoteTitle);
        progressBarLoading = findViewById(R.id.progressBar);

        user = FirebaseAuth.getInstance().getCurrentUser();


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String nTitle = noteTitle.getText().toString();
                String nContent = noteContent.getText().toString();

                if(nTitle.isEmpty() || nContent.isEmpty()){
                    Snackbar.make(view, "Escribe algo para guardar", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    return;
                }



                //guardar notas
                if(!nTitle.isEmpty() && !nContent.isEmpty()) {
                    progressBarLoading.setVisibility(view.VISIBLE);
                    DocumentReference docref = fStore.collection("notes").document(user.getUid()).collection("mynotes").document();
                    Map<String, Object> note = new HashMap<>();
                    note.put("title", nTitle);
                    note.put("content", nContent);

                    docref.set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(AddNotes.this, "Nota Agregada", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(AddNotes.this, MainActivity.class));
                            finish();//correccion de bug al crear la nota, cuando se le daba hacia atras la nota regresaba como editable
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddNotes.this, "Error intente de nuevo", Toast.LENGTH_SHORT).show();
                            // progressBarLoading.setVisibility(view.VISIBLE);
                        }
                    });
                    //final guardar nota
                }else{
                    Toast.makeText(AddNotes.this, "Escribe algo para guardar la nota", Toast.LENGTH_SHORT).show();
                }

            }
        });
       // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater =  getMenuInflater();
        inflater.inflate(R.menu.close_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.close){
            Toast.makeText(this, "Nota no guardada", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
           // onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}
