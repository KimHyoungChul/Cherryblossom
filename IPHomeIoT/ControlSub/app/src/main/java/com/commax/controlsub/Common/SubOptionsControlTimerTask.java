package com.commax.controlsub.Common;

/**
 * Created by OWNER on 2016-09-27.
 */

public class SubOptionsControlTimerTask{// implements Runnable{

   /* int rootDeviceIndex;
    int subDeviceIndex;
    String value;

    boolean timerFlag = false;

    //public final int   TIMEOUT      = 10;

    int count = 0;

    @Override
    public void run() {
        // TODO Auto-generated method stub
        while(timerFlag){

            if(count == 600){

                Message message = null;
                message = mEventHandler.obtainMessage();

                if(value == null) {
                    message.what = HANDLE_EVENT_GET_DEVICE_DATA;
                    message.arg1 = rootDeviceIndex;
                    message.arg2 = subDeviceIndex;
                    mEventHandler.sendMessage(message);
                    return;
                }

                message.what = HANDLE_EVENT_SUB_DEVICE_CONTROL;
                message.arg1 = rootDeviceIndex;
                message.arg2 = subDeviceIndex;

                message.obj = value;

                mEventHandler.sendMessage(message);

                break;

            }

            count++;

            LOG.d(TAG, "count : " + count);

            try {
                Thread.sleep(CONTROL_OPTIONS_TIMEOUT);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


        }
    }

    public void setValue(int rootDeviceIndex, int subDeviceIndex, String value){
        this.rootDeviceIndex = rootDeviceIndex;
        this.subDeviceIndex = subDeviceIndex;
        this.value = value;
    }

    public void startThread(){
        timerFlag = true;
        count = 0;
        mSubOptionsThread = new Thread(mSubOptionsControlTimerTask);
        mSubOptionsThread.start();
    }

    public void stopTimer(){

        count = 0;
        timerFlag = false;


        if(mSubOptionsThread != null){

            try {
                mSubOptionsThread.interrupt();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        mSubOptionsThread = null;

    }

*/
}