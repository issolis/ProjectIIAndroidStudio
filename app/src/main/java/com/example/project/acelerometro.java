package com.example.project;

import android.content.Context;
import android.hardware.SensorEventListener;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

public class acelerometro implements SensorEventListener {
    private TextView points;
    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
    public volatile String info;
    private Socket socket;

    boolean enviado=true;
    private OutputStream outputStream;

    public acelerometro(Context context) throws IOException {

        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        try {
            // Inicializamos el socket y lo conectamos al servidor
            socket = new Socket("192.168.1.5", 5000);
            outputStream = socket.getOutputStream();
        } catch (IOException e) {
            Log.e("Acelerometro", "Error al conectar con el servidor: " + e.getMessage());
        }
        int x=1;
        outputStream.write(x);
    }

    public void start() {
        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        try {
            Thread.sleep(250); // 1000 milisegundos = 1 segundo
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        sensorManager.unregisterListener(this);
        try {
            // Cerramos el socket
            socket.close();
        } catch (IOException e) {
            Log.e("Acelerometro", "Error al cerrar el socket: " + e.getMessage());
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        float x = sensorEvent.values[0];
        float y = sensorEvent.values[1];
        float z = sensorEvent.values[2];


        int direccion = 0;

        if (x > 0 && y > 0) {
            direccion = 1; // Arriba derecha
        } else if (x < 0 && y > 0) {
            direccion = 2; // Arriba izquierda
        } else if (x < 0 && y < 0) {
            direccion = 4; // Abajo izquierda
        } else if (x > 0 && y < 0) {
            direccion = 3; // Abajo derecha
        }

        try {
            int x3=0;
            // Comprobamos si hay datos disponibles para leer en el socket
          if (socket.getInputStream().available() != 0  ) {
                // Enviamos el número correspondiente al servidor

              while(x3!=1000000000){
                  x3++;}
              if(x3==1000000000) {
                  outputStream.write(direccion);
              }
                 // Esperamos un poco antes de enviar el siguiente valor
            }else{
              int availableBytes = socket.getInputStream().available();
              byte[] buffer = new byte[availableBytes];
              outputStream.write(direccion);
          }
        } catch (SocketException e) {
            Log.e("Acelerometro", "Error de socket: " + e.getMessage());
        } catch (IOException e) {
            Log.e("Acelerometro", "Error al enviar datos al servidor: " + e.getMessage());
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
