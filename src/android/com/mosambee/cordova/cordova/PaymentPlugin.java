package com.mosambee.cordova.cordova;

import android.content.Intent;
import android.widget.Toast;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PaymentPlugin extends CordovaPlugin {
    private CallbackContext callbackContext = null;
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext)
            throws JSONException {
        this.callbackContext=callbackContext;
//        Toast.makeText(cordova.getActivity(),"mosambee",Toast.LENGTH_LONG).show();

        MosambeeUtils mosutils;
        mosutils = new MosambeeUtils();
        cordova.setActivityResultCallback(PaymentPlugin.this);

        mosutils.startPayment(cordova.getActivity(), "Sale", 100, false, "8424834651", cordova.getActivity(), "", "9167545444",
                "1478", 200, false, "", "", "", "1234", "", "BT");

        return true;
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Toast.makeText(cordova.getActivity(),"result", Toast.LENGTH_LONG).show();
        System.out.println("ResultCode=" + resultCode
                + "\nrequestCode " + requestCode);
        System.out.println("----------"+data);

        if(data==null){

            callbackContext.error("Fail.");
           // return;
        }

        String response = data.getStringExtra("response");
        callbackContext.success(response);
        System.out.println("=====response" + response);

        try {
            JSONObject jsonObject =new JSONObject(response);
            JSONArray jsonArray = new JSONArray(jsonObject.getString("history"));
            for (int i = 0, n = jsonArray.length(); i < n; i++) {
                JSONArray historyItem = new JSONArray(jsonArray.get(i).toString());
                System.out.println("History single item array"+ historyItem);
                for (int j = 0; j < historyItem.length(); j++) {
                    //System.out.println("history item : "+historyItem.get(j));
                }
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }
}