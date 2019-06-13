package cn.edu.heuet.login;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.edu.heuet.login.adapter.ItemVOAdapter;
import cn.edu.heuet.login.common.Constant;
import cn.edu.heuet.login.viewobject.ItemVO;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;


public class ListItemActivity extends Activity {

    private final String TAG = "ListItemActivity";
    private SwipeRefreshLayout swipeRefresh;
    private ItemVOAdapter itemVOAdapter;
    private String URL = Constant.getGetItemListURL();
    private List<ItemVO> itemVOList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_item);
        // 查找控件
        RecyclerView recyclerView = findViewById(R.id.rv_item_list);
        // 设置布局管理器
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        // 设置数据源
        itemVOList = new ArrayList<>();
        itemVOAdapter = new ItemVOAdapter(this, itemVOList);
        // 设置适配器
        recyclerView.setAdapter(itemVOAdapter);
        // 加载网络数据
        loadUrlData(URL);
        // 下拉刷新
        swipeRefresh = findViewById(R.id.sr_item_list);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        refreshItemList();
                        itemVOAdapter.notifyDataSetChanged();
                        swipeRefresh.setRefreshing(false);
                    }
                }
        );

    }

    // 下拉刷新的实现
    private void refreshItemList() {
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loadUrlData(URL);
                                itemVOAdapter.notifyDataSetChanged();
                                swipeRefresh.setRefreshing(false);
                                Log.d(TAG, "刷新完成！");
                            }
                        });
                    }
                }).start();
    }

    // 加载网络数据的实现
    @SuppressLint("StaticFieldLeak")
    private void loadUrlData(final String url) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                // 执行网络请求
                String responseBodyStr = getResponseBodyStrByokhttp(url);
                return responseBodyStr;
            }

            @Override
            protected void onPostExecute(String responseBodyStr) {
                if ( !TextUtils.isEmpty(responseBodyStr) ) {
                    // 使用前先清除数据
                    itemVOList.clear();
                    // 解析数据
                    // 这里不能使用赋值语法，必须使用AddAll，否则无法显示数据
                    itemVOList.addAll(parseJSONData(responseBodyStr));
                    // 打乱数据
                    Collections.shuffle(itemVOList);
                    // 提示adapter更新数据
                    itemVOAdapter.notifyDataSetChanged();
                }
            }
        }.execute();
    }

    // okhttp获取ResponseBodyStr
    private String getResponseBodyStrByokhttp(String path) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().get().url(path).build();
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                ResponseBody body = response.body();
                return body.string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    // 解析JSON数据的实现
    private List<ItemVO> parseJSONData(String responseBodyStr) {
        JsonObject responseBodyJSONObject = (JsonObject) new JsonParser().parse(responseBodyStr);
        // 响应成功
        if (getStatus(responseBodyJSONObject).equals("success")) {
            // JSON解析为List
            List<ItemVO> jsonToList = parseJSONToList(responseBodyJSONObject);
            Log.d(TAG, "响应success");
            return jsonToList;
        } else {
            getResponseErrMsg(ListItemActivity.this, responseBodyJSONObject);
            Log.d(TAG, "响应fail,获取商品信息失败");
        }
        return null;
    }

    // 获取响应状态
    private String getStatus(JsonObject responseBodyJSONObject) {
        String status = responseBodyJSONObject.get("status").getAsString();
        return status;
    }

    // 注意这里的返回值不在是单个JsonObject，而是JsonArray
    // 强大的Gson可以快速将JsonArray转为List
    private List<ItemVO> parseJSONToList(JsonObject responseBodyJSONObject) {
        JsonArray jsonArray = responseBodyJSONObject.get("data").getAsJsonArray();
        Log.d(TAG, "size: " + jsonArray.size());
        Type listType = new TypeToken<List<ItemVO>>() {
        }.getType();
        List<ItemVO> list = new Gson().fromJson(jsonArray, listType);
        return list;
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
