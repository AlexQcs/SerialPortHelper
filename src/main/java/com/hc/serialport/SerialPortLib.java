package com.hc.serialport;

import android.util.Log;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by alex on 2017/3/10.
 */

public class SerialPortLib {
    private static String TAG="SerialPort";

    private FileDescriptor mFileDes;
    private FileInputStream mFileInputStream;
    private FileOutputStream mFileOutputStream;

    public SerialPortLib(File device, int baudrate, int flags)throws Exception {
        if (!device.canRead() || !device.canWrite()) {
            try {
                Process su;
                su = Runtime.getRuntime().exec("/system/bin/su");
                String cmd = "chmod 666" + device.getAbsolutePath() + "\n" + "exit\n";
                su.getOutputStream().write(cmd.getBytes());

                if ((su.waitFor() != 0) || !device.canRead() || !device.canWrite()) {
                    throw new SecurityException();//请求失败
                }

            } catch (Exception e) {
                e.printStackTrace();
                throw new SecurityException();//请求失败
            }

        }

        mFileDes = open(device.getAbsolutePath(),baudrate,flags);
        if (mFileDes==null){
            Log.e(TAG,"native open retruns null");
            throw new IOException();
        }

        mFileInputStream = new FileInputStream(mFileDes);
        mFileOutputStream = new FileOutputStream(mFileDes);
    }
    public FileInputStream getInputStream() {
        return mFileInputStream;
    }

    public void setInputStream(FileInputStream mFileInputStream) {
        this.mFileInputStream = mFileInputStream;
    }

    public FileOutputStream getOutputStream() {
        return mFileOutputStream;
    }

    public void setOutputStream(FileOutputStream mFileOutputStream) {
        this.mFileOutputStream = mFileOutputStream;
    }

//    //JNI
//    private native static FileDescriptor open(String path, int baudrate, int flags);
//    public native void close();

    private native static FileDescriptor open(String path,int baudrate, int flags);
    public native void close();

    static {
        System.loadLibrary("SerialPortLib");
    }
}
