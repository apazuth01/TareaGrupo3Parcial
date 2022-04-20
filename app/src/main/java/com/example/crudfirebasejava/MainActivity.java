package com.example.crudfirebasejava;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.crudfirebasejava.Adaptadores.ListViewPersonasAdapter;
import com.example.crudfirebasejava.Models.Persona;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Persona> listPersonas= new ArrayList<Persona>();
    ArrayAdapter<Persona> arrayAdapterPersona;
    ListViewPersonasAdapter listViewPersonasAdapter;
    LinearLayout linearLayoutEditar;
    ListView listViewPersonas;

    EditText nombre,apellidos,edad,direccion,puesto;
    Button btnCancelar;

    Persona personaSeleccionada;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nombre= findViewById(R.id.TNombre);
        apellidos= findViewById(R.id.TApellidos);
        edad= findViewById(R.id.TEdad);
        direccion= findViewById(R.id.TDireccion);
        puesto= findViewById(R.id.TPuesto);

        btnCancelar = findViewById(R.id.btnCancelar);

        listViewPersonas = findViewById(R.id.listViewPersonas);
        linearLayoutEditar = findViewById(R.id.linearLayoutEditar);

        listViewPersonas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                personaSeleccionada = (Persona) parent.getItemAtPosition(position);
                nombre.setText(personaSeleccionada.getNombre());
                apellidos.setText(personaSeleccionada.getApellidos());
                edad.setText(personaSeleccionada.getEdad());
                direccion.setText(personaSeleccionada.getDireccion());
                puesto.setText(personaSeleccionada.getPuesto());

                //hacer visible el linearlayout
                linearLayoutEditar.setVisibility(View.VISIBLE);
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayoutEditar.setVisibility(View.GONE);
                personaSeleccionada = null;
            }
        });

        inicializarFirebase();
        listarPersonas();
    }

    private void  inicializarFirebase(){
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    private void listarPersonas(){
        databaseReference.child("ct_empleados").orderByChild("nombre").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listPersonas.clear();
                for(DataSnapshot objSnaptshot : dataSnapshot.getChildren()){
                    Persona p = objSnaptshot.getValue(Persona.class);
                    listPersonas.add(p);
                }
                //iniciar nuestro adaptador
                listViewPersonasAdapter = new ListViewPersonasAdapter(MainActivity.this, listPersonas);
                listViewPersonas.setAdapter(listViewPersonasAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.crud_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.buscar);

        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                listViewPersonasAdapter.getFilter().filter(newText);
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {


        String Nombre = nombre.getText().toString();
        String Apellidos = apellidos.getText().toString();
        String Edad = edad.getText().toString();
        String Direccion = direccion.getText().toString();
        String Puesto = puesto.getText().toString();

        switch (item.getItemId()){

            case R.id.menu_agregar:
                insertar();
                break;
            case R.id.menu_guardar:
                if(personaSeleccionada != null){
                    if(validarInputs()==false){
                        Persona p = new Persona();
                       p.setId(personaSeleccionada.getId());
                        p.setNombre(Nombre);
                        p.setApellidos(Apellidos);
                        p.setEdad(Edad);
                        p.setDireccion(Direccion);
                        p.setPuesto(Puesto);
                   databaseReference.child("ct_empleados").child(p.getId()).setValue(p);
                        Toast.makeText(this, "Registro Actualizado Exitosamente", Toast.LENGTH_LONG).show();
                        linearLayoutEditar.setVisibility(View.GONE);
                        personaSeleccionada = null;
                    }
                }else{
                    Toast.makeText(this, "Seleccione un registro", Toast.LENGTH_LONG).show();

                }
                break;
            case R.id.menu_eliminar:
                if(personaSeleccionada!=null){
                    Persona p2 = new Persona();
                    p2.setId(personaSeleccionada.getId());
                    databaseReference.child("ct_empleados").child(p2.getId()).removeValue();
                    linearLayoutEditar.setVisibility(View.GONE);
                    personaSeleccionada = null;
                    Toast.makeText(this, "Registro Eliminado Exitosamente", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(this, "Seleccione una Registro para eliminar", Toast.LENGTH_LONG).show();

                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }
    public void insertar(){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(
                MainActivity.this
        );
        View mView = getLayoutInflater().inflate(R.layout.insertar, null);
        Button btnInsertar = (Button) mView.findViewById(R.id.btnInsertar);


        final EditText mnombre = (EditText) mView.findViewById(R.id.TNombre);
        final EditText mapellidos = (EditText) mView.findViewById(R.id.TApellidos);
        final EditText medad = (EditText) mView.findViewById(R.id.TEdad);
        final EditText mdireccion = (EditText) mView.findViewById(R.id.TDireccion);
        final EditText mpuesto = (EditText) mView.findViewById(R.id.TPuesto);

        mBuilder.setView(mView);
        final  AlertDialog dialog = mBuilder.create();
        dialog.show();

        btnInsertar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String Apellidos = mapellidos.getText().toString();
                String Nombre = mnombre.getText().toString();
                String Edad = medad.getText().toString();
                String Direccion = mdireccion.getText().toString();
                String Puesto = mpuesto.getText().toString();


                if(Nombre.isEmpty()){
                    showError(mnombre, "Ingrese el Nombre");
                }else if(Apellidos.isEmpty()){
                    showError(mapellidos, "Ingrese el Apellido");
                }else if(Edad.isEmpty()){
                    showError(medad, "Ingrese la Edad");
                }else if(Direccion.isEmpty()){
                    showError(mdireccion, "Ingrese la Direccion");
                }else if(Puesto.isEmpty()){
                    showError(mpuesto, "Ingrese el Puesto");
                }else{
                    Persona p = new Persona();
                    p.setId(UUID.randomUUID().toString());

                    p.setNombre(Nombre);
                    p.setApellidos(Apellidos);
                    p.setEdad(Edad);
                    p.setDireccion(Direccion);
                    p.setPuesto(Puesto);
                 databaseReference.child("ct_empleados").child(p.getId()).setValue(p);
                 Toast.makeText(MainActivity.this, "Registro Ingresado Exitosamente", Toast.LENGTH_LONG).show();

                dialog.dismiss();
                }
            }
        });


    }

    public void showError(EditText input, String s){
        input.requestFocus();
        input.setError(s);
    }

    public long getFechaMilisegundos(){
        Calendar calendar =Calendar.getInstance();
        long tiempounix = calendar.getTimeInMillis();

        return tiempounix;
    }
    public String getFechaNormal(long fechamilisegundos){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT-5"));
        String fecha = sdf.format(fechamilisegundos);
        return fecha;
    }
    public boolean validarInputs(){
        String Apellidos = apellidos.getText().toString();
        String Nombre = nombre.getText().toString();
        String Edad = edad.getText().toString();
        String Direccion = direccion.getText().toString();
        String Puesto = puesto.getText().toString();
        if(Nombre.isEmpty()){
            showError(nombre, "Ingrese el Nombre");
            return true;
        }else if(Apellidos.isEmpty()){
            showError(apellidos, "Ingrese el Apellido");
            return true;
        }else if(Edad.isEmpty()){
            showError(edad, "Ingrese la Edad");
            return true;
        }else if(Direccion.isEmpty()){
            showError(direccion, "Ingrese la Direccion");
            return true;
        }else if(Puesto.isEmpty()){
            showError(puesto, "Ingrese el Puesto");
            return true;
        }else{
            return false;
        }
    }
}