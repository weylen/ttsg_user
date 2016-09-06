package com.guaigou.cd.minutestohome.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.guaigou.cd.minutestohome.BaseActivity;
import com.guaigou.cd.minutestohome.BaseApplication;
import com.guaigou.cd.minutestohome.R;
import com.guaigou.cd.minutestohome.activity.login.LoginActivity;
import com.guaigou.cd.minutestohome.activity.login.LoginData;
import com.guaigou.cd.minutestohome.activity.orderdetails.OrderDetailsActivity;
import com.guaigou.cd.minutestohome.util.DebugUtil;
import com.guaigou.cd.minutestohome.util.DeviceUtil;
import com.xiaomi.mipush.sdk.ErrorCode;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.xiaomi.mipush.sdk.MiPushCommandMessage;
import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.PushMessageReceiver;

import java.util.List;
import java.util.Random;

public class MiMessageReceiver extends PushMessageReceiver {

    @Override // 接收服务器向客户端发送的透传消息
    public void onReceivePassThroughMessage(Context context, MiPushMessage message) {
        DebugUtil.d("onReceivePassThroughMessage is called. " + message.toString());
        doMessage(context, message);
    }

    @Override // 来接收服务器向客户端发送的通知消息， 这个回调方法会在用户手动点击通知后触发
    public void onNotificationMessageClicked(Context context, MiPushMessage message) {
        DebugUtil.d("onNotificationMessageClicked is called. " + message.toString());
    }

    @Override // 方法用来接收服务器向客户端发送的通知消息，这个回调方法是在通知消息到达客户端时触发。另外应用在前台时不弹出通知的通知消息到达客户端也会触发这个回调函数
    public void onNotificationMessageArrived(Context context, MiPushMessage message) {
        DebugUtil.d("onNotificationMessageArrived is called. " + message.toString());
//        doMessage(context, message);
    }

    @Override  // 方法用来接收客户端向服务器发送命令后的响应结果。
    public void onCommandResult(Context context, MiPushCommandMessage message) {
        DebugUtil.d("onCommandResult is called. " + message.toString());
        String command = message.getCommand();
        List<String> arguments = message.getCommandArguments();
        String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);
        String mAlias = null;
        if (MiPushClient.COMMAND_SET_ALIAS.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mAlias = cmdArg1;
                DebugUtil.d("MiMessageReceiver 设置别名：" + mAlias);
            }
        }if (MiPushClient.COMMAND_UNSET_ALIAS.equalsIgnoreCase(command)){
            if (message.getResultCode() == ErrorCode.SUCCESS){
                DebugUtil.d("MiMessageReceiver 取消别名：" + cmdArg1);
            }
        }
    }

    @Override // 方法用来接收客户端向服务器发送注册命令后的响应结果
    public void onReceiveRegisterResult(Context context, MiPushCommandMessage message) {
        DebugUtil.d("onReceiveRegisterResult is called." + message.toString());
        String command = message.getCommand(); // 获取注册的种类

        List<String> arguments = message.getCommandArguments();
        String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);
        String log = cmdArg1;
        // 注册服务
        if (MiPushClient.COMMAND_REGISTER.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                MiPushClient.setAlias(context, DeviceUtil.INSTANCE.getDeviceUuid(context), null);
            }
        } else {
            log = message.getReason();
        }
        DebugUtil.d("MiMessageReceiver 注册服务：" + log);
    }

    /**
     * 显示异地登录框
     * @param context
     */
    private void showAnotherPlaceDialog(Context context){
        DebugUtil.d("MiMessageReceiver 显示对话框：" + BaseActivity.getCurrentContext());
        Handler handler = new Handler(context.getMainLooper());
        handler.post(() -> {
            AlertDialog dialog = new AlertDialog.Builder(BaseActivity.getCurrentContext())
                    .setTitle("提示")
                    .setMessage("您的账号在另外一台设备上登录，如果不是本人操作，请您尽快修改密码")
                    .setNegativeButton("退出", (dialog1, which) -> {
                        BaseApplication.exit();
                        dialog1.dismiss();
                    })
                    .setPositiveButton("重新登录", (dialog1, which) -> {
                        dialog1.dismiss();
                        reLogin(context);
                    })
                    .create();
//        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        });
    }

    private void doMessage(Context context, MiPushMessage message){
        String content = message.getContent();
        Gson gson = new Gson();
        int status = -1;
        try{
            JsonObject jsonObject = gson.fromJson(content, JsonObject.class);
            status = jsonObject.get("stauts").getAsInt();
            DebugUtil.d("MiMessageReceiver status:" + status);
            String orderId = null;
            switch (status){
                case 1: // 异地登录
                    LoginData.INSTANCE.logout(context);
                    showAnotherPlaceDialog(context);
                    break;
                case 4: // 商家已订单
                    orderId = jsonObject.get("data").getAsString();
                    showReceiveOrder(context, orderId);
                    break;
                case 3: // 商家确认送达
                    orderId = jsonObject.get("data").getAsString();
                    showConfirmGoodsNf(context, orderId);
                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
            // 如果弹出对话框失败， 就直接跳转到登录页面
            if (status == 1){
                reLogin(context);
            }
        }
    }

    /**
     * 重新登录
     * @param context
     */
    private void reLogin(Context context){
        LoginData.INSTANCE.logout(context);
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    /**
     * 显示商家接单的通知消息
     */
    private void showReceiveOrder(Context context, String orderId){
        if (!LoginData.INSTANCE.isLogin(context)){
            DebugUtil.d("MiMessageReceiver 用户未登录 不进行通知展示");
            return;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.mipmap.icon_message);
        builder.setTicker("您的订单" + orderId + "商家已接单");
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.launcher_icon));
        builder.setWhen(System.currentTimeMillis());
        builder.setAutoCancel(true);

        Intent intent = new Intent(context, OrderDetailsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(OrderDetailsActivity.ORDER_KEY, orderId);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(pendingIntent);
        builder.setContentTitle("天天闪购提示您");
        builder.setContentText("您的订单" + orderId + "商家已接单");

        Notification notification = builder.build();
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(100, notification);
    }

    /**
     * 显示客户收货的通知
     * @param context
     * @param orderId
     */
    private void showConfirmGoodsNf(Context context, String orderId){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.mipmap.icon_message);
        builder.setTicker("您的订单" + orderId + "商家已确认送达");
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.launcher_icon));
        builder.setWhen(System.currentTimeMillis());
        builder.setAutoCancel(true);

        Intent intent = new Intent(context, OrderDetailsActivity.class);
        intent.putExtra(OrderDetailsActivity.ORDER_KEY, orderId);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(pendingIntent);
        builder.setContentTitle("天天闪购提示您");
        builder.setContentText("您的订单" + orderId + "商家已确认送达");

        Notification notification = builder.build();
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(new Random().nextInt(), notification);
    }

}
