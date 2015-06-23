package com.xinnongyun.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import net.simonvt.numberpicker.NumberPicker;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alex.listviewman.PinyinComparator;
import com.alex.listviewman.adapter.SortBaseAdapter;
import com.alex.listviewman.adapter.SortBaseCheckBoxAdapter;
import com.alex.listviewman.bean.PhoneModelbean;
import com.alex.listviewman.listener.OnTouchingLetterChangedListener;
import com.alex.listviewman.util.CharacterParserUtils;
import com.alex.listviewman.view.ClearEditText;
import com.alex.listviewman.view.SideBar;
import com.xinnongyun.adapter.DatePickerAdapter;
import com.xinnongyun.atyfragment.MainFragment;
import com.xinnongyun.config.NetUrls;
import com.xinnongyun.net.NetConnectionFarming;
import com.xinnongyun.net.NetConnectionFarming.FarmingSuccessCallBack;
import com.xinnongyun.sharepreference.MySharePreference;
import com.xinnongyun.sqlite.BeingDaoDBManager;
import com.xinnongyun.sqlite.CollectDaoDBManager;
import com.xinnongyun.sqlite.FarmingDaoDBManager;
import com.xinnongyun.sqlite.TablesColumns;
import com.xinnongyun.utils.DialogManager;
import com.xinnongyun.utils.StringUtil;

public class SelectBeingActivity extends Activity implements OnClickListener {
	// 获取布局控件
	private ListView sortListView;
	private SideBar sideBar;
	private TextView dialog;
	/** 自定义适配器 */
	private SortBaseAdapter adapter;
	private SortBaseCheckBoxAdapter checkAdapter;
	/** 实例化，汉字转换成拼音的类 */
	private CharacterParserUtils characterParser;
	private List<PhoneModelbean> SourceDateList;
	private String selectCollectsPara = "";;
	private String selectBeingId = "";
	private String defaultSelectCollId = null;
	/**
	 * 多选1    单选2  日期设定3  
	 */
	private int selectType = 1;
	/** 根据拼音来排列ListView里面的数据类 */
	private PinyinComparator pinyinComparator;
	private ClearEditText mClearEditText;
	/**
	 * 类型 动物 作物
	 */
	private String beingType = "1";
	
