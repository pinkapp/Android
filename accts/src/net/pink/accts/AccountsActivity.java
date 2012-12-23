package net.pink.accts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.SimpleAdapter;

public class AccountsActivity extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getListView().setBackgroundResource(R.drawable.bg1);
		getListView().setScrollingCacheEnabled(false);
		getListView().setDividerHeight(0);
		//setTitle("账目");
		Bundle bundle = getIntent().getExtras();
		String data = bundle.getString("data");// 读出数据
		JSONArray jsonArray = null;
		try {
			jsonArray = new JSONArray(data);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			for (int i = 0; i < jsonArray.length(); i++) {
				Map<String, Object> map = new HashMap<String, Object>();
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				Log.d("accouts", jsonObject.getString("item"));
				map.put("item", jsonObject.getString("item"));
				map.put("money", jsonObject.getDouble("money"));
				list.add(map);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		SimpleAdapter adapter = new SimpleAdapter(this, list,
				R.layout.activity_accounts, new String[] { "item", "money" },
				new int[] { R.id.item, R.id.money });
		setListAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.activity_accounts, menu);
		return true;
	}

}
