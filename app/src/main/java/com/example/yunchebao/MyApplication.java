package com.example.yunchebao;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.multidex.MultiDex;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;

import com.costans.PlatformContans;
import com.entity.PhoneUserEntity;
import com.example.yunchebao.net.NetUtils;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.gson.Gson;
import com.http.HttpProxy;
import com.http.ICallBack;
import com.http.processor.OkHttpProcessor;


import com.lzy.ninegrid.NineGridView;
import com.nohttp.Logger;
import com.nohttp.NoHttp;
import com.example.yunchebao.rongcloud.adapter.ListDataSave;
import com.example.yunchebao.rongcloud.sidebar.ContactModel;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.shuyu.gsyvideoplayer.utils.GSYVideoType;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tool.ExceptionHandler;
import com.tool.NineImageLoader;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;
import com.vipcenter.model.UserInfo;
import com.xiasuhuei321.loadingdialog.manager.StyleManager;
import com.xiasuhuei321.loadingdialog.view.LoadingDialog;
import com.yancy.gallerypick.utils.AppUtils;


import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.rong.callkit.util.SPUtils;
import io.rong.imkit.RongExtensionManager;
import io.rong.imkit.RongIM;
import io.rong.imkit.model.GroupUserInfo;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Group;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.message.ImageMessage;
import io.rong.message.RichContentMessage;
import io.rong.message.TextMessage;
import io.rong.message.VoiceMessage;
import io.rong.sight.SightExtensionModule;


/**
 * Created by pengying on 2017/3/9.
 */
public class MyApplication extends Application {
    private static MyApplication instance;
    private static Context context;
    private static UserInfo sUserInfo;
    public static IWXAPI mWxApi;
    private static List<ContactModel> sUserInfos;
    private static AMapLocation aMapLocation;
    public static String token;
    private Vibrator mVibrator;
    private Ringtone mRingtone;
    public static List<ContactModel> getUserInfos() {
        return sUserInfos;
    }


    public static AMapLocation getaMapLocation() {
        return aMapLocation;
    }

    public static void setaMapLocation(AMapLocation aMapLocation) {
        MyApplication.aMapLocation = aMapLocation;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }

    public static void setUserInfos(List<ContactModel> userInfos) {
        sUserInfos = userInfos;
    }

    private static ListDataSave dataSave;

    public static ListDataSave getDataSave() {
        return dataSave;
    }

    public static void setDataSave(ListDataSave listDataSave) {
        MyApplication.dataSave = listDataSave;
        ;
    }

    private PhoneUserEntity user;
    public static boolean isLogin;

    private static final String TAG = "MyApplication--->>>";

    public static UserInfo getUserInfo() {
        return sUserInfo;
    }

    public static void setUserInfo(UserInfo userInfo) {
        sUserInfo = userInfo;
    }

    public static boolean isIsLogin() {
        return isLogin;
    }

    public static void setIsLogin(boolean isLogin) {
        MyApplication.isLogin = isLogin;
    }

    private void registerToWX() {
        //第二个参数是指你应用在微信开放平台上的AppID
        mWxApi = WXAPIFactory.createWXAPI(this, "wx13acff5b460a0164", false);
        // 将该app注册到微信
        mWxApi.registerApp("wx13acff5b460a0164");

    }

