package com.example.smartbedmatress;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice bluetoothDevice;
    private BluetoothSocket bluetoothSocket;
    private OutputStream outputStream;
    private final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private final String specificMacAddress = "0C:B8:15:46:23:B2";

//-
//    private boolean isVibrationOn = false;
    private ImageButton buttonPlus1;
    private ImageButton buttonMinus1;
    private ImageButton buttonReset1;

    private ImageButton buttonPlus2;
    private ImageButton buttonMinus2;
    private ImageButton buttonReset2;

    private boolean plusPressed1 = false;
    private boolean minusPressed1 = false;

    private boolean plusPressed2 = false;
    private boolean minusPressed2 = false;

    private Handler handler = new Handler();
    private Vibrator vibrator;
    private ImageButton vibratorButton;
    private boolean isVibrationOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not supported on this device", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }

        bluetoothDevice = bluetoothAdapter.getRemoteDevice(specificMacAddress);

        // Call the connectToBluetoothDevice() method to establish the Bluetooth connection
        connectToBluetoothDevice();

        buttonPlus1 = findViewById(R.id.buttonPlus1);
        buttonMinus1 = findViewById(R.id.buttonMinus1);
        buttonReset1 = findViewById(R.id.buttonReset1);

        buttonPlus2 = findViewById(R.id.buttonPlus2);
        buttonMinus2 = findViewById(R.id.buttonMinus2);
        buttonReset2 = findViewById(R.id.buttonReset2);

        ImageButton tvModeButton = findViewById(R.id.tvmode);
        ImageButton reclinerModeButton = findViewById(R.id.reclinermode);
        ImageButton zeroGravityButton = findViewById(R.id.zerogravity);
        ImageButton normalModeButton = findViewById(R.id.normalmode);

         vibratorButton = findViewById(R.id.vibratorbuttononoff);


        // Initialize the vibrator
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        vibratorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleVibratorMode();
                vibrate();
            }
        });

        tvModeButton.setOnClickListener(v -> {
            // Create and show a notification for TV Mode
//                showNotification("Smart Bed", "Mode: TV Mode");
            sendCommand("t");
            vibrate();
        });

        reclinerModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create and show a notification for Recliner Mode
//                showNotification("Smart Bed", "Mode: Recliner Mode");
                vibrate();
                sendCommand("s");
            }
        });

        zeroGravityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create and show a notification for Zero Gravity Mode
//                showNotification("Smart Bed", "Mode: 0 Gravity Mode");
                vibrate();
                sendCommand("z");
            }
        });

        normalModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create and show a notification for Normal Mode
//                showNotification("Smart Bed", "Mode: Normal Mode");
                vibrate();
                sendCommand("n");
            }
        });

        buttonPlus1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    plusPressed1 = true;
                    vibrate();
                    handler.postDelayed(plusRunnable1, 100); // Delay to repeat command
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    plusPressed1 = false;
                    handler.removeCallbacks(plusRunnable1); // Stop repeating command
                    sendCommand("0"); // Send the "0" command
                }
                return true;
            }
        });

        buttonMinus1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    minusPressed1 = true;
                    vibrate();
                    handler.postDelayed(minusRunnable1, 100); // Delay to repeat command
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    minusPressed1 = false;
                    handler.removeCallbacks(minusRunnable1); // Stop repeating command
                    sendCommand("0"); // Send the "0" command
                }
                return true;
            }
        });

        buttonReset1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCommand("0");
                vibrate();// Send the "0" command
            }
        });

        buttonPlus2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    plusPressed2 = true;
                    vibrate();
                    handler.postDelayed(plusRunnable2, 100); // Delay to repeat command
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    plusPressed2 = false;
                    handler.removeCallbacks(plusRunnable2); // Stop repeating command
                    sendCommand("1"); // Send the "0" command
                }
                return true;
            }
        });

        buttonMinus2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    minusPressed2 = true;
                    vibrate();
                    handler.postDelayed(minusRunnable2, 100); // Delay to repeat command
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    minusPressed2 = false;
                    handler.removeCallbacks(minusRunnable2); // Stop repeating command
                    sendCommand("1"); // Send the "0" command
                }
                return true;
            }
        });

        buttonReset2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCommand("1");
                vibrate();// Send the "0" command
            }
        });
    }

    // Runnable to send "+" command repeatedly
    private Runnable plusRunnable1 = new Runnable() {
        @Override
        public void run() {
            if (plusPressed1) {
                sendCommand("a");
                vibrate();// Send the "+" command
                handler.postDelayed(this, 100); // Repeat every 100ms
            }
        }
    };

    // Runnable to send "-" command repeatedly
    private Runnable minusRunnable1 = new Runnable() {
        @Override
        public void run() {
            if (minusPressed1) {
                vibrate();
                sendCommand("b"); // Send the "-" command
                handler.postDelayed(this, 100); // Repeat every 100ms
            }
        }
    };

    private Runnable plusRunnable2 = new Runnable() {
        @Override
        public void run() {
            if (plusPressed2) {
                sendCommand("c");
                vibrate();// Send the "+" command
                handler.postDelayed(this, 100); // Repeat every 100ms
            }
        }
    };

    // Runnable to send "-" command repeatedly
    private Runnable minusRunnable2 = new Runnable() {
        @Override
        public void run() {
            if (minusPressed2) {
                sendCommand("d");
                vibrate();// Send the "-" command
                handler.postDelayed(this, 100); // Repeat every 100ms
            }
        }
    };

    private void vibrate() {
        if (vibrator != null) {
            vibrator.vibrate(5); // Vibrate for 10 milliseconds (adjust as needed)
        }
    }
    public void toggleVibratorMode() {
        isVibrationOn = !isVibrationOn;
        // Send the appropriate command based on the state
        if (isVibrationOn) {
            sendCommand("x"); // Send command to turn the vibrator on
        } else {
            sendCommand("y"); // Send command to turn the vibrator off
        }
        // Perform any other actions related to the toggle state here
    }

    private void connectToBluetoothDevice() {
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1);
                return;
            }
            bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid);
            bluetoothSocket.connect();
            outputStream = bluetoothSocket.getOutputStream();
            Toast.makeText(this, "Connected to " + bluetoothDevice.getName(), Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to connect to the device", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendCommand(String command) {
        if (bluetoothSocket == null || outputStream == null) {
            Toast.makeText(this, "Bluetooth not connected", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            outputStream.write(command.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (outputStream != null) {
                outputStream.close();
            }
            if (bluetoothSocket != null) {
                bluetoothSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
