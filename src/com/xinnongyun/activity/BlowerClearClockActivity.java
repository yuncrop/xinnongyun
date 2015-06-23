package com.xinnongyun.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xinnongyun.adapter.ControlCenterForceItemAdapter;
import com.xinnongyun.atyfragment.MainFragment;
import com.xinnongyun.config.NetUrls;
import com.xinnongyun.net.NetConnectionCollector;
import com.xinnongyun.net.NetConnectionCollector.CollectorSuccessCallBack;
import com.xinnongyun.sharepreference.MySharePreference;
import com.xinnongyun.sqlite.BlowerDaoDBManager;
import com.xinnongyun.sqlite.TablesColumns;
import com.xinnongyun.utils.DialogManager;
import com.xinnongyun.view.IconFontTextView;

/**
 * 解除风口锁定
 * @author sm
 *
 */
public class BlowerClearClockActivity extends Activity {

	private GridView gridViewBlower;
	private IconFontTextView freeToClockTv,clockToFreeTv;
	private LinearLayout freeToClockTvll,clockToFreeTvll;
	private boolean isClock = false;
	private String ccId;
	private List<HashMap<String, String>> bMaps;
	private Button complete_btn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.robot_detail_clearclock);
		ccId = getIntent().getStringExtra("ccId");
		freeToClockTv = (IconFontTextView) findViewById(R.id.freeToClockTv);
		clockToFreeTv = (IconFontTextView) findViewById(R.id.clockToFreeTv);
		
		clockToFreeTv.setTextColor(Color.parseColor("#77be30"));
		freeToClockTv.setTextColor(Color.parseColor("#999999"));
		freeToClockTv.setText(String.valueOf(new char[]{0xe660}));
		clockToFreeTv.setText(String.valueOf(new char[]{0xe661}));
		
		findViewById(R.id.collectReturn).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						finish();
					}
				}
				);
		ControlCenterForceItemAdapter.hasList = new ArrayList<String>();
		gridViewBlower = (GridView) findViewById(R.id.robot_item_contents);
		BlowerDaoDBManager blowerDaoDBManager = new BlowerDaoDBManager(this);
		bMaps = blowerDaoDBManager.getAllBlowersByCCId(ccId);
		for(HashMap<String, String> map : bMaps)
		{
			ControlCenterForceItemAdapter.hasList.add(map.get(TablesColumns.TABLEBLOWER_NO).toString());
		}
		int bTSize = 8 - bMaps.size();
		for(int i =0; i<bTSize;i++)
		{
			bMaps.add(new HashMap<String, String>());
		}
		
		gridViewBlower.setAdapter(new ControlCenterForceItemAdapter(this, bMaps));
		
		freeToClockTvll = (LinearLayout) findViewById(R.id.freeToClockTvll);
		clockToFreeTvll = (LinearLayout) findViewById(R.id.clockToFreeTvll);
		freeToClockTvll.setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						isClock = true;
						clockToFreeTv.setTextColor(Color.parseColor("#999999"));
						freeToClockTv.setTextColor(Color.parseColor("#fe462e"));
						((TextView)findViewById(R.id.clockToFreeTvText)).setTextColor(Color.parseColor("#999999"));
						((TextView)findViewById(R.id.freeToClockTvText)).setTextColor(Color.parseColor("#fe462e"));
					}
				}
				);
		clockToFreeTvll.setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						isClock = false;
						clockToFreeTv.setTextColor(Color.parseColor("#77be30"));
						freeToClockTv.setTextColor(Color.parseColor("#999999"));
						((TextView)findViewById(R.id.freeToClockTvText)).setTextColor(Color.parseColor("#999999"));
						((TextView)findViewById(R.id.clockToFreeTvText)).setTextColor(Color.parseColor("#77be30"));
					}
				}
				);
		complete_btn = (Button) findViewById(R.id.complete_btn);
		complete_btn.setOnClickListener(
				new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						clockOrUnClockBlowers();
					}
				}
				);
		
	}
	
	private Dialog dg;
	private void clockOrUnClockBlowers()
	{
		if(bMaps==null || bMaps.size()==0)
		{
			return;
		}
		int idsLength = ControlCenterForceItemAdapter.selectList.size();
		String lockIds = "[";
		String unLockIds = "[";
		
		for(int i = 0;i<idsLength;i++)
		{
			for(HashMap<String, String> map : bMaps)
			{
				if(map.size()>0 && map.get(TablesColumns.TABLEBLOWER_NO).equals(ControlCenterForceItemAdapter.selectList.get(i)) && ControlCenterForceItemAdapter.hasList.contains(map.get(TablesColumns.TABLEBLOWER_NO)))
				{
					lockIds += "" + map.get(TablesColumns.TABLEBLOWER_ID) + ",";
				}
			}
		}
		
		for(HashMap<String, String> map : bMaps)
		{
			if((!ControlCenterForceItemAdapter.selectList.contains(map.get(TablesColumns.TABLEBLOWER_NO))) && ControlCenterForceItemAdapter.hasList.contains(map.get(TablesColumns.TABLEBLOWER_NO)))
			{
				unLockIds += "" + map.get(TablesColumns.TABLEBLOWER_ID) + ",";
			}
		}
		
		if(lockIds.length()>2)
		lockIds = lockIds.substring(0,lockIds.length()-1);
		lockIds +="]";
		if(unLockIds.length()>2)
		unLockIds = unLockIds.substring(0,unLockIds.length()-1);
		unLockIds += "]";
		
		String para = "";
		if(isClock)
		{
			para = "lock="  + lockIds + "&unlock=[]";
		}
		else
		{
			para = "lock=[]"  + "&unlock=" + lockIds;
		}
	    System.out.println(para);
	    complete_btn.setClickable(false);
		complete_btn.setText(getResources().getString(R.string.send_cmd));
		dg = MainFragment.createLoadingDialog(this);
		dg.show();
		NetConnectionCollector.CollectorPutNetConnection(2,NetUrls.moveBlowerDistance(), para, 
				"Token " + MySharePreference.getValueFromKey(this,MySharePreference.ACCOUNTTOKEN),
				new CollectorSuccessCallBack() {
					@Override
					public void onSuccess(HashMap<String, Object> result) {
						if(dg!=null)
						{
							dg.dismiss();
						}
						complete_btn.setClickable(true);
						complete_btn.setText("执行");
						if(result== null)
						{
							Toast.makeText(BlowerClearClockActivity.this,"指令发送失败", Toast.LENGTH_SHORT).show();
							return;
						}
						
						if(result!=null && result.keySet().contains("status_code") && !result.get("status_code").toString().startsWith("2"))
						{
						
							System.out.println(result.get("status_code").toString());
							DialogManager.showDialogSimple(
									BlowerClearClockActivity.this,
									R.string.cmd_send_fail);
							return;
						}
						
						if(result.get("status_code").toString().startsWith("2"))
						{
							//setResult(30);
							Toast.makeText(BlowerClearClockActivity.this, "指令发送成功", Toast.LENGTH_SHORT).show();
							finish();
							return;
						}
					}
				}
				);
	}
}
