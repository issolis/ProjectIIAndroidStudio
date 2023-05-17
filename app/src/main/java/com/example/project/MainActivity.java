package com.example.project;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
    public Socket socket;

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
                // Crear un hilo de ejecución
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Socket socket = null;
                        BufferedReader reader = null;
                        try {
                            // Establecer la conexión con el servidor
                            socket = new Socket("192.168.1.3", 3000);
                            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                            String data;
                            while ((data = reader.readLine()) != null) {
                                final String finalData = data;
                                String[] parts = finalData.split(";");
                                for (String part : parts) {
                                    String[] pair = part.trim().split(":");
                                    if (pair.length == 2) {
                                        String variable = pair[0].trim();
                                        String valor = pair[1].trim();

                                        // Utilizar Handler para realizar la actualización en el hilo principal
                                        Handler handler = new Handler(Looper.getMainLooper());
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                updateTextViews(variable, valor);
                                            }
                                        });
                                    }
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            // Cerrar el socket y el lector de datos
                            if (reader != null) {
                                try {
                                    reader.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            if (socket != null) {
                                try {
                                    socket.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                    private void updateTextViews(String variable, String valor) {
                        TextView lifesTextView = findViewById(R.id.Lifes);
                        TextView pointsTextView = findViewById(R.id.Points);

                        if (variable.equals("lifes")) {
                            lifesTextView.setText("Vidas: " + valor);
                        } else if (variable.equals("points")) {
                            pointsTextView.setText("Puntos: " + valor);
                        }
                    }
                });

                thread.start();

            }
        });
    }


}
