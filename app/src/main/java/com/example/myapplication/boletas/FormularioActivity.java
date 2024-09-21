package com.example.myapplication.boletas;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.view.Gravity;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.baseDeDatos.DatabaseHelper;

import com.example.myapplication.R;

import java.util.Arrays;
import java.util.Calendar;

public class FormularioActivity extends AppCompatActivity {

    private EditText editTextFechainicio;
    private EditText editTextFechafin;
    private Calendar firstSelectedDate;
    private TextView txt_semana;
    private EditText[] editTexts; // edittext horas extras
    private TextView[] dayTextViews;
    private LinearLayout layout_faltos;
    private boolean[] dayEnabledStatus;
    private EditText[] editTexts_bonos;
    private Button buttonGuardar;
    private Spinner selectolabor;

    @SuppressLint({"CutPasteId", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_formulario);

        selectolabor = findViewById(R.id.selectorlabor);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(),
                R.array.labor_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectolabor.setAdapter(adapter);

        View rootView = findViewById(android.R.id.content);
        rootView.setOnApplyWindowInsetsListener((v, insets) -> {
            // Ajustar el padding superior aquí
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                v.setPadding(0, insets.getSystemGestureInsets().top, 0, 0);
            }
            return insets.consumeSystemWindowInsets();
        });

        editTextFechainicio = findViewById(R.id.editTextFechai);
        editTextFechafin = findViewById(R.id.editTextFechaf);
        txt_semana = findViewById(R.id.texto_semana);
        buttonGuardar = findViewById(R.id.button_guardar);

        //txt_dia_de_sem = findViewById(R.id.texto_dia_de_semana);

        editTextFechainicio.setOnClickListener(v -> showDatePickerDialogForStartDate());
        editTextFechafin.setOnClickListener(v -> showDatePickerDialogForEndDate());

        // muestra los textview como botones
        layout_faltos = findViewById(R.id.dias_faltados);

        // Nombres de los días de la semana
        String[] faltas_de_semana = {"LU", "MA", "MI", "JU", "VI", "SA", "DO"};
        TextView[] dayTextViews = new TextView[faltas_de_semana.length]; // Para guardar las referencias de los TextViews

        // Bandera para saber si ya se seleccionó un rango
        dayEnabledStatus = new boolean[faltas_de_semana.length];

        // Recorrer los días de la semana y crear los TextView
        for (int i = 0; i < faltas_de_semana.length; i++) {
            // Contenedor para el TextView
            LinearLayout contenedor = new LinearLayout(this);
            contenedor.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1); // width=0dp y weight=1
            contenedor.setLayoutParams(params);

            // Crear el TextView
            TextView textView = new TextView(this);
            textView.setText(faltas_de_semana[i]);
            textView.setTextSize(16);
            textView.setGravity(Gravity.CENTER);
            textView.setBackgroundColor(Color.LTGRAY); // Color inicial
            textView.setPadding(16, 16, 16, 16); // Añadir algo de padding
            textView.setClickable(true); // Hacer que sea clickeable

            // Guardar la referencia del TextView
            dayTextViews[i] = textView;

            // Añadir evento de clic para cambiar el color cuando se seleccione
            final int index = i; // Necesario para acceder a la posición dentro del array
            textView.setOnClickListener(v -> {
                // Solo permitir el click si el día está habilitado
                if (dayEnabledStatus[index]) {
                    if (textView.isSelected()) {
                        // Si ya está seleccionado, cambiar a no seleccionado
                        textView.setSelected(false);

                        textView.setBackgroundColor(Color.LTGRAY); // Color desactivado
                        editTexts[index].setEnabled(true);
                        editTexts_bonos[index].setEnabled(true);
                    } else {
                        // Si no está seleccionado, marcarlo como seleccionado
                        textView.setSelected(true);
                        textView.setBackgroundColor(Color.GREEN); // Color activado
                        editTexts[index].setEnabled(false);
                        editTexts_bonos[index].setEnabled(false);
                    }
                }
            });

            // Añadir el TextView al contenedor
            contenedor.addView(textView);

