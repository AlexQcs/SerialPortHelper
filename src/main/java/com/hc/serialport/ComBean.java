package com.hc.serialport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alex on 2017/3/10.
 */

public class ComBean {

    public byte[] bRec = null; //接收到的数据
    private String sRecTime = ""; //接收数据的时间
    public String sComPort = ""; //
    public byte[] mBuffer;

    public ComBean() {

    }

    public ComBean(String sPort, byte[] buffer, int size) {
        sComPort = sPort;
        mBuffer = buffer;
        bRec = new byte[size];
        for (int i = 0; i < size; i++) {
            bRec[i] = buffer[i];
        }
        SimpleDateFormat sDateFormat = new SimpleDateFormat("hh:mm:ss");
        sRecTime = sDateFormat.format(new java.util.Date());
    }

    public byte[] getbRec() {
        return bRec;
    }

    public void setbRec(byte[] bRec) {
        this.bRec = bRec;
    }

    public String getsRecTime() {
        return sRecTime;
    }

    public void setsRecTime(String sRecTime) {
        this.sRecTime = sRecTime;
    }

    public String getsComPort() {
        return sComPort;
    }

    public void setsComPort(String sComPort) {
        this.sComPort = sComPort;
    }

    public byte[] getBuffer() {
        return mBuffer;
    }

    public void setBuffer(byte[] buffer) {
        mBuffer = buffer;
    }

    public List<String> getDatas() {

        List<String> list = new ArrayList<>();

        for (int i = 0; i < mBuffer.length; i++) {
            list.add(mBuffer[i] + "");
        }

        return list;
    }

}
