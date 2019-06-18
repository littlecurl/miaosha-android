package cn.edu.heuet.quickshop.activity;


import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;

import java.util.ArrayList;
import java.util.List;

import cn.edu.heuet.quickshop.R;
import cn.edu.heuet.quickshop.adapter.MainPageAdapter;
import cn.edu.heuet.quickshop.page.BasePage;
import cn.edu.heuet.quickshop.page.MinePageFactory;
import cn.edu.heuet.quickshop.page.ShoppingPageFactory;
import cn.edu.heuet.quickshop.page.SortPageFactory;

import static com.ashokvarma.bottomnavigation.BottomNavigationBar.BACKGROUND_STYLE_RIPPLE;


public class MainActivity extends AppCompatActivity {
    // BottomNavigationBar是一个第三方开源库类
    public BottomNavigationBar buttonBottom;

    // ViewPager 是Android官方库类
    public ViewPager viewPager;

    // 所有的ViewPager页面都继承BasePage，故将其父类作为泛型
    private List<BasePage> mList;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullScreenConfig();
        setContentView(R.layout.activity_main);
        // 初始化UI
        initUI();

        initBottomButton();

        initViewpager();

        initListener();
    }

    // 全屏显示
    private void fullScreenConfig() {
        // 去除ActionBar(因使用的是NoActionBar的主题，故此句有无皆可)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 去除状态栏，如 电量、Wifi信号等
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void initUI() {
        buttonBottom = findViewById(R.id.btn_bottom_navigation);
        viewPager = findViewById(R.id.viewpager);
    }

    // 底部导航处理
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

    // viewpager的操作
    private void initViewpager() {
        initViewpagerData();
        viewPager.setAdapter(new MainPageAdapter(mList));
    }

    // viewpager 的数据源
    private void initViewpagerData() {
        mList = new ArrayList<>();
        mList.add(new SortPageFactory(MainActivity.this).produce());
        mList.add(new ShoppingPageFactory(MainActivity.this).produce());
        mList.add(new MinePageFactory(MainActivity.this).produce());
    }

    // viewpager与BottomNavigationBar 的事件监听
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


}

