package com.abdiel.firebasenotasfinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.abdiel.firebasenotasfinal.Actividades.Aboutapp;
import com.abdiel.firebasenotasfinal.Actividades.SplashActivity;
import com.abdiel.firebasenotasfinal.Auth.Login;
import com.abdiel.firebasenotasfinal.Auth.Register;
import com.abdiel.firebasenotasfinal.Notas.AddNotes;
import com.abdiel.firebasenotasfinal.Notas.Content_note_details;
import com.abdiel.firebasenotasfinal.Notas.EditNote;
import com.abdiel.firebasenotasfinal.model.Adapter;
import com.abdiel.firebasenotasfinal.model.Note;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView nav_view;
    RecyclerView noteList;
    Adapter adapter;
    FirebaseFirestore fStore;
    FirestoreRecyclerAdapter<Note, NoteViewHolder> noteAdapter;
    FirebaseUser user;
    FirebaseAuth fAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //declaramos el toolbar a usar
        Toolbar toolbar = findViewById(R.id.toolbar);
        //le damos el soporte para accion de la barra del toggle
        setSupportActionBar(toolbar);


        fStore = FirebaseFirestore.getInstance();

        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();


        Query query = fStore.collection("notes").document(user.getUid()).collection("mynotes").orderBy("title", Query.Direction.DESCENDING);

        //query note > auth > myNotes



        FirestoreRecyclerOptions<Note> allNotes = new FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(query, Note.class)
                .build();

        noteAdapter = new FirestoreRecyclerAdapter<Note, NoteViewHolder>(allNotes) {
            @Override
            protected void onBindViewHolder(@NonNull NoteViewHolder noteViewHolder, final int i, @NonNull final Note note) {
                noteViewHolder.noteTitle.setText(note.getTitle());
                noteViewHolder.notecontent.setText(note.getContent());
                final int code = getRandomColor();
                noteViewHolder.mCardView.setCardBackgroundColor(noteViewHolder.view.getResources().getColor(code, null));
                final String docId = noteAdapter.getSnapshots().getSnapshot(i).getId();

                noteViewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), Content_note_details.class);
                        intent.putExtra("title", note.getTitle());
                        intent.putExtra("content",note.getContent());
                        intent.putExtra("code", code);
                        intent.putExtra("noteId", docId);
                        v.getContext().startActivity(intent);
                    }
                });


                //aqui puedo meter el click del boton
                ImageView menuIcon = noteViewHolder.view.findViewById(R.id.menuIcon);
                menuIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        final String docId = noteAdapter.getSnapshots().getSnapshot(i).getId();
                        PopupMenu menu = new PopupMenu(v.getContext(),v);
                      //  menu.setGravity(Gravity.END);
                        menu.getMenu().add("Editar").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                Intent intent = new Intent(v.getContext(), EditNote.class);
                                intent.putExtra("title", note.getTitle());
                                intent.putExtra("content", note.getContent());
                                intent.putExtra("noteId", docId);
                                startActivity(intent);
                                return false;
                            }
                        });

                        menu.getMenu().add("Eliminar").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                              //  Toast.makeText(MainActivity.this, "eliminar", Toast.LENGTH_SHORT).show();
                                DocumentReference docRef = fStore.collection("notes").document(user.getUid()).collection("mynotes").document(docId);

                                docRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        //note deleted
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(MainActivity.this, "Error al eliminar nota", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                return false;
                            }
                        });

                        menu.show();

                    }
                });


            }

            @NonNull
            @Override
            public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_view_layout, parent, false);
                return new NoteViewHolder(view);
            }
        };

        noteList = findViewById(R.id.notelist);


        drawerLayout = findViewById(R.id.drawer);
        nav_view = findViewById(R.id.nav_view);

        nav_view.setNavigationItemSelectedListener(this);

        //para abrir y cerrar el menu lateral
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,R.string.open,R.string.close);
        //escuchamos la accion del drawer
        drawerLayout.addDrawerListener(toggle);
        //le damos el valor de true al indicador
        toggle.setDrawerIndicatorEnabled(true);
        //sincronizamos el toogle
        toggle.syncState();


        noteList.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        noteList.setAdapter(noteAdapter);

        View headerView = nav_view.getHeaderView(0);
        TextView username = headerView.findViewById(R.id.userDisplayName);
        TextView userEmail = headerView.findViewById(R.id.userDisplayEmail);

        if(user.isAnonymous()){
            userEmail.setVisibility(View.GONE);
            username.setText("Usuario Temporal");
        }else {
            userEmail.setText(user.getEmail());
            username.setText(user.getDisplayName());
        }


        FloatingActionButton fab = findViewById(R.id.addNoteFloat);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), AddNotes.class));
                overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
                finish();
            }
        });

    }

    //listener de los item de la barra lateral
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //seleccionamos dependiendo del id
        //el item tiene los metodos getter and setter para leer los datos dentro de la cadena
        drawerLayout.closeDrawer(GravityCompat.START);
        switch (item.getItemId()){

           case R.id.rating:
               rateMe();
                break;

            case R.id.shareapp:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                //aqui va el link de la app de google
                String shareBody = "Te invito a probar esta App : " +
                        "https://play.google.com/store/apps/details?id=com.naxelgames.fortniteapp&hl=es_MX";
                String shareSub = "Muy pronto estara en la PlayStore";
                intent.putExtra(Intent.EXTRA_SUBJECT, shareSub);
                intent.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(intent, "Comparte mi App"));
                break;

            case R.id.notes:
                startActivity(new Intent(this, MainActivity.class));
                 break;
            case R.id.addNote:
                startActivity(new Intent(this, AddNotes.class));
                overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
                break;
            case R.id.sync:
                if(user.isAnonymous()){
                    startActivity(new Intent(this, Login.class));
                    overridePendingTransition(R.anim.slide_up,R.anim.slide_down);

                }else{
                    Toast.makeText(this, "Ya estas conectado", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.logout:
                checkUser();
                break;
            default:
                Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void checkUser() {
        //verificar si el usuario es real o no
        if(user.isAnonymous()){
            displayAlert();

        }else{
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(), SplashActivity.class));
            overridePendingTransition(R.anim.slide_up,R.anim.slide_down);

        }
    }

    public void rateMe(){
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "com.abdiel.firebasenotasfinal")));


        }catch (ActivityNotFoundException e){
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));


        }
    }

    private void displayAlert() {
        AlertDialog.Builder warning = new AlertDialog.Builder(this)
                .setTitle("Estas seguro?")
                .setMessage("Ha iniciado sesión con una cuenta temporal: al cerrar sesión se eliminarán todas las notas.")
                .setPositiveButton("Sincronizar notas", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getApplicationContext(), Register.class));
                        finish();
                    }
                }).setNegativeButton("Cerrar sesion", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        user.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                startActivity(new Intent(getApplicationContext(), SplashActivity.class));
                                overridePendingTransition(R.anim.slide_up,R.anim.slide_down);

                            }
                        });
                    }
                });
        warning.show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.settings){

            //agregar about aqui
            startActivity(new Intent(getApplicationContext(), Aboutapp.class));

        }

        return super.onOptionsItemSelected(item);
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder{

        TextView noteTitle, notecontent;
        View view;
        CardView mCardView;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);

            noteTitle = itemView.findViewById(R.id.titles);
            notecontent = itemView.findViewById(R.id.content);
            mCardView = itemView.findViewById(R.id.noteCard);
            view = itemView;
        }
    }

    private int getRandomColor(){
        List<Integer> colorCode = new ArrayList<>();
        colorCode.add(R.color.blue);
        colorCode.add(R.color.skyblue);
        colorCode.add(R.color.lightPurple);
        colorCode.add(R.color.gray);
        colorCode.add(R.color.greenlight);
        colorCode.add(R.color.lightGreen);
        colorCode.add(R.color.notgreen);
        colorCode.add(R.color.pink);
        colorCode.add(R.color.red);
        colorCode.add(R.color.yellow);

        Random randomColor = new Random();
        int number = randomColor.nextInt(colorCode.size());
        return colorCode.get(number);
    }


    @Override
    protected void onStart() {
        super.onStart();
        noteAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (noteAdapter != null) {
            noteAdapter.stopListening();
        }
    }
}
