package net.pink.accts;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

@SuppressLint("HandlerLeak")
public class FormActivity extends Activity {
	boolean isExit;
	private String url;
	HttpClient httpClient = new DefaultHttpClient();
	SharedPreferences preferences;
	private ProgressDialog pd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_form);
		preferences = FormActivity.this.getSharedPreferences("shareData",
				MODE_PRIVATE);
		url = preferences.getString("url", "");
		Button button1 = (Button) this.findViewById(R.id.submit);
		Button button2 = (Button) this.findViewById(R.id.gets);
		button2.setOnClickListener(new Button2OnClickListener());
		button1.setOnClickListener(new Button1OnClickListener());
	}

	class Button2OnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			gets();
		}
	}

	class Button1OnClickListener implements OnClickListener {
		public void onClick(View v) {
			add();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_form, menu);
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exit();
			return false;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_settings:
			break;

		case R.id.menu_quit:
			exitApp();
			break;
		}

		return true;
	}

	class QuitOnClickListener implements DialogInterface.OnClickListener {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			android.os.Process.killProcess(android.os.Process.myPid());
		}

	}

	/**
	 * 完完全全退出应用程序
	 */
	public void exitApp() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("提示");
		builder.setMessage("您确定要退出程序吗?");
		builder.setPositiveButton("确定", new QuitOnClickListener());
		builder.setNegativeButton("取消", null);
		builder.show();
	}

	public void exit() {
		if (!isExit) {
			isExit = true;
			Toast.makeText(getApplicationContext(), "再按一次退出程序",
					Toast.LENGTH_SHORT).show();
			mHandler.sendEmptyMessageDelayed(0, 2000);
		} else {
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME);
			startActivity(intent);
			System.exit(0);
		}
	}

	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			isExit = false;
		}

	};

	private Handler getsHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			pd.dismiss();
			if (msg.what != 1) {
				new AlertDialog.Builder(FormActivity.this).setTitle("信息")
						.setMessage("操作失败,错误码：" + msg.what)
						.setPositiveButton("确定", null).show();
			}
		}
	};

	private void gets() {
		// 构建一个下载进度条
		pd = ProgressDialog.show(FormActivity.this, "查询", "正在查询…");
		new Thread() {
			public void run() {
				// 在这里执行长耗时方法
				int n = longTimeMethod();
				// 执行完毕后给handler发送一个消息
				getsHandler.sendEmptyMessage(n);
			}

			private int longTimeMethod() {
				url = preferences.getString("url", "");
				String result = "";
				String uri = url + "/gets/account";
				Log.d("accouts", uri);
				HttpPost httpPost = new HttpPost(uri);
				try {
					HttpResponse response = httpClient.execute(httpPost); // 发起POST请求
					result = EntityUtils
							.toString(response.getEntity(), "utf-8");
					Log.d("accouts", result);
				} catch (ClientProtocolException e) {
					e.printStackTrace();
					return -1;
				} catch (ParseException e) {
					e.printStackTrace();
					return -2;
				} catch (IOException e) {
					e.printStackTrace();
					return -3;

				}
				if (result == null || "".equals(result)) {
					return 0;
				}
				Intent intent = new Intent(FormActivity.this,
						AccountsActivity.class);
				intent.putExtra("data", result);
				startActivity(intent);
				return 1;
			}
		}.start();
	}

	private Handler addHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			pd.dismiss();
			String info = "";
			if (msg.what == 1) {
				info = "操作成功";
			} else {
				info = "操作失败，错误码：" + msg.what;
			}
			new AlertDialog.Builder(FormActivity.this).setTitle("信息")
					.setMessage(info).setPositiveButton("确定", null).show();

		}
	};

	private void add() {
		// 构建一个下载进度条
		pd = ProgressDialog.show(FormActivity.this, "提交", "正在提交…");
		new Thread() {
			public void run() {
				// 在这里执行长耗时方法
				int n = longTimeMethod();
				// 执行完毕后给handler发送一个消息
				addHandler.sendEmptyMessage(n);
			}

			private int longTimeMethod() {
				EditText editText1 = (EditText) findViewById(R.id.editText1);
				DatePicker datePicker1 = (DatePicker) findViewById(R.id.datePicker1);
				EditText editText3 = (EditText) findViewById(R.id.editText3);
				EditText editText4 = (EditText) findViewById(R.id.editText4);
				EditText editText5 = (EditText) findViewById(R.id.editText5);
				String v1 = editText1.getText().toString();
				String v2 = datePicker1.getYear() + "-"
						+ (datePicker1.getMonth() + 1) + "-"
						+ datePicker1.getDayOfMonth();
				String v3 = editText3.getText().toString();
				String v4 = editText4.getText().toString();
				String v5 = editText5.getText().toString();
				String result = "";
				List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
				params.add(new BasicNameValuePair("money", v1));
				params.add(new BasicNameValuePair("date", v2));
				params.add(new BasicNameValuePair("item", v3));
				params.add(new BasicNameValuePair("category", v4));
				params.add(new BasicNameValuePair("note", v5));

				String uri = url + "/add/account";
				Log.d("form", uri);
				try {
					HttpPost httpPost = new HttpPost(uri);
					httpPost.setEntity(new UrlEncodedFormEntity(params,
							HTTP.UTF_8));
					HttpResponse response = httpClient.execute(httpPost); // 发起POST请求
					result = EntityUtils
							.toString(response.getEntity(), "utf-8");
					Log.d("resCode", response.getStatusLine().getStatusCode()
							+ "");
					if (result.length() > 1) {
						return 0;
					} else if ("1".equals(result)) {
						return 1;
					} else {
						return -1;
					}

				} catch (Exception exception) {
					exception.printStackTrace();
					return -2;
				}

			}
		}.start();
	}

}
