package com.example.myapplication.baseDeDatos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.myapplication.boletas.LoginDialogFragment;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "miBaseDeDatos.db";
    private static final int DATABASE_VERSION = 2;

    private static final String TABLE_NAME = "usuarios";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_DNI = "dni";
    private static final String COLUMN_NOMBRES = "nombres";
    private static final String COLUMN_APELLIDOS = "apellidos";
    private static final String COLUMN_PENSIONES = "pensiones";
    private static final String COLUMN_ASIGNACION = "asignacion";

    Boolean inicio_sesion= false;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_DNI + " TEXT, " +
                COLUMN_NOMBRES + " TEXT, " +
                COLUMN_APELLIDOS + " TEXT, " +
                COLUMN_PENSIONES + " TEXT, " +
                COLUMN_ASIGNACION + " INTEGER, " +
                "logged_in INTEGER DEFAULT 0)"; // Nueva columna para estado de inicio de sesión
        db.execSQL(createTable);

        String createDatosBoletaTable = "CREATE TABLE datos_boleta (" +
                "column_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "column_labor TEXT, " +
                "fecha_i TEXT, " +
                "fecha_f TEXT, " +
                "semana TEXT, " +
                "dias TEXT, " +
                "dias_faltados TEXT, " +
                "horas_extras INTEGER, " +
                "bonos INTEGER, " + // BOOLEAN (1 o 0)
                "bonos_dias TEXT, " +
                "empresa TEXT, " +
                "ID_usuario INTEGER, " +
                "FOREIGN KEY (ID_usuario) REFERENCES " + TABLE_NAME + "(" + COLUMN_ID + "))";

        db.execSQL(createDatosBoletaTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // Solo ejecuta esta actualización si se está actualizando a la versión 2
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN logged_in INTEGER DEFAULT 0");

            // Borrar la tabla antigua
            db.execSQL("DROP TABLE IF EXISTS datos_boleta");

        }if (oldVersion < 3) {
            //db.execSQL("DROP TABLE IF EXISTS datos_boleta");

            // Crear la tabla datos_boleta si se está actualizando a la versión 3
            String createDatosBoletaTable = "CREATE TABLE IF NOT EXISTS datos_boleta (" +
                    "column_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "column_labor TEXT, " +
                    "fecha_i DATE, " +
                    "fecha_f DATE, " +
                    "semana TEXT, " +
                    "dias TEXT, " +
                    "dias_faltados TEXT, " +
                    "horas_extras INTEGER, " +
                    "bonos INTEGER, " + // BOOLEAN (1 o 0)
                    "bonos_dias TEXT, " +
                    "empresa TEXT, " +
                    "ID_usuario INTEGER, " +
                    "FOREIGN KEY (ID_usuario) REFERENCES " + TABLE_NAME + "(" + COLUMN_ID + "))";
            db.execSQL(createDatosBoletaTable);
        }
    }

    public Cursor getDatosBoleta() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM datos_boleta", null);
    }


    public boolean isUserLoggedIn() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = 1 LIMIT 1", null);
        boolean isLoggedIn = cursor.getCount() > 0;
        cursor.close();
        return isLoggedIn;
    }

    public boolean setUserLoggedIn(int userId, boolean loggedIn) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("logged_in", loggedIn ? 1 : 0);
        int rowsUpdated = db.update(TABLE_NAME, contentValues, COLUMN_ID + " = ?", new String[]{String.valueOf(userId)});
        return rowsUpdated > 0;
    }

    // Método para insertar un nuevo registro
    public boolean insertUser(String dni, String nombres, String apellidos, String pensiones, boolean asignacion) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_DNI, dni);
        contentValues.put(COLUMN_NOMBRES, nombres);
        contentValues.put(COLUMN_APELLIDOS, apellidos);
        contentValues.put(COLUMN_PENSIONES, pensiones);
        contentValues.put(COLUMN_ASIGNACION, asignacion ? 1 : 0);
        long result = db.insert(TABLE_NAME, null, contentValues);
        return result != -1; // Si la inserción fue exitosa, retorna true
    }

    public void guardarDatos(String labor, String fechaInicio, String fechaFin, String diasFaltados, String horasExtras, String bonos) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("labor", labor);
        values.put("fechainicio", fechaInicio);
        values.put("fechafin", fechaFin);
        values.put("dias_faltados", diasFaltados);
        values.put("horas_extras", horasExtras);
        values.put("bonos", bonos);
        db.insert("datos", null, values);
    }

}
