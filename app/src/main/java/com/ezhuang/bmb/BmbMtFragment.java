package com.ezhuang.bmb;

import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.ezhuang.ActivityFragmentInterface.BmbMtFragmentInterface;
import com.ezhuang.R;
import com.ezhuang.common.BlankViewDisplay;
import com.ezhuang.common.Global;
import com.ezhuang.common.JsonUtil;
import com.ezhuang.common.network.BaseFragment;
import com.ezhuang.common.network.NetworkImpl;
import com.ezhuang.model.SpMaterial;
import com.ezhuang.model.SpMtType;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshExpandableListView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/6/23 0023.
 */
@EFragment(R.layout.fragment_bmb_mt)
public class BmbMtFragment extends BaseFragment {

    @ViewById
    PullToRefreshExpandableListView listView;
    @ViewById
    View blankLayout;

    String QUERY_BMB_MTS = Global.HOST+"/app/bmb/queryBmbMts.do";

    Map<String,List<SpMaterial>> mData = new HashMap<>();
    List<SpMtType> mType = new LinkedList<>();

    String selectBigType = "1";

    public void setSelectBigType(String bigType){
        selectBigType = bigType;
        new LoadDataTask().execute();
    }

    public void notifyDataSetChanged(){
        adapter.notifyDataSetChanged();
    }

    public BmbMtFragmentInterface getInterface(){
        return (BmbMtFragmentInterface)getActivity();
    }

