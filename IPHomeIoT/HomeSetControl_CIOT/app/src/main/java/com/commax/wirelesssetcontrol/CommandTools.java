package com.commax.wirelesssetcontrol;

import android.os.RemoteException;
import android.text.TextUtils;

import com.commax.wirelesssetcontrol.device.SubSort;
import com.commax.wirelesssetcontrol.touchmirror.view.data.DeviceIconData;

import org.json.JSONArray;
import org.json.JSONObject;

public class CommandTools {

    public CommandTools() {

    }

    static final String TAG = "JsonTools";

    public void sendCommand(String raw){
        Log.d(TAG, "command receive");

        try {
            if (MainActivity.getRemoteConnection().iadpter != null) {
                try {
                    int result = MainActivity.getRemoteConnection().iadpter.sendToPAM(raw);
                    Log.d(TAG, "result" + result);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String JsonStringAddSort(String rootUuid, String rootDevice, String subUuid, String value, String sort){

        String result = "";

        try {
            String cmd = "set";
            String[] object = new String[2];
            object[0] = rootUuid;
            object[1] = rootDevice;

            String[] subDevice = new String[4];
            subDevice[0] = subUuid;
            subDevice[1] = "set";
            subDevice[2] = value;
            subDevice[3] = sort;

            try {
                result = makeJsonAddSort(cmd, object, subDevice);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        Log.d("JsonString", "result : " + result);
        return result;
    }

    public String makeJsonAddSort(String str, String[] str2, String[] str3){

        String result = "";

        try{

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("command", str);
            }catch (Exception e){
                e.printStackTrace();
            }

            JSONObject subDeviceObject = new JSONObject();
            try {
                subDeviceObject.put("subUuid", str3[0]);
                subDeviceObject.put("funcCommand", str3[1]);
                subDeviceObject.put("value", str3[2]);
                subDeviceObject.put("sort", str3[3]);
            }catch (Exception e){
                e.printStackTrace();
            }

            JSONArray subDeviceArray = new JSONArray();
            try {
                subDeviceArray.put(subDeviceObject);
            }catch (Exception e){
                e.printStackTrace();
            }

            //object array

            JSONObject object = new JSONObject();
            try {
                object.put("rootUuid", str2[0]);
                object.put("rootDevice", str2[1]);
                object.put("subDevice", subDeviceArray);
            }catch (Exception e){
                e.printStackTrace();
            }

            try{
                jsonObject.put("object",object);
            }catch (Exception e){
                e.printStackTrace();
            }


            result = jsonObject.toString();
//            android.util.Log.d(TAG, "json : " + result);

        }catch (Exception e){
            e.printStackTrace();
        }

        return result;
    }
    
    public String getAfterStatus(String crt_status, String device_type){
        
        String after_status = "";

        try {
            if (crt_status.equalsIgnoreCase(Status.LOCK)) {
                after_status = Status.UNLOCK;
            } else if (crt_status.equalsIgnoreCase(Status.UNLOCK)) {
                after_status = Status.LOCK;
            } else if (crt_status.equalsIgnoreCase(Status.ON)) {
                after_status = Status.OFF;
            } else if (crt_status.equalsIgnoreCase(Status.OFF)) {
                if(device_type.equalsIgnoreCase(CommaxDevice.BOILER)){
                    after_status = Status.HEAT;
                }else {
                    after_status = Status.ON;
                }
            } else if (crt_status.equalsIgnoreCase(Status.HEAT)) {
                after_status = Status.OFF;
            } else if (crt_status.equalsIgnoreCase(Status.COOL)) {
                after_status = Status.OFF;
            } else if (crt_status.equalsIgnoreCase(Status.AUTO)) {
                after_status = Status.OFF;
            } else if (crt_status.equalsIgnoreCase(Status.FAN)) {
                after_status = Status.OFF;
            } else if (crt_status.equalsIgnoreCase(Status.DRY)) {
                after_status = Status.OFF;
            } else if (crt_status.equalsIgnoreCase(Status.MOIST)) {
                after_status = Status.OFF;
            } else if (crt_status.equalsIgnoreCase(Status.ENERGYHEAT)) {
                after_status = Status.OFF;
            } else if (crt_status.equalsIgnoreCase(Status.ENERGYCOOL)) {
                after_status = Status.OFF;
            } else if (crt_status.equalsIgnoreCase(Status.FULLPOWER)) {
                after_status = Status.OFF;
            } else if (crt_status.equalsIgnoreCase(Status.AWAYON)) {
                after_status = Status.HEAT;
            } else if (crt_status.equalsIgnoreCase(Status.RESERVATION)) {
                after_status = Status.OFF;
            } else if (crt_status.equalsIgnoreCase(Status.BATH)) {
                after_status = Status.OFF;
            } else if (crt_status.equalsIgnoreCase(Status.HEATWATER)) {
                after_status = Status.OFF;
            } else if (crt_status.equalsIgnoreCase(Status.H24RESERVATION)) {
                after_status = Status.OFF;
            } else {
                after_status = Status.UNKNOWN;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        
        return after_status;
    }

    protected void setStatus(DeviceIconData data, String on_off){

        String rootDevice = "";
//        set_command_ok=false;
//        view_removed=false;
//
//        if(!doing_control) {
        android.util.Log.d(TAG, "setStatus : " + data.getControlType() + " setting to : " + on_off);
//            doing_control=true;
        try {

            switch (data.getControlType()) {
                case SubSort.SWITCH_BINARY:
                    rootDevice = RootDevice.SWITCH;
                    try {
                        if (on_off.equalsIgnoreCase(Status.ON)) {
                            setStatusAddSort(data.getRootUuid(), rootDevice, data.getMain_subUuid(), "on", data.getControlType());
//                                checkResult();

                        } else if (on_off.equalsIgnoreCase(Status.OFF)) {
                            setStatusAddSort(data.getRootUuid(), rootDevice, data.getMain_subUuid(), "off", data.getControlType());
//                                checkResult();
//                            } else {
//                                doing_control=false;
//                                makeToastNeedSync();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                    case SubSort.THERMOSTAT_MODE:
                        rootDevice = RootDevice.BOILER;
                        if(data.getDeviceType().equalsIgnoreCase(CommaxDevice.BOILER)) {
                            try {
                                if (on_off.equalsIgnoreCase(Status.HEAT)) {
                                    setStatusAddSort(data.getRootUuid(), rootDevice, data.getMain_subUuid(), "on", data.getControlType());

                                } else if (on_off.equalsIgnoreCase(Status.OFF)) {
                                    setStatusAddSort(data.getRootUuid(), rootDevice, data.getMain_subUuid(), "off", data.getControlType());
//                                } else {
//                                    doing_control = false;
//                                    makeToastNeedSync();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                if (on_off.equalsIgnoreCase(Status.ON)) {
                                    setStatusAddSort(data.getRootUuid(), rootDevice, data.getMain_subUuid(), "on", data.getControlType());

                                } else if (on_off.equalsIgnoreCase(Status.OFF)) {
                                    setStatusAddSort(data.getRootUuid(), rootDevice, data.getMain_subUuid(), "off", data.getControlType());
//                                } else {
//                                    doing_control = false;
//                                    makeToastNeedSync();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        break;

                    case SubSort.GAS_LOCK:
                        rootDevice = RootDevice.LOCK;
                        try {
                            if (on_off.equalsIgnoreCase(Status.UNLOCK)) {
//                                makeToastOnlyLocking();

                            } else if (on_off.equalsIgnoreCase(Status.LOCK)) {
                                setStatusAddSort(data.getRootUuid(), rootDevice, data.getMain_subUuid(), "lock", data.getControlType());
//                                checkResult();
//                            } else {
//                                doing_control=false;
//                                makeToastNeedSync();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void setStatusAddSort(String rootUuid, String rootDevice, String subUuid, String value, String sort){

        try {
            Log.d(TAG, "setStatus : rootUuid = " + rootUuid + " / rootDevice : " + rootDevice + " / subUuid : " + subUuid + " / value : " + value);
            if ((rootUuid != null) && (rootDevice != null) && (subUuid != null) && (value != null)) {
                if ((!TextUtils.isEmpty(rootUuid)) && (!TextUtils.isEmpty(rootDevice)) && (!TextUtils.isEmpty(subUuid)) && (!TextUtils.isEmpty(value))) {
                    sendCommand(JsonStringAddSort(rootUuid, rootDevice, subUuid, value, sort));
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
