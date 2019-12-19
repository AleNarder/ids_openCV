package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsClient;
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.nearby.connection.Strategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import java.util.ArrayList;
import java.util.List;
import static java.nio.charset.StandardCharsets.UTF_8;

public class NearbyActivity extends AppCompatActivity {

    private static final String TAG  = "ProvaNearby";
    private static final String TAG1 = "PAYLOAD";

    private static final Strategy STRATEGY = Strategy.P2P_STAR;
    private ConnectionsClient connectionsClient;

    private String deviceA = "EV3MCLOVIN";
    private String deviceB = "stocazzo";
    private String deviceBEndpointId;
    private String presentation = "Benvenuto sono "+deviceA;

    private Button findButton, adButton, discButton, sendButton, stopButton, showMinesButton;

    private List<String> listCoordMines2 = new ArrayList<>();

    Payload bytesPayload = Payload.fromBytes(new byte[] {0xa, 0xb, 0xc, 0xd});

    byte[] bytes = "StoGranCazzooo".getBytes(UTF_8);
    //String s = new String(bytes);
    Payload bytesPayload2 = Payload.fromBytes(bytes);

    /**
     * Per recuperare bytesPayload2 sul deviceB, uso payload.asBytes () che restituirà l'array di byte.
     */

    Payload PayloadSent;
    String PayloadSent2;


