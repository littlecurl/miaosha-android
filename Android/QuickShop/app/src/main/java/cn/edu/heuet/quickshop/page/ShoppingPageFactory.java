package cn.edu.heuet.quickshop.page;

import android.content.Context;

import cn.edu.heuet.quickshop.page.impl.ShoppingPageImpl;


/**
 * Create by SunnyDay on 2019/03/16
 */
public class ShoppingPageFactory implements Provider {

    Context context;

    public ShoppingPageFactory(Context context) {
        this.context = context;
    }

    @Override
    public BasePage produce() {
        return new ShoppingPageImpl(context);
    }
}
