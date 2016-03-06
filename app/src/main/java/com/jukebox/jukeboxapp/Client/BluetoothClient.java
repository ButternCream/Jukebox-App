//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.jukebox.jukeboxapp.Client;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import com.ramimartin.multibluetooth.bluetooth.client.BluetoothConnector;
import com.ramimartin.multibluetooth.bus.BluetoothCommunicator;
import com.ramimartin.multibluetooth.bus.ClientConnectionFail;
import com.ramimartin.multibluetooth.bus.ClientConnectionSuccess;
import de.greenrobot.event.EventBus;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.UUID;

public class BluetoothClient implements Runnable {
    private boolean CONTINUE_READ_WRITE = true;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice mBluetoothDevice;
    private UUID mUuid;
    private String mAdressMac;
    private BluetoothSocket mSocket;
    private InputStream mInputStream;
    private OutputStreamWriter mOutputStreamWriter;
    private BluetoothConnector mBluetoothConnector;

    public BluetoothClient(BluetoothAdapter bluetoothAdapter, String adressMac) {
        this.mBluetoothAdapter = bluetoothAdapter;
        this.mAdressMac = adressMac;
        this.mUuid = UUID.fromString("e0917680-d427-11e4-8830-" + bluetoothAdapter.getAddress().replace(":", ""));
    }

    public void run() {
        this.mBluetoothDevice = this.mBluetoothAdapter.getRemoteDevice(this.mAdressMac);

        while(this.mInputStream == null) {
            this.mBluetoothConnector = new BluetoothConnector(this.mBluetoothDevice, true, this.mBluetoothAdapter, this.mUuid);

            try {
                this.mSocket = this.mBluetoothConnector.connect().getUnderlyingSocket();
                this.mInputStream = this.mSocket.getInputStream();
            } catch (IOException var6) {
                Log.e("", "===> mSocket IOException", var6);
                EventBus.getDefault().post(new ClientConnectionFail());
                var6.printStackTrace();
            }
        }

        if(this.mSocket == null) {
            Log.e("", "===> mSocket == Null");
        } else {
            try {
                this.mOutputStreamWriter = new OutputStreamWriter(this.mSocket.getOutputStream());
                short e = 1024;
                boolean bytesRead = true;
                byte[] buffer = new byte[e];
                EventBus.getDefault().post(new ClientConnectionSuccess());

                StringBuilder sb;
                for(; this.CONTINUE_READ_WRITE; EventBus.getDefault().post(new BluetoothCommunicator(sb.toString()))) {
                    sb = new StringBuilder();
                    int bytesRead1 = this.mInputStream.read(buffer);
                    if(bytesRead1 != -1) {
                        String result;
                        for(result = ""; bytesRead1 == e && buffer[e] != 0; bytesRead1 = this.mInputStream.read(buffer)) {
                            result = result + new String(buffer, 0, bytesRead1);
                        }

                        result = result + new String(buffer, 0, bytesRead1);
                        sb.append(result);
                    }
                }
            } catch (IOException var7) {
                Log.e("", "===> Client run");
                var7.printStackTrace();
                EventBus.getDefault().post(new ClientConnectionFail());
            }

        }
    }

    public void write(String message) {
        try {
            this.mOutputStreamWriter.write(message);
            this.mOutputStreamWriter.flush();
        } catch (IOException var3) {
            Log.e("", "===> Client write");
            var3.printStackTrace();
        }

    }

    public void closeConnexion() {
        if(this.mSocket != null) {
            try {
                this.mInputStream.close();
                this.mInputStream = null;
                this.mOutputStreamWriter.close();
                this.mOutputStreamWriter = null;
                this.mSocket.close();
                this.mSocket = null;
                this.mBluetoothConnector.close();
            } catch (Exception var2) {
                Log.e("", "===> Client closeConnexion");
            }

            this.CONTINUE_READ_WRITE = false;
        }

    }
    public BluetoothAdapter  getbluetoothadapter() {
        return this.mBluetoothAdapter;
    }
    public BluetoothDevice getbluetoothdevice() {
        return this.mBluetoothDevice;
    }
    public UUID getuuid() {
        return this.mUuid;
    }
    public String getadressmac() {
        return this.mAdressMac;
    }
    public BluetoothSocket getbluetoothsocket() {
        return this.mSocket;
    }
    public BluetoothConnector getbluetoothconnector() {
        return this.mBluetoothConnector;
    }
}
