package com.example.yunchebao.friendcircle.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.yunchebao.MyApplication;
import com.bbcircle.view.SampleCoverVideo;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.yunchebao.R;
import com.example.yunchebao.drive.activity.SimplePlayerActivity;
import com.example.yunchebao.friendcircle.NewFriendCircleActivity;
import com.example.yunchebao.view.NineGridTestLayout;
import com.example.yunchebao.washrepair.NewWashrepairDetailActivity;
import com.github.chrisbanes.photoview.PhotoView;
import com.github.chrisbanes.photoview.PhotoViewAttacher;
import com.luffy.imagepreviewlib.core.PictureConfig;
import com.lzy.ninegrid.ImageInfo;
import com.lzy.ninegrid.NineGridView;
import com.lzy.ninegrid.NineGridViewAdapter;
import com.lzy.ninegrid.preview.NineGridViewClickAdapter;
import com.maning.imagebrowserlibrary.ImageEngine;
import com.maning.imagebrowserlibrary.MNImageBrowser;
import com.maning.imagebrowserlibrary.listeners.OnClickListener;
import com.maning.imagebrowserlibrary.listeners.OnLongClickListener;
import com.maning.imagebrowserlibrary.listeners.OnPageChangeListener;
import com.maning.imagebrowserlibrary.model.ImageBrowserConfig;
import com.newversion.CircleData;


import com.newversion.DynamicCommonsAdapter;
import com.newversion.DynamicPicGridAdapter;
import com.newversion.InputTextMsgDialog;
import com.newversion.LikesView;
import com.newversion.MyGridView;
import com.payencai.library.view.CircleImageView;
import com.tool.PicassoImageEngine;
import com.tool.listview.PersonalListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import indi.liyi.viewer.ImageViewer;

/**
 * 作者：凌涛 on 2019/4/26 17:30
 * 邮箱：771548229@qq..com
 */
public class CircleDataAdapter extends BaseQuickAdapter<CircleData, BaseViewHolder> {
    private NewFriendCircleActivity friendsCircleActivity;

    private DynamicCommonsAdapter commonsAdapter;
    /**
     * 动态类型  0 图片说说  1 小视频说说 2 转发链接  3 纯文字说说
     */
    private int dynamicType = 0;
    private InputTextMsgDialog inputTextMsgDialog;
    public CircleDataAdapter(int layoutResId, @Nullable List<CircleData> data) {
        super(layoutResId, data);
    }
    public CircleDataAdapter(Context context,int layoutResId, @Nullable List<CircleData> data) {
        this(layoutResId, data);
        friendsCircleActivity= (NewFriendCircleActivity) context;
    }

