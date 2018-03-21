package com.hc.serialport;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alex on 2017/3/13.
 */

public class SerialUtils {
    public final static String TAG="SERIAL_PORT" ;
    private static SerialFinder mSerialPortFinder= new SerialFinder();

    //获取串口
    public static List<String> getAllSerialPort() {
        List<String> list = new ArrayList<>();
        String[] entryValue = mSerialPortFinder.getAllDevicePath();
        for (int i = 0; i < entryValue.length; i++) {
            list.add(entryValue[i]);
            Log.i(TAG, "串口" + i + ":  " + entryValue[i]);
        }
        return list;
    }
}
