package cn.edu.heuet.quickshop.page.impl;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

import cn.edu.heuet.quickshop.R;
import cn.edu.heuet.quickshop.activity.ListItemActivity;
import cn.edu.heuet.quickshop.page.BasePage;


/**
 * Create by SunnyDay on 2019/03/15
 * 此界面只是简单的模拟  实际开发中 rv+viewpager（fragment的adapter）解决
 */
public class SortPageImpl extends BasePage implements View.OnClickListener {

    private Button bt_item_type_1;
    private Button bt_item_type_2;
    private Button bt_item_type_3;
    private Button bt_item_type_4;

    public SortPageImpl(Context context) {
        super(context);
    }

    @Override
    public Object setContentView() {
        return R.layout.page_sort;
    }

    @Override
    public void init() {
        bt_item_type_1 = view.findViewById(R.id.bt_item_type_1);
        bt_item_type_2 = view.findViewById(R.id.bt_item_type_2);
        bt_item_type_3 = view.findViewById(R.id.bt_item_type_3);
        bt_item_type_4 = view.findViewById(R.id.bt_item_type_4);

        bt_item_type_1.setOnClickListener(this);
        bt_item_type_2.setOnClickListener(this);
        bt_item_type_3.setOnClickListener(this);
        bt_item_type_4.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_item_type_1:
            case R.id.bt_item_type_2:
            case R.id.bt_item_type_3:
            case R.id.bt_item_type_4:
                context.startActivity(new Intent(context, ListItemActivity.class));
                break;
        }
    }
}
