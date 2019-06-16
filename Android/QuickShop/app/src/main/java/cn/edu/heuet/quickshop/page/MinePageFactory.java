package cn.edu.heuet.quickshop.page;

import android.content.Context;

import cn.edu.heuet.quickshop.page.impl.MinePageImpl;


/**
 * Create by SunnyDay on 2019/03/16
 */
public class MinePageFactory implements Provider {

    Context context;

    public MinePageFactory(Context context) {
        this.context = context;
    }

    @Override
    public BasePage produce() {
        return new MinePageImpl(context);
    }
}
