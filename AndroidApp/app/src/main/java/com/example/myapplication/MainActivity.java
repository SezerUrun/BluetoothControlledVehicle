package com.example.myapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EdgeEffect;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static Button button_refreshDevices,button_forward,button_back,button_left,button_right,button_stop;
    private static BluetoothHelper bluetoothHelper;
    private static String message_toSend,message_received;
    private static int requestCodeForBluetooth=1;
    private static ListView listView_pairedDevices;
    private static int targetDevice;
    private static CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        coordinatorLayout=findViewById(R.id.coordinatorLayout);
        button_refreshDevices=findViewById(R.id.button_refreshDevices);
        button_forward=findViewById(R.id.button_forward);
        button_back=findViewById(R.id.button_back);
        button_left=findViewById(R.id.button_left);
        button_right=findViewById(R.id.button_right);
        button_stop=findViewById(R.id.button_stop);
        listView_pairedDevices=findViewById(R.id.listView_pairedDevices);
        button_forward.setOnClickListener(this);
        button_back.setOnClickListener(this);
        button_left.setOnClickListener(this);
        button_right.setOnClickListener(this);
        button_stop.setOnClickListener(this);
        button_refreshDevices.setOnClickListener(this);
        bluetoothHelper=new BluetoothHelper();

        if (!bluetoothHelper.getBluetoothAdapter().isEnabled()) {
            Intent intent_enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent_enableBluetooth, requestCodeForBluetooth);
        }
        else{
            button_refreshDevices.setEnabled(true);
            button_forward.setEnabled(true);
            button_back.setEnabled(true);
            button_left.setEnabled(true);
            button_right.setEnabled(true);
            button_stop.setEnabled(true);
            listView_pairedDevices.setAdapter(new ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item,bluetoothHelper.getBondedDevices()));
        }

        listView_pairedDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                targetDevice = position;
                bluetoothHelper.getConnectionThread().connectToDevice(targetDevice);
                if (isBluetoothEnabled() && !bluetoothHelper.getBondedDevices().isEmpty()) {
                    Snackbar.make(coordinatorLayout, "Connected device: " + bluetoothHelper.getBondedDevices().get(position), Snackbar.LENGTH_SHORT).show();
                }
                else if(!isBluetoothEnabled()){
                    Snackbar.make(coordinatorLayout, "Bluetooth is disabled", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        int viewId=v.getId();
        if(isBluetoothEnabled() && bluetoothHelper.getBluetoothSocket()!=null) {
            if (viewId == button_refreshDevices.getId()) {
                listView_pairedDevices.setAdapter(new ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item,bluetoothHelper.getBondedDevices()));
            }
            else if (viewId == button_forward.getId()) {
                bluetoothHelper.getConnectionThread().write("0".getBytes(),targetDevice);
            }
            else if (viewId == button_back.getId()) {
                bluetoothHelper.getConnectionThread().write("1".getBytes(),targetDevice);
            }
            else if (viewId == button_left.getId()) {
                bluetoothHelper.getConnectionThread().write("2".getBytes(),targetDevice);
            }
            else if (viewId == button_right.getId()) {
                bluetoothHelper.getConnectionThread().write("3".getBytes(),targetDevice);
            }
            else if (viewId == button_stop.getId()) {
                bluetoothHelper.getConnectionThread().write("4".getBytes(),targetDevice);
            }
        }
        else if(!isBluetoothEnabled()){
            Snackbar.make(coordinatorLayout,"Bluetooth is disabled",Snackbar.LENGTH_SHORT).show();
        }
        else if(isBluetoothEnabled() && bluetoothHelper.getBluetoothSocket()==null){
            Snackbar.make(coordinatorLayout,"There is no connected device",Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==requestCodeForBluetooth){
            if(resultCode==RESULT_OK){
                button_refreshDevices.setEnabled(true);
                button_forward.setEnabled(true);
                button_back.setEnabled(true);
                button_left.setEnabled(true);
                button_right.setEnabled(true);
                button_stop.setEnabled(true);
                listView_pairedDevices.setAdapter(new ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item,bluetoothHelper.getBondedDevices()));
            }
            else{
                Snackbar.make(coordinatorLayout,"Please enable the bluetooth to use the app.",Snackbar.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        bluetoothHelper.getConnectionThread().cancel();
        super.onDestroy();
    }

    public boolean isBluetoothEnabled(){
        if(bluetoothHelper.getBluetoothAdapter().isEnabled()){
            return true;
        }
        else{
            return false;
        }
    }
}
