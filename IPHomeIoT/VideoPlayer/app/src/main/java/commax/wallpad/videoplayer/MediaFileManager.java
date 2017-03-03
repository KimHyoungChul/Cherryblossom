package commax.wallpad.videoplayer;

import android.media.MediaMetadata;
import android.media.MediaMetadataRetriever;
import android.media.RemoteController;
import android.os.Environment;
import android.util.Log;

import com.commax.nubbyj.media.demuxer.FFMPEGDemuxer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.StringTokenizer;

interface ITerminatedThread {
    public void isTerminate();
}

public class MediaFileManager {

    private static MediaFileManager instance_ = null;
    private ArrayList<fileInfo> fileList_ = null;
    private String storageRoot_ = null;
    private File file;
    private File[] files;
    private ITerminatedThread iTerminatedThread_;

    class SetFileInfo extends Thread {
        private boolean isStart;
        String filePath_;
        String fileName_;
        int fileId_;
        String from = "";
        String mode = "";
        String type = "";

        SetFileInfo(String filePath) {
            filePath_ = filePath;
        }

        @Override
        public void run() {
            while (!isStart) {

                file = new File(filePath_);
                files = file.listFiles();
                if (files == null)
                    terminate();

                int fileId = 0;
                Arrays.sort(files, new Comparator<File>() {
                    String sort1 = "";
                    String sort2 = "";

                    @Override
                    public int compare(File o1, File o2) {
                        sort1 = o1.lastModified()+"";
                        sort2 = o2.lastModified()+"";
                        return sort1.compareTo(sort2);
                    }
                });

                for (int i=files.length-1; i >-1; i--) {
                    if(files[i].isDirectory()) {
                    } else {
                        separationByDate(files[i].getName(), fileId);
                        fileId++;
                    }
                }
                terminate();
            }
        }

        public void separationByDate(String fileName, int fileId) {
            FFMPEGDemuxer ffmpegDemuxer = new FFMPEGDemuxer();
            fileName_ = fileName;
            fileId_ = fileId;
            String filePath = storageRoot_ + "/" + fileName_;


            String fromOtherParty = ffmpegDemuxer.readComment(filePath);
            StringTokenizer sectionTokenizer = new StringTokenizer(fromOtherParty, ",");
            String[] otherParty = new String[6];

            if (!fromOtherParty.isEmpty()) {
                int count = 0;
                while (sectionTokenizer.hasMoreTokens()) {
                    String data = sectionTokenizer.nextToken();
                    StringTokenizer partyTokenizer = new StringTokenizer(data, ":");

                    while (partyTokenizer.hasMoreTokens()) {
                        String data1 = partyTokenizer.nextToken();
                        otherParty[count] = data1;
                        count++;
                    }
                }
                from = otherParty[1];
                mode = otherParty[3];
                type = otherParty[5];
            }
            ffmpegDemuxer.closeFile();

            StringTokenizer stringTokenizer = new StringTokenizer(fileName_, ".");
            String dateS = stringTokenizer.nextToken();

            long dateL = 0;
            try {
                dateL = Long.parseLong(dateS);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            if (dateL == 0)
                return;

            Calendar cal1 = Calendar.getInstance();
            cal1.setTimeInMillis(dateL);

            Calendar standardDate = Calendar.getInstance();
            standardDate.set(cal1.get(Calendar.YEAR), cal1.get(Calendar.MONTH), cal1.get(Calendar.DATE));

            Date today = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(today);

            int count = 0;
            while (!standardDate.after(calendar)) {
                count++;
                standardDate.add(Calendar.DATE, 1);
            }

            if (count < 2)
                instance_.addFile(fileId_, 0, fileName_, from, mode, type);

            else if (1 < count && count < 9 )
                instance_.addFile(fileId_, 1, fileName_, from, mode, type);

            else
                instance_.addFile(fileId_, 2, fileName_, from, mode, type);
        }
        public void terminate() {
            isStart = true;
            instance_.iTerminatedThread_.isTerminate();
        }
    }

    public static MediaFileManager getInstance() {
        if (instance_ == null)
            instance_ = new MediaFileManager();

        return instance_;
    }

    class fileInfo {
        int fileId_;
        int sectionId_;
        String from_;
        String mode_;
        String type_;
        String fileName_;
    }

