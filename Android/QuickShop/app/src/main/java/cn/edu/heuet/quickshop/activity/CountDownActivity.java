package cn.edu.heuet.quickshop.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import cn.edu.heuet.quickshop.R;
import cn.edu.heuet.quickshop.constant.ModelConstant;
import cn.edu.heuet.quickshop.util.ModelPreference;

public class CountDownActivity extends AppCompatActivity {
    // 右上角的文字控件
    private AppCompatTextView countDownText;
    private CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fullScreenConfig();

        // 上面全屏的代码一定要写在setContentView之前
        setContentView(R.layout.activity_count_down);

        initUI();

        initCountDown();


    }



    // 全屏显示
    private void fullScreenConfig() {
        // 去除ActionBar(因使用的是NoActivity的主题，故此句有无皆可)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 去除状态栏，如 电量、Wifi信号等
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void initUI() {
        countDownText = findViewById(R.id.tv_count_down);
    }

    // 倒计时逻辑
    private void initCountDown() {
        if (!isFinishing()) {
            timer = new CountDownTimer(1000 * 6, 1000) {
                @SuppressLint("SetTextI18n")
                @Override
                public void onTick(long millisUntilFinished) {
                    countDownText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            checkToJump();
                        }
                    });
                    int time = (int) millisUntilFinished;
                    countDownText.setText(time / 1000 + " 跳过");
                }

                @Override
                public void onFinish() {
                    checkToJump();
                }
            }.start();
        }
    }

    // 首次进入引导页判断
    private void checkToJump() {
        boolean isFirstin = ModelPreference.getBoolean(CountDownActivity.this, ModelConstant.FIRST_IN, true);
        if (isFirstin) {
            startActivity(new Intent(CountDownActivity.this, GuideActivity.class));
            ModelPreference.putBoolean(CountDownActivity.this, ModelConstant.FIRST_IN, false);
        } else {
            startActivity(new Intent(CountDownActivity.this, LoginActivity.class));
        }
        // 回收内存
        destoryTimer();
        finish();
    }

    public void destoryTimer() {
        // 避免内存泄漏
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}
