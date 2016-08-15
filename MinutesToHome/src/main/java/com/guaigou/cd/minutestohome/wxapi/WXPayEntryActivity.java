package com.guaigou.cd.minutestohome.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.guaigou.cd.minutestohome.R;
import com.guaigou.cd.minutestohome.http.Constants;
import com.guaigou.cd.minutestohome.util.DebugUtil;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler{
	
	private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";
	
    private IWXAPI api;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_result);
        
    	api = WXAPIFactory.createWXAPI(this, Constants.APP_ID);
        api.handleIntent(getIntent(), this);
    }

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
        api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
	}

	@Override
	public void onResp(BaseResp resp) {
		DebugUtil.d("WXPayEntryActivity 支付结果：" + resp.errCode);
//		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
//			AlertDialog.Builder builder = new AlertDialog.Builder(this);
//			builder.setTitle(R.string.app_tip);
//			builder.setMessage(getString(R.string.pay_result_callback_msg, String.valueOf(resp.errCode)));
//			builder.show();
//		}
		//2.这是我修改的第二个地方, 原版的是弹出一个对话框, 我觉得太丑,并且原版也没有给出详细问题提示,只给出了 0, -1 ,-2 这样会让客户不明所以,所以我替换成如下3个文本. 此外
		// 原版支付后,不会直接跳转到 你的应用,需要按一次 返回键,才行, 所以我加入了finish()便于在提示后,直接返回我的应用
		if(resp.errCode==0){
			Toast.makeText(this,"支付成功!",Toast.LENGTH_SHORT).show();
		}else if(resp.errCode==-1){
			Toast.makeText(this,"支付失败!",Toast.LENGTH_SHORT).show();
		}else if(resp.errCode==-2){
			Toast.makeText(this,"取消支付!",Toast.LENGTH_SHORT).show();
		}
		finish();
	}
}