package com.frogweather.app.activity;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.frogweather.app.R;
import com.frogweather.app.db.FrogWeatherDB;
import com.frogweather.app.model.City;
import com.frogweather.app.model.County;
import com.frogweather.app.model.Province;
import com.frogweather.app.util.HttpCallbackListener;
import com.frogweather.app.util.HttpUtil;
import com.frogweather.app.util.Utility;

public class ChooseAreaActivity extends Activity {

	public static final int LEVEL_PROVINCE = 0;
	public static final int LEVEL_CITY = 1;
	public static final int LEVEL_COUNTY = 2;

	private ProgressDialog progressDialog;
	private TextView titleText;
	private ListView listView;
	private ArrayAdapter<String> adapter;
	private FrogWeatherDB frogWeatherDB;
	private List<String> dataList = new ArrayList<String>();// ����Դ

	/**
	 * ʡ�б�
	 */
	private List<Province> provinceList;

	/**
	 * ���б�
	 */
	private List<City> cityList;

	/**
	 * ���б�
	 */
	private List<County> countyList;

	/**
	 * ѡ�е�ʡ��
	 */
	private Province selectedProvince;

	/**
	 * ѡ�еĳ���
	 */
	private City selectedCity;

	/**
	 * ��ǰѡ�еļ���
	 */
	private int currentLevel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);
		listView = (ListView) findViewById(R.id.list_view);
		titleText = (TextView) findViewById(R.id.title_text);
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, dataList);
		listView.setAdapter(adapter);
		frogWeatherDB = FrogWeatherDB.getInstance(this);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int index,
					long arg3) {
				// TODO Auto-generated method stub
				if (currentLevel == LEVEL_PROVINCE) {
					selectedProvince = provinceList.get(index);
					queryCities();
				} else if (currentLevel == LEVEL_CITY) {
					selectedCity = cityList.get(index);
					queryCounties();

				}
			}

		});
		queryProvinces();// ����ʡ������

	}

	/**
	 * ��ѯȫ�����е�ʡ�����ȴ����ݿ��ѯ�����û�в�ѯ���ٵ��������ϲ�ѯ
	 */
	private void queryProvinces() {
		provinceList = frogWeatherDB.loadProvinces();
		if (provinceList.size() > 0) {
			dataList.clear();
			for (Province province : provinceList) {
				dataList.add(province.getProvinceName());

			}
			adapter.notifyDataSetChanged();// ����֪ͨUI���ݷ����仯
			listView.setSelection(0);
			titleText.setText("�й�");
			currentLevel = LEVEL_PROVINCE;
		} else {
			queryFromServer(null, "province");

		}
	}

	/**
	 * ��ѯ��ѡ�е�ʡ������ʽ�����ȴ����ݿ��ѯ�����û�в�ѯ���ٵ��������ϲ�ѯ
	 */
	private void queryCities() {
		cityList = frogWeatherDB.loadCities(selectedProvince.getId());
		if (cityList.size() > 0) {
			dataList.clear();
			for (City city : cityList) {
				dataList.add(city.getCityName());

			}
			adapter.notifyDataSetChanged();// ����֪ͨUI���ݷ����仯
			listView.setSelection(0);
			titleText.setText(selectedProvince.getProvinceName());
			currentLevel = LEVEL_CITY;
		} else {
			queryFromServer(selectedProvince.getProvinceCode(), "city");

		}
	}

	/**
	 * ��ѯ��ѡ�е����ڵ������أ����ȴ����ݿ��ѯ�����û�в�ѯ���ٵ��������ϲ�ѯ
	 */
	private void queryCounties() {
		countyList = frogWeatherDB.loadCounties(selectedCity.getId());
		if (countyList.size() > 0) {
			dataList.clear();
			for (County county : countyList) {
				dataList.add(county.getCountyName());

			}
			adapter.notifyDataSetChanged();// ����֪ͨUI���ݷ����仯
			listView.setSelection(0);
			titleText.setText(selectedCity.getCityName());
			currentLevel = LEVEL_COUNTY;
		} else {
			queryFromServer(selectedCity.getCityCode(), "county");

		}
	}

	/**
	 * ���ݴ���Ĵ��ź����ʹӷ������ϲ�ѯʡ��������
	 */
	private void queryFromServer(final String code, final String type) {
		String address;
		if (!TextUtils.isEmpty(code)) {
			address = "http://www.weather.com.cn/data/list3/city" + code
					+ ".xml";
		} else {
			address = "http://www.weather.com.cn/data/list3/city.xml";
		}
		showProgressDialog();

		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {

			@Override
			public void onFinish(String response) {
				// TODO Auto-generated method stub
				boolean result = false;
				if ("province".equals(type)) {
					result = Utility.handleProvincesResponse(frogWeatherDB,
							response);

				} else if ("city".equals(type)) {
					result = Utility.handleCitiesResponse(frogWeatherDB,
							response, selectedProvince.getId());

				} else if ("county".equals(type)) {
					result = Utility.handleCountiesResponse(frogWeatherDB,
							response, selectedCity.getId());

				}

				if (result) {
				}
				// ͨ��runOnUiThread()�����ص����̴߳����߼�
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						closeProgressDialog();
						if ("province".equals(type)) {
							queryProvinces();
						} else if ("city".equals(type)) {
							queryCities();
						} else if ("county".equals(type)) {
							queryCounties();
						}
					}
				});

			}

			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				// ͨ��ͨ��runOnUiThread()�����ص����̴߳����߼�
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "����ʧ��",
								Toast.LENGTH_SHORT).show();
					}
				});

			}
		});

	}

	/**
	 * ��ʾ���ȶԻ���
	 */
	private void showProgressDialog() {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("���ڼ���...");
			progressDialog.setCanceledOnTouchOutside(false);

		}
		progressDialog.show();
	}

	/**
	 * �رս��ȶԻ���
	 */
	private void closeProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}

	/**
	 * ����Back���������ݵ�ǰ�ļ������жϣ���ʱӦ�÷������б�ʡ�б�����ֱ���˳�
	 */

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (currentLevel == LEVEL_COUNTY) {
			queryCities();
		} else if (currentLevel == LEVEL_CITY) {
			queryProvinces();

		} else {
		}
	}

}
