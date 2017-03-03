package com.commax.wirelesssetcontrol.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.commax.wirelesssetcontrol.AlertDialog;
import com.commax.wirelesssetcontrol.HandlerEvent;
import com.commax.wirelesssetcontrol.MainActivity;
import com.commax.wirelesssetcontrol.SpaceDialog;
import com.commax.wirelesssetcontrol.touchmirror.view.res.CBitmapDrawable;
import com.commax.wirelesssetcontrol.touchmirror.view.tools.BitmapTool;
import com.commax.wirelesssetcontrol.view.MovingSpaceCell;
import com.commax.wirelesssetcontrol.NameSpace;
import com.commax.wirelesssetcontrol.R;
import com.commax.wirelesssetcontrol.view.SpaceCell;
import com.commax.wirelesssetcontrol.SpaceInfo;
import com.commax.wirelesssetcontrol.data.PageData;
import com.commax.wirelesssetcontrol.data.PageDataManager;
import com.commax.wirelesssetcontrol.data.ResourceManager;
import com.commax.wirelesssetcontrol.tools.PublicTools;
import com.commax.wirelesssetcontrol.touchmirror.common.Constants;

import java.util.ArrayList;

public class EditSpaceActivity extends AppCompatActivity {

    String DEBUG_TAG = EditSpaceActivity.class.getSimpleName();
    ArrayList<SpaceInfo> mSpaces;
    GridView gridview;
    FrameLayout lay_move;
    FrameLayout lay_background;
    ImageAdapter adapter;