    public void getFileList(String filePath, ITerminatedThread iTerminatedThread) {
        iTerminatedThread_ = iTerminatedThread;
        SetFileInfo setFileInfo = new SetFileInfo(filePath);
        setFileInfo.start();
    }

    public MediaFileManager() {
        fileList_ = new ArrayList<fileInfo>();
    }

    public void addFile(int fileId, int sectionId, String fileName, String from, String mode, String type) {
        fileInfo fileinfo = new fileInfo();
        fileinfo.fileId_ = fileId;
        fileinfo.sectionId_ = sectionId;
        fileinfo.fileName_ = fileName;
        fileinfo.from_ = from;
        fileinfo.mode_ = mode;
        fileinfo.type_ = type;

        fileList_.add(fileinfo);
    }

    public void clearFile() {
        if (fileList_ != null)
            fileList_.clear();
    }

    public int size() {
        int size = 0;
        if (fileList_ == null)
            return 0;

        size = fileList_.size();

        return size;
    }

    public ArrayList<fileInfo> getTodayFileInfoList() {
        if (fileList_ == null)
            return null;

        ArrayList<fileInfo> fileNameList = new ArrayList<fileInfo>();
        for (int i= 0; i< fileList_.size(); i++) {
            if (fileList_.get(i).sectionId_ == 0)
                fileNameList.add(fileList_.get(i));
        }

        return fileNameList;
    }

    public ArrayList<fileInfo> getSevenDaysFileInfoList() {
        if (fileList_ == null)
            return null;

        ArrayList<fileInfo> fileNameList = new ArrayList<fileInfo>();
        for (int i= 0; i< fileList_.size(); i++) {
            if (fileList_.get(i).sectionId_ == 1)
                fileNameList.add(fileList_.get(i));
        }

        return fileNameList;
    }

    public ArrayList<fileInfo> getOldDaysFileInfoList() {
        if (fileList_ == null)
            return null;

        ArrayList<fileInfo> fileNameList = new ArrayList<fileInfo>();
        for (int i= 0; i< fileList_.size(); i++) {
            if (fileList_.get(i).sectionId_ == 2)
                fileNameList.add(fileList_.get(i));
        }

        return fileNameList;
    }

    public String getFileName(int position) {
        String fileName = null;
        if (fileList_ == null)
            return null;

        for (int i=0; i<fileList_.size(); i++) {
            if (fileList_.get(i).fileId_ == position) {
                fileName = fileList_.get(i).fileName_;
                break;
            }
        }

        return fileName;
    }

    public int getPosition(String fileName) {
        if (fileList_ == null)
            return -1;

        int filePosition = -1;
        for (int i=0; i< fileList_.size(); i++) {
            String getFileName = fileList_.get(i).fileName_;
            if (getFileName.equals(fileName)) {
                filePosition = i;
                return filePosition;
            }
            else
                continue;
        }

        return filePosition;
    }

    public int getFileId(int filePosition) {
        if (fileList_ == null)
            return -1;

        int fileNum = -1;
  /*      for (int i=0; i< fileList_.size(); i++) {
            if (fileList_.get(i).fileName_.equals(fileName))
                fileNum = fileList_.get(i).fileId_;
        }
        */
        fileNum = fileList_.get(filePosition).fileId_;

        return fileNum;
    }

    public void deleteFromeFilename(String fileName) {
        if (fileList_ == null)
            return;

        for (int i=0; i< fileList_.size(); i++) {
            if (fileList_.get(i).fileName_.equals(fileName))
                fileList_.remove(i);
        }
    }

    public void setStorageRoot(String storageRoot) {
        storageRoot_ = storageRoot;
    }

    public String getStorageRoot() {

        storageRoot_ = ""+Environment.getExternalStorageDirectory().getAbsolutePath() +"/cmxMediaGallery";
        return storageRoot_;
    }

    public boolean isToday(int fileNum) {
        boolean isToday = false;
        if (fileList_ == null)
            return false;

        for (int i=0; i<fileList_.size(); i++) {
            if (fileList_.get(i).fileId_ == fileNum) {
                if (fileList_.get(i).sectionId_ == 0) {
                    isToday =  true;
                    break;
                }
            }
        }
        return isToday;
    }

}
