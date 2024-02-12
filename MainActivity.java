package com.example.stachlewski_spu_bakalarska_praca;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;
import android.os.Handler;

public class MainActivity extends AppCompatActivity {
    BluetoothSocket btSocket = null;
    BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
    OutputStream outStr = null;
    BluetoothDevice modul = null;
    static final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private static final int REQUEST_ENABLE_BT = 1;
    boolean connected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button cnct = findViewById(R.id.connect);
        Button front =  findViewById(R.id.front);
        Button back =  findViewById(R.id.back);
        Button left =  findViewById(R.id.left);
        Button right =  findViewById(R.id.right);
        TextView address =  findViewById(R.id.address);
        TextView txt =  findViewById(R.id.text);



        if (btAdapter == null) {
            System.out.println("Bluetooth nie je podporovaný");
        } else {
            if (!btAdapter.isEnabled()) {
                // Ok tak že tu by malo požiadať o povolenie...dúfam
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }

        cnct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!connected) {

                    boolean allowConnect = true;

                    if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }

                    try {
                        modul = btAdapter.getRemoteDevice(address.getText().toString());
                    } catch (IllegalArgumentException e){
                        allowConnect = false;
                        txt.setText("Pripojenie zlyhalo!"+ System.getProperty("line.separator") + "Adresa modulu BT môže byť nesprávna. Zadajte správnu adresu a stlačte PRIPOJIŤ");
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                txt.setText(""); // Vyčistí text
                            }
                        }, 5000); // Delay 5 sekundy
                    }

                    int i = 0;


                    if (allowConnect){

                        do {

                            try {
                                btSocket = modul.createRfcommSocketToServiceRecord(uuid);
                                btSocket.connect();
                                txt.setText("Úspešne pripojené k:\n" + modul.getName());
                                address.setText("");
                                cnct.setText("Odpojiť");
                                connected = true;
                            }
                            catch (Exception e) {
                                allowConnect = false;
                                txt.setText("Pripojenie zlyhalo!\n\n Adresa modulu BT môže byť nesprávna. Zadajte správnu adresu a stlačte 'PRIPOJIŤ' ");
                                break;
                            }

                            i++;

                        } while(!btSocket.isConnected() && i < 10);

                    }

                }

                else {
                    try {
                        btSocket.close();
                        cnct.setText("Pripojiť");
                        connected = false;
                        txt.setText("Odpojené");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

            }
        });


        front.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (connected) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        sendData(1);
                        txt.setText("Rovno");
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        sendData(0);
                        txt.setText("Stop");
                    }
                } else {
                    txt.setText("Nepripojené");

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            txt.setText(""); // Vyčistí text
                        }
                    }, 1500); // Delay 1,5 sekundy
                }
                return false;

            }
        });

        back.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (connected) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        sendData(2);
                        txt.setText("Dozadu");
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        sendData(0);
                        txt.setText("Stop");
                    }
                } else {
                    txt.setText("Nepripojené");

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            txt.setText(""); // Vyčistí text
                        }
                    }, 1500); // Delay 3 sekundy
                }
                return false;
            }
        });

        left.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (connected) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        sendData(3);
                        txt.setText("Vlavo");
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        sendData(0);
                        txt.setText("Stop");
                    }
                } else {
                    txt.setText("Nepripojené");

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            txt.setText(""); // Vyčistí text
                        }
                    }, 1500); // Delay 3 sekundy
                }
                return false;
            }
        });

        right.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (connected) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        sendData(4);
                        txt.setText("Vpravo");
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        sendData(0);
                        txt.setText("Stop");
                    }
                } else {
                    txt.setText("Nepripojené");

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            txt.setText(""); // Vyčistí text
                        }
                    }, 1500); // Delay 3 sekundy
                }
                return false;
            }
        });

    }

    public void sendData(int data){

        try {
            outStr = btSocket.getOutputStream();
            outStr.write(data);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                //txt.setText ("Bluetooth je teraz zapnutý");
            } else {
                // Používateľ odmietol povoliť Bluetooth. Big problem
            }
        }
    }

}