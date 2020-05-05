package com.abdiel.firebasenotasfinal.Actividades;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.abdiel.firebasenotasfinal.MainActivity;
import com.abdiel.firebasenotasfinal.R;

public class Aboutapp extends AppCompatActivity {

    ImageButton face, git, insta;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutapp);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //flecha hacia atras
        getSupportActionBar().setTitle("Informacion del desarrollador");


        face = findViewById(R.id.face);
        git = findViewById(R.id.github);
        insta = findViewById(R.id.instagram);

        face.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://web.facebook.com/abdiel.chavez.33");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        git.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://github.com/AbdielAzielCanulChavez");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        insta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://www.instagram.com/abdielcanulchavez");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });



    }


    //cuando le demos la flecha hacia atras se activa esto para mandarnos hacia atras
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        startActivity(new Intent(this, MainActivity.class));
        finish();
        return super.onOptionsItemSelected(item);
    }
}
