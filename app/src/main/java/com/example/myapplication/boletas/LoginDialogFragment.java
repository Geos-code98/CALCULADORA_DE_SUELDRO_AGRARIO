package com.example.myapplication.boletas;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.myapplication.MainActivity;
import com.example.myapplication.MenuActivity;
import com.example.myapplication.R;
import com.example.myapplication.baseDeDatos.DatabaseHelper;


public class LoginDialogFragment extends DialogFragment {

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Inflar el layout del diálogo
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.login_dialog, null);

        // Referencias a los campos del login
        EditText campoDNI = view.findViewById(R.id.textDNI);
        EditText camponombres = view.findViewById(R.id.textnombres);
        EditText campoapellidos = view.findViewById(R.id.textapellidos);
        Spinner selectorpensiones = view.findViewById(R.id.selectorpensiones);
        Switch asignacionselec = view.findViewById(R.id.switchasignacion);
        Button loginButton = view.findViewById(R.id.login_button);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.pension_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectorpensiones.setAdapter(adapter);

        // Instanciar DatabaseHelper
        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());

        // Manejar la acción de login
        loginButton.setOnClickListener(v -> {
            String dni = campoDNI.getText().toString();
            String nombres = camponombres.getText().toString();
            String apellidos = campoapellidos.getText().toString();
            String pensiones = selectorpensiones.getSelectedItem().toString();
            boolean asignacionEstado = asignacionselec.isChecked();

            boolean isInserted = dbHelper.insertUser(dni, nombres, apellidos, pensiones, asignacionEstado);
            if (isInserted) {
                Toast.makeText(requireContext(), "Datos insertados correctamente", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), MenuActivity.class); // Usar getActivity() para obtener el contexto
                startActivity(intent);
                dismiss();
                 // Cerrar el diálogo después de insertar
            } else {
                Toast.makeText(requireContext(), "Error al insertar datos", Toast.LENGTH_SHORT).show();
            }
        });
        // Configurar el diálogo
        AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
        builder1.setView(view)
                .setTitle("Iniciar sesión")
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        // Manejar la acción de login


        return builder1.create();
    }

}

