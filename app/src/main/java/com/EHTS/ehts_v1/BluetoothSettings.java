package com.EHTS.ehts_v1;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Set;

public class BluetoothSettings extends AppCompatActivity {

    private static final int REQUEST_BLUETOOTH_PERMISSION = 1;
    private static final int REQUEST_BLUETOOTH_ADMIN_PERMISSION = 2;
    private static final int REQUEST_DISCOVER_BT = 3;

    private TextView mStatusBleTv, mPairedTv;
    private ImageView mBlueIV;
    private Button mOnBtn, mOffBtn, mDiscoverBtn, mPairedBtn;
    private BluetoothAdapter bluetoothAdapter;
    private ActivityResultLauncher<Intent> enableBluetoothLauncher;
    private ActivityResultLauncher<Intent> discoverableLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth_settings);

        mStatusBleTv = findViewById(R.id.statusBluetoothTv);
        mPairedTv = findViewById(R.id.pairTv);
        mBlueIV = findViewById(R.id.bluetoothIv);
        mOnBtn = findViewById(R.id.onButn);
        mOffBtn = findViewById(R.id.offButn);
        mDiscoverBtn = findViewById(R.id.discoverableBtn);
        mPairedBtn = findViewById(R.id.PairedBtn);

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed(); // Go back to the previous page
            }
        });

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) {
            mStatusBleTv.setText("Bluetooth is not available");
        } else {
            mStatusBleTv.setText("Bluetooth is available");

            if (bluetoothAdapter.isEnabled()) {
                mBlueIV.setImageResource(R.drawable.baseline_bluetooth_24);
            } else {
                mBlueIV.setImageResource(R.drawable.baseline_bluetooth_disabled_24);
            }

            enableBluetoothLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK) {
                            mBlueIV.setImageResource(R.drawable.baseline_bluetooth_24);
                            showToast("Bluetooth is On");
                        } else {
                            showToast("Bluetooth is Off");
                        }
                    });

            discoverableLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK) {
                            showToast("Device is discoverable");
                        } else {
                            showToast("Device is not discoverable");
                        }
                    });

            mOnBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ContextCompat.checkSelfPermission(BluetoothSettings.this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                        // Permission has not been granted, so request it
                        ActivityCompat.requestPermissions(BluetoothSettings.this, new String[]{Manifest.permission.BLUETOOTH}, REQUEST_BLUETOOTH_PERMISSION);
                        return;
                    }
                    if (!bluetoothAdapter.isEnabled()) {
                        showToast("Turning on Bluetooth..");
                        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        enableBluetoothLauncher.launch(intent);
                    } else {
                        showToast("Bluetooth is already on");
                    }
                }
            });

            mDiscoverBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ContextCompat.checkSelfPermission(BluetoothSettings.this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
                        // Permission has not been granted, so request it
                        ActivityCompat.requestPermissions(BluetoothSettings.this, new String[]{Manifest.permission.BLUETOOTH_ADMIN}, REQUEST_BLUETOOTH_ADMIN_PERMISSION);
                        return;
                    }
                    if (!bluetoothAdapter.isDiscovering()) {
                        showToast("Making Your Device Discoverable");
                        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                        intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                        discoverableLauncher.launch(intent);
                    }
                }
            });

            mOffBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (bluetoothAdapter.isEnabled()) {
                        if (ContextCompat.checkSelfPermission(BluetoothSettings.this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                            // Permission has not been granted, so request it
                            ActivityCompat.requestPermissions(BluetoothSettings.this, new String[]{Manifest.permission.BLUETOOTH}, REQUEST_BLUETOOTH_PERMISSION);
                            return;
                        }
                        bluetoothAdapter.disable();
                        showToast("Turning Bluetooth off");
                        mBlueIV.setImageResource(R.drawable.baseline_bluetooth_disabled_24);
                    } else {
                        showToast("Bluetooth is already off");
                    }
                }
            });

            mPairedBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (bluetoothAdapter.isEnabled()) {
                        mPairedTv.setText("Paired Devices");
                        if (ContextCompat.checkSelfPermission(BluetoothSettings.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                            // Permission has not been granted, so request it
                           ActivityCompat.requestPermissions(BluetoothSettings.this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_BLUETOOTH_PERMISSION);
                         //   return;
                        }

                        // Permission has already been granted, proceed with getting paired devices
                        Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
                        if (devices.size() > 0) {
                            for (BluetoothDevice device : devices) {
                                mPairedTv.append("\nDevice: " + device.getName() + ", " + device.getAddress());
                            }
                        } else {
                            mPairedTv.append("\nNo paired devices found");
                        }
                    } else {
                        showToast("Turn on Bluetooth to get paired devices");
                    }
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_BLUETOOTH_ADMIN_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted, proceed with making the device discoverable
                showToast("Making Your Device Discoverable");
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                discoverableLauncher.launch(intent);
            } else {
                showToast("Bluetooth Admin permission denied. Cannot make the device discoverable.");
            }
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_DISCOVER_BT) {
            if (resultCode == RESULT_OK) {
                showToast("Device is discoverable");
            } else {
                showToast("Device is not discoverable");
            }
        }
    }
}
