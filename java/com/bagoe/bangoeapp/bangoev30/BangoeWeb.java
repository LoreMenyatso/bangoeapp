package com.bagoe.bangoeapp.bangoev30;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.Toast;

import static java.lang.System.exit;

public class BangoeWeb extends AppCompatActivity {

    private long backPressedTime;
    private Toast backToast;

    private WebView webView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RelativeLayout no_internet_layout;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 1:
                    webView.goBack();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bangoe_web);

        webView = (WebView) findViewById(R.id.bangoeWeb);
        swipeRefreshLayout = findViewById(R.id.webView_relaod);
        no_internet_layout = findViewById(R.id.no_internet_layout);

        // declare web components

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        // webView.loadUrl(getUrl());

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                webView.reload();
            }
        });

        webView.setWebChromeClient(new MyChromeClient());
        webView.setWebViewClient(new BrowserClient(swipeRefreshLayout));

        WebSettings settings = webView.getSettings();

        settings.setJavaScriptEnabled(true);
        settings.setAllowContentAccess(true);
        settings.setAppCacheEnabled(true);

        // Improve loading speed
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        settings.setAppCacheEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        settings.setUseWideViewPort(true);
        settings.setSavePassword(true);
        settings.setSaveFormData(true);
        settings.setEnableSmoothTransition(true);

        webView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey (View view, int i, KeyEvent keyEvent) {
                if(i==KeyEvent.KEYCODE_BACK && keyEvent.getAction() == MotionEvent.ACTION_UP && webView.canGoBack()){
                    handler.sendEmptyMessage(1);
                    return true;

                }
                return false;
            }
        });

        loadWebPage();
    }

    private void loadWebPage() {

        ConnectivityManager cm = (ConnectivityManager) BangoeWeb.this.getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        if(networkInfo!=null && networkInfo.isConnectedOrConnecting()){
            webView.loadUrl("https://bangoe.com/");

            no_internet_layout.setVisibility(View.GONE);
            webView.setVisibility(View.VISIBLE);
        }else {
            no_internet_layout.setVisibility(View.VISIBLE);
            webView.setVisibility(View.GONE);
            Toast.makeText(this,"You do not have internet connection", Toast.LENGTH_LONG).show();
        }

    }


    public void ReconnectWeb(View view) {
        loadWebPage();

    }

    //declared url variable
    public void saveUrl(String url1){
        SharedPreferences sp1 = getSharedPreferences("SP_WEBVIEW_PREFS1", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp1.edit();
        editor.putString("SAVED_URL1", url1);
        editor.commit();
    }

    public String getUrl(){

        SharedPreferences sp1 = getSharedPreferences("SP_WEBVIEW_PREFS1", MODE_PRIVATE);
        //If you haven't saved the url before, the default value will be google's page
        return sp1.getString("SAVED_URL1", "https://www.bangoe.com/");

    }

    private class MyWebClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url1) {

            view.loadUrl(url1);
            return true;

        }

        @Override
        public void onPageFinished(WebView view, String url1) {
            super.onPageFinished(view, url1);

            //Save the last visited URL
            saveUrl(url1);
        }

    }

    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            backToast.cancel();
            super.onBackPressed();
            finish();
            exit(0);

            return;
        } else {
            backToast = Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT);
            backToast.show();
        }
        backPressedTime = System.currentTimeMillis();
    }

    /**
     * Called when the activity has detected the user's press of the back
     * key. The {@link #getOnBackPressedDispatcher() OnBackPressedDispatcher} will be given a
     * chance to handle the back button before the default behavior of
     * {@link InstagramWeb #onBackPressed()} is invoked.
     *
     * @see #getOnBackPressedDispatcher()
     */


}