    EditText et1, et2, et3;
    String s,x,y;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby);

        findButton = findViewById(R.id.button1);
        adButton   = findViewById(R.id.button2);
        discButton = findViewById(R.id.button3);
        sendButton = findViewById(R.id.button4);
        stopButton = findViewById(R.id.button5);
        showMinesButton = findViewById(R.id.button6);
        et1 = findViewById(R.id.coord);

        connectionsClient = Nearby.getConnectionsClient(this);
        if (connectionsClient == null) {
            Toast.makeText(this, "qualcosa è andato a puttane", Toast.LENGTH_SHORT).show();
        }

        findButton.setOnClickListener(v -> {
            startAdvertising();
            startDiscovery();
        });
        adButton.setOnClickListener(v -> startAdvertising());
        discButton.setOnClickListener(v -> startDiscovery());
        //sendButton.setOnClickListener(v -> sendMyPayLoad());

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                x = et1.getText().toString();
                sendMyPayLoad(x);
                //Toast.makeText(MainActivity.this, x, Toast.LENGTH_SHORT).show();
            }
        });

        showMinesButton.setOnClickListener(v -> Log.e(TAG, listCoordMines2.toString()));

        stopButton.setOnClickListener(v -> connectionsClient.stopAllEndpoints());

    }

    /** Step 1: Advertise and Discover ******************************************************************************************************/

    //deviceA (advertiser)
    private void startAdvertising() {
        //Toast.makeText(this, "advertising", Toast.LENGTH_SHORT).show();

        AdvertisingOptions advertisingOptions = new AdvertisingOptions.Builder().setStrategy(STRATEGY).build();

        /*connectionsClient
                .startAdvertising(
                        deviceA, getPackageName(), connectionLifecycleCallback, advertisingOptions)
                .addOnSuccessListener(
                        (Void unused) -> {
                            // We're advertising!
                        })
                .addOnFailureListener(
                        (Exception e) -> {
                            // We were unable to start advertising.
                        });*/

        connectionsClient.startAdvertising(deviceA, getPackageName(),connectionLifecycleCallback, advertisingOptions);
    }

    //deviceB (discoverer)
    private void startDiscovery() {
        //Toast.makeText(this, "discovering", Toast.LENGTH_SHORT).show();

        DiscoveryOptions discoveryOptions = new DiscoveryOptions.Builder().setStrategy(STRATEGY).build();

        /*connectionsClient
                .startDiscovery(getPackageName(), endpointDiscoveryCallback, discoveryOptions)
                .addOnSuccessListener(
                        (Void unused) -> {
                            // We're discovering!
                        })
                .addOnFailureListener(
                        (Exception e) -> {
                            // We're unable to start discovering.
                        });*/

        connectionsClient.startDiscovery(getPackageName(), endpointDiscoveryCallback, discoveryOptions);
    }

    /** Step 2: Manage Connection ***********************************************************************************************************/

    // callbacks for finding other devices
    private final EndpointDiscoveryCallback endpointDiscoveryCallback =
            new EndpointDiscoveryCallback() {

                /*@Override
                public void onEndpointFound(@NonNull String endpointId, @NonNull DiscoveredEndpointInfo info) {
                    // An endpoint was found. We request a connection to it.
                    connectionsClient
                            .requestConnection(deviceB, endpointId, connectionLifecycleCallback) // non sono sicuro vada deviceB
                            .addOnSuccessListener(
                                    (Void unused) -> {
                                        // We successfully requested a connection. Now both sides
                                        // must accept before the connection is established.
                                    })
                            .addOnFailureListener(
                                    (Exception e) -> {
                                        // Nearby Connections failed to request the connection.
                                    });
                }*/

                @Override
                public void onEndpointFound(@NonNull String endpointId, @NonNull DiscoveredEndpointInfo info){
                    Log.e(TAG, "onEndpointFound: endpoint found, connecting");
                    Toast.makeText(NearbyActivity.this, "endpoint found", Toast.LENGTH_SHORT).show();
                    connectionsClient.requestConnection(deviceA, endpointId, connectionLifecycleCallback);
                }

                @Override
                public void onEndpointLost(String endpointId) {
                    // A previously discovered endpoint has gone away.
                    Log.e(TAG, "onEndpointLost: endpoint lost");
                    Toast.makeText(NearbyActivity.this, "endpoint lost", Toast.LENGTH_SHORT).show();
                }
            };

    // callbacks for connections to other devices
    private final ConnectionLifecycleCallback connectionLifecycleCallback =

            new ConnectionLifecycleCallback() {

                // use this to authenticate the connection with token
                @Override
                public void onConnectionInitiated(String endpointId, ConnectionInfo info) {
                    new AlertDialog.Builder(NearbyActivity.this)
                            .setTitle("Accept connection to " + info.getEndpointName())
                            .setMessage("Confirm the code matches on both devices: " + info.getAuthenticationToken())
                            .setPositiveButton(
                                    "Accept",
                                    (DialogInterface dialog, int which) ->
                                            // The user confirmed, so we can accept the connection.
                                            connectionsClient.acceptConnection(endpointId, payloadCallback))
                            .setNegativeButton(
                                    android.R.string.cancel,
                                    (DialogInterface dialog, int which) ->
                                            // The user canceled, so we should reject the connection.
                                            connectionsClient.rejectConnection(endpointId))
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }

                /*@Override //crea un canale di comunicazione tra deviceA e deviceB
                public void onConnectionInitiated(@NonNull String endpointId, @NonNull ConnectionInfo connectionInfo) {
                    Toast.makeText(MainActivity.this, "accepting connection", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "onConnectionInitiated: accepting connection");

                    connectionsClient.acceptConnection(endpointId, payloadCallback);
                    deviceB = connectionInfo.getEndpointName();

                    Log.e(TAG, "ti ho trovato stronzo: "+deviceB);
                }*/

                @Override //chiamato quando entrambi i device hanno accettato o rifiutato la connessione
                public void onConnectionResult(@NonNull String endpointId,@NonNull ConnectionResolution result) {

                    Log.e(TAG, "onConnectionResult: try to connect");

                    /*switch (result.getStatus().getStatusCode()) {
                        case ConnectionsStatusCodes.STATUS_OK:
                            // We're connected! Can now start sending and receiving data.
                            Log.e(TAG, "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
                            break;
                        case ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED:
                            // The connection was rejected by one or both sides.
                            Log.e(TAG, "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb");
                            break;
                        case ConnectionsStatusCodes.STATUS_ERROR:
                            // The connection broke before it was able to be accepted.
                            Log.e(TAG, "ccccccccccccccccccccccccccccccccccccccccccccccccccc");
                            break;
                        default:
                            // Unknown status code
                    }*/

                    if (result.getStatus().isSuccess()) {
                        Log.e(TAG, "onConnectionResult: connection successful");
                        connectionsClient.stopDiscovery();
                        connectionsClient.stopAdvertising();
                        deviceBEndpointId = endpointId;
                        Log.e(TAG, "=================> deviceB: "+deviceBEndpointId);

                        //sendMyPayLoad(x,y);

                    } else {
                        Log.e(TAG, "onConnectionResult: connection failed");
                    }
                }

                @Override //chiamato quando deviceB è andato a puttane
                public void onDisconnected(String endpointId) {
                    Log.e(TAG, "onDisconnected: disconnected from the opponent");
                }
            };


    /** Step 3: Exchange Data ***************************************************************************************************************/

    /** DeviceA */

    private void sendMyPayLoad(String x) {

        /*connectionsClient.sendPayload(endPointId, bytesPayload2)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Called if the sendPayload() method is called successfully.
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Called if there is a failure in calling sendPayload() method.
                    }
                });*/

        Log.e(TAG1, "inizio trasferimento");

        byte[] coordBytes = x.getBytes(UTF_8);

        Payload coordPayload = Payload.fromBytes(coordBytes);

        //connectionsClient.sendPayload(deviceBEndpointId, bytesPayload2);
        connectionsClient.sendPayload(deviceBEndpointId,coordPayload);

    }

    /** Devide B, callbacks for receiving payloads */
    private final PayloadCallback payloadCallback =
            new PayloadCallback() {

                @Override
                public void onPayloadReceived(@NonNull String endpointId, @NonNull Payload payload) { //first byte of payload has been received
                    Log.e(TAG1, "inizio ricevimento");
                    PayloadSent2 = new String(payload.asBytes(),UTF_8);
                    String[] s = PayloadSent2.split(":");
                    String[] s2 = s[1].split(";");
                    String x = s2[0];
                    String y = s2[1];
                    listCoordMines2.add(x);
                    listCoordMines2.add(y);
                    Log.e("====>",listCoordMines2.toString());
                }

                @Override
                public void onPayloadTransferUpdate(@NonNull String endpointId, @NonNull PayloadTransferUpdate update) {
                    if (update.getStatus() == PayloadTransferUpdate.Status.SUCCESS) { //entire payload has been received
                        Log.e(TAG1, "trasferimento completato");
                    }
                    else {
                        if (update.getStatus() == PayloadTransferUpdate.Status.FAILURE) {
                            Log.e(TAG1, "trasferimento fallito coglione");
                        }
                    }
                }
            };

}
