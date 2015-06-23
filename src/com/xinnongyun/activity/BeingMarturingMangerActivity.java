package com.xinnongyun.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import net.simonvt.numberpicker.NumberPicker;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.alex.listviewman.PinyinComparator;
import com.alex.listviewman.adapter.SortBaseBeingMarturingAdapter;
import com.alex.listviewman.bean.PhoneModelbean;
import com.alex.listviewman.listener.OnTouchingLetterChangedListener;
import com.alex.listviewman.util.CharacterParserUtils;
import com.alex.listviewman.view.SideBar;
import com.xinnongyun.adapter.DatePickerAdapter;
import com.xinnongyun.atyfragment.MainFragment;
import com.xinnongyun.config.NetUrls;
import com.xinnongyun.net.NetConnectionFarming;
import com.xinnongyun.net.NetConnectionFarming.FarmingSuccessCallBack;
import com.xinnongyun.sharepreference.MySharePreference;
import com.xinnongyun.sqlite.BeingDaoDBManager;
import com.xinnongyun.sqlite.FarmingDaoDBManager;
import com.xinnongyun.sqlite.TablesColumns;
import com.xinnongyun.utils.StringUtil;

/**
 * 成熟日期设置列表
 * 
 * @author sm
 * 
 */
public class BeingMarturingMangerActivity extends Activity {

	private ImageView collectReturn;
	// 获取布局控件
	private ListView sortListView;
	private SideBar sideBar;
	private TextView dialog;
	/** 自定义适配器 */
	private SortBaseBeingMarturingAdapter adapter;

	/** 实例化，汉字转换成拼音的类 */
	private CharacterParserUtils characterParser;
	private List<PhoneModelbean> SourceDateList;

