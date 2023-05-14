package com.example.project;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.content.Context;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private EditText mEditTextMessage;
    private TextView mTextViewMessage;
    private Button mButtonSend;
    public   Socket socket;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Permitir la conexión en el hilo principal (para fines demostrativos únicamente)
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // Referencias a las vistas

        mButtonSend = findViewById(R.id.button);

        // Listener del botón enviar
        mButtonSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // Crear un hilo de ejecución
                acelerometro miGiroscopio = null;
                try {
                    miGiroscopio = new acelerometro(getApplicationContext());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                miGiroscopio.start();

            }
        });
    }

    // Método que envía un mensaje al servidor y devuelve la respuesta recibida

}