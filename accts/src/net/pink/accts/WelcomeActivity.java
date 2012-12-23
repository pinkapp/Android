package net.pink.accts;

import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;

public class WelcomeActivity extends Activity {
	protected static final int LOGINOVER = 0;
	protected static final String TAG = "WelcomeAct";
	private Handler handler; // 因为要重写构造方法，所以不能用匿名内部类

	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_welcome);
		ProgressBar loading = (ProgressBar) findViewById(R.id.loading);
		loading.setVisibility(View.VISIBLE);
		SharedPreferences preferences = this.getSharedPreferences("shareData", MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString("url", "https://accts-pink.rhcloud.com");
		editor.commit();
		HandlerThread myThread = new HandlerThread("myHandlerThread");
		myThread.start();
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == LOGINOVER) {
					Log.i(TAG, Thread.currentThread().getName());
					Intent intent = new Intent(WelcomeActivity.this,
							FormActivity.class);
					startActivity(intent);
				}
			}

		};
		tt.run();
	}

	TimerTask tt = new TimerTask() {
		@Override
		public void run() {
			Log.i(TAG, Thread.currentThread().getName());
			handler.sendMessage(handler.obtainMessage(LOGINOVER));
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.activity_welcome, menu);
		return true;
	}

}
