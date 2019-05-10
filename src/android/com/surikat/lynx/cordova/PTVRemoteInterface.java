package com.surikat.lynx.cordova;

import java.util.List;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PTVRemoteInterface extends CordovaPlugin {
    private CallbackContext connectCallbackContext;
    private CallbackContext disconnectCallbackContext;
    private CallbackContext getProfileCallbackContext;
    private CallbackContext setProfileCallbackContext;
    private CallbackContext mCallbackContext;

    private JSONObject data = new JSONObject();

    // The navigator service messenger, used to send data to the navigator
    private Messenger foreignService = null;

    // Flag indicating whether we have called bind on the service
    private boolean bound;

    // the navigator application package
    static String componentName;

    private String currentProfile = "INIT";
    protected Messenger clientMessenger;

    private static final String TAG = "Message";

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

            emitCallback("connected");
        }

        public void onServiceDisconnected(ComponentName className)
        {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            foreignService = null;
            bound = false;
            emitCallback("disconnected");
        }
    };

    private void emitCallback(String type) {
        switch (type) {
            case "connected":
                if (connectCallbackContext != null) {
                    PluginResult result = new PluginResult(PluginResult.Status.OK, true);
                    connectCallbackContext.sendPluginResult(result);
                }
                break;
            case "disconnected":
                if (disconnectCallbackContext != null) {
                    PluginResult result = new PluginResult(PluginResult.Status.OK, true);
                    disconnectCallbackContext.sendPluginResult(result);
                }
        }
    }

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        setupClientMessenger();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        disconnectRemoteInterface();
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        mCallbackContext = callbackContext;
        switch (action) {
            case "connect":
                connectCallbackContext = callbackContext;
                if (isBound()) {
                    emitCallback("connected");
                } else {
                    connectRemoteInterface();
                }
                break;

            case "disconnect":
                disconnectCallbackContext = callbackContext;
                disconnectRemoteInterface();
                break;

            case "getProfile":
                getProfileCallbackContext = callbackContext;
                getCurrentProfile();
                break;

            case "setProfile":
                setProfileCallbackContext = callbackContext;
                String profileName = args.getString(0);
                setCurrentProfile(profileName);
                break;

            default:
                return false;  // Returning false results in a "MethodNotFound" error.
        }

        return true;
    }

    private void disconnectRemoteInterface() {
        // unbind from the service
        if (bound)
        {
            Context context = this.cordova.getActivity().getApplicationContext();
            context.unbindService(serviceConnection);
            bound = false;

            // reset variable
            connectCallbackContext = null;
            disconnectCallbackContext = null;
            getProfileCallbackContext = null;
            setProfileCallbackContext = null;
            mCallbackContext = null;
        } else {
            emitCallback("disconnected");
        }
    }

    private boolean connectRemoteInterface() {
        if (componentName == null)
        {
            // on first start resolve the service we want to bind
            final String apps[] = getServiceApps();

            if (apps.length == 0)
            {
                // no application found that implements the RIService
                return false;
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
                Context context = this.cordova.getActivity().getApplicationContext();
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Choose navigator title");
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

        return true;
    }

    protected void getCurrentProfile()
    {
        if (!isBound()) {
            return;
        }

        Message msg = Message.obtain(null, Constants.MSG_RI_GET_CURRENT_PROFILE, 0, 0);
        msg.replyTo = getClientMessenger();

        try
        {
            getForeignService().send(msg);
        } catch (RemoteException e)
        {
            e.printStackTrace();
        }
    }

    protected void setCurrentProfile(String profileName)
    {
        if (!isBound())
            return;

        Message msg = Message.obtain(null, Constants.MSG_RI_SET_CURRENT_PROFILE, 0, 0);
        msg.replyTo = getClientMessenger();

        // Create a bundle and set the profile name
        Bundle bundle = new Bundle();
        bundle.putString("Profile", profileName);
        // Add the bundle to the message
        msg.setData(bundle);
        // Send the message to the navigator
        try
        {
            getForeignService().send(msg);
        } catch (RemoteException e)
        {
            e.printStackTrace();
        }
    }

    private String[] getServiceApps()
    {
        Context context = this.cordova.getActivity().getApplicationContext();
        PackageManager pm = context.getPackageManager();

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
        Log.d(TAG, componentName);
        String serviceName = "com.ptvag.navigation.ri.RIService";
        Intent intent = new Intent();
        intent.setClassName(componentName, serviceName);
        Context context = this.cordova.getActivity().getApplicationContext();
        context.bindService(intent,
                serviceConnection,
                Context.BIND_AUTO_CREATE);
    }

    private void setupClientMessenger()
    {
        createClientMessenger(new IncomingHandler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                if(!checkInitialized(msg)) {
                    PluginResult result = new PluginResult(PluginResult.Status.ERROR, msg.toString());
                    mCallbackContext.sendPluginResult(result);
                    return;
                }
                switch (msg.what)
                {
                    case Constants.MSG_RI_GET_CURRENT_PROFILE:
                    {
                        Bundle bundle = msg.getData();
                        String profile = bundle.getString("Profile");
                        if (profile != null)
                        {
                            currentProfile = profile;
                            Log.d(TAG, currentProfile);
                            if (getProfileCallbackContext != null) {
                                PluginResult result = new PluginResult(PluginResult.Status.OK, currentProfile);
                                getProfileCallbackContext.sendPluginResult(result);
                            }
                        }
                    }
                    break;
                    case Constants.MSG_RI_SET_CURRENT_PROFILE:
                    {
                        Bundle bundle = msg.getData();
                        String profile = bundle.getString("Profile");
                        Log.d(TAG, "Setting profile " + profile);
                        PluginResult result;
                        switch (msg.arg1)
                        {
                            case Constants.RI_ERROR_NONE:
                                Log.d(TAG, "Profile set successful");
                                currentProfile = profile;
                                result = new PluginResult(PluginResult.Status.OK, currentProfile);
                                break;
                            case Constants.RI_ERROR_NO_SUCH_PROFILE: {
                                String message = "No such profile";
                                Log.d(TAG, message);
                                result = new PluginResult(PluginResult.Status.ERROR, message);
                                break;
                            }
                            default: {
                                String message = "Error while setting profile.";
                                Log.d(TAG, message);
                                result = new PluginResult(PluginResult.Status.ERROR, message);
                                break;
                            }
                        }
                        setProfileCallbackContext.sendPluginResult(result);
                    }
                    break;
                    default:
                        super.handleMessage(msg);
                }
            }
        });
    }

    public void createClientMessenger(Handler handler)
    {
        clientMessenger = new Messenger(handler);
    }

    public Messenger getForeignService()
    {
        return foreignService;
    }

    public boolean isBound()
    {
        return bound;
    }

    public boolean checkInitialized(Message msg)
    {
        if(msg.arg1 == Constants.RI_ERROR_NOT_INITIALIZED)
        {
            Log.d(TAG, "ERROR: RI not initialized");
            return false;
        }
        return true;
    }

    public Messenger getClientMessenger()
    {
        return clientMessenger;
    }

}
