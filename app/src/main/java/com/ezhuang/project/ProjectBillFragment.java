package com.ezhuang.project;

import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ezhuang.R;
import com.ezhuang.common.BlankViewDisplay;
import com.ezhuang.common.Global;
import com.ezhuang.common.network.BaseFragment;
import com.ezhuang.model.ProjectBill;
import com.ezhuang.project.detail.ListListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringArrayRes;

import java.util.LinkedList;
import java.util.List;

@EFragment(R.layout.fragment_project_bill)
public class ProjectBillFragment extends BaseFragment {

    @ViewById
    View blankLayout;

    @ViewById
    PullToRefreshListView listView;

    @StringArrayRes
    String[] bill_state;

    ListListener listListener;

    List<ProjectBill> mData;

    @AfterViews
    void init(){
        if(mData==null){
            showDialogLoading();
            mData = new LinkedList<>();
        }

        listView.setAdapter(adapter);
        listView.setMode(PullToRefreshBase.Mode.BOTH);
        listView.setOnRefreshListener(onRefreshListener);

    }

    void setUpDownListListener(ListListener listListener){
        this.listListener = listListener;
    }

    void updateDate(List<ProjectBill> mData){
        this.mData = mData;
        adapter.notifyDataSetChanged();
        listView.onRefreshComplete();

        BlankViewDisplay.setBlank(mData.size(), this, true, blankLayout, null);
    }

    BaseAdapter adapter = new BaseAdapter(){
        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Object getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            ViewHolder viewHolder;

            if(convertView == null){
                viewHolder = new ViewHolder();
                view = mInflater.inflate(R.layout.item_bill,parent,false);

                viewHolder.bill_no = (TextView) view.findViewById(R.id.bill_no);
                viewHolder.bill_create_time = (TextView) view.findViewById(R.id.bill_create_time);
                viewHolder.bill_state = (TextView) view.findViewById(R.id.bill_state);
                viewHolder.item_count = (TextView) view.findViewById(R.id.item_count);

                convertView = view;

            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }

            ProjectBill bill = (ProjectBill) getItem(position);

            viewHolder.bill_no.setText(bill.getBillCode());
            viewHolder.bill_create_time.setText(bill.getBillTime());
            viewHolder.bill_state.setText(bill_state[bill.getState()]);
            viewHolder.item_count.setText(""+bill.getBdCount());

            return convertView;
        }
    };

    class ViewHolder{
        TextView bill_no;
        TextView bill_create_time;
        TextView bill_state;
        TextView item_count;
    }

    PullToRefreshBase.OnRefreshListener onRefreshListener = new PullToRefreshBase.OnRefreshListener<ListView>() {
        @Override
        public void onRefresh(PullToRefreshBase<ListView> refreshView) {

            if(PullToRefreshBase.Mode.PULL_FROM_START == listView.getCurrentMode()){
                new StopPullTask().execute();
            }else{
                if (mData.size()< Global.PAGE_SIZE){
                    new StopPullTask().execute();
                }else{
                    listListener.loadMore();
                }
            }
        }
    };

    private class StopPullTask extends AsyncTask<Void, Void, Object> {

        @Override
        protected Object doInBackground(Void... params) {
            return new Object();
        }

        @Override
        protected void onPostExecute(Object obj) {
            listView.onRefreshComplete();
            super.onPostExecute(obj);
        }
    }
}
