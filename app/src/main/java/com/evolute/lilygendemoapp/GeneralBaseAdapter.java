package com.evolute.lilygendemoapp;

import java.util.List;

import com.evolute.lilydemo.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class GeneralBaseAdapter extends BaseAdapter {
	Context context;
	List<RowItem> rowItems;

	public GeneralBaseAdapter(Context context, List<RowItem> items) {
		this.context = context;
		this.rowItems = items;
	}

	/*private view holder class*/
	private class ViewHolder {
		TextView txtTitle;
		TextView txtDesc;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		LayoutInflater mInflater = (LayoutInflater) 
				context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.list_item1, null);
			holder = new ViewHolder();
			holder.txtDesc = (TextView) convertView.findViewById(R.id.desc);
			holder.txtTitle = (TextView) convertView.findViewById(R.id.title);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		RowItem rowItem = (RowItem) getItem(position);
		holder.txtDesc.setText(rowItem.getDesc());
		holder.txtTitle.setText(rowItem.getTitle());


		Animation animation = null;
		animation = AnimationUtils.loadAnimation(context, R.anim.fade_in);
		animation.setDuration(500);
		convertView.startAnimation(animation);
		animation = null;


		return convertView;
	}

	@Override
	public int getCount() {     
		return rowItems.size();
	}

	@Override
	public Object getItem(int position) {
		return rowItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return rowItems.indexOf(getItem(position));
	}

}