            // Añadir el contenedor al LinearLayout principal
            layout_faltos.addView(contenedor);
        }

        // Guardar la referencia de todos los TextView para usarlos más tarde si es necesario
        this.dayTextViews = dayTextViews;


        // Obtener el LinearLayout donde agregarás los campos
        LinearLayout layout = findViewById(R.id.Horas_extrad_layout);

        // Nombres de los días de la semana
        String[] diasSemana = {"LU", "MA", "MI", "JU", "VI", "SA", "DO"};
        EditText[] editTexts = new EditText[diasSemana.length];

        // Recorrer los días de la semana y crear los campos
        for (int i = 0; i < diasSemana.length; i++) {
            // Contenedor para el TextView y EditText
            LinearLayout contenedor = new LinearLayout(this);
            contenedor.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1); // width=0dp y weight=1
            contenedor.setLayoutParams(params);

            // Crear el TextView
            TextView textView = new TextView(this);
            textView.setText(diasSemana[i]);
            textView.setTextSize(16);
            textView.setGravity(Gravity.CENTER);

            // Crear el EditText
            EditText editText = new EditText(this);
            editText.setInputType(InputType.TYPE_CLASS_NUMBER); // Solo números
            editText.setTextSize(16);
            editText.setGravity(Gravity.CENTER);
            editText.setEms(3); // Para hacer que el EditText sea pequeño
            editText.setEnabled(false); // Inicialmente deshabilitado

            // Guardar la referencia del EditText
            editTexts[i] = editText;

            // Añadir TextView y EditText al contenedor
            contenedor.addView(textView);
            contenedor.addView(editText);

            // Añadir el contenedor al LinearLayout principal
            layout.addView(contenedor);
        }

        // Guardar la referencia de todos los EditText para habilitarlos después
        this.editTexts = editTexts;


        // Obtener el LinearLayout donde agregarás los campos
        LinearLayout layout_bonos = findViewById(R.id.campos_bonos);

        // Nombres de los días de la semana
        String[] diasSemana_bonos = {"LU", "MA", "MI", "JU", "VI", "SA", "DO"};
        EditText[] editTexts_bonose = new EditText[diasSemana_bonos.length];

        // Recorrer los días de la semana y crear los campos
        for (int i = 0; i < diasSemana.length; i++) {
            // Contenedor para el TextView y EditText
            LinearLayout contenedor = new LinearLayout(this);
            contenedor.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1); // width=0dp y weight=1
            contenedor.setLayoutParams(params);

            // Crear el TextView
            TextView textView = new TextView(this);
            textView.setText(diasSemana_bonos[i]);
            textView.setTextSize(16);
            textView.setGravity(Gravity.CENTER);

            // Crear el EditText
            EditText editText = new EditText(this);
            editText.setInputType(InputType.TYPE_CLASS_NUMBER); // Solo números
            editText.setTextSize(16);
            editText.setGravity(Gravity.CENTER);
            editText.setEms(3); // Para hacer que el EditText sea pequeño
            editText.setEnabled(false); // Inicialmente deshabilitado

            // Guardar la referencia del EditText
            editTexts_bonose[i] = editText;

            // Añadir TextView y EditText al contenedor
            contenedor.addView(textView);
            contenedor.addView(editText);

            // Añadir el contenedor al LinearLayout principal
            layout_bonos.addView(contenedor);
        }

        // Guardar la referencia de todos los EditText para habilitarlos después
        this.editTexts_bonos = editTexts_bonose;



        buttonGuardar.setOnClickListener(v -> {
            // Acción que deseas realizar cuando se presiona el botón "Guardar"
            guardarDatos();
        });

    }

    // Método para habilitar/deshabilitar los días según el rango seleccionado
    private void enableDaysBasedOnSelection(Calendar startDate, Calendar endDate) {

        // Clonar la fecha de inicio y asegurarse de que la semana comience el lunes
        Calendar tempDate = (Calendar) startDate.clone();
        tempDate.setFirstDayOfWeek(Calendar.MONDAY);
        tempDate.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); // Empezar el lunes

        // Verificar si se ha seleccionado una fecha de fin
        if (endDate == null) {
            endDate = startDate;  // Si no hay fecha de fin, solo marcar la fecha de inicio
        }

        // Recorrer los días de la semana
        for (int i = 0; i < 7; i++) {
            boolean isInRange = !tempDate.before(startDate) && !tempDate.after(endDate);

            if (isInRange) {
                dayEnabledStatus[i] = true; // Habilitar el día si está dentro del rango
                dayTextViews[i].setBackgroundColor(Color.LTGRAY); // Color por defecto
            } else {
                dayEnabledStatus[i] = false; // Deshabilitar el día si está fuera del rango
                dayTextViews[i].setBackgroundColor(Color.GRAY); // Cambiar color para indicar que no está disponible
                dayTextViews[i].setSelected(false); // Deseleccionar si estaba seleccionado
            }

            tempDate.add(Calendar.DAY_OF_MONTH, 1); // Avanzar al siguiente día
        }
    }

    // Mostrar el DatePicker para la fecha de inicio
    private void showDatePickerDialogForStartDate() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
            firstSelectedDate = Calendar.getInstance();
            firstSelectedDate.setFirstDayOfWeek(Calendar.MONDAY);
            firstSelectedDate.set(selectedYear, selectedMonth, selectedDay);

            editTextFechafin.setText("");  // Limpiar el campo de fecha de fin

            String fecha = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
            editTextFechainicio.setText(fecha);

            updateWeekDaysDisplay(firstSelectedDate, null);  // Actualizar los días de la semana

            // Llamar a verificarFechasCompletas para habilitar layout_faltos si ambas fechas están seleccionadas
        }, year, month, day);

        datePickerDialog.show();
    }



    private void showDatePickerDialogForEndDate() {
        if (firstSelectedDate == null) {
            editTextFechainicio.setError("Primero seleccione la fecha de inicio.");
            return;
        }

        int year = firstSelectedDate.get(Calendar.YEAR);
        int month = firstSelectedDate.get(Calendar.MONTH);
        int day = firstSelectedDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.setFirstDayOfWeek(Calendar.MONDAY);
            selectedDate.set(selectedYear, selectedMonth, selectedDay);

            if (selectedDate.before(firstSelectedDate)) {
                editTextFechafin.setError("Seleccione una fecha igual o posterior a la fecha de inicio.");
                return;
            }

            String fecha = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
            editTextFechafin.setText(fecha);

            // Mostrar la semana de la fecha seleccionada en el TextView
            int weekOfYear = selectedDate.get(Calendar.WEEK_OF_YEAR);
            txt_semana.setText("Semana: " + weekOfYear);

            updateWeekDaysDisplay(firstSelectedDate, selectedDate);  // Actualizar los días de la semana


        }, year, month, day);

        datePickerDialog.getDatePicker().setMinDate(firstSelectedDate.getTimeInMillis());

        Calendar maxDate = (Calendar) firstSelectedDate.clone();
        maxDate.setFirstDayOfWeek(Calendar.MONDAY);
        int daysUntilSunday = Calendar.SUNDAY - firstSelectedDate.get(Calendar.DAY_OF_WEEK);
        if (daysUntilSunday < 0) {
            daysUntilSunday += 7;
        }
        maxDate.add(Calendar.DAY_OF_YEAR, daysUntilSunday);

        datePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());

        datePickerDialog.show();
    }


    // Método para actualizar la visualización de los días de la semana
    private void updateWeekDaysDisplay(Calendar startDate, Calendar endDate) {
        enableDaysBasedOnSelection(startDate, endDate);
        String[] daysOfWeek = {"L", "M", "M", "J", "V", "S", "D"};
        StringBuilder weekDisplay = new StringBuilder();

        // Clonar la fecha de inicio y asegurarse de que la semana comience el lunes
        Calendar tempDate = (Calendar) startDate.clone();
        tempDate.setFirstDayOfWeek(Calendar.MONDAY);
        tempDate.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);  // Empezar el lunes

        // Verificar si se ha seleccionado una fecha de fin
        if (endDate == null) {
            endDate = startDate;  // Si no hay fecha de fin, solo marcar la fecha de inicio
        }

        // Recorrer los días de la semana
        for (int i = 0; i < 7; i++) {
            boolean isSelected = isDateInRange(tempDate, startDate, endDate);  // Verificar si el día está dentro del rango seleccionado

            if (isSelected) {
                // Si el día está seleccionado, habilitar el EditText correspondiente
                editTexts[i].setEnabled(true);
                editTexts_bonos[i].setEnabled(true);
                // Si el día está seleccionado, resaltarlo
                weekDisplay.append("<font color='BLUE'>").append(daysOfWeek[i]).append("</font> ");
            } else {

                // Si no está seleccionado, deshabilitar el EditText correspondiente
                editTexts[i].setEnabled(false);
                editTexts_bonos[i].setEnabled(false);
                // Si no está seleccionado, mostrarlo en color negro
                weekDisplay.append("<font color='black'>").append(daysOfWeek[i]).append("</font> ");
            }

            tempDate.add(Calendar.DAY_OF_MONTH, 1);  // Avanzar al siguiente día
        }

        // Mostrar los días en el TextView
        //txt_dia_de_sem.setText(Html.fromHtml(weekDisplay.toString()), TextView.BufferType.SPANNABLE);
    }

    // Método auxiliar para verificar si una fecha está dentro del rango seleccionado
    private boolean isDateInRange(Calendar currentDate, Calendar startDate, Calendar endDate) {
        return !currentDate.before(startDate) && !currentDate.after(endDate);
    }

    private void guardarDatos() {
        String labor = selectolabor.getSelectedItem().toString();
        String fechaInicio = editTextFechainicio.getText().toString();
        String fechaFin = editTextFechafin.getText().toString();

        // Recopilar días faltados
        StringBuilder diasFaltados = new StringBuilder();
        for (int i = 0; i < dayTextViews.length; i++) {
            if (dayTextViews[i].isSelected()) {
                diasFaltados.append(dayTextViews[i].getText().toString()).append(", ");
            }
        }

        // Recopilar horas extras
        StringBuilder horasExtras = new StringBuilder();
        for (int i = 0; i < editTexts.length; i++) {
            if (!editTexts[i].getText().toString().isEmpty()) {
                horasExtras.append(dayTextViews[i].getText().toString()).append(": ").append(editTexts[i].getText().toString()).append("h, ");
            }
        }

        // Recopilar bonos
        StringBuilder bonos = new StringBuilder();
        for (int i = 0; i < editTexts_bonos.length; i++) {
            if (!editTexts_bonos[i].getText().toString().isEmpty()) {
                bonos.append(dayTextViews[i].getText().toString()).append(": ").append(editTexts_bonos[i].getText().toString()).append(", ");
            }
        }

        // Guardar los datos en la base de datos
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        dbHelper.guardarDatos(labor, fechaInicio, fechaFin, diasFaltados.toString(), horasExtras.toString(), bonos.toString());

        Toast.makeText(FormularioActivity.this, "Datos guardados correctamente", Toast.LENGTH_SHORT).show();
    }





}