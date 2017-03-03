package com.commax.wirelesssetcontrol.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;

import com.commax.wirelesssetcontrol.CommaxDevice;
import com.commax.wirelesssetcontrol.FileEx;
import com.commax.wirelesssetcontrol.NameSpace;
import com.commax.wirelesssetcontrol.R;
import com.commax.wirelesssetcontrol.Status;
import com.commax.wirelesssetcontrol.touchmirror.view.tools.BitmapTool;

/**
 * Created by Shinsung on 2017-02-15.
 */
public class ResourceManager {
    static final String TAG = "ResourceManager";


    //TODO Drawable
    public static Drawable getRoomBackgroundResource(String key, Context context){
        Drawable drawable = null;
        switch (key) {
            case "0": drawable = BitmapTool.copy(context.getApplicationContext(), R.mipmap.bg_home_img_1); break;
            case "1": drawable = BitmapTool.copy(context.getApplicationContext(), R.mipmap.bg_home_img_1); break;
            case "2": drawable = BitmapTool.copy(context.getApplicationContext(), R.mipmap.bg_home_img_2); break;
            case "3": drawable = BitmapTool.copy(context.getApplicationContext(), R.mipmap.bg_home_img_3); break;
            case "4": drawable = BitmapTool.copy(context.getApplicationContext(), R.mipmap.bg_home_img_4); break;
            case "5": drawable = BitmapTool.copy(context.getApplicationContext(), R.mipmap.bg_home_img_5); break;
            case "6": drawable = BitmapTool.copy(context.getApplicationContext(), R.mipmap.bg_home_img_6); break;
            case "7": drawable = BitmapTool.copy(context.getApplicationContext(), R.mipmap.bg_home_img_7); break;
        }

        if (drawable==null){
            FileEx fileEx = new FileEx();
            Bitmap bitmap = fileEx.findImage(key, 800, 480);
            if (bitmap!=null) {
                drawable = BitmapTool.copy(context.getApplicationContext(), bitmap);
            }
        }

        return drawable;
    }

