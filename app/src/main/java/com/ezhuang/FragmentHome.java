package com.ezhuang;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ezhuang.common.Global;
import com.ezhuang.common.network.BaseFragment;
import com.ezhuang.model.AccountInfo;
import com.ezhuang.model.Role;
import com.ezhuang.model.StaffUser;
import com.ezhuang.project.ProjectBillActivity_;
import com.ezhuang.project.ViewBillingActivity_;
import com.ezhuang.project.detail.CreatProjectActivity_;
import com.ezhuang.project.detail.ViewProjectActivity_;
import com.ezhuang.purchase.PurchaseActivity;
import com.ezhuang.purchase.PurchaseRecordActivity_;
import com.ezhuang.quality.ViewProgressActivity_;
import com.readystatesoftware.viewbadger.BadgeView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringArrayRes;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2015/4/7 0007.
 */
@EFragment(R.layout.tab_bar_role)
public class FragmentHome extends BaseFragment {

    List<Object[]> list       =   new LinkedList<Object[]>();
    List<String> groupkey   =   new LinkedList<String>();


    @StringArrayRes
    String[] staff_apps;
    int[] staff_apps_icon = new int[]{R.mipmap.ic_project,R.mipmap.ic_project,R.mipmap.ic_project};

    @StringArrayRes
    String[] project_manager_apps;
    int[] project_manager_apps_icon = new int[]{R.mipmap.bill,R.mipmap.bill,R.mipmap.bill};

    @StringArrayRes
    String[] check_apps;
    int[] check_apps_icon = new int[]{R.mipmap.ic_check,R.mipmap.ic_check};

    @StringArrayRes
    String[] buyer_apps;
    int[] buyer_apps_icon = new int[]{R.mipmap.ic_purchase,R.mipmap.ic_purchase};

    @StringArrayRes
    String[] quality_apps;
    int[] quality_apps_icon = new int[]{R.mipmap.ic_quality};

    @ViewById
    ListView listRoleFunction;


    @AfterViews
    void init(){

        if(list.size()==0){
            StaffUser staffUser = MyApp.currentUser;
            if(staffUser == null){
                staffUser = AccountInfo.loadAccount(getActionBarActivity());
            }
            for(Role role : staffUser.getRoles()){
                groupkey.add(role.getRoleName());
                list.add(new Object[]{role.getRoleName()});
                list.addAll(getApps(role.getRoleId()));
            }


        }
        listRoleFunction.setAdapter(new MyAdapter());

        listRoleFunction.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(list.get(position)[0].equals("采购")){
                    ViewBillingActivity_.intent(getActivity()).roleId(Global.BUYER).start();
                }else
                if(list.get(position)[0].equals("采购记录")){
                    PurchaseRecordActivity_.intent(getActivity()).start();
                }else
                if(list.get(position)[0].equals("查看项目")){
                    ViewProjectActivity_.intent(getActivity()).roleId(Global.STAFF).staffId(MyApp.currentUser.getGlobal_key()).start();
                }else
                if(list.get(position)[0].equals("审核")){
                    ViewBillingActivity_.intent(getActivity()).roleId(Global.CEHCK).start();
                }else
                if(list.get(position)[0].equals("审核记录")){
                    ViewBillingActivity_.intent(getActivity()).roleId(Global.CEHCK).isRecord(true).start();
                }
                else
                if(list.get(position)[0].equals("我的项目")){
                    ViewProjectActivity_.intent(getActivity()).roleId(Global.PROJECT_MANAGER).staffId(MyApp.currentUser.getGlobal_key()).start();
                }else
                if(list.get(position)[0].equals("开单记录")){
                    ViewBillingActivity_.intent(getActivity()).roleId(Global.PROJECT_MANAGER).isRecord(true).start();
                }else
                if(list.get(position)[0].equals("质检")){
                    ViewProgressActivity_.intent(getActivity()).start();
                }else
                if(list.get(position)[0].equals("投诉/报修")){
                    ProblemActicity_.intent(getActivity()).start();
                }else
                {
                    Intent intent = new Intent(getActivity(),CreatProjectActivity_.class);
                    getActivity().startActivity(intent);
                }

            }
        });


    }

    List<Object[]> getApps(String roleId){

        List<Object[]> list    =new LinkedList<>();
        String[] apps = {};
        int[]    icons = {};
        if(Global.PROJECT_MANAGER.equals(roleId)) {
            apps = project_manager_apps;
            icons = project_manager_apps_icon;
        }else if(Global.BUYER.equals(roleId)){
            apps = buyer_apps;
            icons = buyer_apps_icon;
        }else if(Global.STAFF.equals(roleId)){
            apps = staff_apps;
            icons = staff_apps_icon;
        }else if(Global.CEHCK.equals(roleId)){
            apps = check_apps;
            icons = check_apps_icon;
        }else if(Global.QUALITY.equals(roleId)){
            apps = quality_apps;
            icons = quality_apps_icon;
        }

        for (int i=0;i<apps.length;i++){
            list.add(new Object[]{apps[i],icons[i]});
        }

        return list;
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public boolean isEnabled(int position) {
            Object[] item = (Object[])getItem(position);
            if(groupkey.contains(item[0])){
                return false;
            }
            return super.isEnabled(position);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;

            Object[] item = (Object[])getItem(position);

            if(groupkey.contains(item[0])){
                view = LayoutInflater.from(getActivity().getApplicationContext()).inflate(R.layout.role_function_divider, null);
            }else{
                view = LayoutInflater.from(getActivity().getApplicationContext()).inflate(R.layout.role_function_item,null);
                view.findViewById(R.id.icon_function).setBackgroundResource((Integer)item[1]);

                BadgeView badge = (BadgeView) view.findViewById(R.id.badge);
                badge.setText("2");
                badge.setVisibility(View.GONE);
            }
            TextView text=(TextView) view.findViewById(R.id.name_function);
            text.setText((CharSequence) item[0]);

            return view;
        }

    }


}
