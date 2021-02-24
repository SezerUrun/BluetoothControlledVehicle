package com.example.myapplication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelUuid;
import android.util.Log;
import android.widget.Toast;

import androidx.core.graphics.drawable.TintAwareDrawable;

import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BluetoothHelper {
    private static final String TAG = "BtCommunicationApp";
    private static Handler handler; // handler that gets info from Bluetooth service
    private static ConnectionThread connectionThread;
    private static BluetoothAdapter bluetoothAdapter;
    private static BluetoothSocket bluetoothSocket;
    private static BluetoothServerSocket bluetoothServerSocket;
    private static Set<BluetoothDevice> bondedDevices;
    private static OutputStream outputStream;
    private static InputStream inputStream;

    BluetoothHelper(){
        handler=new Handler();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter!=null){
            connectionThread=new ConnectionThread();
        }
    }

    public static BluetoothAdapter getBluetoothAdapter() {
        return bluetoothAdapter;
    }

    public static void setBluetoothAdapter(BluetoothAdapter bluetoothAdapter) {
        BluetoothHelper.bluetoothAdapter = bluetoothAdapter;
    }

    public static BluetoothSocket getBluetoothSocket() {
        return bluetoothSocket;
    }

    public static void setBluetoothSocket(BluetoothSocket bluetoothSocket) {
        BluetoothHelper.bluetoothSocket = bluetoothSocket;
    }

    public static BluetoothServerSocket getBluetoothServerSocket() {
        return bluetoothServerSocket;
    }

    public static void setBluetoothServerSocket(BluetoothServerSocket bluetoothServerSocket) {
        BluetoothHelper.bluetoothServerSocket = bluetoothServerSocket;
    }

    public static String getTAG() {
        return TAG;
    }

    public Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public  ConnectionThread getConnectionThread() {
        return connectionThread;
    }

    public void setConnectionThread(ConnectionThread connectionThread) {
        this.connectionThread = connectionThread;
    }

    public List<String> getBondedDevices(){
        if (getBluetoothAdapter().isEnabled()) {
            bondedDevices = bluetoothAdapter.getBondedDevices();
            List<String> list_pairedDevices = new ArrayList<>();
            if (bondedDevices.size() > 0) {
                String deviceInfo = "";
                for (BluetoothDevice bluetoothDevice : bondedDevices) {
                    deviceInfo = bluetoothDevice.getName();
                    //deviceInfo+= bluetoothDevice.getAddress(); // MAC address
                    list_pairedDevices.add(deviceInfo);
                }
            }
            return list_pairedDevices;
        }
        else{
            return null;
        }
    }

    public class ConnectionThread extends Thread {
        private byte[] buffer;

        public void connectToDevice(int targetDevice){
            try {
                Object[] devices = bondedDevices.toArray();
                BluetoothDevice device = (BluetoothDevice) devices[targetDevice];
                ParcelUuid[] uuids = device.getUuids();
                bluetoothSocket = device.createRfcommSocketToServiceRecord(uuids[0].getUuid());
                bluetoothSocket.connect();
                outputStream = bluetoothSocket.getOutputStream();
                inputStream = bluetoothSocket.getInputStream();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

        public void run() {
            buffer = new byte[1024];
            int numBytes;
            while (true) {
                try {
                    numBytes = inputStream.read(buffer);
                }
                catch (IOException e) {
                    Log.d(TAG, "Input stream was disconnected", e);
                    break;
                }
            }
        }

        public void write(byte[] bytes, int targetDevice) {
            try {
                if(bondedDevices.size() > 0) {
                    outputStream.write(bytes);
                }
            }
            catch (Exception e) {
                Log.e(TAG, "Error occurred when sending data", e);
            }
        }

        public void cancel() {
            try {
                bluetoothSocket.close();
            }
            catch (IOException e) {
                Log.e(TAG, "Could not close the connect socket", e);
            }
        }
    }
}