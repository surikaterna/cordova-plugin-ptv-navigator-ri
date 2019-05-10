package com.surikat.lynx.cordova;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PTVRemoteInterface extends CordovaPlugin {
    private CallbackContext callbackContext;
    private JSONObject data = new JSONObject();

    // The navigator service messenger, used to send data to the navigator
    private Messenger foreignService = null;

    // Flag indicating whether we have called bind on the service
    private boolean bound;

    // the navigator application package
    static String componentName;

    private String currentProfile = 'INIT';


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
        } else if ("getProfile".equals(action)) {
            //
            getCurrentProfile();
        } else if ("getCurrent".equals(action)) {
            // we send back the latest saved data from the event listener to the success callback
            PluginResult result = new PluginResult(PluginResult.Status.OK, this.currentProfile);
            callbackContext.sendPluginResult(result);
            return true;
        } else if ("test".equals(action)) {
            return this.currentProfile;
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

    protected void getCurrentProfile()
    {
        if (!isBound())
            return;

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

    private void setupClientMessenger()
    {
        createClientMessenger(new IncomingHandler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                if(!checkInitialized(msg))
                    return;
                // for every message we receive, we will put some text in the
                // textOutput edit field
                switch (msg.what)
                {

                    case Constants.MSG_RI_QUERY_PROFILES:
                    {
                        Bundle bundle = msg.getData();
//                        textOutput.append("Available profiles:\n");
                        String[] profiles = bundle.getStringArray("Profiles");
                        if (profiles != null && profiles.length > 0)
                        {
                            for (int i=0; i<profiles.length; i++)
                            {
//                                textOutput.append(profiles[i] + "\n");
                            }
//                            textProfile.setText(profiles[new Random().nextInt(profiles.length)]);
                        }
                    }
                    break;
                    case Constants.MSG_RI_GET_CURRENT_PROFILE:
                    {
                        Bundle bundle = msg.getData();
//                        textOutput.append("Current profile:\n");
                        String profile = bundle.getString("Profile");
                        if (profile != null)
                        {
//                            textOutput.append(profile + "\n");
                            currentProfile = profile;
                        }
                    }
                    break;
                    case Constants.MSG_RI_SET_CURRENT_PROFILE:
                    {
                        Bundle bundle = msg.getData();
                        String profile = bundle.getString("Profile");
//                        textOutput.append("Setting profile " + profile + ".\n");
                        switch (msg.arg1)
                        {
                            case Constants.RI_ERROR_NONE:
//                                textOutput.append("Profile set successful.\n");
                                currentProfile = "Profile set successful.\n";
                                break;
                            case Constants.RI_ERROR_NO_SUCH_PROFILE:
//                                textOutput.append("No such profile.\n");
                                currentProfile = "No such profile.\n";
                                break;
                            default:
//                                textOutput.append("Error while setting profile.\n");
                                currentProfile = "Error while setting profile.\n";
                                break;
                        }
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
            currentProfile = "ERROR: RI not initialized\n";
//            textOutput.append("ERROR: RI not initialized\n");
            return false;
        }
        return true;
    }

    public Messenger getClientMessenger()
    {
        return clientMessenger;
    }

}
