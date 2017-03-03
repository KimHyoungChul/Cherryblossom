package com.commax.iphomiot.doorcall.application;

import com.commax.cmxua.LobbyController;
import com.commax.nubbyj.network.onvif.RelayController;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DoorOnvifReqThread extends Thread {
    private class ReqItem {
        public String ip_;
        public int port_;
        public String userId_;
        public String userPass_;

        public void execute() {
        }
    }

    private class OnvifRealyOnWithAutoIdle extends ReqItem {
        @Override
        public void execute() {
            RelayController relayController = new RelayController(ip_, port_);
            try {
                if (!relayController.createDeviceService(userId_, userPass_))
                    throw new Exception();

                if (!relayController.setRealy(true))
                    throw new Exception();

                if (actionTarget_ != null)
                    actionTarget_.onResultDoorOpen(true);

                Thread.sleep(5000);
                relayController.setRealy(false);

                if (actionTarget_ != null)
                    actionTarget_.onResultDoorOpen(false);
            }
            catch (Exception e) {
                if (actionTarget_ != null)
                    actionTarget_.onResultDoorOpen(false);
            }
        }
    }

    private class LobbyOpen extends ReqItem {
        @Override
        public void execute() {
            LobbyController lobbyController = new LobbyController();
            int ret = lobbyController.openDoor(ip_);
            if (actionTarget_ != null)
                actionTarget_.onResultDoorOpen((ret == 0));
        }
    }

    public interface DoorOnvifReqThreadAction {
        void onResultDoorOpen(boolean ret);
    }

    private Queue<ReqItem> reqQueue_ = new ConcurrentLinkedQueue<>();
    private Lock lock_ = new ReentrantLock();
    private final Condition lockSignal_ = lock_.newCondition();
    private boolean terminated_ = false;
    private DoorOnvifReqThreadAction actionTarget_ = null;

    private void signal() {
        lock_.lock();
        try {
            lockSignal_.signal();
        }
        finally {
            lock_.unlock();
        }
    }

    private void waitSignal() {
        lock_.lock();
        try {
            try {
                lockSignal_.await();
            }
            catch (InterruptedException e) {
            }
        }
        finally {
            lock_.unlock();
        }
    }

    private void processReq(OnvifRealyOnWithAutoIdle reqItem) {
        RelayController relayController = new RelayController(reqItem.ip_, reqItem.port_);
        try {
            if (!relayController.createDeviceService(reqItem.userId_, reqItem.userPass_))
                throw new Exception();

            if (!relayController.setRealy(true))
                throw new Exception();

            if (actionTarget_ != null)
                actionTarget_.onResultDoorOpen(true);

            Thread.sleep(5000);
            relayController.setRealy(false);

            if (actionTarget_ != null)
                actionTarget_.onResultDoorOpen(false);
        }
        catch (Exception e) {
            if (actionTarget_ != null)
                actionTarget_.onResultDoorOpen(false);
        }
    }
/*
    private void processReq(LobbyOpen reqItem) {
    }
*/
    public DoorOnvifReqThread(DoorOnvifReqThreadAction actionTarget) {
        super();
        actionTarget_ = actionTarget;
    }

    public void terminate() {
        terminated_ = true;
    }

    public void openDoorWithOnvif(String ip, int port, String userId, String userPass) {
        OnvifRealyOnWithAutoIdle reqItem = new OnvifRealyOnWithAutoIdle();
        reqItem.ip_ = ip;
        reqItem.port_ = port;
        reqItem.userPass_ = userPass;
        reqItem.userId_ = userId;

        reqQueue_.add(reqItem);
        signal();
    }

    public void openDoorWithLobby(String ip) {
        LobbyOpen reqItem = new LobbyOpen();
        reqItem.ip_ = ip;

        reqQueue_.add(reqItem);
        signal();
    }

    @Override
    public void run() {
        while (!terminated_) {
            waitSignal();
            if (terminated_)
                break;

            ReqItem reqItem = reqQueue_.poll();
            if (reqItem == null)
                continue;

            reqItem.execute();
        }
    }
}
