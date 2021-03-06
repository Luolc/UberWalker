package com.luolc.uberwalker;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

/**
 * Created by LuoLiangchen on 16/1/16.
 */
public class StatusFragment extends Fragment {

    private static final String KEY_TITLE = "key_title";
    WebView webview;
    private SharedPreferences pref;

    public static StatusFragment newInstance(String title) {
        StatusFragment fragment = new StatusFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_TITLE, title);
        fragment.setArguments(bundle);
        return fragment;
    }

    class WebAppConnector {
        Context mContext;
        /** Instantiate the interface and set the context */
        WebAppConnector(Context c) {
            mContext = c;
        }

        /** Show a toast from the web page */
        @JavascriptInterface
        public String getUid() {
            String uid = pref.getString("uid", "");
            Log.v("[UID]", uid);
            return uid;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        View rootView = inflater.inflate(R.layout.fragment_budget, container, false);
        webview = (WebView) rootView.findViewById(R.id.webView);
        webview.clearCache(true);

        webview.setWebChromeClient(new WebChromeClient());
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAppCacheEnabled(false);
        webview.addJavascriptInterface(new WebAppConnector(getActivity()), "Android");

        webview.loadUrl("file:///android_asset/status.html");
        return rootView;
    }
}