    SpaceDialog spaceDialog;
    int remove_page;
    boolean visible_delete = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_space);

        PublicTools.hideActionBar(this);
        PublicTools.hideNavigationBar(this);

        TextView bt_ok = (TextView) findViewById(R.id.bt_ok);
        ImageButton bt_back = (ImageButton) findViewById(R.id.bt_back);
        gridview = (GridView) findViewById(R.id.gridview);
        lay_move = (FrameLayout) findViewById(R.id.lay_move);
        lay_background = (FrameLayout) findViewById(R.id.lay_background);

        bt_ok.setOnClickListener(mClick);
        bt_back.setOnClickListener(mClick);

        setAdapter();
        loadSpaces();
        setTouchBackground();
    }

    //해당 페이지의 배경을 그린다
    private void setTouchBackground(){
        try {
            String roomBackground = getIntent().getExtras().getString(Constants.INTENT_KEY_ROOM_IDX);
            Drawable res = ResourceManager.getRoomBackgroundResource(roomBackground, getApplicationContext());

            if (res != null) {
                lay_background.setBackground(BitmapTool.blur(getApplicationContext(), (CBitmapDrawable) res, 15));
            } else {
                res = BitmapTool.copy(getApplicationContext(), R.mipmap.bg_home_img_1);
                lay_background.setBackground(BitmapTool.blur(getApplicationContext(), (CBitmapDrawable) res, 15));
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setAdapter() {
        mSpaces = new ArrayList<>();
        adapter = new ImageAdapter(getApplicationContext());
        try {
            gridview.setAdapter(adapter);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadSpaces(){
        mSpaces.clear();

        for (int i = 0; i< NameSpace.MAX_PAGE; i++){
            try{
                PageData pageData = PageDataManager.getInst().getPageData(i);

                SpaceInfo spaceInfo = new SpaceInfo();
                spaceInfo.index = i;

                if(pageData != null){
                    spaceInfo.name = pageData.name;
                    spaceInfo.icon = pageData.background;
                }else
                    spaceInfo.empty = true;

                mSpaces.add(spaceInfo);

            }catch (Exception e){
                e.printStackTrace();
            }
        }
        checkBottomText();
        adapter.notifyDataSetChanged();
    }

    public Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HandlerEvent.EVENT_HANDLE_ADD_MOVING_SPACE:
                    addMovingSpace(msg.obj, msg.arg1, msg.arg2);
                    break;

                case HandlerEvent.EVENT_HANDLE_CHECK_MOVING_SPACE:
                    checkMovingSpace(msg.obj, msg.arg1, msg.arg2);
                    break;

                case HandlerEvent.EVENT_HANDLE_START_EDIT_SPACE:
                    startEditSpaceActivity(msg.obj);
                    break;

            }
        }
    };

    private void startEditSpaceActivity(Object o){
        try {
            int page = (int)o;

            if (page!=-1) {
                PublicTools.clearNavigationBar(this);
                Intent intent = new Intent(EditSpaceActivity.this, AddSpaceActivity.class);
                intent.putExtra(NameSpace.NUM_PAGES, page);
                intent.putExtra(NameSpace.ACTIVITY_MODE, NameSpace.EDIT_MODE);
                startActivityForResult(intent, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //TODO flow check 필요
        loadSpaces();
    }

    private void addMovingSpace(Object o, int x, int y){
        try {
            MovingSpaceCell movingSpaceCell = (MovingSpaceCell) o;
            movingSpaceCell.setLocation(x, y);
            lay_move.addView((View) o);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void checkMovingSpace(Object o, int x, int y){
        try{
            MovingSpaceCell origin = (MovingSpaceCell) o;

            int space_index = -1;
            int to_index = -1;

            try {
                for (int index = 0; index < mSpaces.size(); index++) {
                    if (mSpaces.get(index).index == origin.getIndex()) {
                        space_index = index;
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }

            try {
                Log.d(DEBUG_TAG, "gridview count " + gridview.getChildCount());

                for (int i = 0; i < gridview.getChildCount(); i++) {
                    SpaceCell spaceCell = (SpaceCell) gridview.getChildAt(i);

                    for (int index = 0; index < mSpaces.size(); index++) {
                        if (mSpaces.get(index).index == spaceCell.getIndex()) {
                            to_index = index;
                        }
                    }

                    int[] location = new int[2];
                    int top = (int) getResources().getDimension(R.dimen.header_height);
                    try {
                        spaceCell.getLocationInWindow(location);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    int x_start = location[0];
                    int y_start = location[1] - top;

                    int x_end = location[0] + spaceCell.getWidth();
                    int y_end = location[1] + spaceCell.getHeight() - top;

                    Log.d(DEBUG_TAG, "location " + i + "= x_start :" + x_start + " y_start :" + y_start + "= x_end :" + x_end + " y_end :" + y_end + " index : " + spaceCell.getIndex());

                    if (spaceCell.getIndex() != -1) {
                        if ((x >= x_start) && (y >= y_start) && (x <= x_end) && (y <= y_end)) {

                            Log.d(DEBUG_TAG, "grid " + space_index + " move to " + to_index);
                            insertList(to_index, space_index);
                            adapter.notifyDataSetChanged();
                            break;
                        }
                    }

                }
            }catch (Exception e){
                e.printStackTrace();
            }

            lay_move.removeView((View) o);
            loadSpaces();

        }catch (Exception e){
            e.printStackTrace();
        }

    }

      View.OnClickListener mClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){

               /* case R.id.bt_ok:
                    saveSpace();
                    finish();
                    break;
*/
                case R.id.bt_back:
                    finish();
                    break;
            }
        }
    };

    class ImageAdapter extends BaseAdapter {
        private Context mContext;

        public ImageAdapter(Context c) {
            mContext = c.getApplicationContext();
        }

        @Override
        public boolean isEnabled(int position) {
            return false;
        }

        public int getCount() {
            return mSpaces.size();
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(final int position, View convertView, ViewGroup parent) {

            SpaceCell spaceCell = new SpaceCell(mContext, mHandler);

            try {
                final SpaceInfo info = mSpaces.get(position);
//                Log.d(DEBUG_TAG, "getView " + position + " index : " + info.index);

                if (info.empty) {
                    spaceCell = setEmpty(mContext);
                } else {
                    spaceCell.setValid();
                    spaceCell.setSpaceName((String) info.name);
                    spaceCell.setSpaceImg(info.icon);
                    spaceCell.setSpaceIndex(info.index);
                    spaceCell.getDeleteBtn().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            switch (v.getId()) {
                                case R.id.btn_delete:
                                    try {
                                        int count = 0;
                                        count = getFilledCount();

                                        if (count <= 1) {
                                            AlertDialog alertDialog = new AlertDialog(EditSpaceActivity.this, getString(R.string.more_space));
                                            alertDialog.setCancelable(false);
                                            alertDialog.show();
                                        } else {
                                            remove_page = position;
                                            showCheckDeviceDeleteDialog();
                                        }
                                        checkBottomText();
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                break;
                            }
                        }
                    });

                    spaceCell.SetDeleteBtnInvisible(visible_delete);
                }
            }catch (Exception e){
                e.printStackTrace();
            }

            return spaceCell;
        }
    }

    private void removePage(int position){
        try {
            Log.d(DEBUG_TAG, "Space deleted " + mSpaces.get(position).index);
            PageDataManager.getInst().removePage(mSpaces.get(position).index);
            mSpaces.get(position).empty = true;
            mSpaces.get(position).name = "";
            mSpaces.get(position).index = -1;
            mSpaces.get(position).icon = "";
            mSpaces.get(position).screen_data = "";
            remakeList(position);
            adapter.notifyDataSetChanged();

            loadSpaces();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void showCheckDeviceDeleteDialog(){

        try{
            spaceDialog = new SpaceDialog(EditSpaceActivity.this, cancelClickListener, okClickListener);
            spaceDialog.setCancelable(false);
            spaceDialog.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private View.OnClickListener cancelClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(DEBUG_TAG, "showCheckDeviceDeleteDialog cancel");
            spaceDialog.cancel();
        }
    };

    private View.OnClickListener okClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(DEBUG_TAG, "showCheckDeviceDeleteDialog ok");
            spaceDialog.cancel();
            removePage(remove_page);
        }
    };

    //위치이동
    private int getFilledCount(){
        int count = 0;

        try {
            for (int i = 0; i < mSpaces.size(); i++) {
                if (!mSpaces.get(i).empty) {
                    count++;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return count;
    }

    private void remakeList(int position){

        try {
            for (int i = position; i < mSpaces.size() - 1; i++) {
                try {
                    mSpaces.get(i).empty = mSpaces.get(i + 1).empty;
                    mSpaces.get(i).name = mSpaces.get(i + 1).name;
                    mSpaces.get(i).index = mSpaces.get(i + 1).index;
                    mSpaces.get(i).icon = mSpaces.get(i + 1).icon;
                    mSpaces.get(i).screen_data = mSpaces.get(i + 1).screen_data;
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            mSpaces.get(mSpaces.size() - 1).empty = true;
            mSpaces.get(mSpaces.size() - 1).name = "";
            mSpaces.get(mSpaces.size() - 1).index = -1;
            mSpaces.get(mSpaces.size() - 1).icon = "";
            mSpaces.get(mSpaces.size() - 1).screen_data = "";
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //하단 문구 표출 여부
    private void checkBottomText(){
        TextView bottomTextView = (TextView) findViewById(R.id.edit_space_btm_txt);
        if(getFilledCount() > 1) {
            bottomTextView.setVisibility(View.VISIBLE);
            visible_delete = true;
        } else {
            bottomTextView.setVisibility(View.INVISIBLE);
            visible_delete = false;
        }
    }

    private void insertList(int where, int index){
        try {
            if (where != -1) {
                int front = 0;
                int rear = mSpaces.size();

                boolean mEmpty = mSpaces.get(index).empty;
                String mName = mSpaces.get(index).name;
                int mIndex = mSpaces.get(index).index;
                String mIcon = mSpaces.get(index).icon;
                String mScreenData = mSpaces.get(index).screen_data;

                if (where == index) {
                    return;
                } else if (where < index) {
                    front = where;
                    rear = index;

                    for (int i = rear - 1; i >= front; i--) {
                        if ((i >= 0) && ((i + 1) <= (mSpaces.size() - 1))) {
                            Log.d(DEBUG_TAG, "insertList " + (i + 1) + " = " + i);
                            mSpaces.get(i + 1).empty = mSpaces.get(i).empty;
                            mSpaces.get(i + 1).name = mSpaces.get(i).name;
                            mSpaces.get(i + 1).index = mSpaces.get(i).index;
                            mSpaces.get(i + 1).icon = mSpaces.get(i).icon;
                            mSpaces.get(i + 1).screen_data = mSpaces.get(i).screen_data;
                        }
                    }
                    Log.d(DEBUG_TAG, "insertList " + front + " = " + index + "/ mIndex " + mIndex);

                    mSpaces.get(front).empty = mEmpty;
                    mSpaces.get(front).name = mName;
                    mSpaces.get(front).index = mIndex;
                    mSpaces.get(front).icon = mIcon;
                    mSpaces.get(front).screen_data = mScreenData;
                } else if (index < where) {
                    front = index;
                    rear = where;

                    for (int i = front; i < rear; i++) {
                        if ((i >= 0) && ((i + 1) <= (mSpaces.size() - 1))) {
                            Log.d(DEBUG_TAG, "insertList " + i + " = " + (i + 1));
                            mSpaces.get(i).empty = mSpaces.get(i + 1).empty;
                            mSpaces.get(i).name = mSpaces.get(i + 1).name;
                            mSpaces.get(i).index = mSpaces.get(i + 1).index;
                            mSpaces.get(i).icon = mSpaces.get(i + 1).icon;
                            mSpaces.get(i).screen_data = mSpaces.get(i + 1).screen_data;
                        }
                    }
                    Log.d(DEBUG_TAG, "insertList " + rear + " = " + index + "/ mIndex " + mIndex);

                    mSpaces.get(rear).empty = mEmpty;
                    mSpaces.get(rear).name = mName;
                    mSpaces.get(rear).index = mIndex;
                    mSpaces.get(rear).icon = mIcon;
                    mSpaces.get(rear).screen_data = mScreenData;
                }
                PageDataManager.getInst().changePagePos(where, index);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private SpaceCell setEmpty(Context context){

        SpaceCell spaceCell = new SpaceCell(context, mHandler);

        try {
            spaceCell.setInvalid();
            spaceCell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(DEBUG_TAG, "clicked");
                    try {
                        PublicTools.clearNavigationBar(EditSpaceActivity.this);

                        Intent intent = new Intent(EditSpaceActivity.this, AddSpaceActivity.class);
                        intent.putExtra(NameSpace.NUM_PAGES, PageDataManager.getInst().getPageSize());
                        intent.putExtra(NameSpace.ACTIVITY_MODE, NameSpace.ADD_MODE);
                        startActivityForResult(intent, 0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

        return spaceCell;
    }

    @Override
    protected void onDestroy() {
        unbindDrawables(gridview);
        BitmapTool.recursive(getWindow().getDecorView().getRootView());
        super.onDestroy();
    }

    private void unbindDrawables(View view) {

        //TODO test 필요
        if (view == null)
            return;

        if (view instanceof ImageView) {
            ((ImageView) view).setImageDrawable(null);
        }
        if (view.getBackground() != null) {
            view.getBackground().setCallback(null);
        }
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                unbindDrawables(((ViewGroup) view).getChildAt(i));
            }
            view.setBackgroundResource(0);
            view.setBackgroundDrawable(null);
        }
    }


}
