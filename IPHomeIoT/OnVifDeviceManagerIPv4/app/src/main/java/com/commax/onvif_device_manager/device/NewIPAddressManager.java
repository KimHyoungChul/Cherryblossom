package com.commax.onvif_device_manager.device;

import android.content.Context;

/**
 * 새로운 IP 주소 계산
 * Created by bagjeong-gyu on 2016. 9. 22..
 */

public class NewIPAddressManager {

    public static String getIP(Context context, int deviceCount) {

        //테스트후 삭제하세요
        return "192.168.1.20" + deviceCount;



//        //디바이스에 할당하는 static ip는 10.동수.층수.호수 + 디바이스 번호
//        //슬레이브 월패드와 ip door camera는 discover되는 순으로 2부터 6까지 할당
//        //대부분 ip door camera 1대만 사용할 것임
//        //층수는 지하에 대한 처리 필요!!
//
//        String address = WallpadInfo.getHouseAddress(context);
//
//        //a64보드는 월패드의 동호수가 지정되어 있지 않아서 ""를 리턴
//        if(address.equals("")) {
//
//            //테스트후 삭제하세요!!
//            return "192.168.1.25" + deviceCount;
//
//           //원래 코드
//           // return "";
//        }
//
//        //////////////////////////
//        //테스트후 삭제하세요!!
//        address = "104-106";
//        //////////////////////////
//
//        String[] tokens = address.split("-");
//
//        String dongNo = tokens[0];
//
//        int hoNo = Integer.parseInt(tokens[1]);
//
//        int floorNo = hoNo / 100;
//
//        int roomNo = hoNo % 100;
//
//        if(Integer.parseInt(dongNo) > 255) {
//            dongNo = "255";
//        }
//
//        if(floorNo > 255) {
//            floorNo = 255;
//        }
//
//        if((roomNo*10 + deviceCount) > 255) {
//            roomNo = 25;
//            deviceCount = 5;
//        }
//
//        //////////////////////////
//        //테스트후 삭제하세요!!
//        return "192." + dongNo + "." + floorNo + "." + (roomNo*10 + deviceCount);
//        //////////////////////////
//
//        //return "10." + dongNo + "." + floorNo + "." + (roomNo*10 + deviceCount);
    }

}
