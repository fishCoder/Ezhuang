package com.ezhuang;



import android.content.Intent;
import android.net.Uri;
import android.view.View;

import com.ezhuang.common.Global;
import com.ezhuang.common.network.BaseFragment;
import com.ezhuang.common.network.NetworkImpl;
import com.ezhuang.model.AccountInfo;
import com.ezhuang.user.UserDetailActivity_;
import com.gc.materialdesign.widgets.Dialog;
import com.pgyersdk.feedback.PgyFeedback;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2015/4/7 0007.
 */
@EFragment(R.layout.tab_bar_settings)
public class FragmentSettings extends BaseFragment {

    @ViewById
    View layout_quit;

    @ViewById
    View layout_about;

    @ViewById
    View layout_info;

    @ViewById
    View layout_version;

    @ViewById
    View layout_feedback;

    String LOGIN_OUT = Global.HOST + "/app/stf/loginOut.do";

    @AfterViews
    void init(){
        layout_quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(getActivity(),"退出", "确定退出当前账户吗？");
                dialog.show();
                dialog.getButtonAccept().setText("确定");
                dialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.hide();
                        AccountInfo.loginOut(getActivity());

                        getNetwork(LOGIN_OUT,LOGIN_OUT);
                        showProgressBar(true,"正在注销");
                    }
                });
                dialog.setOnCancelButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }
        });

        layout_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("http://www.91jzw.com/index/home/share.do");
                Intent it = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(it);
            }
        });

        layout_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserDetailActivity_.intent(getActivity()).companyType(MyApp.currentUser.getCompanyType()).globalKey(MyApp.currentUser.getGlobal_key()).start();
            }
        });

        layout_version.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UpdateService.class);
                intent.putExtra(UpdateService.EXTRA_BACKGROUND, false);
                getActivity().startService(intent);
            }
        });

        layout_feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            PgyFeedback.getInstance().show(getActivity(), "b87e159131abde328237831cd3902aad");
//            AlterPasswordActivity_.intent(getActivity()).start();
            }
        });

    }

    @Override
    public void parseJson(int code, JSONObject respanse, String tag, int pos, Object data) throws JSONException {
        showProgressBar(false);
        if(code == NetworkImpl.REQ_SUCCESSS){
            Intent intent;
            intent = new Intent(getActivity(),LoginActivity_.class);
            startActivity(intent);
            getActivity().finish();
        }else{
            showButtomToast("注销失败");
        }
    }
}
