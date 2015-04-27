package com.ezhuang.purchase;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ezhuang.R;
import com.ezhuang.common.ListModify;
import com.ezhuang.common.network.BaseFragment;
import com.ezhuang.model.SpMaterial;
import com.ezhuang.project.detail.SetProjectInfo_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringArrayRes;

/**
 * Created by Administrator on 2015/4/25 0025.
 */
@EFragment(R.layout.activity_create_project)
public class SelfBuyFragment extends BaseFragment {

    @ViewById
    ListView listView;

    @StringArrayRes(R.array.self_buy)
    String[] item_name;
    String[] item_value;

    SpMaterial spMaterial = new SpMaterial();

    @AfterViews
    void init(){
        item_value = new String[]{spMaterial.mtName,spMaterial.item_count,spMaterial.bmb_name,spMaterial.bmb_m_name,spMaterial.bmb_price};
        listView.setOnItemClickListener(onItemClickListener);
        listView.setAdapter(adapter);
    }

    void setMaterial(SpMaterial spMaterial){
        this.spMaterial = spMaterial;
    }


    boolean fill(SpMaterial spMaterial){

        if(item_value[2].isEmpty()){

            showButtomToast("建材商不能为空");
            return false;
        }
        if(item_value[3].isEmpty()){

            showButtomToast("品牌不能为空");
            return false;
        }
        if(item_value[4].isEmpty()){

            showButtomToast("价格不能为空");
            return false;
        }

        spMaterial.bmb_name = item_value[2];
        spMaterial.bmb_m_name = item_value[3];
        spMaterial.bmb_price = item_value[4];

        return true;
    }

    void setRowValue(int row,String value){
        row = row - 12;
        item_value[row] = value;
        adapter.notifyDataSetChanged();
    }

    BaseAdapter adapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return item_name.length;
        }

        @Override
        public Object getItem(int position) {
            return item_name.length;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.list_item_2_text_align_right, parent, false);
                holder = new ViewHolder();
                holder.first = (TextView) convertView.findViewById(R.id.first);
                holder.second = (TextView) convertView.findViewById(R.id.second);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.first.setText(item_name[position]);

            String seondString = item_value[position];
            if (seondString==null||seondString.isEmpty()) {
                seondString = "未填写";
            }
            holder.second.setText(seondString);

            return convertView;
        }
        class ViewHolder {
            TextView first;
            TextView second;
        }
    };


    public final static int BMB_NAME = 14;
    public final static int BMB_TITLE = 15;
    public final static int BMB_PRICE = 16;

    int[] row = new int[]{0,0,BMB_NAME,BMB_TITLE,BMB_PRICE};
    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(position>1){
                SetProjectInfo_
                        .intent(getActivity())
                        .title(item_name[position])
                        .row(row[position])
                        .rowValue(item_value[position])
                        .startForResult(ListModify.Add);
            }
        }
    };


}