    @Override
    protected void convert(BaseViewHolder helper, CircleData item) {
        helper.setIsRecyclable(false);
        helper.addOnClickListener(R.id.iv_head);
        CircleImageView head = (CircleImageView) helper.getView(R.id.iv_head);
        TextView name = (TextView) helper.getView(R.id.tv_name);
        TextView time = (TextView) helper.getView(R.id.tv_time);
        TextView content = (TextView) helper.getView(R.id.tv_dynamic_content);
        TextView tv_location = (TextView) helper.getView(R.id.tv_location);
        FrameLayout frame_video_player = (FrameLayout) helper.getView(R.id.frame_video_player);
        ImageView iv_play = (ImageView) helper.getView(R.id.iv_play);


        ImageView iv_video = (ImageView) helper.getView(R.id.iv_video);
        ImageView iv_delete = (ImageView) helper.getView(R.id.iv_delete);
        ImageView iv_replay = (ImageView) helper.getView(R.id.iv_replay);
        LikesView likeView = (LikesView) helper.getView(R.id.likeView);
        NineGridTestLayout gv_nine=helper.getView(R.id.gv_nine);

        PersonalListView lv_comment = (PersonalListView) helper.getView(R.id.lv_comment);
        lv_comment.setFocusableInTouchMode(false);
        lv_comment.setFocusable(false);
        if (item.getCommentList() == null || item.getCommentList().size() == 0) {
            lv_comment.setVisibility(View.GONE);
        } else {
            lv_comment.setVisibility(View.VISIBLE);
            commonsAdapter = new DynamicCommonsAdapter(mContext, item.getCommentList());
            lv_comment.setAdapter(commonsAdapter);
        }

        if(!TextUtils.isEmpty(item.getAddress())){
            tv_location.setText(item.getAddress());
        }
        frame_video_player.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SimplePlayerActivity.class);
                intent.putExtra("img", item.getVimg());
                intent.putExtra("video", item.getVideo());
                mContext.startActivity(intent);
            }
        });
        likeView.setListener(new LikesView.onItemClickListener() {
            @Override
            public void onItemClick(CircleData.ClickListBean bean) {
                String userId = bean.getUserId();
                // 跳转到他人朋友圈
                Intent intent = new Intent(mContext, NewFriendCircleActivity.class);
                intent.putExtra("userId", userId);
                mContext.startActivity(intent);
            }
        });

        if (item.getClickList() == null || item.getClickList().size() == 0) {
            likeView.setVisibility(View.GONE);
        } else {
            likeView.setVisibility(View.VISIBLE);
            likeView.setList(item.getClickList());
            likeView.notifyDataSetChanged();
        }

        if (!TextUtils.isEmpty(item.getImgs()) && item.getImgs().split(",").length > 0) {
            dynamicType = 0;//图片说说
            frame_video_player.setVisibility(View.GONE);
            List<String> dynamicPicList = Arrays.asList(item.getImgs().split(","));
            ArrayList<String> images=new ArrayList<>();
            images.addAll(dynamicPicList);
            gv_nine.setIsShowAll(true); //当传入的图片数超过9张时，是否全部显示
            gv_nine.setSpacing(5); //动态设置图片之间的间隔
            gv_nine.setUrlList(images); //最后再设置图片url
        } else if (!TextUtils.isEmpty(item.getVideo())) {
            dynamicType = 1;//视频说说

            frame_video_player.setVisibility(View.VISIBLE);
            String videoPath = item.getVideo();
            String vimg=item.getVimg();
            Glide.with(mContext).load(vimg).into(iv_video);
        } else if (item.getType() == 2) {
            dynamicType = 2;//转发链接
            frame_video_player.setVisibility(View.GONE);

            //ivVimg.setVisibility(View.GONE);
        } else if(item.getType()==3){
            frame_video_player.setVisibility(View.GONE);
            dynamicType = 3;//纯文字说说

            //ivVimg.setVisibility(View.GONE);
        }

        ImageView ivPrise = (ImageView) helper.getView(R.id.iv_prise);

        Glide.with(mContext).load(item.getHeadPortrait()).into(head);
        name.setText(item.getName());
        time.setText(item.getCreateTime());

        if (TextUtils.isEmpty(item.getContent())) {
            content.setVisibility(View.GONE);
        } else {
            content.setVisibility(View.VISIBLE);
            content.setText(item.getContent());
        }
        if (TextUtils.equals(item.getUserId(), MyApplication.getUserInfo().getId())) {
            //我自己的朋友圈
            iv_delete.setVisibility(View.VISIBLE);
            iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    friendsCircleActivity.showDeleteCirclePoppupWindow(iv_delete, item.getId());
                }
            });
        } else {
            iv_delete.setVisibility(View.GONE);
        }


        iv_replay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addReplay(item,helper.getAdapterPosition());
            }
        });

        if (item.getIsClick().equals("1")) {
            ivPrise.setImageResource(R.mipmap.icon_praise_selected);
        } else {
            ivPrise.setImageResource(R.mipmap.icon_praise_normal);
        }

        ivPrise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean didPraise;

                if (item.getIsClick().equals("1")) {
                    didPraise = false;
                    item.setIsClick("0");
                    ivPrise.setImageResource(R.mipmap.icon_praise_normal);

                    for (int i = 0; i < item.getClickList().size(); i++) {
                        if (item.getClickList().get(i).getUserId().equals(MyApplication.getUserInfo().getId())) {
                            item.getClickList().remove(i);
                        }
                    }

                } else {
                    didPraise = true;
                    item.setIsClick("1");
                    ivPrise.setImageResource(R.mipmap.icon_praise_selected);

                    CircleData.ClickListBean clickListBean = new CircleData.ClickListBean();
                    clickListBean.setHeadPortrait(MyApplication.getUserInfo().getHeadPortrait());
                    clickListBean.setName(MyApplication.getUserInfo().getName());
                    clickListBean.setUserId(MyApplication.getUserInfo().getId());

                    if (!item.getClickList().contains(clickListBean)) {
                        item.getClickList().add(clickListBean);
                    }
                }
                likeView.setList(item.getClickList());

                if (item.getClickList() == null || item.getClickList().size() == 0) {
                    likeView.setVisibility(View.GONE);
                } else {
                    likeView.setVisibility(View.VISIBLE);
                    likeView.notifyDataSetChanged();
                }
                //notifyItemChanged(helper.getAdapterPosition());
                friendsCircleActivity.performPraise(didPraise, item.getId());
            }
        });

//        head.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String userId = item.getUserId();
//                // 跳转到他人朋友圈
//                Intent intent = new Intent(mContext, NewFriendCircleActivity.class);
//                intent.putExtra("userId", userId);
//                mContext.startActivity(intent);
//            }
//        });


    }
    /**
     * 弹窗输入回复内容
     *
     * @param circleInfo
     */
    public void addReplay(CircleData circleInfo,int pos) {
        inputTextMsgDialog = new InputTextMsgDialog(mContext, R.style.dialog_center);
        inputTextMsgDialog.setHint("评论");
        inputTextMsgDialog.setmOnTextSendListener(new InputTextMsgDialog.OnTextSendListener() {
            @Override
            public void onTextSend(String msg) {
                friendsCircleActivity.addReplay(circleInfo.getId(), msg);
                CircleData.CommentListBean commentListBean = new CircleData.CommentListBean();
                commentListBean.setContent(msg);
                commentListBean.setName(MyApplication.getUserInfo().getName());
                commentListBean.setUserId(MyApplication.getUserInfo().getId());

                commentListBean.setType(1);//表示添加回复

                circleInfo.getCommentList().add(commentListBean);
                if(commonsAdapter!=null){
                    commonsAdapter.notifyDataSetChanged();
                }
                notifyItemChanged(pos);
            }
        });

        inputTextMsgDialog.show();
    }
}
