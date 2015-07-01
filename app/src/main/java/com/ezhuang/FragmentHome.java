package com.ezhuang;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ezhuang.bmb.NewOrderActivity_;
import com.ezhuang.bmb.NewOrderRecordActivity_;
import com.ezhuang.bmb.OrdersActivity_;
import com.ezhuang.common.Banner;
import com.ezhuang.common.Global;
import com.ezhuang.common.network.BaseFragment;
import com.ezhuang.model.AccountInfo;
import com.ezhuang.model.Role;
import com.ezhuang.model.StaffUser;
import android.widget.LinearLayout.LayoutParams;
import com.ezhuang.project.ViewBillingActivity_;
import com.ezhuang.project.detail.CreatProjectActivity_;
import com.ezhuang.project.detail.ViewProjectActivity_;
import com.ezhuang.purchase.PurchaseRecordActivity_;
import com.ezhuang.quality.ViewProgressActivity_;
import com.readystatesoftware.viewbadger.BadgeView;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;

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
    int[] staff_apps_icon = new int[]{R.mipmap.ic_new_project,R.mipmap.ic_project_rec,R.mipmap.ic_fix};

    @StringArrayRes
    String[] project_manager_apps;
    int[] project_manager_apps_icon = new int[]{R.mipmap.ic_bill,R.mipmap.ic_bill_rec};

    @StringArrayRes
    String[] check_apps;
    int[] check_apps_icon = new int[]{R.mipmap.ic_check,R.mipmap.ic_check_rec};

    @StringArrayRes
    String[] buyer_apps;
    int[] buyer_apps_icon = new int[]{R.mipmap.ic_buy,R.mipmap.ic_buy_rec,R.mipmap.ic_buy_rec};

    @StringArrayRes
    String[] quality_apps;
    int[] quality_apps_icon = new int[]{R.mipmap.ic_quolity};

    @StringArrayRes
    String[] dispatcher_apps;
    int[] dispatcher_apps_icons = new int[]{R.mipmap.ic_bill_scheduling};

    @StringArrayRes
    String[] storage_apps;
    int[] storage_apps_icons = new int[]{R.mipmap.ic_buy_rec,R.mipmap.ic_new_project,R.mipmap.ic_project_rec};

    @ViewById
    ListView listRoleFunction;


    ViewPager   viewpager;

    TextView    tv_image_description;

    LinearLayout ll_point_group;

    List<Banner.PhotoItem> items;

    View banner;

    boolean isShow = true;

    private List<ImageView> mImageList = new LinkedList<>();
    private int previousPointEnale = 0;
    private boolean isStop = false;

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


        listRoleFunction.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(isShow){
                    position--;
                }

                if(list.get(position)[0].equals("采购")){
                    ViewBillingActivity_.intent(getActivity()).roleId(Global.BUYER).start();
                }else
                if(list.get(position)[0].equals("自购记录")){
                    PurchaseRecordActivity_.intent(getActivity()).type(1).start();
                }else
                if(list.get(position)[0].equals("线上采购记录")){
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
                }else
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
                if(list.get(position)[0].equals("订单调度")){
                    OrdersActivity_.intent(getActivity()).roleId(Global.DISPATCH).start();
                }else
                if(list.get(position)[0].equals("创建项目"))
                {
                    Intent intent = new Intent(getActivity(),CreatProjectActivity_.class);
                    getActivity().startActivity(intent);
                }else
                if(list.get(position)[0].equals("建材出库")){
                    OrdersActivity_.intent(getActivity()).roleId(Global.STORAGE).start();
                }else
                if(list.get(position)[0].equals("新建订单")){
                    NewOrderActivity_.intent(getActivity()).start();
                }else
                if(list.get(position)[0].equals("查看订单")){
                    NewOrderRecordActivity_.intent(getActivity()).start();
                }

            }
        });

        loadBanners();


        listRoleFunction.setAdapter(new MyAdapter());
    }

    void loadBanners(){

        items = AccountInfo.loadBanners(getActivity());
        if(items.size()==0){
            isShow = false;
            return;
        }
        for (Banner.PhotoItem item : items){
            if(!item.isCached(getActivity())){
                isShow = false;
                return;
            }
        }
        if(banner!=null){
            listRoleFunction.removeHeaderView(banner);
            listRoleFunction.addHeaderView(banner);
            isShow = true;
            return;
        }
        banner = getActivity().getLayoutInflater().inflate(R.layout.banner,null);
        listRoleFunction.addHeaderView(banner);
        viewpager = (ViewPager) banner.findViewById(R.id.viewpager);
        ll_point_group = (LinearLayout) banner.findViewById(R.id.ll_point_group);
        tv_image_description = (TextView) banner.findViewById(R.id.tv_image_description);

        ImageView mImageView;
        LayoutParams params;
        // 初始化广告条资源
        for (Banner.PhotoItem item : items) {
            final String jump_url = item.getUrl();
            mImageView = new ImageView(getActivity());
            Uri uri = Uri.fromFile(item.getCacheFile(getActivity()));
            Log.v("uri",uri.toString());
            mImageView.setImageURI(uri);
            mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WebViewActivity_.intent(getActivity()).url(jump_url).start();
                }
            });
            mImageList.add(mImageView);

            // 初始化广告条正下方的"点"
            View dot = new View(getActivity());
            dot.setBackgroundResource(R.color.item_press);
            params = new LayoutParams(10, 10);
            params.leftMargin = 10;
            dot.setLayoutParams(params);
            dot.setEnabled(false);
            ll_point_group.addView(dot);
        }

        viewpager.setAdapter(new BannerAdapter());

        // 设置广告条跳转时，广告语和状态语的变化
        viewpager.setOnPageChangeListener(new BannerListener());


        // 初始化广告条，当前索引Integer.MAX_VALUE的一半
        int index = (Integer.MAX_VALUE / 2) - (Integer.MAX_VALUE / 2 % items.size());
        viewpager.setCurrentItem(index); // 设置当前选中的Page，会触发onPageChangListener.onPageSelected方法

        isShow = true;
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
        }else if(Global.DISPATCH.equals(roleId)){
            apps = dispatcher_apps;
            icons = dispatcher_apps_icons;
        }else if(Global.STORAGE.equals(roleId)){
            apps = storage_apps;
            icons = storage_apps_icons;
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

    private class BannerListener implements OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageSelected(int arg0) {
            // 获取新的位置
            int newPosition = arg0 % items.size();
            // 设置广告标语
            tv_image_description.setText(items.get(newPosition).description);
            // 消除上一次的状态点
            ll_point_group.getChildAt(previousPointEnale).setBackgroundResource(R.color.item_press);
            // 设置当前的状态点“点”
            ll_point_group.getChildAt(newPosition).setBackgroundResource(R.color.white);
            // 记录位置
            previousPointEnale = newPosition;
        }

    }

    /**
     * ViewPager数据适配器
     */
    private class BannerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            // 将viewpager页数设置成Integer.MAX_VALUE，可以模拟无限循环
            return Integer.MAX_VALUE;
        }

        /**
         * 复用对象 true 复用view false 复用的是Object
         */
        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            // TODO Auto-generated method stub
            return arg0 == arg1;
        }

        /**
         * 销毁对象
         *
         * @param position
         *            被销毁对象的索引位置
         */
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mImageList.get(position % mImageList.size()));
        }

        /**
         * 初始化一个对象
         *
         * @param position
         *            初始化对象的索引位置
         */
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mImageList.get(position % mImageList.size()));
            return mImageList.get(position % mImageList.size());
        }

    }

    @Override
    public void onDestroy() {
        isStop = true;
        super.onDestroy();
    }
}
