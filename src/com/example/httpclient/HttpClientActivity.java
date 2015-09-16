package com.example.httpclient;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class HttpClientActivity extends Activity implements OnClickListener
{

	public static final int SHOW_RESPONSE =0;
	private Button sendRequest;
	private TextView responseText;
	
	private Handler handler = new Handler(){
		//主线程获取子线程传递回来的message,进行处理，显示在界面
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
				 case SHOW_RESPONSE:
					 String response = (String) msg.obj;
					// 在这里进行UI操作，将结果显示到界面上
					 responseText.setText(response);
			}	
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_http_client);
		sendRequest=(Button)findViewById(R.id.send_request);
		responseText=(TextView)findViewById(R.id.response);
		
		sendRequest.setOnClickListener(this);
			
	}
	
	@Override
	public void onClick(View v)
	{
		if(v.getId()== R.id.send_request)
		{
			//调用该方法
			sendRequestWithHttpClient();
		}
	}
	
	public void sendRequestWithHttpClient()
	{
		new Thread(new Runnable()
		{			
			@Override
			public void run()
			{
				try
				{
					//HttpClient 是一个接口，因此无法创建它的实例，通常情况下都会创建一个 DefaultHttpClient 的实例
					HttpClient httpClient= new  DefaultHttpClient();
					//创建一个 HttpGet 对象，并传入目标的网络地址
					HttpGet httpGet=new HttpGet("http://www.zhihu.com");
					//调用 HttpClient 的 execute()方法,发送 GET请求并等待响应
					//执行 execute()方法之后会返回一个 HttpResponse对象,服务器所返回的所有信息就会包含在这里面
					HttpResponse httpResponse = httpClient.execute(httpGet);
					// 判断网络连接是否成功
					//先取出服务器返回的状态码，如果等于 200就说明请求和响应都成功了
					if(httpResponse.getStatusLine().getStatusCode()==200)
					{
						//调用 getEntity()方法获取到一个 HttpEntity 实例
						HttpEntity entity =httpResponse.getEntity();
						//用 EntityUtils.toString()这个静态方法将 HttpEntity 转换成字符串
						//如果服务器返回的数据是带有中文的,将字符集指定成 utf-8 
						String response = EntityUtils.toString(entity,"utf-8");
						
						Message message = new Message();
						message.what = SHOW_RESPONSE;
						//  将服务器返回的结果存放到Message
						message.obj = response.toString();
						
						handler.sendMessage(message);
					}
					
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	
}