    public static ResourceData getDeviceResId(String status, String deviceType) {
        ResourceData resData = new ResourceData();
        resData.arg1 = status;

        if (deviceType != null) {
            if (deviceType.equalsIgnoreCase(CommaxDevice.LIGHT)) {
                if (status.equalsIgnoreCase(Status.ON))
                    resData.arg2 = R.mipmap.ic_sethome_light;
                else if (status.equalsIgnoreCase(Status.OFF))
                    resData.arg2 = R.mipmap.ic_sethome_light_off;
                else {
                    resData.arg1 = Status.UNKNOWN;
                    resData.arg2 = R.mipmap.ic_sethome_light_off;
                }
            } else if (deviceType.equalsIgnoreCase(CommaxDevice.DIMMER)) {
                if (status.equalsIgnoreCase(Status.ON))
                    resData.arg2 = R.mipmap.ic_sethome_light;
                else if (status.equalsIgnoreCase(Status.OFF))
                    resData.arg2 = R.mipmap.ic_sethome_light_off;
                else {
                    resData.arg1 = Status.UNKNOWN;
                    resData.arg2 = R.mipmap.ic_sethome_light_off;
                }
            } else if (deviceType.equalsIgnoreCase(CommaxDevice.SMART_PLUG)) {
                if (status.equalsIgnoreCase(Status.ON))
                    resData.arg2 = R.mipmap.ic_sethome_smartplug;
                else if (status.equalsIgnoreCase(Status.OFF))
                    resData.arg2 = R.mipmap.ic_sethome_smartplug_off;
                else {
                    resData.arg1 = Status.UNKNOWN;
                    resData.arg2 = R.mipmap.ic_sethome_smartplug_off;
                }
            } else if (deviceType.equalsIgnoreCase(CommaxDevice.GAS_LOCK)) {
                if (status.equalsIgnoreCase(Status.UNLOCK)) {
                    resData.arg2 = R.mipmap.ic_sethome_gas_open;
                } else if (status.equalsIgnoreCase(Status.LOCK)) {
                    resData.arg2 = R.mipmap.ic_sethome_gas_close_off;
                } else {
                    resData.arg1 = Status.UNKNOWN;
                    resData.arg2 = R.mipmap.ic_sethome_gas_close_off;
                }
            } else if (deviceType.equalsIgnoreCase(CommaxDevice.MAIN_SWITCH)) {
                if (status.equalsIgnoreCase(Status.ON)) {
                    resData.arg2 = R.mipmap.ic_sethome_all_light;
                } else if (status.equalsIgnoreCase(Status.OFF)) {
                    resData.arg2 = R.mipmap.ic_sethome_all_light_off;
                } else {
                    resData.arg1 = Status.UNKNOWN;
                    resData.arg2 = R.mipmap.ic_sethome_all_light_off;
                }
            } else if (deviceType.equalsIgnoreCase(CommaxDevice.CURTAIN)) {
                if (status.equalsIgnoreCase(Status.ON)) {
                    resData.arg2 = R.mipmap.ic_sethome_curtain_open;
                } else if (status.equalsIgnoreCase(Status.OFF)) {
                    resData.arg2 = R.mipmap.ic_sethome_curtain_close_off;
                } else {
                    resData.arg1 = Status.UNKNOWN;
                    resData.arg2 = R.mipmap.ic_sethome_curtain_close_off;
                }
            } else if (deviceType.equalsIgnoreCase(CommaxDevice.FCU)) {
                if (status.equalsIgnoreCase(Status.ON)) {
                    resData.arg2 = R.mipmap.ic_sethome_heat_cool;
                } else if (status.equalsIgnoreCase(Status.OFF)) {
                    resData.arg2 = R.mipmap.ic_sethome_heat_cool_off;
                } else {
                    resData.arg1 = Status.UNKNOWN;
                    resData.arg2 = R.mipmap.ic_sethome_heat_cool_off;
                }
            } else if (deviceType.equalsIgnoreCase(CommaxDevice.BOILER)) {
                if (status.equalsIgnoreCase(Status.HEAT)) {
                    resData.arg2 = R.mipmap.ic_sethome_heat;
                } else if (status.equalsIgnoreCase(Status.OFF)) {
                    resData.arg2 = R.mipmap.ic_sethome_heat_off;
                } else if (status.equalsIgnoreCase(Status.COOL)) {
                    resData.arg2 = R.mipmap.ic_sethome_heat;
                } else if (status.equalsIgnoreCase(Status.AUTO)) {
                    resData.arg2 = R.mipmap.ic_sethome_heat;
                } else if (status.equalsIgnoreCase(Status.FAN)) {
                    resData.arg2 = R.mipmap.ic_sethome_heat;
                } else if (status.equalsIgnoreCase(Status.DRY)) {
                    resData.arg2 = R.mipmap.ic_sethome_heat;
                } else if (status.equalsIgnoreCase(Status.MOIST)) {
                    resData.arg2 = R.mipmap.ic_sethome_heat;
                } else if (status.equalsIgnoreCase(Status.ENERGYHEAT)) {
                    resData.arg2 = R.mipmap.ic_sethome_heat;
                } else if (status.equalsIgnoreCase(Status.ENERGYCOOL)) {
                    resData.arg2 = R.mipmap.ic_sethome_heat;
                } else if (status.equalsIgnoreCase(Status.FULLPOWER)) {
                    resData.arg2 = R.mipmap.ic_sethome_heat;
                } else if (status.equalsIgnoreCase(Status.AWAYON)) {
                    resData.arg2 = R.mipmap.ic_sethome_heat_off;
                } else if (status.equalsIgnoreCase(Status.RESERVATION)) {
                    resData.arg2 = R.mipmap.ic_sethome_heat;
                } else if (status.equalsIgnoreCase(Status.BATH)) {
                    resData.arg2 = R.mipmap.ic_sethome_heat;
                } else if (status.equalsIgnoreCase(Status.HEATWATER)) {
                    resData.arg2 = R.mipmap.ic_sethome_heat;
                } else if (status.equalsIgnoreCase(Status.H24RESERVATION)) {
                    resData.arg2 = R.mipmap.ic_sethome_heat;
                } else {
                    resData.arg1 = Status.UNKNOWN;
                    resData.arg2 = R.mipmap.ic_sethome_heat_off;
                }
            } else if (deviceType.equalsIgnoreCase(CommaxDevice.STANDBY_POWER)) {
                if (status.equalsIgnoreCase(Status.ON)) {
                    resData.arg2 = R.mipmap.ic_sethome_stpower;
                } else if (status.equalsIgnoreCase(Status.OFF)) {
                    resData.arg2 = R.mipmap.ic_sethome_stpower_off;
                } else {
                    resData.arg1 = Status.UNKNOWN;
                    resData.arg2 = R.mipmap.ic_sethome_stpower_off;
                }
            } else if (deviceType.equalsIgnoreCase(CommaxDevice.FAN_SYSTEM)) {
                if (status.equalsIgnoreCase(Status.OFF)) {
                    resData.arg2 = R.mipmap.ic_sethome_ventilation_off;
                } else if (status.equalsIgnoreCase(Status.ON)) {
                    resData.arg2 = R.mipmap.ic_sethome_ventilation;
                } else {
                    resData.arg1 = Status.UNKNOWN;
                    resData.arg2 = R.mipmap.ic_sethome_ventilation_off;
                }
            } else {
                if (status.equalsIgnoreCase(Status.ON)) {
                    resData.arg2 = R.mipmap.ic_sethome_power;
                } else if (status.equalsIgnoreCase(Status.OFF)) {
                    resData.arg2 = R.mipmap.ic_sethome_power_off;
                } else {
                    resData.arg1 = Status.UNKNOWN;
                    resData.arg2 = R.mipmap.ic_sethome_power_off;
                }
            }

        }
        return resData;
    }

