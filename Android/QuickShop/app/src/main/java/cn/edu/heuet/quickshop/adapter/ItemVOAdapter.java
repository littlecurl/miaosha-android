package cn.edu.heuet.quickshop.adapter;
/*
适配器一旦写好了
基本就不用动了
至于填充数据
是在Activity中填充的
 */

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.DecimalFormat;
import java.util.List;

import cn.edu.heuet.quickshop.R;
import cn.edu.heuet.quickshop.activity.DetailItemActivity;
import cn.edu.heuet.quickshop.viewobject.ItemVO;

public class ItemVOAdapter extends RecyclerView.Adapter<ItemVOAdapter.ViewHolder> {
    private Context mContext;
    private List<ItemVO> mItemVOList;
    private ItemVO itemVO;
    private String id;
    private String price;
    private String imgUrl;
    private String description;
    private String sales;
    private String title;
    private String stock;

    public ItemVOAdapter(Context context, List<ItemVO> itemVOList) {
        mContext = context;
        mItemVOList = itemVOList;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView itemImg;
        TextView itemTitle;
        TextView itemPrice;
        TextView itemSales;
        TextView itemHidden;

        // 在ViewHolder构造方法中获取item的控件
        ViewHolder(View cdview) {
            super(cdview);
            cardView = (CardView) cdview;
            itemImg = cdview.findViewById(R.id.iv_item_img);
            itemTitle = cdview.findViewById(R.id.tv_item_title);
            itemPrice = cdview.findViewById(R.id.tv_item_price);
            itemSales = cdview.findViewById(R.id.tv_item_sales);
            itemHidden = cdview.findViewById(R.id.tv_item_hidden);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (mContext == null) {
            mContext = viewGroup.getContext();
        }
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item, viewGroup, false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder listItemViewHolder, int i) {
        // 获取每一个itemVO对象
        itemVO = mItemVOList.get(i);
        // id, 下单的时候需要
        id = itemVO.getId();
        // 名称
        title = itemVO.getTitle();
        // 图片Url
        imgUrl = itemVO.getImgUrl();
        // 描述
        description = itemVO.getDescription();
        // 价格 保留两位小数、默认四舍五入
        DecimalFormat df1 = new DecimalFormat("0.00");
        price = df1.format(itemVO.getPrice());
        // 销量
        sales = itemVO.getSales();
        // 库存
        stock = itemVO.getStock();

        // 绑定对象属性到布局中
        // 获取JSON反序列化出的对象的Url对应图片并填充
        Glide.with(mContext).load(imgUrl).into(listItemViewHolder.itemImg);
        // 获取JSON反序列化出的对象的title、price、sales并填充
        listItemViewHolder.itemTitle.setText(title);
        listItemViewHolder.itemPrice.setText(price);
        listItemViewHolder.itemSales.setText(sales);

        // 卡片点击事件
        listItemViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 这里跳转重点是获取到上下文
                Intent it_list_to_detail = new Intent(v.getContext(), DetailItemActivity.class);
                it_list_to_detail.putExtra("id",id);
                it_list_to_detail.putExtra("title", title);
                it_list_to_detail.putExtra("description", description);
                it_list_to_detail.putExtra("price", price);
                it_list_to_detail.putExtra("imgUrl", imgUrl);
                it_list_to_detail.putExtra("sales", sales);
                it_list_to_detail.putExtra("stock", stock);
                // 这里跳转重点是获取到上下文
                v.getContext().startActivity(it_list_to_detail);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mItemVOList.size();
    }

}
