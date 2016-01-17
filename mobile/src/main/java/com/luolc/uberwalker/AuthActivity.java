package com.luolc.uberwalker;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVObject;
import com.luolc.uberwalker.Manager.ApiManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AuthActivity extends AppCompatActivity {

    WebView webview;
    private SharedPreferences pref;

    class WebAppConnector {
        Context mContext;
        /** Instantiate the interface and set the context */
        WebAppConnector(Context c) {
            mContext = c;
        }

        /** Show a toast from the web page */
        @JavascriptInterface
        public void setUid(String uid) {
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("uid", uid);
            editor.apply();

            AVObject testObject = new AVObject("UberAuth");
            testObject.put("installationId", AVInstallation.getCurrentInstallation().getInstallationId());
            testObject.put("uid", uid);
            testObject.saveInBackground();

            Log.v("[UID]", uid);
            Toast.makeText(mContext, "登录成功！", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void getAuthURL() {
        ApiManager api = ApiManager.getInstance();
        api.get("login", null,
                new Response.Listener<String>() {
            @Override
            public void onResponse(String resp) {
                try {
                    JSONObject obj = new JSONObject(resp);
                    String authURL = obj.getString("auth_url");
                    webview.loadUrl(authURL);
                } catch (JSONException e) { // TODO
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError e) { // TODO

                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pref = PreferenceManager.getDefaultSharedPreferences(this);
        setContentView(R.layout.activity_auth);
        webview = (WebView) findViewById(R.id.webView);
        webview.clearCache(true);

        webview.setWebViewClient(new WebViewClient());
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAppCacheEnabled(false);

        webview.addJavascriptInterface(new WebAppConnector(this), "Android");
        //getAuthURL();
        webview.loadUrl("https://pkurider.leanapp.cn/login");
    }
}
;