    public static int getDeviceBgResId(String status) {
        if (status.equalsIgnoreCase(Status.ON)) {
            return R.mipmap.btn_home_setbg_s;
        } else if (status.equalsIgnoreCase(Status.HEAT)) {
            return R.mipmap.btn_home_setbg_s;
        } else if (status.equalsIgnoreCase(Status.UNLOCK)) {
            return R.mipmap.btn_home_setbg_s;
        } else if (status.equalsIgnoreCase(Status.LOCK)) {
            return R.mipmap.btn_home_setbg_n;
        } else if (status.equalsIgnoreCase(Status.OFF)) {
            return R.mipmap.btn_home_setbg_n;
        } else if (status.equalsIgnoreCase(Status.COOL)) {
            return R.mipmap.btn_home_setbg_s;
        } else if (status.equalsIgnoreCase(Status.AUTO)) {
            return R.mipmap.btn_home_setbg_s;
        } else if (status.equalsIgnoreCase(Status.FAN)) {
            return R.mipmap.btn_home_setbg_s;
        } else if (status.equalsIgnoreCase(Status.DRY)) {
            return R.mipmap.btn_home_setbg_s;
        } else if (status.equalsIgnoreCase(Status.MOIST)) {
            return R.mipmap.btn_home_setbg_s;
        } else if (status.equalsIgnoreCase(Status.ENERGYHEAT)) {
            return R.mipmap.btn_home_setbg_s;
        } else if (status.equalsIgnoreCase(Status.ENERGYCOOL)) {
            return R.mipmap.btn_home_setbg_s;
        } else if (status.equalsIgnoreCase(Status.FULLPOWER)) {
            return R.mipmap.btn_home_setbg_s;
        } else if (status.equalsIgnoreCase(Status.AWAYON)) {
            return R.mipmap.btn_home_setbg_n;
        } else if (status.equalsIgnoreCase(Status.RESERVATION)) {
            return R.mipmap.btn_home_setbg_s;
        } else if (status.equalsIgnoreCase(Status.BATH)) {
            return R.mipmap.btn_home_setbg_s;
        } else if (status.equalsIgnoreCase(Status.HEATWATER)) {
            return R.mipmap.btn_home_setbg_s;
        } else if (status.equalsIgnoreCase(Status.H24RESERVATION)) {
            return R.mipmap.btn_home_setbg_s;
        } else
            return R.mipmap.btn_home_setbg_n;
    }
}
