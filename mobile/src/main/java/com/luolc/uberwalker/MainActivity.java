package com.luolc.uberwalker;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import com.avos.avoscloud.*;
import com.avos.avospush.*;

import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.luolc.uberwalker.Manager.UserManager;
import com.mobvoi.android.common.ConnectionResult;
import com.mobvoi.android.common.api.MobvoiApiClient;
import com.mobvoi.android.common.api.MobvoiApiClient.ConnectionCallbacks;
import com.mobvoi.android.common.api.MobvoiApiClient.OnConnectionFailedListener;
import com.mobvoi.android.common.api.ResultCallback;
import com.mobvoi.android.common.data.FreezableUtils;
import com.mobvoi.android.wearable.Asset;
import com.mobvoi.android.wearable.DataApi;
import com.mobvoi.android.wearable.DataApi.DataItemResult;
import com.mobvoi.android.wearable.DataEvent;
import com.mobvoi.android.wearable.DataEventBuffer;
import com.mobvoi.android.wearable.MessageApi;
import com.mobvoi.android.wearable.MessageApi.SendMessageResult;
import com.mobvoi.android.wearable.MessageEvent;
import com.mobvoi.android.wearable.Node;
import com.mobvoi.android.wearable.NodeApi;
import com.mobvoi.android.wearable.PutDataMapRequest;
import com.mobvoi.android.wearable.PutDataRequest;
import com.mobvoi.android.wearable.Wearable;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String KEY_TITLLE = "key_title";

    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;
    private LeftMenuFragment mLeftMenuFragment;
    private Fragment mCurrentFragment;
    private TextView tvTitle;
    private String mTitle;

    private UserManager mUserManager;

    // Watch
    private static final int REQUEST_RESOLVE_ERROR = 1000;

    private static final String START_ACTIVITY_PATH = "/start-activity";
    private static final String COUNT_PATH = "/count";
    private static final String IMAGE_PATH = "/image";
    private static final String IMAGE_KEY = "photo";
    private static final String COUNT_KEY = "count";

    private MobvoiApiClient mMobvoiApiClient;
    private boolean mResolvingError = false;

    private View mStartActivityBtn;

    // Send DataItems.
    private ScheduledExecutorService mGeneratorExecutor;
    private ScheduledFuture<?> mDataItemGeneratorFuture;

    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUserManager = new UserManager(this);

        tvTitle = (TextView) findViewById(R.id.title);
        initToolBar();

        // AVAnalytics.trackAppOpened(getIntent());

        PushService.setDefaultPushCallback(this, MainActivity.class);

        AVInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
            public void done(AVException e) {
                if (e == null) {
                    // 保存成功
                    String installationId = AVInstallation.getCurrentInstallation().getInstallationId();
                    Log.v(TAG, installationId);
                    // 关联  installationId 到用户表等操作……
                } else {
                    // 保存失败，输出错误信息
                }
            }
        });

        // 授权WebView的Dialog
        if (!isUserExist()) {
            new AlertDialog.Builder(this)
                    .setTitle("请求授权")
                    .setMessage("当前尚未绑定Uber账户，点击确认以获得授权许可，或取消以推出程序。")
                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(getApplicationContext(), AuthActivity.class));
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }).show();

        }
        initViews();

        // Watch
