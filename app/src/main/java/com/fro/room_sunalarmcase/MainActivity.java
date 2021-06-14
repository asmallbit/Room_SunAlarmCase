package com.fro.room_sunalarmcase;

//import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fro.room_sunalarmcase.view.PushSlideSwitchView;
//import com.mylhyl.acp.Acp;
//import com.mylhyl.acp.AcpListener;
//import com.mylhyl.acp.AcpOptions;

//import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.AsyncTask;
//import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends Activity {

	private Context context;

	private EditText sunIp_et;
	private EditText sunPort_et;
	private EditText tubeIp_et;
	private EditText tubePort_et;
	private EditText buzzerIp_et;
	private EditText buzzerPort_et;
	private EditText curtainIp_et;
	private EditText curtainPort_et;

	private EditText time_et;
	private TextView sun_tv;
	private EditText maxLim_et;
	private ToggleButton connect_tb;
	private TextView info_tv;
	private PushSlideSwitchView linkage_sw;
	private ProgressBar progressBar;
	private ConnectTask connectTask;

	private PushSlideSwitchView storeStatus;
	private Button showDataBtn;

	private long cycle=5000;
	private long delay=0;

	private int bool;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		context = this;

		/*
		//数据库是不需要存储权限的
		//Check Permission
		if (Build.VERSION.SDK_INT >= 23) {
			checkPermission();
		}
		 */

		// 绑定控件
		bindView();
		// 初始化数据
		initData();
		// 事件监听
		initEvent();
	}

	/**
	 * 绑定控件
	 */
	private void bindView() {
		sunIp_et = findViewById(R.id.sunIp_et);
		sunPort_et = findViewById(R.id.sunPort_et);
		tubeIp_et = findViewById(R.id.tubeIp_et);
		tubePort_et = findViewById(R.id.tubePort_et);
		buzzerIp_et = findViewById(R.id.buzzerIp_et);
		buzzerPort_et = findViewById(R.id.buzzerPort_et);
		curtainIp_et = findViewById(R.id.curtainIp_et);
		curtainPort_et = findViewById(R.id.curtainPort_et);

		time_et = findViewById(R.id.time_et);
		maxLim_et = findViewById(R.id.maxLim_et);
		connect_tb = findViewById(R.id.connect_tb);
		info_tv = findViewById(R.id.info_tv);
		sun_tv = findViewById(R.id.sun_tv);
		linkage_sw = findViewById(R.id.linkage_sw);
		progressBar = findViewById(R.id.progressBar);
		storeStatus = findViewById(R.id.store_status);
		showDataBtn = findViewById(R.id.show_data);
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		sunIp_et.setText(Const.SUN_IP);
		sunPort_et.setText(String.valueOf(Const.SUN_PORT));
		tubeIp_et.setText(Const.TUBE_IP);
		tubePort_et.setText(String.valueOf(Const.TUBE_PORT));
		buzzerIp_et.setText(Const.BUZZER_IP);
		buzzerPort_et.setText(String.valueOf(Const.BUZZER_PORT));
		curtainIp_et.setText(Const.CURTAIN_IP);
		curtainPort_et.setText(String.valueOf(Const.CURTAIN_PORT));

		time_et.setText(String.valueOf(Const.time));
		maxLim_et.setText(String.valueOf(Const.maxLim));
		linkage_sw.setChecked(true);

		//默认不会保存数据
		storeStatus.setChecked(false);

		info_tv.setText("请点击连接!");
	}

	/**
	 * 按钮监听
	 */
	private void initEvent() {

		// 联动
		linkage_sw.setOnChangeListener((switchView, isChecked) -> {
			if (isChecked) {
				Const.linkage = true;
			} else {
				Const.linkage = false;
			}
		});

		// 连接
		connect_tb.setOnCheckedChangeListener((buttonView, isChecked) -> {
			//保存数据的定时任务
			Timer timer = new Timer();

			if (isChecked) {

				// 获取IP和端口
				String SUN_IP = sunIp_et.getText().toString().trim();
				String SUN_PORT = sunPort_et.getText().toString().trim();
				String TUBE_IP = tubeIp_et.getText().toString().trim();
				String TUBE_PORT = tubePort_et.getText().toString().trim();
				String BUZZER_IP = buzzerIp_et.getText().toString().trim();
				String BUZZER_PORT = buzzerPort_et.getText().toString().trim();
				String CURTAIN_IP = curtainIp_et.getText().toString().trim();
				String CURTAIN_PORT = curtainPort_et.getText().toString().trim();

				if (checkIpPort(SUN_IP, SUN_PORT) && checkIpPort(TUBE_IP, TUBE_PORT)
						&& checkIpPort(BUZZER_IP, BUZZER_PORT) && checkIpPort(CURTAIN_IP, CURTAIN_PORT)) {
					Const.SUN_IP = SUN_IP;
					Const.SUN_PORT = Integer.parseInt(SUN_PORT);
					Const.TUBE_IP = TUBE_IP;
					Const.TUBE_PORT = Integer.parseInt(TUBE_PORT);
					Const.BUZZER_IP = BUZZER_IP;
					Const.BUZZER_PORT = Integer.parseInt(BUZZER_PORT);
					Const.CURTAIN_IP = CURTAIN_IP;
					Const.CURTAIN_PORT = Integer.parseInt(CURTAIN_PORT);
				} else {
					Toast.makeText(context, "配置信息不正确,请重输！", Toast.LENGTH_SHORT).show();
					return;
				}
				// 获取其他参数
				Const.time = Integer.parseInt(time_et.getText().toString().trim());
				Const.maxLim = Integer.parseInt(maxLim_et.getText().toString().trim());

				// 进度条显示
				progressBar.setVisibility(View.VISIBLE);

				// 开启任务
				connectTask = new ConnectTask(context, sun_tv, info_tv, progressBar);
				connectTask.setCIRCLE(true);
				connectTask.execute();

				//保存数据
				storeStatus.setOnChangeListener((switchView, isCheck) -> {
					//如果选中,就将数据保存
					if(isCheck){
						//保存到数据库
						if(connect_tb.isChecked()){
							//每隔5s记录一次
							timer.schedule(new HandleData(), delay, cycle);
						}
					}
				});
			} else {

				// 取消任务
				if (connectTask != null && connectTask.getStatus() == AsyncTask.Status.RUNNING) {
					connectTask.setCIRCLE(false);
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					// 如果Task还在运行，则先取消它
					connectTask.cancel(true);
					connectTask.closeSocket();
				}
				// 进度条消失
				progressBar.setVisibility(View.GONE);
				info_tv.setText("请点击连接！");
				info_tv.setTextColor(context.getResources().getColor(R.color.gray));
				//https://stackoverflow.com/questions/1409116/how-to-stop-the-task-scheduled-in-java-util-timer-class
				//停止向数据库写入数据
				timer.cancel();
				timer.purge();
			}
		});

		//显示存储的数据
		showDataBtn.setOnClickListener(v -> {
			//Intent  到ShowStoredData
			Intent mIntent = new Intent(MainActivity.this, ShowStoredData.class);
			startActivity(mIntent);
		});

	}

	//handle the data, push them to database
	public class HandleData extends TimerTask {
		public void run(){
			//将返回的数据保存到SQlite数据库
			DBHelper mDBHelper = new DBHelper(MainActivity.this);
			if (Const.sun>Const.maxLim){
				bool = 1;
			} else {
				bool = 0;
			}
			boolean result = mDBHelper.addOne(getTime(), Const.sun,  Const.maxLim, bool);
			//数据保存失败提示
			if(result==false){
				Toast.makeText(MainActivity.this, "数据保存失败", Toast.LENGTH_LONG).show();
			}
		}
	}

	/**
	 * IP地址可用端口号验证，可用端口号（1024-65536）
	 *
	 * @param IP
	 * @param port
	 * @return
	 */
	private boolean checkIpPort(String IP, String port) {
		boolean isIpAddress = false;
		boolean isPort = false;

		if (IP == null || IP.length() < 7 || IP.length() > 15 || "".equals(IP) || port == null || port.length() < 4
				|| port.length() > 5 || "".equals(port)) {
			return false;
		}
		// 判断IP格式和范围
		String rexp = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";

		Pattern pat = Pattern.compile(rexp);

		Matcher mat = pat.matcher(IP);

		isIpAddress = mat.find();

		// 判断端口
		int portInt = Integer.parseInt(port);
		if (portInt > 1024 && portInt < 65536) {
			isPort = true;
		}

		return (isIpAddress && isPort);
	}

	@Override
	public void finish() {
		super.finish();
		// 取消任务
		if (connectTask != null && connectTask.getStatus() == AsyncTask.Status.RUNNING) {
			connectTask.setCIRCLE(false);
			// 如果Task还在运行，则先取消它
			connectTask.cancel(true);
			connectTask.closeSocket();
		}
	}

	public String getTime(){
		//时间
		@SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("yy.MM.dd 'at' HH:mm:ss");
		String date = df.format(Calendar.getInstance().getTime());
		return date;
	}

	/*
	//ask for permission
	private void checkPermission() {
		Acp.getInstance(this).request(new AcpOptions.Builder()
						.setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
						.build(),
				new AcpListener() {
					@Override
					public void onGranted() {

					}

					@Override
					public void onDenied(List permissions) {
						Toast.makeText(null, "请授予存储权限,否则数据无法保存至数据库", Toast.LENGTH_SHORT).show();
					}
				});
	}
	 */
}
