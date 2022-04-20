package com.example.crudfirebasejava.Adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.crudfirebasejava.FiltroBuscador.CustomFilter;
import com.example.crudfirebasejava.Models.Persona;
import com.example.crudfirebasejava.R;

import java.util.ArrayList;

public class ListViewPersonasAdapter extends BaseAdapter implements Filterable {
    Context context;
    public ArrayList<Persona> personaData, filterList;
    LayoutInflater layoutInflater;
    Persona personaModel;
    CustomFilter filter;

    public ListViewPersonasAdapter(Context context, ArrayList<Persona> personaData) {
        this.context = context;
        this.personaData = personaData;
        this.filterList = personaData;
        layoutInflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE
        );
    }

    @Override
    public int getCount() {
        return personaData.size();
    }

    @Override
    public Object getItem(int position) {
        return personaData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        if(rowView==null){
            rowView = layoutInflater.inflate(R.layout.lista_personas,
                    null,
                    true);
        }
        //enlazar vistas


        TextView nombre = rowView.findViewById(R.id.nombre);
        TextView apellidos = rowView.findViewById(R.id.apellidos);


        personaModel = personaData.get(position);
      nombre.setText(personaModel.getNombre());
      apellidos.setText(personaModel.getApellidos());



        return rowView;
    }

    @Override
    public Filter getFilter() {
        if(filter==null){
            filter = new CustomFilter(filterList, this);
        }
        return  filter;
    }
}