	/** 根据拼音来排列ListView里面的数据类 */
	private PinyinComparator pinyinComparator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		requestWindowFeature(Window.FEATURE_NO_TITLE); //隐藏ActionBar
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//		         WindowManager.LayoutParams.FLAG_FULLSCREEN); // 设置全屏
		setContentView(R.layout.activity_beingmarturing);
		initViews();
	}

	/** 获取布局及处理 */
	private void initViews() {
		collectReturn = (ImageView) findViewById(R.id.collectReturn);
		collectReturn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		// 实例化汉字转拼音类
		characterParser = CharacterParserUtils.getInstance();

		pinyinComparator = new PinyinComparator();

		sideBar = (SideBar) findViewById(R.id.beingmaturing_sliderBar);
		dialog = (TextView) findViewById(R.id.beingmaturing_dialog);
		sideBar.setTextView(dialog);

		// 设置右侧触摸监听
		sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

			@Override
			public void onTouchingLetterChanged(String s) {
				// 该字母首次出现的位置
				int position = adapter.getPositionForSection(s.charAt(0));
				if (position != -1) {
					sortListView.setSelection(position);
				}
			}
		});

		sortListView = (ListView) findViewById(R.id.beingmaturing_list);
		sortListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				changeMaturing(position,
						((PhoneModelbean) adapter.getItem(position)).getId());
			}
		});

		/** 获取数据 */
		// SourceDateList = filledData();
		SourceDateList = new ArrayList<PhoneModelbean>();
		// 根据a-z进行排序源数据
		// Collections.sort(SourceDateList, pinyinComparator);
		/** 自定义适配器 */
		adapter = new SortBaseBeingMarturingAdapter(
				BeingMarturingMangerActivity.this, SourceDateList);
		sortListView.setAdapter(adapter);
		updateInfos();
	}

	public Handler dialogHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (dg != null) {
				dg.dismiss();
			}
		}
	};

	public Dialog dg;

	public void updateInfos() {
		dg = MainFragment.createLoadingDialog(this);
		dg.show();
		new Thread(new Runnable() {
			@Override
			public void run() {
				Message msg = adapter.datatHandler.obtainMessage();
				SourceDateList = filledData();
				Collections.sort(SourceDateList, pinyinComparator);
				msg.obj = SourceDateList;
				adapter.datatHandler.sendMessage(msg);
				dialogHandler.sendEmptyMessage(0);
			}
		}).start();
	}

	/** 为ListView填充数据 */
	private List<PhoneModelbean> filledData() {
		List<PhoneModelbean> mSortList = new ArrayList<PhoneModelbean>();

		FarmingDaoDBManager farmingDao = new FarmingDaoDBManager(
				BeingMarturingMangerActivity.this);

		List<HashMap<String, String>> farmingDatas = farmingDao.getAllFarming();
		if (farmingDatas == null) {
			return mSortList;
		}
		BeingDaoDBManager beingDao = new BeingDaoDBManager(
				BeingMarturingMangerActivity.this);
		int farminglength = farmingDatas.size();
		HashMap<String, String> beingItem, farmingItem;

		for (int i = 0; i < farminglength; i++) {
			try {
				farmingItem = farmingDatas.get(i);
				beingItem = beingDao.getBeingById(farmingItem
						.get(TablesColumns.TABLEFARMING_BEING));
				// if(beingItem.get(TablesColumns.TABLEBEING_KINGDOM).equals("2"))
				// {
				// continue;
				// }
				PhoneModelbean sortModel = new PhoneModelbean();
				sortModel.setName(beingItem.get(TablesColumns.TABLEBEING_NAME));
				sortModel.setNameSecond(beingItem.get(TablesColumns.TABLEBEING_KINGDOM));
				sortModel.setImgSrc(beingItem.get(TablesColumns.TABLEBEING_IMAGE));
				sortModel.setId(farmingItem.get(TablesColumns.TABLEFARMING_ID));
				// if(beingItem.get(TablesColumns.TABLEBEING_KINGDOM).equals("1"))
				// {
				if (!farmingItem.get(TablesColumns.TABLEFARMING_MATURINGAT).equals(
						"null")) {
					sortModel.setDate(farmingItem
							.get(TablesColumns.TABLEFARMING_MATURINGAT));
				}
				// }

				/** 将汉字转换成拼音 */
				String pinyin = characterParser.getSelling(beingItem
						.get(TablesColumns.TABLEBEING_NAME));
				/** 截取拼音的首字母 */
				String sortString = pinyin.substring(0, 1).toUpperCase();

				/** 判断首字母是否是英文字母，正则表达式 */
				if (sortString.matches("[A-Z]")) {
					sortModel.setSortLetters(sortString.toUpperCase());
				} else {
					sortModel.setSortLetters("#");
				}

				/** 填充数据 */
				mSortList.add(sortModel);
			} catch (Exception e) {
				continue;
			}
		}

		return mSortList;
	}

	// @Override
	// protected void onActivityResult(int requestCode, int resultCode, Intent
	// data) {
	// if(resultCode == 2)
	// {
	// /** 获取数据 */
	// SourceDateList = filledData();
	//
	// // 根据a-z进行排序源数据
	// Collections.sort(SourceDateList, pinyinComparator);
	// /** 自定义适配器 */
	// adapter = new
	// SortBaseBeingMarturingAdapter(BeingMarturingMangerActivity.this,
	// SourceDateList);
	// sortListView.setAdapter(adapter);
	// }
	// super.onActivityResult(requestCode, resultCode, data);
	// }

	// 初始化参数
	private String initialParas(String farmingId, String date) {
		FarmingDaoDBManager farmingDao = new FarmingDaoDBManager(
				BeingMarturingMangerActivity.this);
		HashMap<String, String> farmingMap = farmingDao
				.getFarmingInfoById(farmingId);
		String collectsInfo = farmingMap.get(
				TablesColumns.TABLEFARMING_COLLECTORS)
				.substring(
						1,
						farmingMap.get(TablesColumns.TABLEFARMING_COLLECTORS)
								.length() - 1);
		String collect[] = collectsInfo.split(",");
		String paras = TablesColumns.TABLEFARMING_BEING + "="
				+ farmingMap.get(TablesColumns.TABLEFARMING_BEING);
		for (String collceitem : collect) {
			paras += "&" + TablesColumns.TABLEFARMING_COLLECTORS + "="
					+ collceitem;
		}
		paras += "&" + TablesColumns.TABLEFARMING_MATURINGAT + "=" + date;
		paras += "&" + TablesColumns.TABLEFARMING_FARM + "="
				+ farmingMap.get(TablesColumns.TABLEFARMING_FARM);
		return paras;
	}

	private void changeMaturing(int position, final String farmingId) {
		final AlertDialog dlg = new AlertDialog.Builder(
				BeingMarturingMangerActivity.this).create();
		dlg.show();
		dlg.setCanceledOnTouchOutside(false);
		Window window = dlg.getWindow();
		window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
		window.setContentView(R.layout.dialog_beingmarturing_modify_maturingat);

		TextView textHead = (TextView) window.findViewById(R.id.dialog_head);
		textHead.setText(R.string.farming_maturingat_setting);
		final NumberPicker yearPicker = (NumberPicker) window
				.findViewById(R.id.farming_being_year);
		final NumberPicker mouthPicker = (NumberPicker) window
				.findViewById(R.id.farming_being_mouth);
		final NumberPicker dayPicker = (NumberPicker) window
				.findViewById(R.id.farming_being_day);
		DatePickerAdapter.yearMouthDay(yearPicker, mouthPicker, dayPicker);
		if (!SourceDateList.get(position).getDate().equals("null")) {
			String time = SourceDateList.get(position).getDate().toString();
			yearPicker.setValue(Integer.parseInt(time.substring(0,
					time.indexOf("-"))));
			mouthPicker.setValue(Integer.parseInt(time.substring(
					time.indexOf("-") + 1, time.lastIndexOf("-"))));
			dayPicker.setValue(Integer.parseInt(time.substring(
					time.lastIndexOf("-") + 1, time.lastIndexOf("T"))));
		} else {
			yearPicker.setValue(StringUtil.getCurrentYear());
			mouthPicker.setValue(StringUtil.getCurrentMouth());
			dayPicker.setValue(StringUtil.getCurrentDay());
		}
		Button ok = (Button) window.findViewById(R.id.dialog_btn_submit);
		ok.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				dg = MainFragment
						.createLoadingDialog(BeingMarturingMangerActivity.this);
				dg.show();
				NetConnectionFarming.FarmingPutNetConnection(
						NetUrls.URL_FARMING_MODIFY(farmingId),
						initialParas(
								farmingId,
								yearPicker.getValue() + "-"
										+ mouthPicker.getValue() + "-"
										+ dayPicker.getValue() + "T00:00:00"),
						"Token "
								+ MySharePreference.getValueFromKey(
										BeingMarturingMangerActivity.this,
										MySharePreference.ACCOUNTTOKEN),
						new FarmingSuccessCallBack() {
							@Override
							public void onSuccess(HashMap<String, Object> result) {
								dg.dismiss();
								if (result != null) {
									if (result.keySet().contains("status_code")) {
										ErrorMarkToast
												.showCallBackToast(
														BeingMarturingMangerActivity.this,
														result);
										return;
									}
									FarmingDaoDBManager farmingDao = new FarmingDaoDBManager(
											BeingMarturingMangerActivity.this);
									farmingDao
											.changeMarturingAt(
													farmingId,
													result.get(
															TablesColumns.TABLEFARMING_MATURINGAT)
															.toString());
									/** 获取数据 */
									SourceDateList = filledData();

									// 根据a-z进行排序源数据
									Collections.sort(SourceDateList,
											pinyinComparator);
									/** 自定义适配器 */
									adapter = new SortBaseBeingMarturingAdapter(
											BeingMarturingMangerActivity.this,
											SourceDateList);
									sortListView.setAdapter(adapter);
									dlg.dismiss();
								} else {
									showToast(R.string.interneterror);
								}
							}
						});

			}
		});
		Button cancel = (Button) window.findViewById(R.id.dialog_btn_cancel);
		cancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dlg.dismiss();
			}
		});
	}

	private void showToast(int id) {
		Toast.makeText(BeingMarturingMangerActivity.this, id,
				Toast.LENGTH_SHORT).show();
	}

}
