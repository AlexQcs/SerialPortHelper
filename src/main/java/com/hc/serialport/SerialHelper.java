package com.hc.serialport;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import static com.hc.serialport.HexUtils.HexToByteArr;

/**
 * Created by alex on 2017/3/10.
 */

public abstract class SerialHelper {
    private SerialPortLib mSerialPort;

    private OutputStream mOutputStream;
    private InputStream mInputStream;
    private ReadThread mReadThread;
    private SendThread mSendThread;

    private String sPort = "/dev/s3c2410_serial0";
    private int iDelay = 200;  //延迟
    private int iBaudRate = 2400; //波特率
    private boolean _isOpen = false;

    private byte[] _bLoopData = new byte[]{0x30};

    //-----构造方法-----------
    public SerialHelper(String sPort, int iBaudRate) {
        this.sPort = sPort;
        this.iBaudRate = iBaudRate;
    }

    //-----构造方法-----------
    public SerialHelper() {
        this("/dev/s3c2410_serial0", 9600);
    }

    //-----构造方法-----------
    public SerialHelper(String sPort, String sBaudRate) {
        this(sPort, Integer.parseInt(sBaudRate));
    }

    //------------开启串口通信操作----------
    public void open() throws Exception {

        mSerialPort = new SerialPortLib(new File(sPort), iBaudRate, 0);

        mOutputStream = mSerialPort.getOutputStream();
        mInputStream = mSerialPort.getInputStream();

        mReadThread = new ReadThread();
        mReadThread.start();

        mSendThread = new SendThread();
        mSendThread.setSuspendFlag();
        mSendThread.start();

        _isOpen = true;
    }

    public void close() {
        if (mReadThread != null) {
            mReadThread.interrupt();
        }

        if (mSendThread != null) {
            mSendThread.interrupt();
        }

        if (mSerialPort != null) {
            mSerialPort.close();
            mSerialPort = null;
        }

        _isOpen = false;
    }


    //----------------读取线程----------------
    private class ReadThread extends Thread {


        @Override
        public void run() {
            super.run();
            while (!isInterrupted()) {
                try {
                    if (mInputStream == null) return;
                    byte[] buffer = new byte[1024];


                    if (mInputStream.available() > 0) {
                        Thread.sleep(100);
                    } else {
                        Thread.sleep(1);
                        continue;
                    }

                    int size = mInputStream.read(buffer);
                    if (size > 0) {
                        ComBean comBean = new ComBean(sPort, buffer, size);
//                        comBean.setsComPort(sPort);
//                        comBean.setBuffer(buffer);
                        onDataReceived(comBean);
                    }
                    Thread.sleep(200);
                    System.gc();
                } catch (Throwable e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    //---------------发送线程-----------------
    private class SendThread extends Thread {
        public boolean suspendFlag = true;//控制线程的执行

        @Override
        public void run() {
            super.run();
            while (!isInterrupted()) {
                synchronized (this) {
                    while (suspendFlag) {
                        try {
                            wait();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                send(getbLoopData());

                try {
                    Thread.sleep(iDelay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        //线程暂停
        public void setSuspendFlag() {
            this.suspendFlag = true;
        }


        //唤醒线程
        public synchronized void setResume() {
            this.suspendFlag = false;
            notify();
        }
    }

    //----------发送数据--------------------------------
    public void send(byte[] bOutArray) {
        try {
            mOutputStream.write(bOutArray);
            clearLoopData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //-----------getbLoopData
    public byte[] getbLoopData() {
        return _bLoopData;
    }

    public void clearLoopData() {
        _bLoopData = null;
    }

    //----------------------------------------------------
    public void sendHex(String sHex) {
        byte[] bOutArray = HexToByteArr(sHex);
        send(bOutArray);
    }

    public void sendHex(byte[] bytes) {
        send(bytes);
    }

    //----------------------------------------------------
    public void sendTxt(String sTxt) {
        byte[] bOutArray = new byte[0];
        try {
            bOutArray = sTxt.getBytes("GB2312");
            send(bOutArray);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    public int getBaudRate() {
        return iBaudRate;
    }

    public boolean setBaudRate(int iBaud) {
        if (_isOpen) {
            return false;
        } else {
            iBaudRate = iBaud;
            return true;
        }
    }

    public boolean setBaudRate(String sBaud) {
        int iBaud = Integer.parseInt(sBaud);
        return setBaudRate(iBaud);
    }

    //----------------------------------------------------
    public String getPort() {
        return sPort;
    }

    public boolean setPort(String sPort) {
        if (_isOpen) {
            return false;
        } else {
            this.sPort = sPort;
            return true;
        }
    }

    //----------------------------------------------------
    public boolean isOpen() {
        return _isOpen;
    }
    //----------------------------------------------------

    //----------------------------------------------------
    public void setbLoopData(byte[] bLoopData) {
        this._bLoopData = bLoopData;
    }

    //----------------------------------------------------
    public void setTxtLoopData(String sTxt) {
        this._bLoopData = sTxt.getBytes();
    }

    //----------------------------------------------------
    public void setHexLoopData(String sHex) {
        this._bLoopData = HexUtils.HexToByteArr(sHex);
    }

    //----------------------------------------------------
    public int getiDelay() {
        return iDelay;
    }

    //----------------------------------------------------
    public void setiDelay(int iDelay) {
        this.iDelay = iDelay;
    }

    //----------------------------------------------------
    public void startSend() {
        if (mSendThread != null) {
            mSendThread.setResume();
        }
    }

    //----------------------------------------------------
    public void stopSend() {
        if (mSendThread != null) {
            mSendThread.setSuspendFlag();
        }
    }

    protected abstract void onDataReceived(ComBean comBean);
}
