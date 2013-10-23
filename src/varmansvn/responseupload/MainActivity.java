package varmansvn.responseupload;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {

	private final String TAG = "ResponseUpload";
	private final String FAILED_MSG = "submitted failed";
	private final String SUCCESS_MSG = "submitted successful";

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Bundle bundle = msg.getData();
			String httpStatus = bundle.getString("httpStatus");

			String prompt = FAILED_MSG;

			if (httpStatus.equalsIgnoreCase("200") == true) {
				prompt = SUCCESS_MSG;
			}

			Context context = getApplicationContext();
			int duration = Toast.LENGTH_SHORT;
			Toast toast = Toast.makeText(context, prompt, duration);
			toast.show();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	public void onSubmit(View view) {

		Runnable runnable = new Runnable() {

			private int submitData() {

				int httpStatus = 400;

				HttpClient client = new DefaultHttpClient();

				HttpPost post = new HttpPost(
						"https://docs.google.com/forms/d/1c2ckWuXeiY-vKmKMfU9RSnEkVmlJa4LZvwrSg4_GczU/formResponse");

				List<BasicNameValuePair> results = new ArrayList<BasicNameValuePair>();

				results.add(new BasicNameValuePair("entry.1597203616",
						"test first name from android"));
				results.add(new BasicNameValuePair("entry.376115040",
						"test last name from android"));
				results.add(new BasicNameValuePair("entry.597467651",
						"test age from android"));

				try {
					post.setEntity(new UrlEncodedFormEntity(results));

				} catch (UnsupportedEncodingException e) {

					Log.e(TAG, "unsupported encoding exception", e);
				}
				try {

					HttpResponse httpResponse = client.execute(post);

					httpStatus = httpResponse.getStatusLine().getStatusCode();

				} catch (ClientProtocolException e) {

					Log.e(TAG, "client protocol exception", e);

				} catch (IOException e) {

					Log.e(TAG, "io exception", e);
				}

				return httpStatus;
			}

			public void run() {
				int httpStatus = submitData();

				Message msg = handler.obtainMessage();
				Bundle bundle = new Bundle();

				bundle.putString("httpStatus", String.valueOf(httpStatus));
				msg.setData(bundle);
				handler.sendMessage(msg);
			}
		};

		Thread mythread = new Thread(runnable);
		mythread.start();
	}
}
