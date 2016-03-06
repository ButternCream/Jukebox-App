//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.jukebox.jukeboxapp.Manager;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.util.Log;
import com.ramimartin.multibluetooth.bluetooth.client.BluetoothClient;
import com.ramimartin.multibluetooth.bluetooth.server.BluetoothServer;
import com.ramimartin.multibluetooth.bus.BondedDevice;
import de.greenrobot.event.EventBus;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class BluetoothManager extends BroadcastReceiver {
    public static final int REQUEST_DISCOVERABLE_CODE = 114;
    public static int BLUETOOTH_REQUEST_ACCEPTED;
    public static final int BLUETOOTH_REQUEST_REFUSED = 0;
    public static final int BLUETOOTH_TIME_DICOVERY_60_SEC = 60;
    public static final int BLUETOOTH_TIME_DICOVERY_120_SEC = 120;
    public static final int BLUETOOTH_TIME_DICOVERY_300_SEC = 300;
    public static final int BLUETOOTH_TIME_DICOVERY_600_SEC = 600;
    public static final int BLUETOOTH_TIME_DICOVERY_900_SEC = 900;
    public static final int BLUETOOTH_TIME_DICOVERY_1200_SEC = 1200;
    public static final int BLUETOOTH_TIME_DICOVERY_3600_SEC = 3600;
    private static int BLUETOOTH_NBR_CLIENT_MAX = 7;
    private Activity mActivity;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothClient mBluetoothClient;
    private ArrayList<String> mAdressListServerWaitingConnection;
    private HashMap<String, BluetoothServer> mServeurWaitingConnectionList;
    private ArrayList<BluetoothServer> mServeurConnectedList;
    private HashMap<String, Thread> mServeurThreadList;
    private int mNbrClientConnection;
    public BluetoothManager.TypeBluetooth mType;
    private int mTimeDiscoverable;
    public boolean isConnected;
    private boolean mBluetoothIsEnableOnStart;
    private String mBluetoothNameSaved;

    public BluetoothManager(Activity activity) {
        this.mActivity = activity;
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.mBluetoothNameSaved = this.mBluetoothAdapter.getName();
        this.mBluetoothIsEnableOnStart = this.mBluetoothAdapter.isEnabled();
        this.mType = BluetoothManager.TypeBluetooth.None;
        this.isConnected = false;
        this.mNbrClientConnection = 0;
        this.mAdressListServerWaitingConnection = new ArrayList();
        this.mServeurWaitingConnectionList = new HashMap();
        this.mServeurConnectedList = new ArrayList();
        this.mServeurThreadList = new HashMap();
    }

    public void selectServerMode() {
        this.startDiscovery();
        this.mType = BluetoothManager.TypeBluetooth.Server;
        this.setServerBluetoothName();
    }

    private void setServerBluetoothName() {
        this.mBluetoothAdapter.setName("Server " + (this.getNbrClientMax() - this.mNbrClientConnection) + " places available " + Build.MODEL);
    }

    public void selectClientMode() {
        this.startDiscovery();
        this.mType = BluetoothManager.TypeBluetooth.Client;
        this.mBluetoothAdapter.setName("Client " + Build.MODEL);
    }

    public String getYourBtMacAddress() {
        return this.mBluetoothAdapter != null?this.mBluetoothAdapter.getAddress():null;
    }

    public void setNbrClientMax(int nbrClientMax) {
        if(nbrClientMax <= BLUETOOTH_NBR_CLIENT_MAX) {
            BLUETOOTH_NBR_CLIENT_MAX = nbrClientMax;
        }

    }

    public int getNbrClientMax() {
        return BLUETOOTH_NBR_CLIENT_MAX;
    }

    public boolean isNbrMaxReached() {
        return this.mNbrClientConnection == this.getNbrClientMax();
    }

    public void setServerWaitingConnection(String address, BluetoothServer bluetoothServer, Thread threadServer) {
        this.mAdressListServerWaitingConnection.add(address);
        this.mServeurWaitingConnectionList.put(address, bluetoothServer);
        this.mServeurThreadList.put(address, threadServer);
    }

    public void incrementNbrConnection() {
        ++this.mNbrClientConnection;
        this.setServerBluetoothName();
        if(this.mNbrClientConnection == this.getNbrClientMax()) {
            ;
        }

        Log.e("", "===> incrementNbrConnection mNbrClientConnection : " + this.mNbrClientConnection);
    }

    private void resetWaitingThreadServer() {
        Iterator i$ = this.mServeurThreadList.entrySet().iterator();

        Entry bluetoothServerMap;
        while(i$.hasNext()) {
            bluetoothServerMap = (Entry)i$.next();
            if(this.mAdressListServerWaitingConnection.contains(bluetoothServerMap.getKey())) {
                Log.e("", "===> resetWaitingThreadServer Thread : " + (String)bluetoothServerMap.getKey());
                ((Thread)bluetoothServerMap.getValue()).interrupt();
            }
        }

        i$ = this.mServeurWaitingConnectionList.entrySet().iterator();

        while(i$.hasNext()) {
            bluetoothServerMap = (Entry)i$.next();
            Log.e("", "===> resetWaitingThreadServer BluetoothServer : " + (String)bluetoothServerMap.getKey());
            ((BluetoothServer)bluetoothServerMap.getValue()).closeConnection();
        }

        this.mAdressListServerWaitingConnection.clear();
        this.mServeurWaitingConnectionList.clear();
    }

    public void decrementNbrConnection() {
        if(this.mNbrClientConnection != 0) {
            --this.mNbrClientConnection;
            if(this.mNbrClientConnection == 0) {
                this.isConnected = false;
            }

            Log.e("", "===> decrementNbrConnection mNbrClientConnection : " + this.mNbrClientConnection);
            this.setServerBluetoothName();
        }
    }

    public void setTimeDiscoverable(int timeInSec) {
        this.mTimeDiscoverable = timeInSec;
        BLUETOOTH_REQUEST_ACCEPTED = this.mTimeDiscoverable;
    }

    public boolean checkBluetoothAviability() {
        return this.mBluetoothAdapter != null;
    }

    public void cancelDiscovery() {
        if(this.isDiscovering()) {
            this.mBluetoothAdapter.cancelDiscovery();
        }

    }

    public boolean isDiscovering() {
        return this.mBluetoothAdapter.isDiscovering();
    }

    public void startDiscovery() {
        if(this.mBluetoothAdapter != null) {
            if(this.mBluetoothAdapter.isEnabled() && this.isDiscovering()) {
                Log.e("", "===> mBluetoothAdapter.isDiscovering()");
            } else {
                Log.e("", "===> startDiscovery");
                Intent discoverableIntent = new Intent("android.bluetooth.adapter.action.REQUEST_DISCOVERABLE");
                discoverableIntent.putExtra("android.bluetooth.adapter.extra.DISCOVERABLE_DURATION", this.mTimeDiscoverable);
                this.mActivity.startActivityForResult(discoverableIntent, 114);
            }
        }
    }

    public void scanAllBluetoothDevice() {
        IntentFilter intentFilter = new IntentFilter("android.bluetooth.device.action.FOUND");
        this.mActivity.registerReceiver(this, intentFilter);
        this.mBluetoothAdapter.startDiscovery();
    }

    public void createClient(String addressMac) {
        if(this.mType == BluetoothManager.TypeBluetooth.Client) {
            IntentFilter bondStateIntent = new IntentFilter("android.bluetooth.device.action.BOND_STATE_CHANGED");
            this.mActivity.registerReceiver(this, bondStateIntent);
            this.mBluetoothClient = new BluetoothClient(this.mBluetoothAdapter, addressMac);
            (new Thread(this.mBluetoothClient)).start();
        }

    }

    public void createServeur(String address) {
        if(this.mType == BluetoothManager.TypeBluetooth.Server && !this.mAdressListServerWaitingConnection.contains(address)) {
            BluetoothServer mBluetoothServer = new BluetoothServer(this.mBluetoothAdapter, address);
            Thread threadServer = new Thread(mBluetoothServer);
            threadServer.start();
            this.setServerWaitingConnection(address, mBluetoothServer, threadServer);
            IntentFilter bondStateIntent = new IntentFilter("android.bluetooth.device.action.BOND_STATE_CHANGED");
            this.mActivity.registerReceiver(this, bondStateIntent);
            Log.e("", "===> createServeur address : " + address);
        }

    }

    public void onServerConnectionSuccess(String addressClientConnected) {
        Iterator i$ = this.mServeurWaitingConnectionList.entrySet().iterator();

        Entry bluetoothServerMap;
        do {
            if(!i$.hasNext()) {
                return;
            }

            bluetoothServerMap = (Entry)i$.next();
        } while(!addressClientConnected.equals(((BluetoothServer)bluetoothServerMap.getValue()).getClientAddress()));

        this.mServeurConnectedList.add(bluetoothServerMap.getValue());
        this.incrementNbrConnection();
        Log.e("", "===> onServerConnectionSuccess address : " + addressClientConnected);
    }

    public void onServerConnectionFailed(String addressClientConnectionFailed) {
        int index = 0;

        for(Iterator i$ = this.mServeurConnectedList.iterator(); i$.hasNext(); ++index) {
            BluetoothServer bluetoothServer = (BluetoothServer)i$.next();
            if(addressClientConnectionFailed.equals(bluetoothServer.getClientAddress())) {
                ((BluetoothServer)this.mServeurConnectedList.get(index)).closeConnection();
                this.mServeurConnectedList.remove(index);
                ((BluetoothServer)this.mServeurWaitingConnectionList.get(addressClientConnectionFailed)).closeConnection();
                this.mServeurWaitingConnectionList.remove(addressClientConnectionFailed);
                ((Thread)this.mServeurThreadList.get(addressClientConnectionFailed)).interrupt();
                this.mServeurThreadList.remove(addressClientConnectionFailed);
                this.mAdressListServerWaitingConnection.remove(addressClientConnectionFailed);
                this.decrementNbrConnection();
                Log.e("", "===> onServerConnectionFailed address : " + addressClientConnectionFailed);
                return;
            }
        }

    }

    public void sendMessage(String message) {
        if(this.mType != null && this.isConnected) {
            if(this.mServeurConnectedList != null) {
                for(int i = 0; i < this.mServeurConnectedList.size(); ++i) {
                    ((BluetoothServer)this.mServeurConnectedList.get(i)).write(message);
                }
            }

            if(this.mBluetoothClient != null) {
                this.mBluetoothClient.write(message);
            }
        }

    }

    public void onReceive(Context context, Intent intent) {
        BluetoothDevice device = (BluetoothDevice)intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
        if(intent.getAction().equals("android.bluetooth.device.action.FOUND") && (this.mType == BluetoothManager.TypeBluetooth.Client && !this.isConnected || this.mType == BluetoothManager.TypeBluetooth.Server && !this.mAdressListServerWaitingConnection.contains(device.getAddress()))) {
            EventBus.getDefault().post(device);
        }

        if(intent.getAction().equals("android.bluetooth.device.action.BOND_STATE_CHANGED")) {
            int prevBondState = intent.getIntExtra("android.bluetooth.device.extra.PREVIOUS_BOND_STATE", -1);
            int bondState = intent.getIntExtra("android.bluetooth.device.extra.BOND_STATE", -1);
            if(prevBondState == 11 && (bondState == 12 || bondState == 10)) {
                EventBus.getDefault().post(new BondedDevice());
            }
        }

    }

    public void disconnectClient() {
        this.mType = BluetoothManager.TypeBluetooth.None;
        this.cancelDiscovery();
        this.resetClient();
    }

    public void disconnectServer() {
        this.mType = BluetoothManager.TypeBluetooth.None;
        this.cancelDiscovery();
        this.resetServer();
    }

    public void resetServer() {
        if(this.mServeurConnectedList != null) {
            for(int i = 0; i < this.mServeurConnectedList.size(); ++i) {
                ((BluetoothServer)this.mServeurConnectedList.get(i)).closeConnection();
            }
        }

        this.mServeurConnectedList.clear();
    }

    public void resetClient() {
        if(this.mBluetoothClient != null) {
            this.mBluetoothClient.closeConnexion();
            this.mBluetoothClient = null;
        }

    }

    public void closeAllConnexion() {
        this.mBluetoothAdapter.setName(this.mBluetoothNameSaved);

        try {
            this.mActivity.unregisterReceiver(this);
        } catch (Exception var2) {
            ;
        }

        this.cancelDiscovery();
        if(!this.mBluetoothIsEnableOnStart) {
            this.mBluetoothAdapter.disable();
        }

        this.mBluetoothAdapter = null;
        if(this.mType != null) {
            this.resetServer();
            this.resetClient();
        }

    }

    public static enum TypeBluetooth {
        Client,
        Server,
        None;

        private TypeBluetooth() {
        }
    }
}
