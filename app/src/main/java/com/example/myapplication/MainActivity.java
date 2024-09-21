package com.example.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;

import com.example.myapplication.baseDeDatos.DatabaseHelper;
import com.example.myapplication.boletas.LoginDialogFragment;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "MyPrefsFile";  // Nombre del archivo de preferencias
    private static final String KEY_LAST_SHOW_DATE = "lastShowDate";  // Clave para la última fecha de muestra
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        databaseHelper = new DatabaseHelper(this);

        // Verificar si el usuario ha iniciado sesión
        if (databaseHelper.isUserLoggedIn()) {
            // Redirigir a MenuActivity si el usuario ya ha iniciado sesión
            Intent intent = new Intent(MainActivity.this, MenuActivity.class);
            startActivity(intent);
            finish(); // Finalizar MainActivity para que no se pueda volver a ella con el botón "atrás"
            return;
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button btnChangeActivity = findViewById(R.id.button_Menu);
        btnChangeActivity.setOnClickListener(v -> {
            FragmentManager fragmentManager = getSupportFragmentManager();
            LoginDialogFragment loginDialog = new LoginDialogFragment();
            loginDialog.show(fragmentManager, "login_dialog");
        });
    }

}