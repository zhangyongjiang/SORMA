package com.gaoshin.sorma.browser;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.gaoshin.sorma.AndroidContentResolver;

public class SormaBrowser extends Activity {
    private WebView web;
    private HttpServer server;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        web = new WebView(this);
        web.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        setContentView(web);
        
        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setAllowFileAccess(true);
        WebViewJavascriptInterface javascriptInterface = new WebViewJavascriptInterface();
        web.addJavascriptInterface(javascriptInterface, "android");
        web.setWebChromeClient(new WebChromeClient());
        web.setWebViewClient(new GenericWebViewClient());
        
        Intent intent = getIntent();
        String contentProviderBaseUri = intent.getStringExtra("base_uri");
        try {
            server = new HttpServer(6789, "/explorer");
            server.registerHandler("/db", new DatabaseHandler(new AndroidContentResolver(getContentResolver()), contentProviderBaseUri));
        }
        catch (IOException e) {
            Toast.makeText(this, "Cannot create builtin data browser. Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        
        String url = "http://" + javascriptInterface.getMyIpAddress() + ":6789/index.html";
        if(intent.getData() != null) {
            url = intent.getData().toString();
        }
        Toast.makeText(this, "Loading \n" + url, Toast.LENGTH_LONG).show();
        web.loadUrl(url);
    }
    
    @Override
    protected void onDestroy() {
        if(server != null) {
            server.stop();
        }
        super.onDestroy();
    }

    public class GenericWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
        
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Toast.makeText(getBaseContext(), "Loading\n" + url, Toast.LENGTH_SHORT).show();
            super.onPageStarted(view, url, favicon);
        }
        
    }
    
    @Override
    public void onBackPressed() {
        if(web.canGoBack()) {
            web.goBack();
        }
        else {
            super.onBackPressed();
        }
    }

    public class WebViewJavascriptInterface {
        public void exit() {
            finish();
        }

        public void email(String to, String subject, String msg) {
            String action = "android.intent.action.SENDTO";
            Uri uri = Uri.parse("mailto:" + to);
            Intent intent = new Intent(action, uri);
            intent.putExtra("android.intent.extra.TEXT", msg);
            SormaBrowser.this.startActivity(intent);
        }

        public String getMyIpAddress() {
            String loopback = null;
            try {
                for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                    NetworkInterface intf = en.nextElement();
                    for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        String ip = inetAddress.getHostAddress().toString();
                        if (!inetAddress.isLoopbackAddress()) {
                            return ip;
                        }
                        else {
                            loopback = ip;
                        }
                    }
                }
            } catch (Exception ex) {
            }
            return loopback == null ? "localhost" : loopback;
        }    
    }
}