    @AfterViews
    void init(){
        if(mData.size()==0)
            new LoadDataTask().execute();

        listView.getRefreshableView().setAdapter(adapter);
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ExpandableListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ExpandableListView> refreshView) {
                getNetwork(QUERY_BMB_MTS, QUERY_BMB_MTS);
            }
        });
        listView.getRefreshableView().setOnChildClickListener(onChildClickListener);
    }

    ExpandableListView.OnChildClickListener onChildClickListener = new ExpandableListView.OnChildClickListener() {
        @Override
        public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
            ((BmbMtFragmentInterface)getActivity()).showAddDailog(mData.get(mType.get(groupPosition).typeId).get(childPosition));
            return false;
        }
    };

    private class LoadDataTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {

            mType = SpMtType.getTypeByBigType(selectBigType);

            if(mType==null || mType.size()==0){
                selectBigType = selectBigType.equals("1")?"2":"1";
                mType = SpMtType.getTypeByBigType(selectBigType);
                if(mType==null || mType.size()==0){
                    return false;
                }
                publishProgress();
            }

            for (SpMtType type:mType){
                if(mData.get(type.typeId)==null)
                    mData.put(type.typeId, SpMaterial.getListByType(type.typeId));
            }
            return true;



        }
        @Override
        protected void onProgressUpdate(Void... values) {
            if(selectBigType.equals("1")){
                showButtomToast("没有主材物料");
            }else{
                showButtomToast("没有基础物料");
            }
            getInterface().setSpinnerSelection(Integer.parseInt(selectBigType)-1);
            super.onProgressUpdate(values);

        }

        @Override
        protected void onPostExecute(Boolean reslut) {
            super.onPostExecute(reslut);

            if(reslut){
                adapter.notifyDataSetChanged();
            }else{
                showProgressBar(true,"从服务器加载材料数据\n时间较长 请稍等");
                getNetwork(QUERY_BMB_MTS,QUERY_BMB_MTS);
            }
        }
    }

    @Override
    public void parseJson(int code, JSONObject respanse, String tag, int pos, Object data) throws JSONException {
        if(QUERY_BMB_MTS.equals(tag)){
            listView.onRefreshComplete();
            if(code == NetworkImpl.REQ_SUCCESSS){

                JSONArray jsonArray;

                List<SpMaterial> totalSpMaterial = new LinkedList<>();
                try {
                    jsonArray = respanse.getJSONObject("data").getJSONArray("mainMetarials");
                    mType.clear();
                    for (int i=0;i<jsonArray.length();i++){
                        SpMtType type = JsonUtil.Json2Object(jsonArray.getJSONObject(i).getString("type"), SpMtType.class);
                        mType.add(type);

                        List<SpMaterial> spMaterials = new LinkedList<>();
                        JSONArray childList = jsonArray.getJSONObject(i).getJSONArray("childList");

                        for(int k=0; k<childList.length(); k++){
                            SpMaterial spMaterial = new SpMaterial(childList.getJSONObject(k));
                            spMaterials.add(spMaterial);
                            totalSpMaterial.add(spMaterial);

                        }

                        mData.put(type.typeId, spMaterials);
                    }

                    jsonArray = respanse.getJSONObject("data").getJSONArray("baseMetarials");
                    for (int i=0;i<jsonArray.length();i++){
                        SpMtType type = JsonUtil.Json2Object(jsonArray.getJSONObject(i).getString("type"), SpMtType.class);
                        mType.add(type);

                        List<SpMaterial> spMaterials = new LinkedList<>();
                        JSONArray childList = jsonArray.getJSONObject(i).getJSONArray("childList");

                        for(int k=0; k<childList.length(); k++){
                            SpMaterial spMaterial = new SpMaterial(childList.getJSONObject(k));
                            spMaterials.add(spMaterial);
                            totalSpMaterial.add(spMaterial);

                        }

                        mData.put(type.typeId, spMaterials);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();

                }

                SpMtType.clear();
                SpMtType.saveAll(mType);

                List<SpMtType> list = new LinkedList<>();
                for (int i=0;i<mType.size();i++){
                    SpMtType type = mType.get(i);
                    if(type.bigTypeId.equals(selectBigType)){
                        list.add(type);
                    }
                }

                mType = list;

                BlankViewDisplay.setBlank(mType.size(), this, true, blankLayout, null);

                SpMaterial.clear();
                SpMaterial.saveAll(totalSpMaterial);

                showProgressBar(false);
                adapter.notifyDataSetChanged();
            }
        }
    }

    BaseExpandableListAdapter adapter = new BaseExpandableListAdapter() {

        @Override
        public void registerDataSetObserver(DataSetObserver observer) {
            super.registerDataSetObserver(observer);
        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public int getGroupCount() {
            return mType.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return mData.get(mType.get(groupPosition).typeId).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return mType.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {

            return mData.get(mType.get(groupPosition).typeId).get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return 0;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            View view;
            ViewHolderforHead viewHolder;
            if(convertView == null){
                viewHolder = new ViewHolderforHead();
                view = mInflater.inflate(R.layout.item_material_type,parent,false);
                viewHolder.type_icon =  (ImageView) view.findViewById(R.id.type_icon);
                viewHolder.material_type = (TextView) view.findViewById(R.id.material_type);

                view.setTag(viewHolder);
                convertView = view;

            }else{
                viewHolder = (ViewHolderforHead) convertView.getTag();
            }
            SpMtType spMtType = (SpMtType) getGroup(groupPosition);
            viewHolder.material_type.setText(spMtType.typeName);

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
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
                view.setTag(viewHolder);
                convertView = view;

            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }
            SpMaterial spMaterial = (SpMaterial) getChild(groupPosition,childPosition);

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

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public void onGroupExpanded(int groupPosition) {

        }

        @Override
        public void onGroupCollapsed(int groupPosition) {

        }

        @Override
        public long getCombinedChildId(long groupId, long childId) {
            return 0;
        }

        @Override
        public long getCombinedGroupId(long groupId) {
            return 0;
        }


    };

    class ViewHolderforHead{
        ImageView type_icon;
        TextView  material_type;
    }

    class ViewHolder{
        TextView sp_m_name;
        TextView sp_m_spec;
        TextView sp_m_unit_name;
        TextView bmb_m_price;
        View     icon_mark;
    }
}