    private void initLoading() {

        StyleManager s = new StyleManager();

//在这里调用方法设置s的属性
//code here...
        s.Anim(false).repeatTime(0).contentSize(-1).intercept(true);

        LoadingDialog.initStyle(s);
    }
    private void getPrivateStatus(String id){
        RongIM.getInstance().getConversationNotificationStatus(Conversation.ConversationType.PRIVATE, id, new RongIMClient.ResultCallback<Conversation.ConversationNotificationStatus>() {
            @Override
            public void onSuccess(Conversation.ConversationNotificationStatus conversationNotificationStatus) {
                final int value = conversationNotificationStatus.getValue();
                if (value == 1) {
                    notifyRing();
                   // notifyVibrator();
                }

            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                //ToastUtil.showToast(FriendDetailActivity.this, errorCode.getMessage() + "");
            }
        });
    }
    private void getGroupStatus(String hxCrowdId){
        RongIM.getInstance().getConversationNotificationStatus(Conversation.ConversationType.GROUP, hxCrowdId, new RongIMClient.ResultCallback<Conversation.ConversationNotificationStatus>() {
            @Override
            public void onSuccess(Conversation.ConversationNotificationStatus conversationNotificationStatus) {
                final int value = conversationNotificationStatus.getValue();

                if (value == 1) {
                    notifyRing();
                    //notifyVibrator();

                }

            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                //ToastUtil.showToast(GroupDetailActivity.this, errorCode.getMessage() + "");
            }
        });
    }
    private void showNickDialog() {
        final Dialog dialog = new Dialog(this, R.style.dialog);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_exit, null);
        //获得dialog的window窗口
        //将自定义布局加载到dialog上
        TextView tv_confirm= (TextView) dialogView.findViewById(R.id.tv_confirm);

        tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });
        dialog.setContentView(dialogView);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        Window window = dialog.getWindow();

        //设置dialog在屏幕底部
        window.setGravity(Gravity.CENTER);
        //设置dialog弹出时的动画效果，从屏幕底部向上弹出
        //获得window窗口的属性
        android.view.WindowManager.LayoutParams lp = window.getAttributes();
        //设置窗口宽度为充满全屏
        lp.height=WindowManager.LayoutParams.MATCH_PARENT;
        //设置窗口高度为包裹内容
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //将设置好的属性set回去
        window.setAttributes(lp);
    }
    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化震动通知
        if (isInitVibratorNotify()) {
            mVibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
        }
        NetUtils.getInstance().initNetWorkUtils(this);//okgo初始化
        if (isInitRingNotify()) {
            Uri notifyUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            mRingtone = RingtoneManager.getRingtone(this, notifyUri);
        }

        if (getApplicationInfo().packageName.equals(getCurProcessName(getApplicationContext()))) {
            RongIM.init(this);
            RongIM.getInstance().setSendMessageListener(new RongIM.OnSendMessageListener() {
                @Override
                public Message onSend(Message message) {

                    if (message.getConversationType().equals(Conversation.ConversationType.PRIVATE)) {
                        Map<String, String> params = new HashMap<>();
                        params.put("userId", MyApplication.getUserInfo().getId());
                        params.put("nickName", MyApplication.getUserInfo().getName());
                        params.put("avatar", MyApplication.getUserInfo().getHeadPortrait());
                        String extra = new Gson().toJson(params);
                        MessageContent messageContent = message.getContent();

                        if (messageContent instanceof TextMessage) {//文本消息
                            TextMessage textMessage = (TextMessage) messageContent;
                            textMessage.setExtra(extra);
                            message.setContent(textMessage);
                            Log.d(TAG, "onSent-TextMessage:" + textMessage.getContent());
                        } else if (messageContent instanceof ImageMessage) {//图片消息
                            ImageMessage imageMessage = (ImageMessage) messageContent;
                            imageMessage.setExtra(extra);
                            message.setContent(imageMessage);
                            Log.d(TAG, "onSent-ImageMessage:" + imageMessage.getRemoteUri());
                        } else if (messageContent instanceof VoiceMessage) {//语音消息
                            VoiceMessage voiceMessage = (VoiceMessage) messageContent;
                            voiceMessage.setExtra(extra);
                            message.setContent(voiceMessage);
                            Log.d(TAG, "onSent-voiceMessage:" + voiceMessage.getUri().toString());
                        } else if (messageContent instanceof RichContentMessage) {//图文消息
                            RichContentMessage richContentMessage = (RichContentMessage) messageContent;
                            richContentMessage.setExtra(extra);
                            message.setContent(richContentMessage);
                            Log.d(TAG, "onSent-RichContentMessage:" + richContentMessage.getContent());
                        } else {

                        }
                    }

                    return message;
                }

                @Override
                public boolean onSent(Message message, RongIM.SentMessageErrorCode sentMessageErrorCode) {
                    return false;
                }
            });
            RongIM.setOnReceiveMessageListener(new RongIMClient.OnReceiveMessageListener() {
                @Override
                public boolean onReceived(Message message, int i) {

                    String extra = "";
                    MessageContent messageContent = message.getContent();
                    if (messageContent instanceof TextMessage) {//文本消息
                        TextMessage textMessage = (TextMessage) messageContent;
                        extra = textMessage.getExtra();
                        Log.d(TAG, "onSent-TextMessage:" + textMessage.getContent());
                    } else if (messageContent instanceof ImageMessage) {//图片消息
                        ImageMessage imageMessage = (ImageMessage) messageContent;
                        extra = imageMessage.getExtra();
                        Log.d(TAG, "onSent-ImageMessage:" + imageMessage.getRemoteUri());
                    } else if (messageContent instanceof VoiceMessage) {//语音消息
                        VoiceMessage voiceMessage = (VoiceMessage) messageContent;
                        extra = voiceMessage.getExtra();
                        Log.d(TAG, "onSent-voiceMessage:" + voiceMessage.getUri().toString());
                    } else if (messageContent instanceof RichContentMessage) {//图文消息
                        RichContentMessage richContentMessage = (RichContentMessage) messageContent;
                        extra = richContentMessage.getExtra();
                        Log.d(TAG, "onSent-RichContentMessage:" + richContentMessage.getContent());
                    } else {

                    }

                    if (message.getConversationType().equals(Conversation.ConversationType.GROUP)) {
                        Log.e("extra", message.getTargetId()+"");
                        getGroupStatus(message.getMessageId()+"");
                        JSONObject jsonObject = null;
                        try {
                            if (!TextUtils.isEmpty(extra)) {

                                jsonObject = new JSONObject(extra);
                                String username = jsonObject.getString("nickName");
                                String groupName=jsonObject.getString("groupName");
                                String groupAvatar=jsonObject.getString("groupAvatar");

                                if (!TextUtils.isEmpty(username)) {
                                    GroupUserInfo info = new GroupUserInfo(message.getTargetId(), message.getSenderUserId(), username);
                                    RongIM.getInstance().refreshGroupUserInfoCache(info);
                                    RongIM.setGroupInfoProvider(new RongIM.GroupInfoProvider() {
                                        @Override
                                        public Group getGroupInfo(String s) {
                                            Group group=new Group(s,groupName,Uri.parse(groupAvatar));
                                            return group;
                                        }
                                    },true);
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    } else  {
                        Log.e("userid",message.getSenderUserId()+"");
                        getPrivateStatus(message.getSenderUserId()+"");
                        if (!TextUtils.isEmpty(extra)) {
                            try {
                                JSONObject jsonObject = new JSONObject(extra);
                                String userid = jsonObject.getString("userId");

                                String username = jsonObject.getString("nickName");
                                String avatar = jsonObject.getString("avatar");
                                avatar = avatar.replaceAll("\\\\", "");
                                String finalAvatar = avatar;

                                RongIM.setUserInfoProvider(new RongIM.UserInfoProvider() {
                                    @Override
                                    public io.rong.imlib.model.UserInfo getUserInfo(String s) {
                                        io.rong.imlib.model.UserInfo userInfo = new io.rong.imlib.model.UserInfo(userid, username, Uri.parse(finalAvatar));
                                        return userInfo;
                                    }
                                }, true);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    return true;
                }
            });
            RongExtensionManager.getInstance().registerExtensionModule(new SightExtensionModule());//小视频
        }
        CrashReport.initCrashReport(getApplicationContext(), "8acda1d42d", true); // bugly
        context = getApplicationContext();
        instance = this;
        user = new PhoneUserEntity();
        GSYVideoType.setShowType(GSYVideoType.SCREEN_MATCH_FULL);
        LitePal.initialize(this);
        initLoading();
        registerToWX();
        // 系统崩溃处理
        // initCrashHandler();
        //初始化Nohttp
        initNohttp();
        //初始化Fresco
        initFresco();
        //初始化EaseUI
        // 初始化代码需要在Application中完成。
        NineGridView.setImageLoader(new NineImageLoader());
        HttpProxy.init(new OkHttpProcessor());
        ZXingLibrary.initDisplayOpinion(this);
        initImageLoader();
        registerActivityLifecycleCallbacks(lifecycleCallbacks);
    }
    private void initImageLoader() {//九图
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(this);
        ImageLoader.getInstance().init(configuration);

    }
    private ActivityLifecycleCallbacks lifecycleCallbacks = new ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            if (!isLogin) {
                autoLogin();
            }
        }

        @Override
        public void onActivityStarted(Activity activity) {

        }

        @Override
        public void onActivityResumed(Activity activity) {
            if (!isLogin) {
                autoLogin();
            }
        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {

        }
    };


    private void autoLogin() {
        String phone = (String) SPUtils.get(getContext(), "phone", "");
        String pwd = (String) SPUtils.get(getContext(), "pwd", "");
        if (!TextUtils.isEmpty(phone)) {
            loginByPwd(phone, pwd);
        }
    }

    private void loginByPwd(String account, String pwd) {
        Map<String, Object> params = new HashMap<>();
        params.put("username", account);
        params.put("password", pwd);
        HttpProxy.obtain().post(PlatformContans.User.loginByPwd, params, new ICallBack() {
            @Override
            public void OnSuccess(String result) {
                Log.e("loginByPwd", result);
                try {
                    //Toast.makeText(RegisterActivity.this,"",Toast.LENGTH_LONG).show();
                    JSONObject object = new JSONObject(result);
                    JSONObject data = object.getJSONObject("data");
                    int code = object.getInt("resultCode");
                    if (code == 0) {
                        //Toast.makeText(MainActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
                        UserInfo userInfo = new Gson().fromJson(data.toString(), UserInfo.class);
                        MyApplication.token = userInfo.getToken();
                        MyApplication.setUserInfo(userInfo);
                        MyApplication.setIsLogin(true);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String error) {

            }
        });
    }


    private void initCrashHandler() { // 系统崩溃处理
        ExceptionHandler crashHandler = ExceptionHandler.getInstance();
        crashHandler.init(this);
    }

    //初始化Fresco
    private void initFresco() {
        Fresco.initialize(this);
    }

    //初始化Nohttp
    private void initNohttp() {
        Logger.setDebug(true); // 开启NoHttp调试模式。
        Logger.setTag("NoHttpSample"); // 设置NoHttp打印Log的TAG。
        NoHttp.initialize(this);


    }

    public static String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }

    public static MyApplication getInstance() {
        return instance;
    }

    public static Context getContext() {
        return context;
    }

    public PhoneUserEntity getUser() {
        return user;
    }

    public void setUser(PhoneUserEntity user) {
        this.user = user;
    }

    /**
     * 震动通知
     */
    protected void notifyVibrator() {
        if (mVibrator != null) {
            // 震动 1s
            mVibrator.vibrate(1000);
        }
    }

    /**
     * 声音通知
     */
    protected void notifyRing() {
        if (mRingtone != null) {
            mRingtone.play();
        }
    }

    /**
     * 是否打开震动
     *
     * @return 震动
     */
    protected boolean isInitVibratorNotify() {
        return true;
    }

    /**
     * 是否打开声音提醒
     *
     * @return 声音
     */
    protected boolean isInitRingNotify() {
        return true;
    }

}
