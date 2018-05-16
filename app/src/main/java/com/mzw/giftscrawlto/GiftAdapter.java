package com.mzw.giftscrawlto;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import java.util.List;

public class GiftAdapter extends BaseAdapter {
	private List<GiftBean> emojiList;
	private LayoutInflater mInflater;
	private Context mContext;
	private int mWidth = 0;

	public GiftAdapter(Context context, List<GiftBean> emojiList, int width) {
		super();
		this.emojiList = emojiList;
		this.mInflater = LayoutInflater.from(context);
		this.mContext = context;
		mWidth = width - dip2px(mContext, 10);
	}

	@Override
	public int getCount() {
		return emojiList.size();
	}

	@Override
	public GiftBean getItem(int position) {
		return emojiList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		if (convertView==null) {
			convertView=mInflater.inflate(R.layout.gift_item, null);
			holder=new ViewHolder();
			holder.mImageView = (ImageView) convertView.findViewById(R.id.gifttion);
			holder.price = (TextView) convertView.findViewById(R.id.id_price);
			holder.mPicLayout = (LinearLayout) convertView.findViewById(R.id.giftlayout);
			LayoutParams params = new LayoutParams(mWidth / 5, mWidth / 5);
			holder.mPicLayout.setLayoutParams(params);
			int paddding = dip2px(mContext, 5);
			holder.mPicLayout.setPadding(paddding, paddding, paddding, paddding);
			convertView.setTag(holder);
		}else {
			holder=(ViewHolder) convertView.getTag();
		}

		try {
			GiftBean bean = getItem(position);
			String fileName = bean.fileName;
//			Field field = R.drawable.class.getDeclaredField(fileName);
//			int resId = field.getInt(null);
			//Bitmap bm = BitmapFactory.decodeResource(context.getResources(), resId);

			Bitmap mBitmap = GiftData.getGiftBitmap(mContext,fileName);

			if(bean.sign == 1){
				holder.mPicLayout.setBackgroundColor(Color.parseColor("#50A2A2A2"));
			}else{
				holder.mPicLayout.setBackgroundColor(Color.parseColor("#00000000"));
			}
			holder.mImageView.setImageBitmap(mBitmap);
//			holder.mImageView.setImageResource(resId);
			holder.price.setText(bean.price + " 钱币");
		} catch (Exception e) {
		}

		return convertView;
	}


	final static class ViewHolder {
		ImageView mImageView;
		TextView price;
		LinearLayout mPicLayout;

		@Override
		public int hashCode() {
			return this.mImageView.hashCode() + mPicLayout.hashCode();
		}
	}



	public int dip2px(Context context, int dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}
}
