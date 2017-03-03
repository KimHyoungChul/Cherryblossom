package commax.wallpad.cctvview;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

public class CameraListAdapter extends BaseAdapter {
    private ArrayList<CameraInfo> listViewItemList_;
    private ArrayList<CameraInfo> listFavorites_;
    private Context context_;
    private int previousPosition_ = -1;
    private int nowPosition_ = -1;
    private CCTVDBAccessManager cctvdbAccessManager_;

    public CameraListAdapter(Context context, CCTVDBAccessManager cctvdbAccessManager) {
        context_ = context;
        listViewItemList_ = new ArrayList<CameraInfo>();
        cctvdbAccessManager_ = cctvdbAccessManager;
        listFavorites_ = cctvdbAccessManager_.getFavoritesList();
    }

    public int setFavoritesList(String selectIp) {
        this.notifyDataSetChanged();
        if (listViewItemList_ != null) {
            if (!listViewItemList_.isEmpty())
                listViewItemList_.clear();
        }

        if (listFavorites_ != null) {
            if (!listFavorites_.isEmpty())
                listFavorites_.clear();
        }

        listFavorites_ = cctvdbAccessManager_.getFavoritesList();
        listViewItemList_.addAll(listFavorites_);
        nowPosition_ = getSelectPosition(selectIp);
        return nowPosition_;
       }

    private int getSelectPosition(String selectIp) {
        int position = 0;

        for (int i = 0; i< listViewItemList_.size(); i++) {
            if (listViewItemList_.get(i).getIp().equals(selectIp))
                position = i;
        }
        return position;
    }

    public int setCCTVList(ArrayList<CameraInfo> items, String selectIp) {
        this.notifyDataSetChanged();
        if (listViewItemList_ != null) {
            if (!listViewItemList_.isEmpty())
                listViewItemList_.clear();
        }

        listViewItemList_.addAll(items);
        nowPosition_ = getSelectPosition(selectIp);
        return nowPosition_;
    }

    public int getListViewItemSize() {
        return listViewItemList_.size();
    }

    public int getCameraId() {
        if (!listViewItemList_.isEmpty())
            return listViewItemList_.get(nowPosition_).getCameraId();
        else
            return -1;
    }

    public void setPositionSelected(int position) {
        this.notifyDataSetChanged();
        if (previousPosition_ == -1)
            previousPosition_ = position;
        else
            previousPosition_ = nowPosition_;

        if (nowPosition_ == -1)
            nowPosition_ = previousPosition_;
        else
            nowPosition_ = position;
    }

    public String getSelectedIp(int position) {
        if (listViewItemList_ == null)
            return null;

        if (listViewItemList_.isEmpty())
            return null;

        return listViewItemList_.get(position).getIp();
    }

    @Override
    public int getCount() {
        return listViewItemList_.size() ;
    }

    @Override
    public long getItemId(int position) {
        return position ;
    }

    @Override
    public Object getItem(int position) {
        return listViewItemList_.get(position) ;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View cameraListView = convertView;
        if (cameraListView == null) {
            LayoutInflater layoutInflater = (LayoutInflater)context_.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            cameraListView = layoutInflater.inflate(R.layout.item_cameralist, null);
        }

        CameraInfo cameraInfo = listViewItemList_.get(position);
        final ImageView favoriteImage = (ImageView) cameraListView.findViewById(R.id.favoriteImage);
        TextView cameraName = (TextView) cameraListView.findViewById(R.id.cameraName);

        if (cameraInfo != null) {
            if (cameraName != null){
                cameraName.setText(cameraInfo.getName());
            }
        }

        cameraListView.setBackgroundColor(Color.WHITE);
        cameraName.setTextColor(Color.BLACK);

        if (nowPosition_ == position) {
            cameraListView.setBackgroundColor(Color.argb(255, 107, 117, 199));
            cameraName.setTextColor(Color.WHITE);
        }

  /*      if (cctvdbAccessManager_.isFavoriteCamera(cameraInfo.getIp()))
            favoriteImage.setImageDrawable(context_.getDrawable(R.mipmap.btn_favorite_small_on));
        else
            favoriteImage.setImageDrawable(context_.getDrawable(R.mipmap.btn_favorite_small_off));

        favoriteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraInfo cameraInfo = listViewItemList_.get(position);
                if (!cctvdbAccessManager_.isFavoriteCamera(cameraInfo.getIp())) {
                    favoriteImage.setImageDrawable(context_.getDrawable(R.mipmap.btn_favorite_small_on));
                    cctvdbAccessManager_.insert(cameraInfo.getCameraId(), cameraInfo.getIp(), cameraInfo.getName(), cameraInfo.getId(), cameraInfo.getPassword(), cameraInfo.getRtspUrl(), cameraInfo.getStreamNo());
                }
                else {
                    favoriteImage.setImageDrawable(context_.getDrawable(R.mipmap.btn_favorite_small_off));
                    cctvdbAccessManager_.delete(cameraInfo.getIp().toString());
                }
            }
        });
*/
        return cameraListView;
    }
}