////        setupViews();
//
//        mGeneratorExecutor = new ScheduledThreadPoolExecutor(1);
//
//        mMobvoiApiClient = new MobvoiApiClient.Builder(this)
//                .addApi(Wearable.API)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .build();


        //恢复title
        restoreTitle(savedInstanceState);

        FragmentManager fm = getSupportFragmentManager();

        //查找当前显示的Fragment
        mCurrentFragment = fm.findFragmentByTag(mTitle);

        if (mCurrentFragment == null) {
            mCurrentFragment = MainFragment.newInstance(mTitle);
            fm.beginTransaction().add(R.id.content_container, mCurrentFragment, mTitle).commit();
        }

        mLeftMenuFragment = (LeftMenuFragment) fm.findFragmentById(R.id.left_menu_container);
        if (mLeftMenuFragment == null) {
            mLeftMenuFragment = new LeftMenuFragment();
            fm.beginTransaction().add(R.id.left_menu_container, mLeftMenuFragment).commit();
        }

        //隐藏别的Fragment，如果存在的话
        List<Fragment> fragments = fm.getFragments();
        if (fragments != null)

            for (Fragment fragment : fragments) {
                if (fragment == mCurrentFragment || fragment == mLeftMenuFragment) continue;
                fm.beginTransaction().hide(fragment).commit();
            }

        //设置MenuItem的选择回调
        mLeftMenuFragment.setOnMenuItemSelectedListener(new LeftMenuFragment.OnMenuItemSelectedListener() {
            @Override
            public void menuItemSelected(String title) {

                FragmentManager fm = getSupportFragmentManager();
                Fragment fragment = getSupportFragmentManager().findFragmentByTag(title);
                if (fragment == mCurrentFragment) {
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                    return;
                }

                FragmentTransaction transaction = fm.beginTransaction();
                transaction.hide(mCurrentFragment);

                if (fragment == null) {
                    if(title == "修改预算")
                        fragment = BudgetFragment.newInstance(title);
                    else if(title == "修改地址")
                        fragment = LocationFragment.newInstance(title);
                    else if(title == "完成度")
                        fragment = StatusFragment.newInstance(title);
                    else if(title == "注销") {
                        try {
                            mUserManager.clearInfo();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        finish();
                    }
                    try {

                        transaction.add(R.id.content_container, fragment, title);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    transaction.show(fragment);
                }
                transaction.commit();

                mCurrentFragment = fragment;
                mTitle = title;
                mToolbar.setTitle("");
                setTitle(mTitle);
                mDrawerLayout.closeDrawer(Gravity.LEFT);
            }
        });
    }

    private void restoreTitle(Bundle savedInstanceState) {
        if (savedInstanceState != null)
            mTitle = savedInstanceState.getString(KEY_TITLLE);

        if (TextUtils.isEmpty(mTitle)) {
            mTitle = "UWalker";
        }

        mToolbar.setTitle("");
        setTitle(mTitle);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_TITLLE, mTitle);
    }

    private void initToolBar() {
        Toolbar toolbar = mToolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setTitle("UWalker");
        setSupportActionBar(toolbar);
    }

    private void initViews() {

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mActionBarDrawerToggle = new ActionBarDrawerToggle(this,
                mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mActionBarDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);
    }

    private boolean isUserExist() {
        Log.v(TAG, "UID: " + mUserManager.getUid());
        if ("".equals(mUserManager.getUid())) {
            return false;
        }
        return true;
    }

    private void setTitle(String title) {
        tvTitle.setText(title);
    }

    // Watch
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//            Bundle extras = data.getExtras();
//        }
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        if (!mResolvingError) {
//            mMobvoiApiClient.connect();
//        }
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        mDataItemGeneratorFuture = mGeneratorExecutor.scheduleWithFixedDelay(
//                new DataItemGenerator(), 1, 5, TimeUnit.SECONDS);
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        mDataItemGeneratorFuture.cancel(true /* mayInterruptIfRunning */);
//    }
//
//    @Override
//    protected void onStop() {
//        if (!mResolvingError) {
//            Wearable.DataApi.removeListener(mMobvoiApiClient, this);
//            Wearable.MessageApi.removeListener(mMobvoiApiClient, this);
//            Wearable.NodeApi.removeListener(mMobvoiApiClient, this);
//            mMobvoiApiClient.disconnect();
//        }
//        super.onStop();
//    }
//
//    @Override //ConnectionCallbacks
//    public void onConnected(Bundle connectionHint) {
//        mResolvingError = false;
//        mStartActivityBtn.setEnabled(true);
//        Wearable.DataApi.addListener(mMobvoiApiClient, this);
//        Wearable.MessageApi.addListener(mMobvoiApiClient, this);
//        Wearable.NodeApi.addListener(mMobvoiApiClient, this);
//    }
//
//    @Override //ConnectionCallbacks
//    public void onConnectionSuspended(int cause) {
//        mStartActivityBtn.setEnabled(false);
//    }
//
//    @Override //OnConnectionFailedListener
//    public void onConnectionFailed(ConnectionResult result) {
//        if (mResolvingError) {
//            // Already attempting to resolve an error.
//            return;
//        } else if (result.hasResolution()) {
//            try {
//                mResolvingError = true;
//                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
//            } catch (IntentSender.SendIntentException e) {
//                // There was an error with the resolution intent. Try again.
//                mMobvoiApiClient.connect();
//            }
//        } else {
//            mResolvingError = false;
//            mStartActivityBtn.setEnabled(false);
//            Wearable.DataApi.removeListener(mMobvoiApiClient, this);
//            Wearable.MessageApi.removeListener(mMobvoiApiClient, this);
//            Wearable.NodeApi.removeListener(mMobvoiApiClient, this);
//        }
//    }
//
//    @Override //DataListener
//    public void onDataChanged(DataEventBuffer dataEvents) {
//        LOGD(TAG, "onDataChanged: " + dataEvents);
//        final List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);
//        dataEvents.close();
////        runOnUiThread(new Runnable() {
////            @Override
////            public void run() {
////                for (DataEvent event : events) {
////                    if (event.getType() == DataEvent.TYPE_CHANGED) {
////                        mDataItemListAdapter.add(
////                                new Event("DataItem Changed", event.getDataItem().toString()));
////                    } else if (event.getType() == DataEvent.TYPE_DELETED) {
////                        mDataItemListAdapter.add(
////                                new Event("DataItem Deleted", event.getDataItem().toString()));
////                    }
////                }
////            }
////        });
//    }
//
//    @Override //MessageListener
//    public void onMessageReceived(final MessageEvent messageEvent) {
//        LOGD(TAG, "onMessageReceived() A message from watch was received:" + messageEvent
//                .getRequestId() + " " + messageEvent.getPath());
////        mHandler.post(new Runnable() {
////            @Override
////            public void run() {
////                mDataItemListAdapter.add(new Event("Message from watch", messageEvent.toString()));
////            }
////        });
//
//    }
//
//    @Override //NodeListener
//    public void onPeerConnected(final Node peer) {
//        LOGD(TAG, "onPeerConnected: " + peer);
////        mHandler.post(new Runnable() {
////            @Override
////            public void run() {
////                mDataItemListAdapter.add(new Event("Connected", peer.toString()));
////            }
////        });
//
//    }
//
//    @Override //NodeListener
//    public void onPeerDisconnected(final Node peer) {
//        LOGD(TAG, "onPeerDisconnected: " + peer);
////        mHandler.post(new Runnable() {
////            @Override
////            public void run() {
////                mDataItemListAdapter.add(new Event("Disconnected", peer.toString()));
////            }
////        });
//    }
//
//    /**
//     * A View Adapter for presenting the Event objects in a list
//     */
//    private static class DataItemAdapter extends ArrayAdapter<Event> {
//
//        private final Context mContext;
//
//        public DataItemAdapter(Context context, int unusedResource) {
//            super(context, unusedResource);
//            mContext = context;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            ViewHolder holder;
//            if (convertView == null) {
//                holder = new ViewHolder();
//                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(
//                        Context.LAYOUT_INFLATER_SERVICE);
//                convertView = inflater.inflate(android.R.layout.two_line_list_item, null);
//                convertView.setTag(holder);
//                holder.text1 = (TextView) convertView.findViewById(android.R.id.text1);
//                holder.text2 = (TextView) convertView.findViewById(android.R.id.text2);
//            } else {
//                holder = (ViewHolder) convertView.getTag();
//            }
//            Event event = getItem(position);
//            holder.text1.setText(event.title);
//            holder.text2.setText(event.text);
//            return convertView;
//        }
//
//        private class ViewHolder {
//
//            TextView text1;
//            TextView text2;
//        }
//    }
//
//    private class Event {
//
//        String title;
//        String text;
//
//        public Event(String title, String text) {
//            this.title = title;
//            this.text = text;
//        }
//    }
//
//    private Collection<String> getNodes() {
//        HashSet<String> results = new HashSet<String>();
//        NodeApi.GetConnectedNodesResult nodes =
//                Wearable.NodeApi.getConnectedNodes(mMobvoiApiClient).await();
//
//        for (Node node : nodes.getNodes()) {
//            results.add(node.getId());
//        }
//
//        return results;
//    }
//
//    private void sendStartActivityMessage(String node) {
//        Wearable.MessageApi.sendMessage(
//                mMobvoiApiClient, node, START_ACTIVITY_PATH, new byte[0]).setResultCallback(
//                new ResultCallback<MessageApi.SendMessageResult>() {
//                    @Override
//                    public void onResult(MessageApi.SendMessageResult sendMessageResult) {
//                        if (!sendMessageResult.getStatus().isSuccess()) {
//                            Log.e(TAG, "Failed to send message with status code: "
//                                    + sendMessageResult.getStatus().getStatusCode());
//                        }
//                    }
//                }
//        );
//    }
//
//    private class StartWearableActivityTask extends AsyncTask<Void, Void, Void> {
//
//        @Override
//        protected Void doInBackground(Void... args) {
//            Collection<String> nodes = getNodes();
//            for (String node : nodes) {
//                sendStartActivityMessage(node);
//            }
//            return null;
//        }
//    }
//
//    /** Sends an RPC to start a fullscreen Activity on the wearable. */
//    public void onStartWearableActivityClick(View view) {
//        LOGD(TAG, "Generating RPC");
//
//        // Trigger an AsyncTask that will query for a list of connected nodes and send a
//        // "start-activity" message to each connected node.
//        new StartWearableActivityTask().execute();
//    }
//
//    /** Generates a DataItem based on an incrementing count. */
//    private class DataItemGenerator implements Runnable {
//
//        private int count = 0;
//
//        @Override
//        public void run() {
//            PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(COUNT_PATH);
//            putDataMapRequest.getDataMap().putInt(COUNT_KEY, count++);
//            PutDataRequest request = putDataMapRequest.asPutDataRequest();
//
//            LOGD(TAG, "Generating DataItem: " + request);
//            if (!mMobvoiApiClient.isConnected()) {
//                return;
//            }
//            Wearable.DataApi.putDataItem(mMobvoiApiClient, request)
//                    .setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
//                        @Override
//                        public void onResult(DataApi.DataItemResult dataItemResult) {
//                            if (!dataItemResult.getStatus().isSuccess()) {
//                                Log.e(TAG, "ERROR: failed to putDataItem, status code: "
//                                        + dataItemResult.getStatus().getStatusCode());
//                            }
//                        }
//                    });
//        }
//    }
//
//    /**
//     * Sets up UI components and their callback handlers.
//     */
////    private void setupViews() {
////        mStartActivityBtn = findViewById(R.id.start_wearable_activity);
////    }
//
//    /**
//     * As simple wrapper around Log.d
//     */
//    private static void LOGD(final String tag, String message) {
//        if (Log.isLoggable(tag, Log.DEBUG)) {
//            Log.d(tag, message);
//        }
//    }
}
