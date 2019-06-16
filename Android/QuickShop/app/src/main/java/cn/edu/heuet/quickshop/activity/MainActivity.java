package cn.edu.heuet.quickshop.activity;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewPager;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.edu.heuet.quickshop.R;
import cn.edu.heuet.quickshop.activity.customview.ToastUtils;
import cn.edu.heuet.quickshop.adapter.MainPageAdapter;
import cn.edu.heuet.quickshop.page.BasePage;
import cn.edu.heuet.quickshop.page.MinePageFactory;
import cn.edu.heuet.quickshop.page.ShoppingPageFactory;
import cn.edu.heuet.quickshop.page.SortPageFactory;

import static com.ashokvarma.bottomnavigation.BottomNavigationBar.BACKGROUND_STYLE_RIPPLE;


public class MainActivity extends BaseActivity {
    // BottomNavigationBar是一个第三方开源库类
    @BindView(R.id.btn_bottom_navigation)
    public BottomNavigationBar buttonBottom;

    // ViewPager 是Android官方库类
    @BindView(R.id.viewpager)
    public ViewPager viewPager;

    // 所有的ViewPager页面都继承BasePage，故将其父类作为泛型
    private List<BasePage> mList;

    // 提供本页面布局
    @Override
    public Object offerLayout() {
        return R.layout.activity_main;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindView() {
        // 设置沉浸式状态栏
        setImmersionStatusBar();
        // 初始化UI
        initBottomButton();

        initViewpager();

        initListener();
    }

    /**
     * 底部导航处理
     */
    private void initBottomButton() {
        buttonBottom
                .setBackgroundStyle(BACKGROUND_STYLE_RIPPLE)  // 点击样式
                .setBarBackgroundColor(R.color.red) // 字体 、图标 背景颜色
                .setInActiveColor(R.color.gray) // 未选中状态颜色
                .setActiveColor(R.color.white) // 条目背景色
                .addItem(new BottomNavigationItem(R.drawable.fenlei, "分类"))
                .addItem(new BottomNavigationItem(R.drawable.gouwuche, "购物车"))
                .addItem(new BottomNavigationItem(R.drawable.wode, "我的"))
                .initialise();
    }

    /**
     * viewpager的操作
     */
    private void initViewpager() {
        initViewpagerData();
        viewPager.setAdapter(new MainPageAdapter(mList));
    }

    /**
     * viewpager 的数据源
     */
    private void initViewpagerData() {
        mList = new ArrayList<>();
        mList.add(new SortPageFactory(MainActivity.this).produce());
        mList.add(new ShoppingPageFactory(MainActivity.this).produce());
        mList.add(new MinePageFactory(MainActivity.this).produce());
    }

    /**
     * viewpager与BottomNavigationBar 的事件监听
     */
    private void initListener() {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                buttonBottom.selectTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        buttonBottom.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position) {
                viewPager.setCurrentItem(position);
            }

            @Override
            public void onTabUnselected(int position) {
            }

            @Override
            public void onTabReselected(int position) {
            }
        });
    }

    @Override
    public void destory() {

    }


    /**
     * 扫码回调
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                ToastUtils.showToast(this,"ModelMall：您取消了扫码",ToastUtils.LENGTH_LONG);
            } else {
                ToastUtils.showToast(this, "ModelMall:扫码结果" + result.getContents(), ToastUtils.LENGTH_LONG);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}

