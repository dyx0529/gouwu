package com.example.android_shopping_2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {
    private RecyclerView mRv;
    /**
     * 全选
     */
    private CheckBox cb;
    /**
     * 0￥
     */
    private TextView mTv;
    private ArrayList<ShopBean> list;
    private ShopAdapter adapter;
    int mPage=1;
    private static final String TAG = "MainActivity";
    //丁雅鑫  H1811A
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    private void initData() {
        Retrofit build = new Retrofit.Builder()
                .baseUrl(ApiService.Url)
                .build();
        ApiService apiService = build.create(ApiService.class);
        apiService.getFoodList("1", "20", mPage++).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(final Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    final String result = response.body().string();
                    Log.i(TAG, "onResponse: result = " + result);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject jsonObject = new JSONObject(result);
                                JSONArray data = jsonObject.getJSONArray("data");
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject object = data.getJSONObject(i);
                                    String pic = object.optString("pic");
                                    String title = object.optString("title");
                                    int price = object.optInt("num");

                                    ShopBean car = new ShopBean();
                                    car.setImgUrl(pic);
                                    car.setTitle(title);
                                    car.setPrice((int) (price * 0.1));
                                    list.add(car);
                                }
                                adapter.setList(list);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.i(TAG, "onFailure: 错误 = " + t.getMessage());
            }
        });
    }

    private void initView() {
        mRv = (RecyclerView) findViewById(R.id.rv);
        mTv = (TextView) findViewById(R.id.tv);
        mRv.setLayoutManager(new LinearLayoutManager(this));
        mRv.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        list = new ArrayList<>();
        adapter = new ShopAdapter(list, this,mTv,cb);
        mRv.setAdapter(adapter);
        adapter.setOnClick(new ShopAdapter.onClick() {

            private int sum;


            @Override
            public void onClickListenner(int postion, View v) {
                ShopBean bean = list.get(postion);
                int price = bean.getPrice();
                cb = v.findViewById(R.id.cb);
                boolean checked = cb.isChecked();
                if (checked){
                    adapter.list.get(postion).setChicket(true);
                    sum += Integer.parseInt(String.valueOf(price));
                }else {
                    adapter.list.get(postion).setChicket(false);
                    sum -= Integer.parseInt(String.valueOf(price));
                }
                mTv.setText(sum +"￥");
            }
        });

    }
}
