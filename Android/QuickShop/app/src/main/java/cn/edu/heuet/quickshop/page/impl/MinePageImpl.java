package cn.edu.heuet.quickshop.page.impl;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.RelativeLayout;

import cn.edu.heuet.quickshop.R;
import cn.edu.heuet.quickshop.activity.CreateItemActivity;
import cn.edu.heuet.quickshop.activity.LoginActivity;
import cn.edu.heuet.quickshop.activity.customview.ToastUtils;
import cn.edu.heuet.quickshop.page.BasePage;

import static android.content.Context.MODE_PRIVATE;

/**
 * Create by SunnyDay on 2019/03/15
 */
public class MinePageImpl extends BasePage implements View.OnClickListener {

    public MinePageImpl(Context context) {
        super(context);
    }

    @Override
    public Object setContentView() {
        return R.layout.page_mine;
    }

    @Override
    public void init() {
        RelativeLayout myGoods = view.findViewById(R.id.rl_my_goods);
        RelativeLayout myGoodsAddr = view.findViewById(R.id.rl_goods_addr);
        RelativeLayout setting = view.findViewById(R.id.rl_setting);
        RelativeLayout admin = view.findViewById(R.id.rl_admin);
        RelativeLayout logout = view.findViewById(R.id.rl_logout);
        admin.setOnClickListener(this);
        myGoods.setOnClickListener(this);
        myGoodsAddr.setOnClickListener(this);
        setting.setOnClickListener(this);
        logout.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        // 这里的context继承自BasePage
        switch (view.getId()){
            case R.id.rl_my_goods:
                ToastUtils.showToast(context,"待续",ToastUtils.LENGTH_SHORT);
                break;
            case R.id.rl_admin:
                Intent it_mine_to_create = new Intent(context, CreateItemActivity.class);
                view.getContext().startActivity(it_mine_to_create);
                break;
            case R.id.rl_goods_addr:
                ToastUtils.showToast(context,"待续",ToastUtils.LENGTH_SHORT);
                break;
            case R.id.rl_setting:
                ToastUtils.showToast(context,"待续",ToastUtils.LENGTH_SHORT);
                break;
            case R.id.rl_logout:
                logout();
                break;
        }
    }

    private void logout() {
        SharedPreferences sp = context.getSharedPreferences("login_info", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("token", "");
        editor.putString("telphone", "");
        editor.putString("password", "");
        if (editor.commit()){
            context.startActivity(new Intent(context, LoginActivity.class));
        }

    }
}
