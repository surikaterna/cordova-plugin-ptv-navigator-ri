package com.surikat.lynx.cordova;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import android.content.Context;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.IBinder;
import android.os.Messenger;
import android.support.v4.app.FragmentActivity;

public class PTVRemoteInterface extends CordovaPlugin {
    private CallbackContext callbackContext;
    private JSONObject data = new JSONObject();

    // The navigator service messenger, used to send data to the navigator
    private Messenger foreignService = null;

    // Flag indicating whether we have called bind on the service
    private boolean bound;

    // the navigator application package
    static String componentName;


    /** Class for interacting with the main interface of the service. */
    protected ServiceConnection serviceConnection = new ServiceConnection()
    {
        public void onServiceConnected(ComponentName className, IBinder service)
        {
            // This is called when the connection with the service has been
            // established, giving us the object we can use to
            // interact with the service. We are communicating with the
            // service using a Messenger, so here we get a client-side
            // representation of that from the raw IBinder object.
            foreignService = new Messenger(service);
            bound = true;
        }

        public void onServiceDisconnected(ComponentName className)
        {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            foreignService = null;
            bound = false;
        }
    };



    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        // unbind from the service
        if (bound)
        {
            getApplicationContext().unbindService(serviceConnection);
            bound = false;
        }
    };

    @Override
    protected void onStart()
    {
        super.onStart();
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if ("start".equals(action)) {
            // binding service
            connectRemoteInterface();
        } else if ("stop".equals(action)) {
            disconnectRemoteInterface();
        }
        return false;  // Returning false results in a "MethodNotFound" error.
    }

    private void disconnectRemoteInterface() {
        // unbind from the service
        if (bound)
        {
            getApplicationContext().unbindService(serviceConnection);
            bound = false;
        }
    }

    private void connectRemoteInterface() {
        if (componentName == null)
        {
            // on first start resolve the service we want to bind
            final String apps[] = getServiceApps();

            if (apps.length == 0)
            {
                // no application found that implements the RIService
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.navigator_not_found_title);
                builder.setMessage(R.string.navigator_not_found_message);
                AlertDialog dlg = builder.create();
                dlg.show();
            }
            else if (apps.length == 1)
            {
                // only one application implements the RIService so take it an bind to it
                componentName = apps[0];

                bindService();
            }
            else
            {
                // more than one application implement the RIService so let the user choose which he would like to use
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.choose_navigator_title);
                builder.setItems(apps, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        componentName = apps[which];

                        bindService();

                        dialog.dismiss();
                    }
                });

                AlertDialog dlg = builder.create();
                dlg.show();
            }
        }
        else
        {
            bindService();
        }
    }

    private String[] getServiceApps()
    {
        PackageManager pm = getApplicationContext().getPackageManager();

        Intent serviceIntent = new Intent("com.ptvag.navigation.RIService");
        List<ResolveInfo> infos = pm.queryIntentServices(serviceIntent, 0);
        String apps[] = new String[infos.size()];
        int i = 0;
        for(ResolveInfo info : infos)
        {
            apps[i] = info.serviceInfo.applicationInfo.packageName;
            i++;
        }

        return apps;
    }

    private void bindService()
    {
        Intent intent = new Intent();
        intent.setClassName(componentName, "com.ptvag.navigation.ri.RIService");

        getApplicationContext().bindService(intent,
                serviceConnection,
                Context.BIND_AUTO_CREATE);
    }

    public Messenger getForeignService()
    {
        return foreignService;
    }

    public boolean isBound()
    {
        return bound;
    }

}
