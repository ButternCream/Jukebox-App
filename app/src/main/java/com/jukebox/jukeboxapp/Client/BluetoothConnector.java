//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.jukebox.jukeboxapp.Client;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BluetoothConnector {
    private BluetoothConnector.BluetoothSocketWrapper bluetoothSocket;
    private BluetoothDevice device;
    private boolean secure;
    private BluetoothAdapter adapter;
    private List<UUID> uuidCandidates;
    private int candidate;

    public BluetoothConnector(BluetoothDevice device, boolean secure, BluetoothAdapter adapter, UUID uuid) {
        this.device = device;
        this.secure = secure;
        this.adapter = adapter;
        this.uuidCandidates = new ArrayList();
        this.uuidCandidates.add(uuid);
        if(this.uuidCandidates != null && this.uuidCandidates.isEmpty()) {
            ;
        }

    }

    public BluetoothConnector.BluetoothSocketWrapper connect() throws IOException {
        boolean success = false;

        while(this.selectSocket()) {
            this.adapter.cancelDiscovery();

            try {
                this.bluetoothSocket.connect();
                success = true;
                break;
            } catch (IOException var7) {
                try {
                    this.bluetoothSocket = new BluetoothConnector.FallbackBluetoothSocket(this.bluetoothSocket.getUnderlyingSocket());
                    Thread.sleep(500L);
                    this.bluetoothSocket.connect();
                    success = true;
                    break;
                } catch (BluetoothConnector.FallbackException var4) {
                    Log.w("BT", "Could not initialize FallbackBluetoothSocket classes.", var7);
                } catch (InterruptedException var5) {
                    Log.w("BT", var5.getMessage(), var5);
                } catch (IOException var6) {
                    Log.w("BT", "Fallback failed. Cancelling.", var6);
                }
            }
        }

        if(!success) {
            throw new IOException("===> Could not connect to device: " + this.device.getAddress());
        } else {
            return this.bluetoothSocket;
        }
    }

    private boolean selectSocket() throws IOException {
        if(this.candidate >= this.uuidCandidates.size()) {
            return false;
        } else {
            UUID uuid = (UUID)this.uuidCandidates.get(this.candidate++);
            Log.e("BT", "===> Attempting to connect to Protocol: " + uuid);
            BluetoothSocket tmp;
            if(this.secure) {
                tmp = this.device.createRfcommSocketToServiceRecord(uuid);
            } else {
                tmp = this.device.createInsecureRfcommSocketToServiceRecord(uuid);
            }

            this.bluetoothSocket = new BluetoothConnector.NativeBluetoothSocket(tmp);
            return true;
        }
    }

    public void close() {
        if(this.bluetoothSocket != null) {
            try {
                this.bluetoothSocket.close();
            } catch (IOException var2) {
                var2.printStackTrace();
            }
        }

    }

    public static class FallbackException extends Exception {
        private static final long serialVersionUID = 1L;

        public FallbackException(Exception e) {
            super(e);
        }
    }

    public class FallbackBluetoothSocket extends BluetoothConnector.NativeBluetoothSocket {
        private BluetoothSocket fallbackSocket;

        public FallbackBluetoothSocket(BluetoothSocket tmp) throws BluetoothConnector.FallbackException {
            super(tmp);

            try {
                Class e = tmp.getRemoteDevice().getClass();
                Class[] paramTypes = new Class[]{Integer.TYPE};
                Method m = e.getMethod("createRfcommSocket", paramTypes);
                Object[] params = new Object[]{Integer.valueOf(1)};
                this.fallbackSocket = (BluetoothSocket)m.invoke(tmp.getRemoteDevice(), params);
            } catch (Exception var7) {
                throw new BluetoothConnector.FallbackException(var7);
            }
        }

        public InputStream getInputStream() throws IOException {
            return this.fallbackSocket.getInputStream();
        }

        public OutputStream getOutputStream() throws IOException {
            return this.fallbackSocket.getOutputStream();
        }

        public void connect() throws IOException {
            this.fallbackSocket.connect();
        }

        public void close() throws IOException {
            this.fallbackSocket.close();
        }
    }

    public static class NativeBluetoothSocket implements BluetoothConnector.BluetoothSocketWrapper {
        private BluetoothSocket socket;

        public NativeBluetoothSocket(BluetoothSocket tmp) {
            this.socket = tmp;
        }

        public InputStream getInputStream() throws IOException {
            return this.socket.getInputStream();
        }

        public OutputStream getOutputStream() throws IOException {
            return this.socket.getOutputStream();
        }

        public String getRemoteDeviceName() {
            return this.socket.getRemoteDevice().getName();
        }

        public void connect() throws IOException {
            this.socket.connect();
        }

        public String getRemoteDeviceAddress() {
            return this.socket.getRemoteDevice().getAddress();
        }

        public void close() throws IOException {
            this.socket.close();
        }

        public BluetoothSocket getUnderlyingSocket() {
            return this.socket;
        }
    }

    public interface BluetoothSocketWrapper {
        InputStream getInputStream() throws IOException;

        OutputStream getOutputStream() throws IOException;

        String getRemoteDeviceName();

        void connect() throws IOException;

        String getRemoteDeviceAddress();

        void close() throws IOException;

        BluetoothSocket getUnderlyingSocket();
    }
}
