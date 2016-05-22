package com.frogweather.app.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.frogweather.app.model.City;
import com.frogweather.app.model.County;
import com.frogweather.app.model.Province;

public class FrogWeatherDB {
	/**
	 * ���ݿ���
	 */
	public static final String DB_NAME = "frog_weather";

	/**
	 * ���ݿ�汾
	 */
	public static final int VERSION = 1;
	private static FrogWeatherDB frogWeatherDB;
	private SQLiteDatabase db;

	/**
	 * �����췽��˽�л�
	 */
	private FrogWeatherDB(Context context) {

		FrogWeatherOpenHelper dbHelper = new FrogWeatherOpenHelper(context,
				DB_NAME, null, VERSION);

		db = dbHelper.getWritableDatabase();
	}

	/**
	 * ��ȡFrogWeather��ʵ��
	 */
	public synchronized static FrogWeatherDB getInstance(Context context) {
		if (frogWeatherDB == null) {
			frogWeatherDB = new FrogWeatherDB(context);
		}
		return frogWeatherDB;
	}

	/**
	 * ��Provinceʵ���洢�����ݿ�
	 */
	public void savaProvince(Province province) {
		if (province != null) {
			ContentValues values = new ContentValues();
			values.put("province_name", province.getProvinceName());
			values.put("province_code", province.getProvinceCode());
			db.insert("Province", null, values);
		}
	}

	/**
	 * �����ݿ��ȡȫ������ʡ����Ϣ
	 */
	public List<Province> loadProvinces() {
		List<Province> list = new ArrayList<Province>();
		Cursor cursor = db
				.query("Province", null, null, null, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				Province province = new Province();
				province.setId(cursor.getInt(cursor.getColumnIndex("id")));
				province.setProvinceName(cursor.getString(cursor
						.getColumnIndex("province_name")));
				province.setProvinceCode(cursor.getString(cursor
						.getColumnIndex("province_code")));
				list.add(province);

			} while (cursor.moveToNext());
		}
		if (cursor != null) {
			cursor.close();

		}
		return list;
	}

	/**
	 * ��Cityʵ���洢�����ݿ�
	 */
	public void savaCity(City city) {
		if (city != null) {
			ContentValues values = new ContentValues();
			values.put("city_name", city.getCityName());
			values.put("city_code", city.getCityCode());
			values.put("province_id", city.getProvinceId());
			db.insert("City", null, values);
		}
	}

	/**
	 * �����ݿ��ĳʡ���������е���Ϣ
	 */
	public List<City> loadCities(int provinceId) {
		List<City> list = new ArrayList<City>();
		Cursor cursor = db.query("City", null, "province_id = ?",
				new String[] { String.valueOf(provinceId) }, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				City city = new City();
				city.setId(cursor.getInt(cursor.getColumnIndex("id")));
				city.setCityName(cursor.getString(cursor
						.getColumnIndex("city_name")));
				city.setCityCode(cursor.getString(cursor
						.getColumnIndex("city_code")));
				city.setProvinceId(provinceId);
				list.add(city);

			} while (cursor.moveToNext());
		}
		if (cursor != null) {
			cursor.close();

		}
		return list;
	}

	/**
	 * ��Countyʵ���洢�����ݿ�
	 */
	public void savaCounty(County county) {
		if (county != null) {
			ContentValues values = new ContentValues();
			values.put("county_name", county.getCountyName());
			values.put("county_code", county.getCountyCode());
			values.put("city_id", county.getCityId());
			db.insert("County", null, values);
		}
	}

	/**
	 * �����ݿ��ĳʡ���������е���Ϣ
	 */
	public List<County> loadCounties(int cityId) {
		List<County> list = new ArrayList<County>();
		Cursor cursor = db.query("County", null, "city_id = ?",
				new String[] { String.valueOf(cityId) }, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				County county = new County();
				county.setId(cursor.getInt(cursor.getColumnIndex("id")));
				county.setCountyName(cursor.getString(cursor
						.getColumnIndex("county_name")));
				county.setCountyCode(cursor.getString(cursor
						.getColumnIndex("county_code")));
				county.setCityId(cityId);
				list.add(county);

			} while (cursor.moveToNext());
		}
		if (cursor != null) {
			cursor.close();

		}
		return list;
	}

}
