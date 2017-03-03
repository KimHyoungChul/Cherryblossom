package commax.wallpad.cctvview;

import com.commax.nubbyj.device.camera.CameraClient;

public class CameraInfo {

    private int cameraId_;
    private String ip_;
    private String name_;
    private String id_;
    private String password_;
    private String rtspUrl_;
    private int streamNo_;
    private CameraClient cameraClient_;

    public CameraInfo(CameraClient cameraClient, int cameraId, String ip, String name, String id, String password, String rtspUrl, int streamNo) {
        cameraId_ = cameraId;
        ip_ = ip;
        name_ = name;
        id_ = id;
        password_ = password;
        rtspUrl_ = rtspUrl;
        streamNo_ = streamNo;
        cameraClient_ = cameraClient;
    }

    public int getCameraId() {
        return cameraId_;
    }

    public String getIp() {
        return ip_;
    }

    public String getName() {
        return name_;
    }

    public String getId() {
        return id_;
    }

    public String getPassword() {
        return password_;
    }

    public String getRtspUrl() {
        return rtspUrl_;
    }

    public int getStreamNo() {
        return streamNo_;
    }

    public CameraClient getCameraClient() {
        return cameraClient_;
    }
}
