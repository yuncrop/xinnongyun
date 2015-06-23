package com.xinnongyun.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import com.baidu.mapapi.SDKInitializer;
import com.xinnongyun.adapter.BoxSoilAdapter;
import com.xinnongyun.adapter.MyListView;
import com.xinnongyun.atyfragment.MainFragment;
import com.xinnongyun.config.NetUrls;
import com.xinnongyun.gps.GpsManager;
import com.xinnongyun.net.NetConnectionCollector;
import com.xinnongyun.net.NetConnectionCollector.CollectorSuccessCallBack;
import com.xinnongyun.net.NetConnectionGateWay;
import com.xinnongyun.net.NetConnectionGateWay.GateWaySuccessCallBack;
import com.xinnongyun.sharepreference.MySharePreference;
import com.xinnongyun.sqlite.CollectDaoDBManager;
import com.xinnongyun.sqlite.FarmingDaoDBManager;
import com.xinnongyun.sqlite.SoilDaoDBManager;
import com.xinnongyun.sqlite.TablesColumns;
import com.xinnongyun.utils.ImageUtils;
import com.xinnongyun.utils.StringUtil;

/**
 * 盒子管理
 * 
 * @author sm
 * 
 */
public class BoxActivity extends Activity implements OnClickListener{

	private ImageView collectImage,imageReturn,popdelete_iv;
	private TextView collectName,colectAddress;
	private SoilDaoDBManager dao;
	private List<HashMap<String,String>> soilItems;
	//private Button saveBoxBtn;
	private String collectId = "";
	private String collectGateWay = "";
	private String collectSoilId = "";
	private TextView collectGateWayShow,collectType;
	private String type;
	private PopupWindow popWindow;
	private LinearLayout box_changename_ll,collect_being_type_ll;
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE); //隐藏ActionBar
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//		         WindowManager.LayoutParams.FLAG_FULLSCREEN); // 设置全屏
		dao = new SoilDaoDBManager(BoxActivity.this);
		soilItems = dao.getAllSoil();
		// 在使用SDK各组件之前初始化context信息，传入ApplicationContext
		// 注意该方法要再setContentView方法之前实现
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.activity_box_main);
		Intent intent = getIntent();
		initialView();
		if(intent!=null)
		{
			type = intent.getStringExtra("type");
			if(type!=null)
			{
				if(type!=null && type.equals("addFarm"))
				{
					findViewById(R.id.complete_btn).setVisibility(View.VISIBLE);
					findViewById(R.id.complete_btn).setOnClickListener(this);
					popdelete_iv.setVisibility(View.INVISIBLE);
					
				}
				
				String colType = intent.getStringExtra(TablesColumns.TABLECOLLECTOR_CATEGORY);
				
				if(colType.equals("1"))
				{
					collect_being_type_ll.setVisibility(View.GONE);
					collectType.setText(R.string.box_collect_type1);
				}else
				{
					
					collectType.setText(R.string.box_collect_type2);
				}
				String name = intent.getStringExtra(TablesColumns.TABLECOLLECTOR_NAME);
				if(name.equals("null"))
				{
					name = getResources().getString(R.string.farming_being_name_default);
				}
				collectName.setText(name);
				//BitmapFactory.Options options = new BitmapFactory.Options();
				//options.inSampleSize = 2;
				
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
					collectImage.setBackground(new BitmapDrawable(ImageUtils
							.Create2DCode(intent.getStringExtra(TablesColumns.TABLECOLLECTOR_QRCODE))));
				} else {
					collectImage.setBackgroundDrawable(new BitmapDrawable(ImageUtils
							.Create2DCode(intent.getStringExtra(TablesColumns.TABLECOLLECTOR_QRCODE))));
				}
				
				
				collectImage.setTag(intent.getStringExtra(TablesColumns.TABLECOLLECTOR_QRCODE));
				GpsManager pps = GpsManager.getInstance();
				colectAddress = (TextView) findViewById(R.id.collect_address_type);
				colectAddress.setOnClickListener(this);
				pps.setGpsView(colectAddress, 2);
				
				collectGateWay = intent.getStringExtra(TablesColumns.TABLECOLLECTOR_GATEWAY);
				collectSoilId = intent.getStringExtra(TablesColumns.TABLECOLLOCTOR_SOIL);
				collectId = intent.getStringExtra(TablesColumns.TABLECOLLECTOR_ID);
				if(type!=null && type.equals("addFarm"))
				saveCollectInfo(1);
				final String qrocdeName = StringUtil.getFileNameFromUrl(intent.getStringExtra(TablesColumns.TABLECOLLECTOR_QRCODE));
				if(null!=collectGateWay && !collectGateWay.equals("null") && !collectGateWay.equals(""))
				{
					NetConnectionGateWay.GateWayGetNetConnection(NetUrls.URL_GATEWAY_GET(collectGateWay), null, null, 
							new GateWaySuccessCallBack() {
								@Override
								public void onSuccess(HashMap<String, Object> result) {
									if(result!=null)
									{
										if(result.keySet().contains("status_code"))
										{
											collectGateWayShow.setText(R.string.gateway_error);
										}
										else
										{
											collectGateWayShow.setText(qrocdeName+getString(R.string.gateway_connect)+ 
													StringUtil.getFileNameFromUrl(result.get("qrcode").toString())+
													" (" + result.get("collectors").toString() +
													"/" + result.get("volume").toString() +")");
										}
									}
									else
									{
										collectGateWayShow.setText(R.string.gateway_error);
									}
								}
							}
							);
				}
				if(null!=collectSoilId && !collectSoilId.equals("null") && !collectSoilId.equals(""))
				{
					for(int i=0;i<soilItems.size();i++)
					{
						if(soilItems.get(i).get(TablesColumns.TABLESOIL_ID).toString().equals(collectSoilId))
						{
							colectAddress.setText(soilItems.get(i).get(TablesColumns.TABLESOIL_NAME).toString());
						}
					}
				}
				
			}
		}
	}

	private void initialView() {
		collect_being_type_ll = (LinearLayout) findViewById(R.id.collect_being_type_ll);
		//collect_being_type_ll.setOnClickListener(this);
		box_changename_ll = (LinearLayout) findViewById(R.id.box_changename_ll);
		box_changename_ll.setOnClickListener(this);
		popdelete_iv = (ImageView) findViewById(R.id.popdelete_iv);
		popdelete_iv.setOnClickListener(this);
		collectType = (TextView) findViewById(R.id.collectType);
		imageReturn = (ImageView) findViewById(R.id.collectReturn);
		imageReturn.setOnClickListener(this);
		collectGateWayShow = (TextView) findViewById(R.id.collectGateWay);
		//saveBoxBtn = (Button) findViewById(R.id.saveBoxInfo);
		//saveBoxBtn.setOnClickListener(this);
		collectName = (TextView) findViewById(R.id.collect_name);
		//collectName.setOnClickListener(this);
		collectImage = (ImageView) findViewById(R.id.collect_image);
	}

	
	@SuppressWarnings("deprecation")
	public void btn_PopupMenu(View view) {
		View popContent = LayoutInflater.from(view.getContext()).inflate(R.layout.pop_delete, null);
//		popWindow = new PopupWindow(popContent,
//				(int)getResources().getDimension(R.dimen.height_70),
//				(int)getResources().getDimension(R.dimen.height_40));
//		//popWindow.getContentView().setla
//		android.widget.LinearLayout.LayoutParams lp =  new android.widget.LinearLayout.LayoutParams((int)getResources().getDimension(R.dimen.height_70),
//				(int)getResources().getDimension(R.dimen.height_40));
//		lp.setMargins(0, (int)getResources().getDimension(R.dimen.height_6_), 0, 0);
//		popContent.setLayoutParams(lp);
//		popWindow.setBackgroundDrawable(new BitmapDrawable());
		popWindow = new PopupWindow(popContent,LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		popWindow.setBackgroundDrawable(new BitmapDrawable());
		popWindow.setFocusable(true);
		popWindow.setOutsideTouchable(true);
		popWindow.showAsDropDown(view);
		LinearLayout popll = (LinearLayout) popContent.findViewById(R.id.popdelet_ll);
		popll.setOnClickListener(this);
		
	}


	
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.collect_address_type:
			showPopWindow(view);
			break;
		case R.id.collectReturn:
			finish();
			break;
		case R.id.popdelet_ll:
			showDialogDelete(BoxActivity.this);
			popWindow.dismiss();
			break;
		case R.id.popdelete_iv:
			btn_PopupMenu(popdelete_iv);
			break;
		case R.id.box_changename_ll:
			Intent nameIntent = new Intent();
			nameIntent.putExtra("collectId", collectId);
			if(collectName.getText()!=null && collectName.getText().toString().trim().length()>1)
			nameIntent.putExtra("name", collectName.getText().toString().trim());
			nameIntent.setClass(BoxActivity.this, BoxNameModifyActivity.class);
			startActivityForResult(nameIntent, 10);
			break;
		case R.id.complete_btn:
			finish();
			break;
		default:
			break;
		}
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == 10)
		{
			CollectDaoDBManager colleDao = new CollectDaoDBManager(BoxActivity.this);
			HashMap<String, String> map = colleDao.getCollectById(collectId);
			if(map.keySet().size()>0)
			collectName.setText(map.get(TablesColumns.TABLECOLLECTOR_NAME).toString());
		}
	}
	
	
	private Dialog dg;
	private void showDialogDelete(Context context)
	{
		final AlertDialog dlg = new AlertDialog.Builder(context).create();
		dlg.setCancelable(false);
		dlg.show();
		Window window = dlg.getWindow();
		// *** 主要就是在这里实现这种效果的.
		// 设置窗口的内容页面,shrew_exit_dialog.xml文件中定义view内容
		window.setContentView(R.layout.tishi_show_dialog_twobtn);
		((TextView) window.findViewById(R.id.dialog_head))
				.setText(R.string.pop_delete);
		((TextView) window.findViewById(R.id.dialog_content))
				.setText(R.string.pop_delete_collect);
		// 为确认按钮添加事件,执行退出应用操作
		Button ok = (Button) window.findViewById(R.id.dialog_btn_submit);
		
		ok.setTextColor(getResources().getColor(R.color.text_fe462e));
		ok.setText(R.string.pop_delete);
		ok.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dg = MainFragment.createLoadingDialog(BoxActivity.this);
				dg.show();
				NetConnectionCollector.CollectorDeleteNetConnection(NetUrls.URL_COLLECT_UPDATE(collectId), null, 
						"Token "+ MySharePreference.getValueFromKey(BoxActivity.this,MySharePreference.ACCOUNTTOKEN), 
						new CollectorSuccessCallBack() {
							@Override
							public void onSuccess(HashMap<String, Object> result) {
								dg.dismiss();
								if(result!=null)
								{
									if(result.get("status_code").toString().equals("204"))
									{
										//TODO 删除盒子 删除盒子信息  删除盒子的种养记录
										CollectDaoDBManager collDao = new CollectDaoDBManager(BoxActivity.this);
										collDao.deleteCollectById(collectId);
										FarmingDaoDBManager farmingDao = new FarmingDaoDBManager(BoxActivity.this);
										farmingDao.deleteCollectNewFarming(collectId);
										dlg.dismiss();
										setResult(10);
										finish();
									}
									else
									{
										ErrorMarkToast.showCallBackToast(BoxActivity.this, result);
										dlg.dismiss();
									}
								}else
								{
									showToast(R.string.interneterror);
								}
								
							}
						}
						);
			}
		});
		Button cancel = (Button) window.findViewById(R.id.dialog_btn_cancel);
		cancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dlg.dismiss();
			}
		});
	}
	
	String soilType = null;
	@SuppressWarnings("deprecation")
	private void showPopWindow(View view)
	{
		
		View popContent = LayoutInflater.from(view.getContext()).inflate(R.layout.soillist, null);
		MyListView soliList = (MyListView) popContent.findViewById(R.id.solilist);
		
		if(colectAddress.getText().toString().trim().length()!= 0)
		{
			soilType = colectAddress.getText().toString().trim();
		}
		BoxSoilAdapter  adapter = new BoxSoilAdapter(view.getContext(),soilItems,soilType,1);
		soliList.setAdapter(adapter);
		final PopupWindow popWindow = new PopupWindow(popContent,view.getWidth(),LayoutParams.WRAP_CONTENT);
		popWindow.setBackgroundDrawable(new BitmapDrawable());
		popWindow.setFocusable(true);
		popWindow.setOutsideTouchable(true);
		soliList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
						dg = MainFragment.createLoadingDialog(BoxActivity.this);
						dg.show();
						colectAddress.setText(soilItems.get(position).get(TablesColumns.TABLESOIL_NAME).toString());
						popWindow.dismiss();
						saveCollectInfo(2);
			}
		});
		popWindow.showAsDropDown(view);
	}
	
	
	
	private  void saveCollectInfo(final int save)
	{
		String paraUrl = initialUrlPara(save);
		NetConnectionCollector.CollectorPutNetConnection(1,NetUrls.URL_COLLECT_UPDATE(collectId), paraUrl,
				"Token "+MySharePreference.getValueFromKey(BoxActivity.this, MySharePreference.ACCOUNTTOKEN),
				new CollectorSuccessCallBack() {
					@Override
					public void onSuccess(HashMap<String, Object> result) {
						if(dg!=null)
						dg.dismiss();
						if(result!=null)
						{
							if(result.keySet().contains("status_code"))
							{
								colectAddress.setText(soilType);
								ErrorMarkToast.showCallBackToast(BoxActivity.this, result);
								return;
							}
							
							CollectDaoDBManager dao = new CollectDaoDBManager(BoxActivity.this);
							if(type!=null && type.equals("addFarm"))
							{
								List<HashMap<String, Object>> listdata = new ArrayList<HashMap<String,Object>>();
								listdata.add(result);
								dao.insertAllCollect(listdata);
								type = "ModifyFarm";
							}
							if(type!=null && type.equals("ModifyFarm"))
							{
								dao.updateCollectById(result);
							}
						}
						else
						{
							colectAddress.setText(soilType);
							showToast(R.string.interneterror);
						}
					}
				}
				);
	}
	
	
	//'name={name}&farm={farm}&lat=&lng=&being=&soil='
	//{"category":["这个字段是必填项。"],"farm":["Invalid pk '10' - object does not exist."],"serial":["This field may not be blank."]}
	private String initialUrlPara(int saveType)
	{
		if(saveType == 1)
		{
			return  TablesColumns.TABLECOLLOCTOR_FARM +"="+MySharePreference.getValueFromKey(BoxActivity.this, MySharePreference.FARMID);
		}
//		String para = TablesColumns.TABLECOLLECTOR_NAME +"=" + collectName.getText().toString().trim();
//		para += "&" + colectAddress.getTag();
//		para += "&" + TablesColumns.TABLECOLLOCTOR_FARM +"="+MySharePreference.getValueFromKey(BoxActivity.this, MySharePreference.FARMID);
//		para += "&" + TablesColumns.TABLECOLLECTOR_CATEGORY + "=" + collectCategory;
//		para += "&" + TablesColumns.TABLECOLLECTOR_SERIAL + "=" + collectSerial;
//		para += "&" + TablesColumns.TABLECOLLECTOR_GATEWAY + "=" +collectGateWay;
 		String soilId = null;
 		if(colectAddress.getText().toString().length()>0)
 		{
 			for(int i=0;i<soilItems.size();i++)
			{
				if(soilItems.get(i).get(TablesColumns.TABLESOIL_NAME).toString().equals(colectAddress.getText().toString()))
				{
					soilId = soilItems.get(i).get(TablesColumns.TABLESOIL_ID).toString();
				}
			}
 		}
 		String para = "";//TablesColumns.TABLECOLLOCTOR_FARM +"="+MySharePreference.getValueFromKey(BoxActivity.this, MySharePreference.FARMID);;
		if(soilId != null)
		{
			para +="" + TablesColumns.TABLECOLLOCTOR_SOIL + "=" +soilId;
		}
		return para;
	}
	
	
	private void showToast(int id)
	{
		Toast.makeText(BoxActivity.this, id, Toast.LENGTH_SHORT).show();
	}
	
	@Override
	protected void onDestroy() {
		GpsManager.getInstance().destory();
		super.onDestroy();
	}
}
