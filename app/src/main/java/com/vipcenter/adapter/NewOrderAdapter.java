package com.vipcenter.adapter;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.coorchice.library.SuperTextView;
import com.entity.PhoneOrderEntity;
import com.example.yunchebao.R;
import com.example.yunchebao.maket.GoodDetailActivity;
import com.example.yunchebao.maket.MarketSelectListActivity;
import com.example.yunchebao.maket.adapter.GoodsOrderImageAdapter;
import com.example.yunchebao.maket.model.GoodList;
import com.tool.ActivityAnimationUtils;
import com.tool.ActivityConstans;
import com.tool.view.HorizontalListView;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：凌涛 on 2019/3/20 10:40
 * 邮箱：771548229@qq..com
 */
public class NewOrderAdapter extends BaseQuickAdapter<PhoneOrderEntity, BaseViewHolder> {

    GoodsOrderImageAdapter goodsOrderImageAdapter;

    public NewOrderAdapter(int layoutResId, @Nullable List<PhoneOrderEntity> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, PhoneOrderEntity item) {
        helper.setIsRecyclable(false);
        helper.addOnClickListener(R.id.lianxi)
                .addOnClickListener(R.id.quxiao)
                .addOnClickListener(R.id.fukuan)
                .addOnClickListener(R.id.wuliu)
                .addOnClickListener(R.id.shouhuo)
                .addOnClickListener(R.id.shouhou)
                .addOnClickListener(R.id.delete)
               .addOnClickListener(R.id.pingjia).addOnClickListener(R.id.st_see).addOnClickListener(R.id.tixing);
        TextView shopName = (TextView) helper.getView(R.id.shopName);
        HorizontalListView hl_good = (HorizontalListView) helper.getView(R.id.hl_good);

        LinearLayout root=helper.getView(R.id.root);
        hl_good.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return root.onTouchEvent(event);
            }
        });

        TextView orderStatus = (TextView) helper.getView(R.id.statusName);
        TextView totalNum = (TextView) helper.getView(R.id.item1);
        TextView totalPrice = (TextView) helper.getView(R.id.item2);
        SuperTextView delete = (SuperTextView) helper.getView(R.id.delete);
        SuperTextView quxiao = (SuperTextView) helper.getView(R.id.quxiao);
        SuperTextView shouhuo = (SuperTextView) helper.getView(R.id.shouhuo);
        SuperTextView shouhou = (SuperTextView) helper.getView(R.id.shouhou);
        SuperTextView pingjia = (SuperTextView) helper.getView(R.id.pingjia);
        SuperTextView lianxi = (SuperTextView) helper.getView(R.id.lianxi);
        SuperTextView fukuan = (SuperTextView) helper.getView(R.id.fukuan);
        SuperTextView tixing = (SuperTextView) helper.getView(R.id.tixing);
        SuperTextView yanchang = (SuperTextView) helper.getView(R.id.yanchang);
        SuperTextView wuliu = (SuperTextView) helper.getView(R.id.wuliu);
        SuperTextView zailai = (SuperTextView) helper.getView(R.id.zailai);
        SuperTextView st_see = (SuperTextView) helper.getView(R.id.st_see);
        totalNum.setText("共" + item.getNumber() + "件商品  需付款：");
        totalPrice.setText("￥" + item.getTotal());
        shopName.setText(item.getShopName());

        List<String> images = new ArrayList<>();
        for (int i = 0; i < item.getItemList().size(); i++) {
            images.add(item.getItemList().get(i).getCommodityImage());
        }
        goodsOrderImageAdapter = new GoodsOrderImageAdapter(helper.itemView.getContext(), images);
        hl_good.setAdapter(goodsOrderImageAdapter);
        if (item.getState() == 1) {
            orderStatus.setText("待付款");
            quxiao.setVisibility(View.VISIBLE);
            shouhuo.setVisibility(View.GONE);
            shouhou.setVisibility(View.GONE);
            pingjia.setVisibility(View.GONE);
            lianxi.setVisibility(View.VISIBLE);
            fukuan.setVisibility(View.VISIBLE);
            tixing.setVisibility(View.GONE);
            yanchang.setVisibility(View.GONE);
            wuliu.setVisibility(View.GONE);
            zailai.setVisibility(View.GONE);
        } else if (item.getState() == 2) {
            orderStatus.setText("待发货");
            quxiao.setVisibility(View.GONE);
            shouhuo.setVisibility(View.GONE);
            shouhou.setVisibility(View.VISIBLE);
            pingjia.setVisibility(View.GONE);
            lianxi.setVisibility(View.GONE);
            fukuan.setVisibility(View.GONE);
            tixing.setVisibility(View.VISIBLE);
            yanchang.setVisibility(View.GONE);
            wuliu.setVisibility(View.GONE);
            zailai.setVisibility(View.GONE);
        } else if (item.getState() == 3) {
            orderStatus.setText("待收货");
            quxiao.setVisibility(View.GONE);
            shouhuo.setVisibility(View.VISIBLE);
            shouhou.setVisibility(View.GONE);
            pingjia.setVisibility(View.GONE);
            lianxi.setVisibility(View.GONE);
            fukuan.setVisibility(View.GONE);
            tixing.setVisibility(View.GONE);
            yanchang.setVisibility(View.GONE);
            wuliu.setVisibility(View.VISIBLE);
            zailai.setVisibility(View.GONE);
        } else if (item.getState() == 4) {
            if(item.getIsComment()==1){
                orderStatus.setText("交易完成");
                quxiao.setVisibility(View.GONE);
                shouhuo.setVisibility(View.GONE);
                shouhou.setVisibility(View.GONE);
                pingjia.setVisibility(View.GONE);
                lianxi.setVisibility(View.GONE);
                fukuan.setVisibility(View.GONE);
                tixing.setVisibility(View.GONE);
                yanchang.setVisibility(View.GONE);
                wuliu.setVisibility(View.GONE);
                zailai.setVisibility(View.GONE);
                delete.setVisibility(View.VISIBLE);
            }else{
                orderStatus.setText("待评价");
                quxiao.setVisibility(View.GONE);
                shouhuo.setVisibility(View.GONE);
                shouhou.setVisibility(View.GONE);
                pingjia.setVisibility(View.VISIBLE);
                lianxi.setVisibility(View.GONE);
                fukuan.setVisibility(View.GONE);
                tixing.setVisibility(View.GONE);
                yanchang.setVisibility(View.GONE);
                wuliu.setVisibility(View.VISIBLE);
                zailai.setVisibility(View.GONE);
            }

        }else if(item.getState() == 5){
            orderStatus.setText("售后");
            st_see.setVisibility(View.VISIBLE);
        }else if(item.getState()==0){
            orderStatus.setText("交易取消");
            delete.setVisibility(View.VISIBLE);
        }

    }
}
