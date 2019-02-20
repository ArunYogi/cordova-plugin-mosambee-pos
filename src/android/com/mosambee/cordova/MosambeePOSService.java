package com.mosambee.cordova;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import com.mosambee.cordova.serial.SerialPortIOManage;
import com.mosambee.cordova.serial.SerialPortService;
import com.mosambee.lib.TRACE;
import com.printer.sdk.PrinterConstants;
import com.printer.sdk.PrinterInstance;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;

public class MosambeePOSService extends CordovaPlugin {
    private Intent intent;

    public PrinterInstance myPrinter;
    private boolean isConnected;
    public static TextView tvScannarData;
    private String devicesName = "Serial device";
    private String devicesAddress = "/dev/ttyMT0";
    private int baudrate;
    private MyBroadcastReceiver myReceiver;
    private static final String TAG = "Mosambee";
    private CallbackContext printerConnectCB;

    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        myReceiver = new MyBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("MY_ACTION");
        cordova.getActivity().registerReceiver(myReceiver, intentFilter);
    }

    private void injectConnectionCallback(CallbackContext callbackContext) {
        synchronized (this) {
            if (printerConnectCB != null && !printerConnectCB.isFinished()) {
                printerConnectCB.error("Got new request");
            }
            printerConnectCB = callbackContext;
        }
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

        if (action.equals("closePort")) {
            try {

                Toast.makeText(cordova.getActivity(), "closePort", Toast.LENGTH_LONG).show();

                intent = new Intent();
                intent.putExtra("openPort", false);
                intent.putExtra("deviceType", "Both");
                intent.setClassName("com.mosambee.printService", "com.mosambee.printService.PrinterService");
                cordova.getActivity().startService(intent);

                String responseText = "Hello world, " + args.getString(0);
                callbackContext.success(responseText);
            } catch (JSONException e) {
                callbackContext.error("Failed to parse parameters");
            }
            return true;
        } else if (action.equals("connectToPrinter")) {
            Toast.makeText(cordova.getActivity(), "connectToPrinter", Toast.LENGTH_LONG).show();

            intent = new Intent();
            intent.putExtra("openPort", true);
            intent.putExtra("deviceType", "Printer");
            intent.setClassName("com.mosambee.printService", "com.mosambee.printService.PrinterService");
            cordova.getActivity().startService(intent);
            injectConnectionCallback(callbackContext);
            return true;
        } else if (action.equals("printText")) {
            try {
                Toast.makeText(cordova.getActivity(), "printText", Toast.LENGTH_LONG).show();
                String printer_content = args.getString(0);
                printText(printer_content, callbackContext);
            } catch (JSONException e) {
                callbackContext.error("Failed to parse parameters");
            }
            return true;
        } else if (action.equals("startScanner")) {
            try {

                Toast.makeText(cordova.getActivity(), "startScanner", Toast.LENGTH_LONG).show();
                intent = new Intent();
                intent.putExtra("openPort", true);
                intent.putExtra("deviceType", "Scanner");
                intent.setClassName("com.mosambee.printService", "com.mosambee.printService.PrinterService");
                cordova.getActivity().startService(intent);

            } catch (NoSuchMethodError | Exception er) {
                Toast.makeText(cordova.getActivity().getApplicationContext(),
                        "Connection to scanner failed." + er.getMessage(), Toast.LENGTH_SHORT).show();
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

            } catch (NoSuchMethodError | Exception er) {
                Toast.makeText(cordova.getActivity().getApplicationContext(),
                        "Connection to scanner failed." + er.getMessage(), Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        return true;
    }

    @Override
    public void onDestroy() {
        synchronized (this) {
        }
    }
    // Monochromatic image pass to printImage()

    public void printText(String text, CallbackContext callback) {
        Log.d(TAG, "Printing text in mosambee printer");
        devicesName = "Serial device";
        String com_baudrate = "115200";
        baudrate = Integer.parseInt(com_baudrate);
        if (this.myPrinter == null) {
            this.myPrinter = PrinterInstance.getPrinterInstance(new File(this.devicesAddress), baudrate, 0, mHandler);
        }
        Log.d(TAG, "myPrinter.getCurrentStatus()-" + myPrinter.getCurrentStatus());
        boolean b = this.myPrinter.openConnection();
        Log.d(TAG, "Open connection Status of printer " + b);
        if (b && this.myPrinter != null && this.myPrinter.getCurrentStatus() == 0) {
            this.myPrinter.printText(text);
            callback.success("success");
        } else {
            Toast.makeText(cordova.getActivity().getApplicationContext(), "Connection to printer failed.",
                    Toast.LENGTH_SHORT).show();
            callback.error("printer connection failed");
        }

    }

    private Handler mHandler = new Handler() {
        @SuppressLint("ShowToast")
        @Override
        public void handleMessage(Message msg) {

            Log.d(TAG, "@@@@@@@@@@@@ mHandler in mosambee" + msg.what);
            switch (msg.what) {
            case PrinterConstants.Connect.SUCCESS:
                isConnected = true;
                Log.d(TAG, "isConnected status:::;" + isConnected);
                break;
            case PrinterConstants.Connect.FAILED:
                isConnected = false;

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
            Log.d("Mosambee", "Message in handler " + msg.what);
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
            TRACE.i("===== onReceive in mosambee");
            Log.d("Mosambee", "received broadcast message with action " + intent.getAction());

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
                    if (printerConnectCB != null && !printerConnectCB.isFinished()) {
                        printerConnectCB.success("Connected successfully");
                    }
                } else {
                    Toast.makeText(cordova.getActivity().getApplicationContext(), "" + deviceType + "\n else",
                            Toast.LENGTH_LONG).show();
                    if (printerConnectCB != null && !printerConnectCB.isFinished()) {
                        printerConnectCB.error("Connection failed");
                    }
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

        // Monochromatic image pass to printImage()

        public void goForPrint() {
            Log.d(TAG, "----------buttonPrint--------");
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
                // BitmapFactory.decodeResource(cordova.getActivity().getResources(),
                // R.drawable.screen);
                // Drawable myDrawable =
                // cordova.getActivity().getResources().getDrawable(R.drawable.screen);
                // Bitmap anImage = ((BitmapDrawable) myDrawable).getBitmap();
                // myPrinter.printImage(anImage, PrinterConstants.PAlign.CENTER,1,false);
                myPrinter.printText("Sample print for Mosambee");
                // myPrinter.drawQrCode(2, 2, "http://www.gokhana.com/",
                // PrinterConstants.PRotate.Rotate_0, 2, 2);
            } else
                Toast.makeText(cordova.getActivity().getApplicationContext(), "Connection to printer failed.",
                        Toast.LENGTH_SHORT).show();

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

            } catch (NoSuchMethodError | Exception er) {
                Toast.makeText(cordova.getActivity().getApplicationContext(),
                        "Connection to scanner failed." + er.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

    }
}