	private NumberPicker yearPicker,mouthPicker,dayPicker;
	private ImageView newBeingBtn;
	private ImageView maturingatReturn;
	private Button nextBtn;
	private TextView collectslelect_head;
	private LinearLayout beingtselect_headll;
	private Button beingselect_head_animal,beingselect_head_crop;
	private LinearLayout tv_beingMarturingll;
	private Button farmingbeingnew;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		requestWindowFeature(Window.FEATURE_NO_TITLE); //隐藏ActionBar
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//		         WindowManager.LayoutParams.FLAG_FULLSCREEN); // 设置全屏
		setContentView(R.layout.activity_selectbeing);
		Intent intent= getIntent();
		if(intent!=null && intent.getStringExtra("type")!=null)
		beingType = intent.getStringExtra("type");
		if(intent!=null && intent.getStringExtra(TablesColumns.TABLETIMELINE_COLLECTOR)!=null)
		{
			defaultSelectCollId = intent.getStringExtra(TablesColumns.TABLETIMELINE_COLLECTOR);
		}
		initViews();
		if(beingType.equals("2"))
		{
			beingselect_head_animal.setBackgroundResource(R.drawable.slider_animal);
			beingselect_head_animal.setTextColor(getResources().getColor(R.color.text_green));
			beingselect_head_crop.setBackgroundResource(R.drawable.slider_green_crop);
			beingselect_head_crop.setTextColor(getResources().getColor(R.color.text_white));
		}
	}
	
	/** 获取布局及处理 */
	private void initViews() {
		mClearEditText = (ClearEditText) findViewById(R.id.filter_edit);
		mClearEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				filterData(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		
		yearPicker = (NumberPicker) findViewById(R.id.yearPicker);
		mouthPicker = (NumberPicker) findViewById(R.id.mouthPicker);
		dayPicker = (NumberPicker) findViewById(R.id.dayPicker);
		farmingbeingnew = (Button) findViewById(R.id.farmingbeingnew);
		farmingbeingnew.setOnClickListener(this);
		collectslelect_head = (TextView) findViewById(R.id.collectslelect_head);
		beingtselect_headll = (LinearLayout) findViewById(R.id.beingtselect_headll);
		beingselect_head_animal = (Button) findViewById(R.id.beingselect_head_animal);
		beingselect_head_animal.setOnClickListener(this);
		beingselect_head_crop = (Button) findViewById(R.id.beingselect_head_crop);
		beingselect_head_crop.setOnClickListener(this);
		maturingatReturn = (ImageView) findViewById(R.id.maturingatReturn);
		maturingatReturn.setOnClickListener(this);
		newBeingBtn = (ImageView) findViewById(R.id.selectbeing_new);
		newBeingBtn.setOnClickListener(this);
		tv_beingMarturingll = (LinearLayout) findViewById(R.id.beingMarturingll);
		nextBtn = (Button) findViewById(R.id.selectbeing_foot);
		nextBtn.setOnClickListener(this);
		// 实例化汉字转拼音类
		characterParser = CharacterParserUtils.getInstance();
		pinyinComparator = new PinyinComparator();
		sideBar = (SideBar) findViewById(R.id.sidrbar);
		dialog = (TextView) findViewById(R.id.dialog);
		sideBar.setTextView(dialog);

		// 设置右侧触摸监听
		sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

			@Override
			public void onTouchingLetterChanged(String s) {
				// 该字母首次出现的位置
				if(selectType == 2)
				{
					int position = adapter.getPositionForSection(s.charAt(0));
					if (position != -1) {
						sortListView.setSelection(position);
					}
				}
				if(selectType == 1)
				{
					int position = checkAdapter.getPositionForSection(s.charAt(0));
					if (position != -1) {
						sortListView.setSelection(position);
					}
				}
			}
		});

		sortListView = (ListView) findViewById(R.id.list_country);
		sortListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				if(selectType==2)
				{
					newBeingBtn.setVisibility(View.INVISIBLE);
					collectslelect_head.setVisibility(View.VISIBLE);
					beingtselect_headll.setVisibility(View.INVISIBLE);
					selectBeingId = view.findViewById(R.id.title).getTag().toString();
					collectslelect_head.setText(R.string.farming_maturingat_beings_head);
					selectType=3;
					nextBtn.setVisibility(View.INVISIBLE);
					tv_beingMarturingll.setVisibility(View.VISIBLE);
					farmingbeingnew.setVisibility(View.VISIBLE);
					DatePickerAdapter.yearMouthDay(yearPicker, mouthPicker, dayPicker);
					yearPicker.setValue(StringUtil.getCurrentYear());
					mouthPicker.setValue(StringUtil.getCurrentMouth());
					dayPicker.setValue(StringUtil.getCurrentDay());
					sortListView.setVisibility(View.INVISIBLE);
				}
				if(selectType == 1)
				{
					SortBaseCheckBoxAdapter.ViewHolder vHollder = (SortBaseCheckBoxAdapter.ViewHolder) view.getTag();
					SortBaseCheckBoxAdapter.isSelected.put(position, !(Boolean)vHollder.checkBox.getTag());
					checkAdapter.notifyDataSetChanged();
					return;
				}
			}
		});

		
		collectslelect_head.setVisibility(View.VISIBLE);
		beingtselect_headll.setVisibility(View.INVISIBLE);
		newBeingBtn.setVisibility(View.INVISIBLE);
		SourceDateList = new ArrayList<PhoneModelbean>();
		
		// 根据a-z进行排序源数据
		
		checkAdapter = new SortBaseCheckBoxAdapter(SelectBeingActivity.this, SourceDateList);
		checkAdapter.selectDefaultId = defaultSelectCollId;
		sortListView.setAdapter(checkAdapter);
		
		nextBtn.setVisibility(View.VISIBLE);
		//nextBtn.setText("完成");
		nextBtn.setOnClickListener(SelectBeingActivity.this);
		updateInfos();
	}

	
	
	private void filterData(String filterStr) {
		List<PhoneModelbean> filterDateList = new ArrayList<PhoneModelbean>();

		if (TextUtils.isEmpty(filterStr)) {
			filterDateList = SourceDateList;
		} else {
			filterDateList.clear();
			for (PhoneModelbean sortModel : SourceDateList) {
				String name = sortModel.getName();
				if (name.indexOf(filterStr.toString()) != -1
						|| characterParser.getSelling(name).startsWith(
								filterStr.toString())) {
					filterDateList.add(sortModel);
				}
			}
		}

		Collections.sort(filterDateList, pinyinComparator);
		adapter.updateListView(filterDateList);
	}
	
	
	
	
	
	
	
	public Handler dialogHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg) {
			if(dg!=null)
			{
				dg.dismiss();
			}
		}
	}; 
	
	public Dialog dg;
	public void updateInfos()
	{
		dg = MainFragment.createLoadingDialog(this);
		dg.show();
		new Thread(new Runnable() {
			@Override
			public void run() {
				Message msg = checkAdapter.datatHandler.obtainMessage();
				SourceDateList = getCollectDarta();
				Collections.sort(SourceDateList, pinyinComparator);
				msg.obj = SourceDateList;
		        checkAdapter.datatHandler.sendMessage(msg);
		        dialogHandler.sendEmptyMessage(0);
			}
		}).start();
	}
	
	/** 为Being  ListView填充数据 */
	private List<PhoneModelbean> filledBeingData(String type) {
		BeingDaoDBManager beingDao = new BeingDaoDBManager(SelectBeingActivity.this);
		List<HashMap<String, String>> datas = beingDao.getAllBeing(type);
		
		List<PhoneModelbean> mSortList = new ArrayList<PhoneModelbean>();
		if(datas!=null)
		for (int i = 0; i <datas.size(); i++) {
			PhoneModelbean sortModel = new PhoneModelbean();
			HashMap<String, String> map;
			map = datas.get(i);
			sortModel.setName(map.get(TablesColumns.TABLEBEING_NAME).toString());
			sortModel.setId(map.get(TablesColumns.TABLEBEING_ID).toString());
			/** 将汉字转换成拼音 */
			String pinyin = characterParser.getSelling(map.get(TablesColumns.TABLEBEING_NAME).toString().trim());
			
			/** 截取拼音的首字母 */
			if(pinyin.length()>1)
			{
				String sortString = pinyin.substring(0, 1).toUpperCase();
				
				/** 判断首字母是否是英文字母，正则表达式 */
				if (sortString.matches("[A-Z]")) {
					sortModel.setSortLetters(sortString.toUpperCase());
				} else {
					sortModel.setSortLetters("#");
				}
			}else {
				sortModel.setSortLetters("#");
			}
			sortModel.setImgSrc(map.get(TablesColumns.TABLEBEING_IMAGE).toString());
			/** 填充数据 */
			mSortList.add(sortModel);
		}
		return mSortList;

	}
	
	
	/** 为盒子  ListView填充数据 */
	private List<PhoneModelbean> getCollectDarta() {
		List<PhoneModelbean> mSortList = new ArrayList<PhoneModelbean>();
			CollectDaoDBManager collectDao = new CollectDaoDBManager(SelectBeingActivity.this);
			List<HashMap<String, String>> datas = collectDao.getAllCollect();
			BeingDaoDBManager beingDao = new BeingDaoDBManager(SelectBeingActivity.this);
			FarmingDaoDBManager farmingDao = new FarmingDaoDBManager(SelectBeingActivity.this);
			List<HashMap<String, String>> farmingInfos = farmingDao.getAllFarming();
			if(datas!=null)
			for (int i = 0; i < datas.size(); i++) {
				PhoneModelbean sortModel = new PhoneModelbean();
				HashMap<String, String> map;
				map = datas.get(i);
				sortModel.setName(map.get(TablesColumns.TABLECOLLECTOR_NAME).toString());
				sortModel.setId(map.get(TablesColumns.TABLECOLLECTOR_ID).toString());
				int farmingLength = farmingInfos.size();
				sortModel.setDate("");
				sortModel.setNameSecond("");
				for(int farmingI = 0; farmingI < farmingLength ; farmingI ++)
				{
					int cNum= 0;
					HashMap<String, String> farmingMap = farmingInfos.get(farmingI);
					String collectsRaw = farmingMap.get(TablesColumns.TABLEFARMING_COLLECTORS);
					String collectsInfo = collectsRaw.substring(1, collectsRaw.length()-1);
					String collect[] = collectsInfo.split(",");
					for(String collId : collect)
					{
						if(collId.equals(sortModel.getId()))
						{
							cNum = 1;
							sortModel.setDate(farmingMap.get(TablesColumns.TABLEFARMING_MATURINGAT));
							sortModel.setNameSecond(beingDao.getBeingById(farmingMap.get(TablesColumns.TABLEFARMING_BEING)).get(TablesColumns.TABLEBEING_NAME));
							break;
						}
					}
					if(cNum == 1)
					{
						break;
					}
				}
				
				/** 将汉字转换成拼音 */
				String pinyin = characterParser.getSelling(map.get(TablesColumns.TABLECOLLECTOR_NAME).toString().trim());
				/** 截取拼音的首字母 */
				if(pinyin != null && pinyin.length()>=1)
				{
					String sortString = pinyin.substring(0, 1).toUpperCase();
					
					/** 判断首字母是否是英文字母，正则表达式 */
					if (sortString.matches("[A-Z]")) {
						sortModel.setSortLetters(sortString.toUpperCase());
					} else {
						sortModel.setSortLetters("#");
					}
				}
				else
				{
					sortModel.setSortLetters("#");
				}
				/** 填充数据 */
				mSortList.add(sortModel);
			}
		
		return mSortList;

	}

	
	private boolean checkCollectSelect()
	{
		if(SortBaseCheckBoxAdapter.isSelected.containsValue(true))
		{
			return true;
		}
		DialogManager.showDialogSimple(SelectBeingActivity.this,
				//R.string.beingselect_collecthead,
				R.string.beingselect_collectselect_error);
		return false;
	}
	
	
	/**
	 * 初始化请求属性 
	 * //being=1 &collector=1&collector=2&collector=3&maturing_at=' -H 'Authorization: Token 467c2b68aab26f151a7d200707f54ffb2a612222'
	 * @return
	 */
	private String initialUrlParams()
	{
		String para = TablesColumns.TABLEFARMING_BEING + "=" + selectBeingId;
		para +=selectCollectsPara;
//		if(beingType.equals("1"))
//		{
		para += "&" + TablesColumns.TABLEFARMING_MATURINGAT + "=" + yearPicker.getValue() + "-" + mouthPicker.getValue() + "-" + dayPicker.getValue() + "T00:00:00";
//		}
		if(!MySharePreference.getValueFromKey(SelectBeingActivity.this, MySharePreference.FARMID).equals(MySharePreference.NOTSTATE))
		{
			para += "&" + TablesColumns.TABLEFARMING_FARM + "=" + MySharePreference.getValueFromKey(SelectBeingActivity.this, MySharePreference.FARMID);
		}
		para+="&" + TablesColumns.TABLEFARMING_BEGINAT + "=" + StringUtil.dataChangeToT(StringUtil.getCurrentData());
		
		return para;
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == 2)
		{
			/** 获取数据 */
			SourceDateList = filledBeingData(beingType);

			// 根据a-z进行排序源数据
			Collections.sort(SourceDateList, pinyinComparator);
			/** 自定义适配器 */
			adapter = new SortBaseAdapter(SelectBeingActivity.this, SourceDateList,beingType);
			sortListView.setAdapter(adapter);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.selectbeing_new:
			Intent intent = new Intent();
			intent.putExtra("type", beingType);
			intent.setClass(SelectBeingActivity.this, BeingNewActivity.class);
			startActivityForResult(intent, 2);
			break;
		case R.id.selectbeing_foot:
			if(checkCollectSelect())
			{
				newBeingBtn.setVisibility(View.VISIBLE);
				for(int i : SortBaseCheckBoxAdapter.isSelected.keySet())
				{
					if(SortBaseCheckBoxAdapter.isSelected.get(i))
					{
						selectCollectsPara += "&" + TablesColumns.TABLEFARMING_COLLECTORS+"=" + SourceDateList.get(i).getId();
					}
				}
				mClearEditText.setVisibility(View.VISIBLE);
				selectType = 2;
				collectslelect_head.setVisibility(View.INVISIBLE);
				beingtselect_headll.setVisibility(View.VISIBLE);
				view.setVisibility(View.GONE);
				/** 获取数据 */
				SourceDateList = filledBeingData(beingType);

				// 根据a-z进行排序源数据
				Collections.sort(SourceDateList, pinyinComparator);
				/** 自定义适配器 */
				adapter = new SortBaseAdapter(SelectBeingActivity.this, SourceDateList,beingType);
				sortListView.setAdapter(adapter);
			}
			break;

		case R.id.maturingatReturn:
			finish();
			break;
		case R.id.beingselect_head_animal:
			if(beingType.equals("2"))
			{
				return;
			}
			mClearEditText.setText("");
			beingType = "2";
			/** 获取数据 */
			SourceDateList = filledBeingData(beingType);

			// 根据a-z进行排序源数据
			Collections.sort(SourceDateList, pinyinComparator);
			/** 自定义适配器 */
			adapter = new SortBaseAdapter(SelectBeingActivity.this, SourceDateList,beingType);
			sortListView.setAdapter(adapter);
			beingselect_head_animal.setBackgroundResource(R.drawable.slider_animal);
			beingselect_head_animal.setTextColor(getResources().getColor(R.color.text_green));
			beingselect_head_crop.setBackgroundResource(R.drawable.slider_green_crop);
			beingselect_head_crop.setTextColor(getResources().getColor(R.color.text_white));
			
			break;
		case R.id.beingselect_head_crop:
			if(beingType.equals("1"))
			{
				return;
			}
			mClearEditText.setText("");
			beingType = "1";
			/** 获取数据 */
			SourceDateList = filledBeingData(beingType);

			// 根据a-z进行排序源数据
			Collections.sort(SourceDateList, pinyinComparator);
			/** 自定义适配器 */
			adapter = new SortBaseAdapter(SelectBeingActivity.this, SourceDateList, beingType);
			sortListView.setAdapter(adapter);
			beingselect_head_animal.setBackgroundResource(R.drawable.slider_green_animal);
			beingselect_head_animal.setTextColor(getResources().getColor(R.color.text_white));
			beingselect_head_crop.setBackgroundResource(R.drawable.slider_crop);
			beingselect_head_crop.setTextColor(getResources().getColor(R.color.text_green));
			break;
		case R.id.farmingbeingnew:
			farmingbeingnew.setClickable(false);
			addFarmingBeing();
			break;
		default:
			break;
		}
		
	}

	
	private void addFarmingBeing()
	{
		dg = MainFragment.createLoadingDialog(this);
		dg.show();
		NetConnectionFarming.FarmingPostNetConnection(NetUrls.URL_FARMING_NEW,
				initialUrlParams(),
				"Token " + MySharePreference.getValueFromKey(SelectBeingActivity.this,MySharePreference.ACCOUNTTOKEN),
				new FarmingSuccessCallBack() {
					
					@Override
					public void onSuccess(HashMap<String, Object> result) {
						farmingbeingnew.setClickable(true);
						dg.dismiss();
						if(result!=null)
						{
							if(result.keySet().contains("status_code"))
							{
								ErrorMarkToast.showCallBackToast(SelectBeingActivity.this, result);
								return;
							}
							FarmingDaoDBManager farmingDao = new FarmingDaoDBManager(SelectBeingActivity.this);
							farmingDao.insertNewFarming(result);
							finish();
						}
						else
						{
							showToast(R.string.interneterror);
						}
					}
				});
	}
	
	private void showToast(int id)
	{
		Toast.makeText(SelectBeingActivity.this, id, Toast.LENGTH_SHORT).show();
	}
	
}