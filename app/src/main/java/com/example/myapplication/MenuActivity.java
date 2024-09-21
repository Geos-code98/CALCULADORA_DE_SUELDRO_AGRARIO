package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.myapplication.baseDeDatos.DatabaseHelper;
import com.example.myapplication.boletas.FormularioActivity;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Calendar;
import java.util.Objects;

public class MenuActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "MyPrefsFile";  // Nombre del archivo de preferencias
    private static final String KEY_LAST_SHOW_DATE = "lastShowDate";  // Clave para la última fecha de muestra

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu);

        // Configura el Toolbar (menu)
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // fin
        //ajuste de activity (barra de notificacione
        View rootView = findViewById(android.R.id.content);
        rootView.setOnApplyWindowInsetsListener((v, insets) -> {
            // Ajustar el padding superior aquí
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                v.setPadding(0, insets.getSystemGestureInsets().top, 0, 0);
            }
            return insets.consumeSystemWindowInsets();
        });
        // fin

        // En tu Activity o Fragment donde quieres mostrar los datos
        LinearLayout linearLayout = findViewById(R.id.linearlayout); // Asegúrate de que el id del LinearLayout sea correcto

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        Cursor cursor = dbHelper.getDatosBoleta();

        // Verificar que el cursor tiene datos
        if (cursor != null && cursor.moveToFirst()) {
            do {
                // Inflar el diseño del ítem dinámico
                View itemView = LayoutInflater.from(this).inflate(R.layout.activity_menu, null);

                // Obtener los TextViews del diseño inflado
                TextView txtLabor = itemView.findViewById(R.id.txt_labor);
                TextView txtFechas = itemView.findViewById(R.id.txt_fechas);
                TextView txtSemanaDias = itemView.findViewById(R.id.txt_semana_dias);
                TextView txtHorasBonos = itemView.findViewById(R.id.txt_horas_bonos);
                TextView txtEmpresa = itemView.findViewById(R.id.txt_empresa);

                // Obtener los datos del cursor y asignarlos a los TextViews
                @SuppressLint("Range") String labor = cursor.getString(cursor.getColumnIndex("column_labor"));
                @SuppressLint("Range") String fechaInicio = cursor.getString(cursor.getColumnIndex("fecha_i"));
                @SuppressLint("Range") String fechaFin = cursor.getString(cursor.getColumnIndex("fecha_f"));
                @SuppressLint("Range") String semana = cursor.getString(cursor.getColumnIndex("semana"));
                @SuppressLint("Range") String dias = cursor.getString(cursor.getColumnIndex("dias"));
                @SuppressLint("Range") int horasExtras = cursor.getInt(cursor.getColumnIndex("horas_extras"));
                @SuppressLint("Range") int bonos = cursor.getInt(cursor.getColumnIndex("bonos"));
                @SuppressLint("Range") String empresa = cursor.getString(cursor.getColumnIndex("empresa"));

                // Asignar los valores obtenidos de la base de datos a los TextViews

                txtLabor.setText(labor);
                txtFechas.setText("Fecha Inicio: " + fechaInicio + " - Fecha Fin: " + fechaFin);
                txtSemanaDias.setText("Semana: " + semana + ", Días: " + dias);
                txtHorasBonos.setText("Horas Extras: " + horasExtras + ", Bonos: " + (bonos == 1 ? "Sí" : "No"));
                txtEmpresa.setText("Empresa: " + empresa);

                // Finalmente, agregar el ítem inflado al LinearLayout principal
                linearLayout.addView(itemView);

            } while (cursor.moveToNext());

            // Cerrar el cursor cuando ya no lo necesites
            cursor.close();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflar el menú; esto agrega elementos a la barra de acción si está presente.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Recorremos los ítems del menú y les cambiamos el color del texto
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            SpannableString spanString = new SpannableString(Objects.requireNonNull(item.getTitle()).toString());
            // Cambiar el color del texto a negro
            spanString.setSpan(new ForegroundColorSpan(Color.BLACK), 0, spanString.length(), 0);
            item.setTitle(spanString);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Obtener el id del ítem seleccionado
        int id = item.getItemId();

        if (id == R.id.action_create) {
            // Acción cuando se selecciona "Settings"
            Intent intent = new Intent(MenuActivity.this, FormularioActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_perfil) {
            // Acción cuando se selecciona "Search"

            return true;
        } else if (id == R.id.action_settings) {
            // Acción cuando se selecciona "Search"

            return true;
        } else if (id == R.id.action_help) {
            // Acción cuando se selecciona "Help"

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean shouldShowDialog() {// confirma si el mensaje ya se lanzo en el dia
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME , MODE_PRIVATE);
        long lastShowDate = preferences.getLong(KEY_LAST_SHOW_DATE, 0);

        // Obtener la fecha actual
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        long todayMillis = today.getTimeInMillis();

        // Comparar la última vez que se mostró con la fecha de hoy
        if (lastShowDate < todayMillis) {
            // Actualizar la última fecha de visualización
            SharedPreferences.Editor editor = preferences.edit();
            editor.putLong(KEY_LAST_SHOW_DATE, todayMillis);
            editor.apply();
            return true;
        } else {
            return false;
        }
    }

    // Método para mostrar el diálogo ( este genera el mensaje)
    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Comunicado¡")
                .setMessage("Bienvenidos a Calculadora Agraria, una aplicación diseñada para ayudarte a calcular de manera rápida y precisa el promedio de ingresos que podrás percibir durante una semana de trabajo en el campo y packing.")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Acción al hacer clic en Aceptar
                        dialog.dismiss(); // Cierra el diálogo
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Acción al hacer clic en Cancelar
                        dialog.dismiss(); // Cierra el diálogo
                    }
                });
        // Crear y mostrar el diálogo
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}