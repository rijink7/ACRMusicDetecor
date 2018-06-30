package com.nxt8.plugins.ACRMusicDetector;


import android.util.Log;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import com.acrcloud.rec.sdk.ACRCloudConfig;
import com.acrcloud.rec.sdk.ACRCloudClient;
import com.acrcloud.rec.sdk.IACRCloudListener;

/**
 * This class echoes a string called from JavaScript.
 */
public class ACRMusicDetector extends CordovaPlugin implements IACRCloudListener {



    /*
    * ACR Cloud SDK
    * */
    private ACRCloudConfig mConfig;
    private ACRCloudClient mClient;



    private  boolean initState = false;
    private  boolean mProcessing = false;

    /***/


    private Config config = null;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("coolMethod")) {
            String message = args.getString(0);
            this.coolMethod(message, callbackContext);
            return true;
        }else if(action.equals("init")){
            this.init(new JSONObject(args.getString(0)),callbackContext);
            return true;
        }else if(action.equals("start")){
            this.callbaclContext = callbackContext;
            this.start();
            return true;
        }else if(action.equals("stop")){
            this.stop();
            return true;
        }
        return false;
    }

    private void coolMethod(String message, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {
            callbackContext.success(message);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }



    private CallbackContext callbaclContext = null;

    private void init(JSONObject config,CallbackContext context){

        this.callbaclContext = context;

        try{
        String host = config.getString("host");
        String user = config.getString("user");
        String auth_key = config.getString("auth_key");

            Log.d("Init:","init : " + host + " " + user + " " + auth_key);
            System.out.println(host + " " + user + " " + auth_key);

        if((host != null) && (user != null) && (auth_key != null)){

            this.config = new Config(host,user,auth_key);




            this.mConfig = new ACRCloudConfig();
            this.mConfig.acrcloudListener = this;

            // If you implement IACRCloudResultWithAudioListener and override "onResult(ACRCloudResult result)", you can get the Audio data.
            //this.mConfig.acrcloudResultWithAudioListener = this;

            this.mConfig.context = this.cordova.getActivity().getApplicationContext();

            this.mConfig.host = this.config.getHost();//
            //this.mConfig.host = "us-west-2.api.acrcloud.com";
            //this.mConfig.dbPath = path; // offline db path, you can change it with other path which this app can access.
            this.mConfig.accessKey = this.config.getUser();
            this.mConfig.accessSecret = this.config.getAuth_key();
            this.mConfig.protocol = ACRCloudConfig.ACRCloudNetworkProtocol.PROTOCOL_HTTPS; // PROTOCOL_HTTPS
            //this.mConfig.reqMode = ACRCloudConfig.ACRCloudRecMode.REC_MODE_REMOTE;
            //this.mConfig.reqMode = ACRCloudConfig.ACRCloudRecMode.REC_MODE_LOCAL;
            this.mConfig.reqMode = ACRCloudConfig.ACRCloudRecMode.REC_MODE_REMOTE;

            this.mClient = new ACRCloudClient();
            // If reqMode is REC_MODE_LOCAL or REC_MODE_BOTH,
            // the function initWithConfig is used to load offline db, and it may cost long time.
            this.initState = this.mClient.initWithConfig(this.mConfig);
            if (this.initState) {
             //   this.mClient.startPreRecord(3000); //start prerecord, you can call "this.mClient.stopPreRecord()" to stop prerecord.
            }


           // context.success("init successfull");
            this.sendMessage("init Successfull");




        }else {

            context.error("init failed, invalid configuration");
        }

        }catch (Exception ex){
            context.error("Invalid Configuation" + ex.getMessage());
        }




    }




    private class Config{

        private String host;
        private String user;
        private String auth_key;


        public Config(String host, String user, String auth_key) {
            this.host = host;
            this.user = user;
            this.auth_key = auth_key;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getAuth_key() {
            return auth_key;
        }

        public void setAuth_key(String auth_key) {
            this.auth_key = auth_key;
        }
    }


    // Old api
    @Override
    public void onResult(String result) {


        if (this.mClient != null) {
            this.mClient.cancel();
            mProcessing = false;
        }
        
        this.sendMessage(result);
    }

    @Override
    public void onVolumeChanged(double volume) {

    }





    public void start() {
        if (!this.initState) {
            return;
        }

        if (!mProcessing) {
            mProcessing = true;
            if (this.mClient == null || !this.mClient.startRecognize()) {
                mProcessing = false;
            }
        }
    }

    protected void stop() {
        if (mProcessing && this.mClient != null) {
            this.mClient.stopRecordToRecognize();
        }
        mProcessing = false;

    }

    protected void cancel() {
        if (mProcessing && this.mClient != null) {
            mProcessing = false;
            this.mClient.cancel();
        }
    }



    private void sendMessage(String message){

        if(this.callbaclContext != null){
            PluginResult result = new PluginResult(PluginResult.Status.OK, message);
            // PluginResult result = new PluginResult(PluginResult.Status.ERROR, "YOUR_ERROR_MESSAGE");
            result.setKeepCallback(true);
            this.callbaclContext.sendPluginResult(result);
        }
    }
}
