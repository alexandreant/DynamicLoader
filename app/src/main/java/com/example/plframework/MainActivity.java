package com.example.plframework;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {

	private static final String TAG = "MainActivity";
	private ListView mListView;
	private ArrayList<MsgInfo> datas = new ArrayList<MsgInfo>();
	public Intent mIntent;
	private MsgAdapter mMsgAdapter;
	//TODO: make builder
	class MsgInfo {
		private String type;
		private String title;
		private String time;
		private String content;
		private String description;
		private String path;
		
		public String getType() {
			return type == null ? "type" : type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public String getTitle() {
			return title == null ? "title" : title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public String getTime() {
			return time == null ? "time" : time;
		}
		public void setTime(String time) {
			this.time = time;
		}
		public String getContent() {
			return content == null ? "content" : content;
		}
		public void setContent(String content) {
			this.content = content;
		}
		public String getDescription() {
			return description == null ? "description" : description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public String getPath() {
			return path;
		}
		public void setPath(String path) {
			this.path = path;
		}		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		probePlugin();
		mListView = (ListView) findViewById(R.id.msglist);

		mMsgAdapter = new MsgAdapter(this);
		mListView.setAdapter(mMsgAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
//				MyApplication.CUSTOM_LOADER = ;
				String title = datas.get(position).getTitle();
				MyApplication.CUSTOM_LOADER = MyApplication.PLUGIN_LOADER.get(title);
				Log.d(TAG, title);
				Intent i = new Intent("com.example.intent.action.stub_ACTIVITY");
				startActivity(i);
			}
		});
		
	}

	private void probePlugin() {
		try {
			AssetManager asset = getAssets();
			for (String s : asset.list("apks")) {
				addItem(s, "apks/" + s);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void addItem(String title, String path) {
		MsgInfo data = new MsgInfo();
		data.setPath(path);
		data.setTitle(title);
		datas.add(data);
	}
	 
	@Override
	public Resources getResources() {
		// TODO Auto-generated method stub
		return super.getResources();
	}
	
	@Override
	public AssetManager getAssets() {
		// TODO Auto-generated method stub
		return super.getAssets();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private class ViewHolder {
		public TextView title;
		public ImageView headImage;
		public TextView description;
		public TextView time;
	}
	class MsgAdapter extends BaseAdapter {
		
		private LayoutInflater inflater = null;
		
		public MsgAdapter(Context context) {
			inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return datas.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return datas.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder mViewHolder = null;
			if (convertView == null) {
				mViewHolder = new ViewHolder();
				convertView = inflater.inflate(R.layout.msg_item, null);
				
				mViewHolder.title = (TextView) convertView.findViewById(R.id.uin);
				mViewHolder.headImage = (ImageView) convertView.findViewById(R.id.head_image);
				mViewHolder.time = (TextView) convertView.findViewById(R.id.time);
				mViewHolder.description = (TextView) convertView.findViewById(R.id.description);
				mViewHolder.description.setSingleLine(true);
				mViewHolder.description.setTextSize(16.0f);
				mViewHolder.description.setEllipsize(TruncateAt.END);
				
				convertView.setTag(mViewHolder);
			} else {
				mViewHolder = (ViewHolder) convertView.getTag();
			}
//			if (((MyApplication)getApplication()).mBitmap != null)
//				mViewHolder.headImage.setImageBitmap(((MyApplication)getApplication()).mBitmap);
			mViewHolder.title.setText(datas.get(position).getTitle());
			mViewHolder.time.setText(datas.get(position).getTime());
			return convertView;
		}		
	}
}
