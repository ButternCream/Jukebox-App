//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.jukebox.jukeboxapp.Server;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import com.ramimartin.multibluetooth.bus.BluetoothCommunicator;
import com.ramimartin.multibluetooth.bus.ServeurConnectionFail;
import com.ramimartin.multibluetooth.bus.ServeurConnectionSuccess;
import de.greenrobot.event.EventBus;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.UUID;

public class BluetoothServer implements Runnable {
    private boolean CONTINUE_READ_WRITE = true;
    private UUID mUUID;
    public String mClientAddress;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothServerSocket mServerSocket;
    private BluetoothSocket mSocket;
    private InputStream mInputStream;
    private OutputStreamWriter mOutputStreamWriter;

    public String getServerAddress(){
        return mBluetoothAdapter.getAddress();
    }

    public BluetoothServer(BluetoothAdapter bluetoothAdapter, String clientAddress) {
        this.mBluetoothAdapter = bluetoothAdapter;
        this.mClientAddress = clientAddress;
        this.mUUID = UUID.fromString("e0917680-d427-11e4-8830-" + this.mClientAddress.replace(":", ""));
    }

    public void run() {
        try {
            this.mServerSocket = this.mBluetoothAdapter.listenUsingRfcommWithServiceRecord("BLTServer", this.mUUID);
            this.mSocket = this.mServerSocket.accept();
            this.mInputStream = this.mSocket.getInputStream();
            this.mOutputStreamWriter = new OutputStreamWriter(this.mSocket.getOutputStream());
            short e = 1024;
            boolean bytesRead = true;
            byte[] buffer = new byte[e];
            EventBus.getDefault().post(new ServeurConnectionSuccess(this.mClientAddress));

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
        } catch (IOException var6) {
            Log.e("", "ERROR : " + var6.getMessage());
            EventBus.getDefault().post(new ServeurConnectionFail(this.mClientAddress));
        }

    }

    public void write(String message) {
        try {
            if(this.mOutputStreamWriter != null) {
                this.mOutputStreamWriter.write(message);
                this.mOutputStreamWriter.flush();
            }
        } catch (IOException var3) {
            var3.printStackTrace();
        }

    }

    public String getClientAddress() {
        return this.mClientAddress;
    }

    public void closeConnection() {
        if(this.mSocket != null) {
            try {
                this.mInputStream.close();
                this.mInputStream = null;
                this.mOutputStreamWriter.close();
                this.mOutputStreamWriter = null;
                this.mSocket.close();
                this.mSocket = null;
                this.mServerSocket.close();
                this.mServerSocket = null;
                this.CONTINUE_READ_WRITE = false;
            } catch (Exception var2) {
                ;
            }

            this.CONTINUE_READ_WRITE = false;
        }

    }
}
