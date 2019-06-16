package cn.edu.heuet.quickshop.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

public class CreateItemActivity extends AppCompatActivity implements View.OnClickListener {

    EditText et_title;
    EditText et_description;
    EditText et_price;
    EditText et_img_url;
    EditText et_stock;
    Button bt_submit_create;
    Button bt_directly_login;

    final String TAG = "CreateItemActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_item);

        initUI();

        setOnClickListener();


    }

    private void initUI() {
        et_title = findViewById(R.id.et_title);
        et_description = findViewById(R.id.et_description);
        et_price = findViewById(R.id.et_price);
        et_img_url = findViewById(R.id.et_img_url);
        et_stock = findViewById(R.id.et_stock);
        bt_submit_create = findViewById(R.id.bt_submit_create);
        bt_directly_login = findViewById(R.id.bt_directly_login);
    }

    private void setOnClickListener() {
        bt_submit_create.setOnClickListener(this);
        bt_directly_login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_submit_create:
                // 操作数据库，属于耗时操作
                createItem();
                break;

            case R.id.bt_directly_login:
                Intent it_directly_login = new Intent(CreateItemActivity.this, MainActivity.class);
                startActivity(it_directly_login);
                break;
        }
    }

    private void createItem() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String title = et_title.getText().toString();
                String description = et_description.getText().toString();
                String price = et_price.getText().toString();
                String imgUrl = et_img_url.getText().toString();
                String stock = et_stock.getText().toString();
                // 先进行判空
                if (TextUtils.isEmpty(title) || TextUtils.isEmpty(description) || TextUtils.isEmpty(price)
                    || TextUtils.isEmpty(imgUrl) || TextUtils.isEmpty(stock)
                ){
                    showToastInThread(CreateItemActivity.this, "还有数据未填写");
                } else {

                    // okhttp
                    // okhttp异步POST请求； 总共5步
                    // 1、初始化okhttpClient对象
                    OkHttpClient okHttpClient = new OkHttpClient();
                    // 2、构建请求体requestBody
                    RequestBody requestBody = new FormBody.Builder()
                            .add("title", title)
                            .add("description", description)
                            .add("price", price)
                            .add("imgUrl", imgUrl)
                            .add("stock", stock)
                            .build();
                    // 3、发送请求，因为要传密码，所以用POST方式
                    Request request = new Request.Builder()
                            .url(NetConstant.getCreateItemURL())
                            .post(requestBody)
                            .build();
                    // 4、使用okhttpClient对象获取请求的回调方法，enqueue()方法代表异步执行
                    okHttpClient.newCall(request).enqueue(new Callback() {
                        // 5、重写两个回调方法
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.d(TAG, "请求URL失败： " + e.getMessage());
                            showToastInThread(CreateItemActivity.this, "请求URL失败, 请重试！");
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
                                if (getStatus(responseBodyJSONObject)) {
                                    showToastInThread(CreateItemActivity.this, "创建成功");
                                    // 创建成功后，不像视频中那样直接登录
                                    // 而是可以多次次创建
                                } else {
                                    getResponseData(CreateItemActivity.this, responseBodyJSONObject);
                                }
                            } else {
                                Log.d(TAG, "服务器异常");
                                showToastInThread(CreateItemActivity.this, responseStr);
                            }
                        }
                    });
                }
            }
        }).start();


    }

    /*
     使用Gson解析response的JSON数据
     本来总共是有三步的，一、二步在方法调用之前执行了
   */
    private boolean getStatus(JsonObject responseBodyJSONObject) {
        /* 使用Gson解析response的JSON数据的第三步
           通过JSON对象获取对应的属性值 */
        String status = responseBodyJSONObject.get("status").getAsString();
        // 登录成功返回的json为{ "status":"success", "data":null }
        // 只获取status即可，data为null
        return status.equals("success");
    }

    /*
      使用Gson解析response返回异常信息的JSON中的data对象
      这也属于第三步，一、二步在方法调用之前执行了
     */
    private void getResponseData(Context context, JsonObject responseBodyJSONObject) {
        JsonObject dataObject = responseBodyJSONObject.get("data").getAsJsonObject();
        String errorCode = dataObject.get("errorCode").getAsString();
        String errorMsg = dataObject.get("errorMsg").getAsString();
        Log.d(TAG, "errorCode: " + errorCode + " errorMsg: " + errorMsg);
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
