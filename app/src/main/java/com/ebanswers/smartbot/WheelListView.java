package com.ebanswers.smartbot;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ebanswers.smartlib.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Callanna on 2016/8/21.
 */
public class WheelListView extends ListView {

    private List<String> datas = new ArrayList<>();
    private ListWheelAdapter adapter = new ListWheelAdapter();
    private int lastPosition ,middlePosition;

    public WheelListView(Context context) {
        this(context, null);
    }

    public WheelListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setAdapter(adapter);
        setDivider(null);
        setOnScrollListener(new OnScrollListener() {

            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
                    //adapter.notifyDataSetChanged();
//                    setSelection(getSelectionPosition() % (datas.size()));
                    //setSelection(middlePosition);
                    //adapter.setMiddlePos(middlePosition);
                   // adapter.notifyDataSetChanged();
                    LogUtil.d("duanyl==============>onScrollStateChanged:"+middlePosition+",getSelectionPosition:"+(getSelectionPosition() % (datas.size())));
                }
            }

            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                middlePosition = firstVisibleItem + visibleItemCount/2;         // lastPosition 为了防止同一重复回调
                 if (adapter != null && middlePosition != lastPosition) {
                     adapter.setMiddlePos(middlePosition);
                     adapter.notifyDataSetChanged();
                 }
                lastPosition = middlePosition;
             LogUtil.d("duanyl=========>firstVisibleItem:"+firstVisibleItem+",middlePosition:"+middlePosition+",visibleItemCount:"+visibleItemCount+",getSelectionPosition:"+getSelectionPosition() % (datas.size()));
            }
        });
        setClipChildren(false);
        setClipToPadding(false);
    }

    public void setDatas(List<String> datas) {
        this.datas = datas;
        adapter.setDatas(this.datas);
        setAdapter(adapter);
    }

    int getSelectionPosition() {
        int ret = 0;
        if (getChildCount() > 0) {
            int h = getChildAt(0).getMeasuredHeight();
            Rect r = new Rect();
            getChildAt(0).getLocalVisibleRect(r);
            if (r.height() > h / 2) {
                ret = getFirstVisiblePosition();
            } else {
                ret = getFirstVisiblePosition() + 1;
            }
        }
        return ret;
    }

    class ListWheelAdapter extends BaseAdapter {
        List<String> listdatas;
        public TextView selectText;
        private int mMiddlePosition;

        public ListWheelAdapter() {
            initDatas();
        }

        private void initDatas() {
            listdatas = new ArrayList<>();
            for (int i = 0; i < 60; i++) {
                listdatas.add(String.valueOf(i));
                datas.add(String.valueOf(i));
            }
        }

        public ListWheelAdapter(List<String> list) {
            listdatas = list;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ListWheelHolder holder = null;
            if (convertView == null) {
                convertView = View.inflate(getContext(), R.layout.item_num, null);
                holder = new ListWheelHolder();
                holder.tv_num = (TextView) convertView.findViewById(R.id.tv_num);
                holder.tv_per = (TextView) convertView.findViewById(R.id.tv_per);
                convertView.setTag(holder);
            } else {
                holder = (ListWheelHolder) convertView.getTag();
            }

//            int index = position % (datas.size() );
//            String data = datas.get(index);
//            holder.tv_num.setText(listdatas.get(index));
//            if ((getSelectionPosition() % datas.size()) % datas.size() == position % datas.size()) {
//                holder.tv_num.setTextColor(Color.BLUE);
//                holder.tv_per.setVisibility(VISIBLE);
//                selectText = holder.tv_num;
//            } else {
//                holder.tv_num.setTextColor(Color.GRAY);
//                holder.tv_per.setVisibility(GONE);
//            }
            int newpos = position;
            if (position >= listdatas.size()) {
                newpos = position % listdatas.size();
            }
            holder.tv_num.setText(listdatas.get(newpos));
            if (newpos == (mMiddlePosition) % listdatas.size()) {
                holder.tv_num.setTextColor(Color.BLUE);
                holder.tv_per.setVisibility(VISIBLE);
                selectText = holder.tv_num;
            } else {
                holder.tv_num.setTextColor(Color.GRAY);
                holder.tv_per.setVisibility(GONE);
            }

            return convertView;
        }

        public void setDatas(List<String> datas) {
            this.listdatas = datas;
        }

        public String getSelectView() {
            if (selectText != null) {
                return selectText.getText().toString();
            } else {
                return "";
            }
        }

        public void setSelectView(String data) {
            if (selectText != null) {
                selectText.setText(data);
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        public void setMiddlePos(int middlePosition) {
            this.mMiddlePosition = middlePosition;
            setSelection(middlePosition);
        }

        class ListWheelHolder {
            TextView tv_num, tv_per;
        }
    }
}