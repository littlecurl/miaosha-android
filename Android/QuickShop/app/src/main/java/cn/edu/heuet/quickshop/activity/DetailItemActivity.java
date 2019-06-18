package cn.edu.heuet.quickshop.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;

import cn.edu.heuet.quickshop.R;
import cn.edu.heuet.quickshop.constant.NetConstant;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DetailItemActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "DetailActivity";
    Button bt_submit_order;

    TextView tv_detail_ing;
    TextView tv_detail_title;
    TextView tv_detail_description;
    TextView tv_detail_price;
    TextView tv_detail_quickprice;
    ImageView iv_detail_img;
    TextView tv_detail_stock;
    TextView tv_detail_sales;
    private String id;
    private String title;
    private String description;
    private String price;
    private String imgUrl;
    private String sales;
    private String stock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullScreenConfig();
        setContentView(R.layout.activity_detail_item);

        initUI();

        setOnClickListener();
        getIntentData();

        bandData();


    }


    private void initUI() {
        bt_submit_order = findViewById(R.id.bt_submit_order);

        tv_detail_ing = findViewById(R.id.tv_detail_ing);
        tv_detail_title = findViewById(R.id.tv_detail_title);
        tv_detail_description = findViewById(R.id.tv_detail_description);
        tv_detail_price = findViewById(R.id.tv_detail_price);

        tv_detail_quickprice = findViewById(R.id.tv_detail_quickprice);

        iv_detail_img = findViewById(R.id.iv_detail_img);
        tv_detail_stock = findViewById(R.id.tv_detail_stock);
        tv_detail_sales = findViewById(R.id.tv_detail_sales);
    }


    private void setOnClickListener() {
        bt_submit_order.setOnClickListener(this);
    }

    private void getIntentData() {
        Intent it_detail = getIntent();
        id = it_detail.getStringExtra("id");
        title = it_detail.getStringExtra("title");
        description = it_detail.getStringExtra("description");
        price = it_detail.getStringExtra("price");

        //String quickprice = it_detail.getStringExtra("quickprice");

        imgUrl = it_detail.getStringExtra("imgUrl");
        sales = it_detail.getStringExtra("sales");
        stock = it_detail.getStringExtra("stock");
    }

    private void bandData() {
//        tv_detail_ing.setText();
        tv_detail_title.setText(title);
        tv_detail_description.setText(description);
        tv_detail_price.setText("￥ " + price);
//        tv_detail_quickprice.setText();
        Glide.with(DetailItemActivity.this).load(imgUrl).into(iv_detail_img);
        tv_detail_stock.setText(stock);
        tv_detail_sales.setText(sales);
    }


    // 全屏显示
    private void fullScreenConfig() {
        // 去除ActionBar(因使用的是NoActionBar的主题，故此句有无皆可)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 去除状态栏，如 电量、Wifi信号等
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_submit_order:
                asyncValidate(id, "1");
                break;
        }
    }

    /*
      okhttp异步POST请求 要求API level 21+
      account 本来想的是可以是 telphone或者username
      但目前只实现了telphone
     */
    private void asyncValidate(final String itemId, final String amount) {
        /*
         发送请求属于耗时操作，所以开辟子线程执行
         上面的参数都加上了final，否则无法传递到子线程中
        */
        new Thread(new Runnable() {
            @Override
            public void run() {
                // okhttp异步POST请求； 总共5步
                // 1、初始化okhttpClient对象
                OkHttpClient okHttpClient = new OkHttpClient();
                // 2、构建请求体requestBody
                RequestBody requestBody = new FormBody.Builder()
                        .add("itemId", itemId)
                        .add("amount", amount)
                        .build();
                // 3、发送请求，因为要传密码，所以用POST方式
                Request request = new Request.Builder()
                        .url(NetConstant.getSubmitOrderURL())
                        .post(requestBody)
                        .build();
                // 4、使用okhttpClient对象获取请求的回调方法，enqueue()方法代表异步执行
                okHttpClient.newCall(request).enqueue(new Callback() {
                    // 5、重写两个回调方法
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d(TAG, "请求URL失败： " + e.getMessage());
                        showToastInThread(DetailItemActivity.this, "请求URL失败, 请重试！");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        // 先判断一下服务器是否异常
                        String responseStr = response.toString();
                        if (responseStr.contains("200")) {
                            /* 使用Gson解析response的JSON数据的第一步 */
                            String responseBodyStr = response.body().string();
                            /* 使用Gson解析response的JSON数据的第二步 */
                            JsonObject responseBodyJSONObject = (JsonObject) new JsonParser().parse(responseBodyStr);
                            // 如果返回的status为success，则getStatus返回true，登录验证通过
                            if (getStatus(responseBodyJSONObject).equals("success")) {
                                // 下单成功，执行跳转
                                showToastInThread(DetailItemActivity.this, "下单成功！");
                            } else {
                                Log.d(TAG, "用户未登陆或商品信息异常");
                                getResponseErrMsg(DetailItemActivity.this, responseBodyJSONObject);
                            }
                        } else {
                            Log.d(TAG, "服务器异常");
                            showToastInThread(DetailItemActivity.this, responseStr);
                        }
                    }
                });

            }
        }).start();
    }


    /*
   使用Gson解析response的JSON数据
   本来总共是有三步的，一、二步在方法调用之前执行了
 */
    private String getStatus(JsonObject responseBodyJSONObject) {
        /* 使用Gson解析response的JSON数据的第三步
           通过JSON对象获取对应的属性值 */
        String status = responseBodyJSONObject.get("status").getAsString();
        // 登录成功返回的json为{ "status":"success", "data":null }
        // 只获取status即可，data为null
        return status;
    }

    /*
      使用Gson解析response返回异常信息的JSON中的data对象
      这也属于第三步，一、二步在方法调用之前执行了
     */
    private void getResponseErrMsg(Context context, JsonObject responseBodyJSONObject) {
        JsonObject dataObject = responseBodyJSONObject.get("data").getAsJsonObject();
        String errorCode = dataObject.get("errorCode").getAsString();
        String errorMsg = dataObject.get("errorMsg").getAsString();
        Log.d(TAG, "errorCode: " + errorCode + " errorMsg: " + errorMsg);
        // 用户未登陆，跳转登陆
        if (errorCode.equals("20003")) {
            Intent it_to_login = new Intent(DetailItemActivity.this, LoginActivity.class);
            startActivity(it_to_login);
        }
        // 在子线程中显示Toast
        showToastInThread(context, errorMsg);
    }


    // 实现在子线程中显示Toast
    private void showToastInThread(Context context, String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
            }
        });
    }
}
