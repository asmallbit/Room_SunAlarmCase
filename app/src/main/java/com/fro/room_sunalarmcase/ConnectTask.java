package com.fro.room_sunalarmcase;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fro.util.FRODigTube;
import com.fro.util.FROSun;
import com.fro.util.StreamUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import androidx.annotation.RequiresApi;

/**
 * Created by Jorble on 2016/3/4.
 */
public class ConnectTask extends AsyncTask<Void, Void, Void> {

	@SuppressLint("StaticFieldLeak")
	private final Context context;
	@SuppressLint("StaticFieldLeak")
	TextView sun_tv;
	@SuppressLint("StaticFieldLeak")
	TextView info_tv;
	@SuppressLint("StaticFieldLeak")
	ProgressBar progressBar;

	private Socket sunSocket;
	private Socket tubeSocket;
	private Socket buzzerSocket;
	private Socket curtainSocket;

	private boolean CIRCLE = false;

	public ConnectTask(Context context, TextView sun_tv, TextView info_tv, ProgressBar progressBar) {
		this.context = context;
		this.sun_tv = sun_tv;
		this.info_tv = info_tv;
		this.progressBar = progressBar;
	}

	/**
	 * 更新界面
	 */
	@Override
	protected void onProgressUpdate(Void... values) {
		if (sunSocket != null && tubeSocket != null && buzzerSocket != null && curtainSocket != null) {
			info_tv.setTextColor(context.getResources().getColor(R.color.green));
			info_tv.setText("连接正常！");
		} else {
			info_tv.setTextColor(context.getResources().getColor(R.color.red));
			info_tv.setText("连接失败！");
		}

		// 进度条消失
		progressBar.setVisibility(View.GONE);

		// 显示数据
		if (Const.sun != null) {
			sun_tv.setText(String.valueOf(Const.sun));
		}

	}

	/**
	 * 准备
	 */
	@Override
	protected void onPreExecute() {
		info_tv.setText("正在连接...");
	}

	/**
	 * 子线程任务
	 * 
	 * @param params
	 * @return
	 */
	@RequiresApi(api = Build.VERSION_CODES.N)
	@Override
	protected Void doInBackground(Void... params) {
		// 连接
		sunSocket = getSocket(Const.SUN_IP, Const.SUN_PORT);
		tubeSocket = getSocket(Const.TUBE_IP, Const.TUBE_PORT);
		buzzerSocket = getSocket(Const.BUZZER_IP, Const.BUZZER_PORT);
		curtainSocket = getSocket(Const.CURTAIN_IP, Const.CURTAIN_PORT);

		//设置检测的数据
		String date;
		int sunkey, sunmax;
		boolean isover;



		// 循环读取数据
		while (CIRCLE) {
			try {
				// 如果全部连接成功
				if (sunSocket != null && tubeSocket != null && buzzerSocket != null && curtainSocket != null) {
					// 查询光照度
					StreamUtil.writeCommand(sunSocket.getOutputStream(), Const.SUN_CHK);
					Thread.sleep(Const.time / 2);
					byte[] read_buff = StreamUtil.readData(sunSocket.getInputStream());
					Float sun = FROSun.getData(Const.SUN_LEN, Const.SUN_NUM, read_buff);
					if (sun != null) {
						//把光照度显示到屏幕上
						Const.sun = (int) (float) sun;

						//获取到光照度,可以用sqlite存储光照度数据
						sunkey = Const.sun;
					}

					// 数码管显示
					Const.TUBE_CMD = FRODigTube.intToCmdString(Const.sun);
					StreamUtil.writeCommand(tubeSocket.getOutputStream(), Const.TUBE_CMD);
					Thread.sleep(Const.time / 2);

					// 如果联动打开状态并且超过上限，蜂鸣器报警1s，打开窗帘
					Log.i(Const.TAG, "Const.linkage=" + Const.linkage);
					Log.i(Const.TAG, "Const.sun=" + Const.sun);
					Log.i(Const.TAG, "Const.maxLim=" + Const.maxLim);
					if (Const.linkage && Const.sun > Const.maxLim) {
						// 蜂鸣器
						if (!Const.isBuzzerOn) {
							StreamUtil.writeCommand(buzzerSocket.getOutputStream(), Const.BUZZER_ON);
							Thread.sleep(1000);
							StreamUtil.writeCommand(buzzerSocket.getOutputStream(), Const.BUZZER_OFF);
							Thread.sleep(200);
						}
						// 窗帘
						if (!Const.isCurtainOn) {
							StreamUtil.writeCommand(curtainSocket.getOutputStream(), Const.CURTAIN_ON);
							Thread.sleep(200);
						}
					}
				}
				// 更新界面
				publishProgress();
				Thread.sleep(200);

			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// 最后关闭蜂鸣器，关闭风扇
		try {
			if (buzzerSocket != null) {
				Const.isBuzzerOn = false;
				StreamUtil.writeCommand(buzzerSocket.getOutputStream(), Const.BUZZER_OFF);
				Thread.sleep(200);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			closeSocket();
		}
		return null;

	}

	/**
	 * 建立连接并返回socket，若连接失败返回null
	 * 
	 * @param ip
	 * @param port
	 * @return
	 */
	private Socket getSocket(String ip, int port) {
		Socket mSocket = new Socket();
		InetSocketAddress mSocketAddress = new InetSocketAddress(ip, port);
		// socket连接
		try {
			// 设置连接超时时间为3秒
			mSocket.connect(mSocketAddress, 3000);
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 检查是否连接成功
		if (mSocket.isConnected()) {
			Log.i(Const.TAG, ip + "连接成功！");
			return mSocket;
		} else {
			Log.i(Const.TAG, ip + "连接失败！");
			return null;
		}
	}

	public void setCIRCLE(boolean cIRCLE) {
		CIRCLE = cIRCLE;
	}

	@Override
	protected void onCancelled() {
		info_tv.setTextColor(context.getResources().getColor(R.color.gray));
		info_tv.setText("请点击连接！");
	}

	/**
	 * 关闭socket
	 */
	void closeSocket() {
		try {
			if (sunSocket != null) {
				sunSocket.close();
			}
			if (tubeSocket != null) {
				tubeSocket.close();
			}
			if (buzzerSocket != null) {
				buzzerSocket.close();
			}
			if (curtainSocket != null) {
				curtainSocket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
