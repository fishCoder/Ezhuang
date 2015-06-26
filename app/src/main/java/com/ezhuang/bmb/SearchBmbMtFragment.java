package com.ezhuang.bmb;

import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ezhuang.ActivityFragmentInterface.BmbMtFragmentInterface;
import com.ezhuang.R;
import com.ezhuang.common.network.BaseFragment;
import com.ezhuang.model.SpMaterial;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2015/6/23 0023.
 */
@EFragment(R.layout.fragment_search_bmb_material)
public class SearchBmbMtFragment extends BaseFragment {
    @ViewById
    PullToRefreshListView listView;

    List<SpMaterial> mData = new LinkedList<>();


    @AfterViews
    void init(){

        listView.setMode(PullToRefreshBase.Mode.DISABLED);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((BmbMtFragmentInterface)getActivity()).showAddDailog(mData.get(position-1));
            }
        });
    }

    public void notifyDataSetChanged(){
        adapter.notifyDataSetChanged();
    }

    void searchMaterial(String keyword){
        new AsyncTask<String,Void,Void>(){
            @Override
            protected Void doInBackground(String... params) {
                showDialogLoading();
                mData = SpMaterial.search(params[0]);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                adapter.notifyDataSetChanged();
                hideProgressDialog();
                super.onPostExecute(aVoid);
            }
        }.execute(keyword);
    }

    BaseAdapter adapter = new BaseAdapter() {
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
                view = mInflater.inflate(R.layout.item_bmb_material,parent,false);
                viewHolder.sp_m_name = (TextView) view.findViewById(R.id.sp_m_name);
                viewHolder.sp_m_spec = (TextView) view.findViewById(R.id.sp_m_spec);
                viewHolder.sp_m_unit_name = (TextView) view.findViewById(R.id.sp_m_unit_name);
                viewHolder.bmb_m_price = (TextView) view.findViewById(R.id.bmb_m_price);
                viewHolder.icon_mark = view.findViewById(R.id.icon_mark);
                view.findViewById(R.id.divider).setVisibility(View.GONE);
                view.setTag(viewHolder);
                convertView = view;

            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }
            SpMaterial spMaterial = (SpMaterial) getItem(position);

            viewHolder.sp_m_name.setText(spMaterial.mtName);
            viewHolder.sp_m_spec.setText(spMaterial.spec);
            viewHolder.sp_m_unit_name.setText(spMaterial.unitName);
            viewHolder.bmb_m_price.setText(spMaterial.bmb_price+"元");

            if(((BmbMtFragmentInterface)getActivity()).isSelect(spMaterial)){
                viewHolder.icon_mark.setVisibility(View.VISIBLE);
            }else{
                viewHolder.icon_mark.setVisibility(View.GONE);
            }

            return convertView;
        }
    };

    class ViewHolder{
        TextView sp_m_name;
        TextView sp_m_spec;
        TextView sp_m_unit_name;
        TextView bmb_m_price;
        View     icon_mark;
    }
}
