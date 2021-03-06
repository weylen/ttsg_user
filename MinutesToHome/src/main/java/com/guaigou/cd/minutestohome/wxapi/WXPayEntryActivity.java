package com.guaigou.cd.minutestohome.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.guaigou.cd.minutestohome.activity.pay.PayActivity;
import com.guaigou.cd.minutestohome.http.Constants;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler{
	
    private IWXAPI api;

	public static final String ACTION = "WXPayEntryActivity_ResultCode";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
		Constants.WX_RESP = resp.errCode;
		Intent intent = new Intent(this, PayActivity.class);
		intent.putExtra("WXResult", resp.errCode);
		intent.setAction(ACTION);
		startActivity(intent);
		finish();
	}
}