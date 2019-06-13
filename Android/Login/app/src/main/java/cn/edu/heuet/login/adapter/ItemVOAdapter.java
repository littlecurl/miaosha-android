package cn.edu.heuet.login.adapter;
/*
适配器一旦写好了
基本就不用动了
至于填充数据
是在Activity中填充的
 */
import android.content.Context;
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

import cn.edu.heuet.login.R;
import cn.edu.heuet.login.viewobject.ItemVO;

public class ItemVOAdapter extends RecyclerView.Adapter<ItemVOAdapter.ViewHolder> {
    private Context mContext;
    private List<ItemVO> mItemVOList;

    public ItemVOAdapter(Context context, List<ItemVO> itemVOList){
        mContext = context;
        mItemVOList = itemVOList;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        ImageView itemImg;
        TextView itemDescription;
        TextView itemPrice;
        TextView itemSales;
        TextView itemHidden;

        public ViewHolder(View cdview){
            super(cdview);
            cardView = (CardView) cdview;
            itemImg = cdview.findViewById(R.id.iv_item_img);
            itemDescription = cdview.findViewById(R.id.tv_item_description);
            itemPrice = cdview.findViewById(R.id.tv_item_price);
            itemSales = cdview.findViewById(R.id.tv_item_sales);
            itemHidden = cdview.findViewById(R.id.tv_item_hidden);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (mContext == null){
            mContext = viewGroup.getContext();
        }
        View view = LayoutInflater.from(mContext)
                                  .inflate(R.layout.item, viewGroup, false);

        return new ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        // 获取每一个itemVO对象
        ItemVO itemVO = mItemVOList.get(i);
        // 绑定对象属性到布局中
        // 图片
        Glide.with(mContext)
                .load(itemVO.getImgUrl())
                .into(viewHolder.itemImg);
        // 描述
        viewHolder.itemDescription.setText(itemVO.getDescription());
        // 价格 保留两位小数、默认四舍五入
        DecimalFormat df1 = new DecimalFormat("0.00");
        String price = df1.format(itemVO.getPrice());
        viewHolder.itemPrice.setText(price);
        // 销量
        viewHolder.itemSales.setText(itemVO.getSales());
    }

    @Override
    public int getItemCount() {
        return mItemVOList.size();
    }

}
