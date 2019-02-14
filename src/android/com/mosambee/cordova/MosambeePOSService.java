package com.mosambee.cordova;

import android.app.PendingIntent;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.mosambee.cordova.serial.SerialPortIOManage;
import com.mosambee.cordova.serial.SerialPortService;
import com.mosambee.lib.Mosambee;
import com.mosambee.lib.MosCallback;
import com.mosambee.lib.ResultData;
import com.mosambee.lib.TransactionCallback;
import com.mosambee.lib.TransactionResult;
import com.mosambee.lib.TRACE;
import com.printer.sdk.PrinterConstants;
import com.printer.sdk.PrinterInstance;

import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;

public class MosambeePOSService extends CordovaPlugin {

    // final static MosCallback mc;
    private Intent intent;
    public PrinterInstance myPrinter;
    private boolean isConnected;
    public static TextView tvScannarData;
    private String devicesName = "Serial device";
    private String devicesAddress = "/dev/ttyMT0";
    private String default_baudrate = "115200";
    private int baudrate;
    private MyBroadcastReceiver brocastReceiver;
    private static String TAG = "MosambeePOS";
    private static String ACTION = "com.mosambee.MosambeePOS";

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        Log.d(TAG, "Initializing Mosambee POS Service");
        // this.mc = new MosCallback(cordova.getActivity().getApplicationContext());
        this.brocastReceiver = new MyBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(ACTION);
        cordova.getActivity().getApplicationContext().registerReceiver(this.brocastReceiver, intentFilter);
    }

    public void onDestroy() {
        synchronized (this) {
            try {
                cordova.getActivity().getApplicationContext().unregisterReceiver(this.brocastReceiver);
            } catch (Exception exp) {
                Log.e(TAG, "Issue while unregistering mosambee port listener", exp);
            }
        }
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("closePort")) {
            Toast.makeText(cordova.getActivity(), "closePort", Toast.LENGTH_LONG).show();
            intent = new Intent(ACTION);
            intent.putExtra("openPort", false);
            intent.putExtra("deviceType", "Both");
            intent.setClassName("com.mosambee.printService", "com.mosambee.printService.PrinterService");
            cordova.getActivity().getApplicationContext().startService(intent);

            String responseText = "Hello world, close port";
            callbackContext.success(responseText);

            return true;
        } else if (action.equals("connectToPrinter")) {
            Toast.makeText(cordova.getActivity(), "connectToPrinter", Toast.LENGTH_LONG).show();
            intent = new Intent(ACTION);
            intent.putExtra("openPort", true);
            intent.putExtra("deviceType", "Printer");
            intent.setClassName("com.mosambee.printService", "com.mosambee.printService.PrinterService");
            cordova.getActivity().getApplicationContext().startService(intent);
            String responseText = "Hello world, connectToPrinter";
            callbackContext.success(responseText);

            return true;
        } else if (action.equals("print")) {
            Toast.makeText(cordova.getActivity(), "Print action", Toast.LENGTH_LONG).show();
            //goForPrint();
            String responseText = "Hello world, connectToPrinter";
            callbackContext.success(responseText);
            return true;
        } else if (action.equals("startScanner")) {
            try {
                Toast.makeText(cordova.getActivity(), "startScanner", Toast.LENGTH_LONG).show();
                intent = new Intent(ACTION);
                intent.putExtra("openPort", true);
                intent.putExtra("deviceType", "Scanner");
                intent.setClassName("com.mosambee.printService", "com.mosambee.printService.PrinterService");
                cordova.getActivity().getApplicationContext().startService(intent);
                String responseText = "Hello world, startScanner";
                callbackContext.success(responseText);
            } catch (NoSuchMethodError er) {
                Toast.makeText(cordova.getActivity().getApplicationContext(),
                        "Connection to mosambee scanner failed." + er.getMessage(), Toast.LENGTH_SHORT).show();
                callbackContext.error("Failed to start scanner activity");
            } catch (Exception er) {
                Toast.makeText(cordova.getActivity().getApplicationContext(),
                        "Connection to mosambee scanner failed." + er.getMessage(), Toast.LENGTH_SHORT).show();
                callbackContext.error("Failed to start scanner activity");
            }
            return true;
        } else if (action.equals("stopScanner")) {
            try {
                Toast.makeText(cordova.getActivity(), "stopScanner", Toast.LENGTH_LONG).show();
                Log.d(TAG, "-----------in closeTheSerialPort");
                handler.removeMessages(1000);
                handler.removeMessages(1001);
                handler.removeMessages(999);
                SerialPortIOManage.getInstance().disConnect();
                Log.d(TAG, "-----------in closeTheSerialPort");
                String responseText = "Hello world, stopScanner";
                callbackContext.success(responseText);
            } catch (NoSuchMethodError er) {
                Toast.makeText(cordova.getActivity().getApplicationContext(),
                        "Connection to mosambee scanner failed." + er.getMessage(), Toast.LENGTH_SHORT).show();
                callbackContext.error("Failed to stop scanner activity");
            } catch (Exception er) {
                Toast.makeText(cordova.getActivity().getApplicationContext(),
                        "Connection to mosambee scanner failed." + er.getMessage(), Toast.LENGTH_SHORT).show();
                callbackContext.error("Failed to stop scanner activity");
            }
            return true;
        }
        return true;
    }

    // Print method
    public void printText(String printtext) {
        Log.d(TAG, "----------printText--------");
        devicesAddress = "/dev/ttyMT0";
        String com_baudrate = "115200";
        baudrate = Integer.parseInt(com_baudrate);
        myPrinter = PrinterInstance.getPrinterInstance(new File(devicesAddress), baudrate, 0, mHandler);
        Log.d(TAG, "myPrinter.getCurrentStatus()-" + myPrinter.getCurrentStatus());
        boolean b = myPrinter.openConnection();
        Log.d(TAG, "-----------" + b);
        if (b && myPrinter != null && myPrinter.getCurrentStatus() == 0) {
            // printData();
            // Bitmap icon =
            // BitmapFactory.decodeResource(cordova.getActivity().getResources(),R.drawable.download);
            // Drawable myDrawable =
            // cordova.getActivity().getResources().getDrawable(R.drawable.download);
            // Bitmap anImage = ((BitmapDrawable) myDrawable).getBitmap();
            // myPrinter.printImage(anImage, PrinterConstants.PAlign.CENTER,1,false);
            myPrinter.printText(printtext);
            // myPrinter.drawQrCode(2, 2, "123456", PrinterConstants.PRotate.Rotate_0, 2,
            // 2);
        } else {
            Toast.makeText(cordova.getActivity().getApplicationContext(), "Connection to mosambee printer failed.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public Object onMessage(String id, Object data) {
        Log.d(TAG, "on recieving message: " + id + " " + data);
        return null;
    }

    private Handler mHandler = new Handler() {
        @SuppressLint("ShowToast")
        @Override
        public void handleMessage(Message msg) {

            Log.d(TAG, "@@@@@@@@@@@@  mosambee" + msg.what);
            switch (msg.what) {
            case PrinterConstants.Connect.SUCCESS:
                isConnected = true;
                Log.d(TAG, "isConnected status:::;" + isConnected);
                Toast.makeText(cordova.getActivity().getApplicationContext(), "Connection success", Toast.LENGTH_SHORT)
                        .show();
                break;
            case PrinterConstants.Connect.FAILED:
                isConnected = false;
                Toast.makeText(cordova.getActivity().getApplicationContext(), "Connection failed", Toast.LENGTH_SHORT)
                        .show();
                break;
            case PrinterConstants.Connect.CLOSED:
                isConnected = false;
                Toast.makeText(cordova.getActivity().getApplicationContext(), "Connection closed", Toast.LENGTH_SHORT)
                        .show();
                break;
            case PrinterConstants.Connect.NODEVICE:
                isConnected = false;
                Toast.makeText(cordova.getActivity().getApplicationContext(), "No connection", Toast.LENGTH_SHORT)
                        .show();
                break;
            case 0:
                Toast.makeText(cordova.getActivity().getApplicationContext(), "0", Toast.LENGTH_SHORT).show();
                break;
            case -1:
                Toast.makeText(cordova.getActivity().getApplicationContext(), "-1", Toast.LENGTH_SHORT).show();
                break;
            case -2:
                Toast.makeText(cordova.getActivity().getApplicationContext(), "-2", Toast.LENGTH_SHORT).show();
                break;
            case -3:
                Toast.makeText(cordova.getActivity().getApplicationContext(), "-3", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
            }
        }
    };

    public static Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
            case 888:
                SerialPortIOManage.getInstance().sendDataToDevice("00");
                sendEmptyMessageDelayed(1000, 20);
                break;
            case 999:
                SerialPortIOManage.getInstance().sendDataToDevice("00");
                sendEmptyMessageDelayed(1001, 20);
                break;
            case 1000:
                SerialPortIOManage.getInstance().sendDataToDevice("04 E4 04 00 FF 14");
                break;
            case 1001:
                SerialPortIOManage.getInstance().sendDataToDevice("07 C6 04 00 FF 8A 08 FD 9E");
                // SerialPortIOManage.getInstance().sendDataToDevice("07 C6 04 80 14 8A 01 FE
                // 10");
                break;
            }
        }

        ;
    };

    public class MyBroadcastReceiver extends BroadcastReceiver {

        public MyBroadcastReceiver() {
            super();
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "===== mosambee onReceive " + intent.getAction());

            Bundle bundle = intent.getExtras();
            String deviceType = bundle.getString("deviceType");
            int deviceState = bundle.getInt("deviceState");
            int deviceOpen1 = bundle.getInt("deviceOpen1");
            int deviceOpen2 = bundle.getInt("deviceOpen2");

            assert deviceType != null;
            switch (deviceType) {
            case "Printer":
                if (deviceState == 0 && deviceOpen1 == 0 && deviceOpen2 == 0) {
                    Toast.makeText(
                            cordova.getActivity().getApplicationContext(), "" + deviceType + "\ndeviceState: "
                                    + deviceState + "\ndeviceOpen1: " + deviceOpen1 + "\ndeviceOpen2: " + deviceOpen2,
                            Toast.LENGTH_LONG).show();
                    goForPrint();
                } else {
                    Toast.makeText(cordova.getActivity().getApplicationContext(), "" + deviceType + "\n else",
                            Toast.LENGTH_LONG).show();
                }
                break;
            case "Scanner":
                if (deviceState == 0 && deviceOpen1 == 0) {
                    goForScanner();
                    Toast.makeText(cordova.getActivity().getApplicationContext(),
                            "" + deviceType + "\ndeviceState: " + deviceState + "\ndeviceOpen1: " + deviceOpen1,
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(cordova.getActivity().getApplicationContext(), "" + deviceType + "\n else",
                            Toast.LENGTH_LONG).show();
                }
            case "Both":
                if (deviceState == 0 && deviceOpen1 == 0 && deviceOpen2 == 0) {
                    Toast.makeText(
                            cordova.getActivity().getApplicationContext(), "" + deviceType + "\ndeviceState: "
                                    + deviceState + "\ndeviceOpen1: " + deviceOpen1 + "\ndeviceOpen2: " + deviceOpen2,
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(cordova.getActivity().getApplicationContext(), "" + deviceType + "\n else",
                            Toast.LENGTH_LONG).show();
                }
                break;
            }
        }

        // Print simple text for testing
        public void goForPrint() {
            Log.d(TAG, "----------goForPrint--------");
            devicesName = "Serial device";
            devicesAddress = "/dev/ttyMT0";
            String com_baudrate = "115200";
            baudrate = Integer.parseInt(com_baudrate);
            myPrinter = PrinterInstance.getPrinterInstance(new File(devicesAddress), baudrate, 0, mHandler);
            Log.d(TAG, "myPrinter.getCurrentStatus()-" + myPrinter.getCurrentStatus());
            boolean b = myPrinter.openConnection();
            Log.d(TAG, "-----------" + b);
            if (b && myPrinter != null && myPrinter.getCurrentStatus() == 0) {
                // printData();
                // Bitmap icon =
                // BitmapFactory.decodeResource(cordova.getActivity().getResources(),R.drawable.download);
                // Drawable myDrawable =
                // cordova.getActivity().getResources().getDrawable(R.drawable.download);
                // Bitmap anImage = ((BitmapDrawable) myDrawable).getBitmap();
                // myPrinter.printImage(anImage, PrinterConstants.PAlign.CENTER,1,false);
                myPrinter.printText("Synergistic Financial Network");
                myPrinter.drawQrCode(2, 2, "123456", PrinterConstants.PRotate.Rotate_0, 2, 2);
            } else {
                Toast.makeText(cordova.getActivity().getApplicationContext(), "Connection to mosambee printer failed.",
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Monochromatic image pass to printImage()
        public void printImage() {
            Log.d(TAG, "----------printImage--------");
            devicesName = "Serial device";
            devicesAddress = "/dev/ttyMT0";
            String com_baudrate = "115200";
            baudrate = Integer.parseInt(com_baudrate);
            myPrinter = PrinterInstance.getPrinterInstance(new File(devicesAddress), baudrate, 0, mHandler);
            Log.d(TAG, "myPrinter.getCurrentStatus()-" + myPrinter.getCurrentStatus());
            boolean b = myPrinter.openConnection();
            Log.d(TAG, "-----------" + b);
            if (b && myPrinter != null && myPrinter.getCurrentStatus() == 0) {
                // printData();
                // Bitmap icon =
                // BitmapFactory.decodeResource(cordova.getActivity().getResources(),R.drawable.download);
                // Drawable myDrawable =
                // cordova.getActivity().getResources().getDrawable(R.drawable.download);
                // Bitmap anImage = ((BitmapDrawable) myDrawable).getBitmap();
                // myPrinter.printImage(anImage, PrinterConstants.PAlign.CENTER,1,false);
                myPrinter.printText("Synergistic Financial Network");
                myPrinter.drawQrCode(2, 2, "123456", PrinterConstants.PRotate.Rotate_0, 2, 2);
            } else {
                Toast.makeText(cordova.getActivity().getApplicationContext(), "Connection to mosambee printer failed.",
                        Toast.LENGTH_SHORT).show();
            }

        }

        public void goForScanner() {
            try {
                Log.d(TAG, "-----------goForScanner");

                Log.d(TAG, "-----------in openTheSerialPort");
                Intent startIntent2 = new Intent(cordova.getActivity().getApplicationContext(),
                        SerialPortService.class);
                startIntent2.putExtra("serial", "dev/ttyMT1");
                cordova.getActivity().startService(startIntent2);
                handler.removeMessages(1000);
                handler.removeMessages(1001);
                handler.removeMessages(999);
                handler.sendEmptyMessageDelayed(999, 10);

                Log.d(TAG, "-----------in openTheScanHead");
                SerialPortIOManage.getInstance().resetBuffer();
                handler.removeMessages(888);
                handler.removeMessages(999);
                handler.removeMessages(1000);
                handler.removeMessages(1001);
                handler.sendEmptyMessageDelayed(888, 1000);

            } catch (NoSuchMethodError er) {
                Toast.makeText(cordova.getActivity().getApplicationContext(),
                        "Connection to mosambee scanner failed." + er.getMessage(), Toast.LENGTH_SHORT).show();
            } catch (Exception er) {
                Toast.makeText(cordova.getActivity().getApplicationContext(),
                        "Connection to mosambee scanner failed." + er.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}