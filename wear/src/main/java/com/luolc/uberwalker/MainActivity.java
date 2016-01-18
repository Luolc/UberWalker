package com.luolc.uberwalker;

import static com.luolc.uberwalker.DataLayerListenerService.LOGD;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.mobvoi.android.common.ConnectionResult;
import com.mobvoi.android.common.api.MobvoiApiClient;
import com.mobvoi.android.common.api.MobvoiApiClient.ConnectionCallbacks;
import com.mobvoi.android.common.api.MobvoiApiClient.OnConnectionFailedListener;
import com.mobvoi.android.common.data.FreezableUtils;
import com.mobvoi.android.wearable.Asset;
import com.mobvoi.android.wearable.DataApi;
import com.mobvoi.android.wearable.DataEvent;
import com.mobvoi.android.wearable.DataEventBuffer;
import com.mobvoi.android.wearable.DataMapItem;
import com.mobvoi.android.wearable.MessageApi;
import com.mobvoi.android.wearable.MessageEvent;
import com.mobvoi.android.wearable.Node;
import com.mobvoi.android.wearable.NodeApi;
import com.mobvoi.android.wearable.Wearable;

import java.io.InputStream;
import java.util.List;

/**
 * Shows events and photo from the Wearable APIs.
 */
public class MainActivity extends Activity implements ConnectionCallbacks,
        OnConnectionFailedListener, DataApi.DataListener, MessageApi.MessageListener,
        NodeApi.NodeListener {

    private static final String TAG = "MainActivity";

    private MobvoiApiClient mMobvoiApiClient;
    private ListView mDataItemList;
    private TextView mIntroText;
    private View mLayout;
    private Handler mHandler;

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        mHandler = new Handler();
        LOGD(TAG, "onCreate");
        setContentView(R.layout.main_activity);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mIntroText = (TextView) findViewById(R.id.intro);
        mLayout = findViewById(R.id.layout);

        mMobvoiApiClient = new MobvoiApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMobvoiApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Wearable.DataApi.removeListener(mMobvoiApiClient, this);
        Wearable.MessageApi.removeListener(mMobvoiApiClient, this);
        Wearable.NodeApi.removeListener(mMobvoiApiClient, this);
        mMobvoiApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Wearable.DataApi.addListener(mMobvoiApiClient, this);
        Wearable.MessageApi.addListener(mMobvoiApiClient, this);
        Wearable.NodeApi.addListener(mMobvoiApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int cause) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.e(TAG, "onConnectionFailed(): Failed to connect, with result: " + result);
    }

    private void generateEvent(final String title, final String text) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                mIntroText.setVisibility(View.INVISIBLE);
//                mDataItemListAdapter.add(new Event(title, text));
//            }
//        });
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        LOGD(TAG, "onDataChanged(): " + dataEvents);
//
//        final List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);
//        dataEvents.close();
//        for (DataEvent event : events) {
//            if (event.getType() == DataEvent.TYPE_CHANGED) {
//                String path = event.getDataItem().getUri().getPath();
//                if (DataLayerListenerService.IMAGE_PATH.equals(path)) {
//                    DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
//                    Asset photo = dataMapItem.getDataMap()
//                            .getAsset(DataLayerListenerService.IMAGE_KEY);
//                    final Bitmap bitmap = loadBitmapFromAsset(mMobvoiApiClient, photo);
//                    mHandler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            Log.d(TAG, "Setting background image..");
//                            mLayout.setBackground(new BitmapDrawable(getResources(), bitmap));
//                        }
//                    });
//
//                } else if (DataLayerListenerService.COUNT_PATH.equals(path)) {
//                    LOGD(TAG, "Data Changed for COUNT_PATH");
//                    generateEvent("DataItem Changed", event.getDataItem().toString());
//                } else {
//                    LOGD(TAG, "Unrecognized path: " + path);
//                }
//
//            } else if (event.getType() == DataEvent.TYPE_DELETED) {
//                generateEvent("DataItem Deleted", event.getDataItem().toString());
//            } else {
//                generateEvent("Unknown data event type", "Type = " + event.getType());
//            }
//        }
    }


    @Override
    public void onMessageReceived(MessageEvent event) {
        LOGD(TAG, "onMessageReceived: " + event);
        generateEvent("Message", event.toString());
    }

    @Override
    public void onPeerConnected(Node node) {
        generateEvent("Node Connected", node.getId());
    }

    @Override
    public void onPeerDisconnected(Node node) {
        generateEvent("Node Disconnected", node.getId());
    }

    public void onClickAccept(View view) {
        // TO-DO
        finish();
    }

    public void onClickRefuse(View view) {
        // TO-DO
        finish();
    }
}
