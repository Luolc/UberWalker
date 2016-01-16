package com.luolc.uberwalker;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVObject;

/**
 * Created by LuoLiangchen on 16/1/16.
 */
public class BudgetFragment extends Fragment {

    private static final String KEY_TITLE = "key_title";
    WebView webview;
    private SharedPreferences pref;

    public static BudgetFragment newInstance(String title) {
        BudgetFragment fragment = new BudgetFragment();
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

        webview.setWebViewClient(new WebViewClient());
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAppCacheEnabled(false);
        webview.addJavascriptInterface(new WebAppConnector(getActivity()), "Android");

        webview.loadUrl("http://193.168.4.174:5001/plan.html");
        return rootView;
    }
}
