package com.mzw.giftscrawlto;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by think on 2018/4/20.
 */

public class GiftPopupWindow extends PopupWindow implements ViewPager.OnPageChangeListener,View.OnClickListener {

    List<List<GiftBean>> mGiftList;
    private int screenWidth, screenHeight;

    ViewPager mViewPager;
    LinearLayout mLayoutCircle;
    RelativeLayout mEmotionLayout;
    GiftPagerAdapter mEmotionAdapter;
    private LinkedList<View> mViewList = new LinkedList<View>();
    private LinkedList<GiftAdapter> mScrawlAdapterList = new LinkedList<GiftAdapter>();
    Context mContext;
    public int mPageIndxe = 0;

    public int selectI= 0,selectJ= 0;

    ScrawlView mScrawlView;
    ImageView clear_view;
    ImageView hide_view;
    TextView total_price_view;
    private int totalPrice = 0;
    TextView chongzhi_view;
    TextView my_coins;//钱币总额
    TextView send_view;
    private PopupWindowScrawlListener mPopupWindowScrawlListener;
    private int myCoins = 0;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    totalPrice = msg.arg1;
                    if(totalPrice > 0){
                        total_price_view.setText("价值"+totalPrice+"钱币");
                    }else{
                        total_price_view.setText("可在上方画出图案");
                    }
                    break;
            }
        }
    };



    public GiftPopupWindow(Context mContext, View parent, List<List<GiftBean>> mGiftList, PopupWindowScrawlListener mPopupWindowScrawlListener, int myCoins) {
        this.mGiftList = mGiftList;
        this.mContext = mContext;
        this.mPopupWindowScrawlListener = mPopupWindowScrawlListener;
        this.myCoins = myCoins;

        LogUtils.i("-----","我有钱币：" + myCoins + " 个");
        View view = View.inflate(mContext, R.layout.gift_popup_window, null);
        view.startAnimation(AnimationUtils.loadAnimation(mContext,R.anim.gift_ins));
        LinearLayout ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);
        ll_popup.startAnimation(AnimationUtils.loadAnimation(mContext,R.anim.push_bottom_in_2));

        mScrawlView = (ScrawlView)view.findViewById(R.id.id_ScrawlView);
        mScrawlView.setGiftBean(mGiftList.get(0).get(0));
        mScrawlView.setOnScrawlChangedListener(onScrawlChangedListener);

        clear_view = (ImageView) view.findViewById(R.id.id_clear_view);
        hide_view = (ImageView) view.findViewById(R.id.id_hide_view);
        total_price_view = (TextView) view.findViewById(R.id.id_total_price_view);
        chongzhi_view = (TextView) view.findViewById(R.id.id_chongzhi_view);
        my_coins = (TextView) view.findViewById(R.id.id_my_coins);
        send_view = (TextView) view.findViewById(R.id.id_send_view);

        my_coins.setText(""+myCoins);
        clear_view.setOnClickListener(this);
        hide_view.setOnClickListener(this);
        chongzhi_view.setOnClickListener(this);
        send_view.setOnClickListener(this);

        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        this.screenWidth = outMetrics.widthPixels;
        this.screenHeight = outMetrics.heightPixels;

        mViewPager = (ViewPager) view.findViewById(R.id.imagepager);
        mViewPager.setOnPageChangeListener(this);
        mLayoutCircle = (LinearLayout) view.findViewById(R.id.circlelayout);
        mEmotionLayout = (RelativeLayout) view.findViewById(R.id.emotionlayout);

        for (int i = 0; i < mGiftList.size(); i++) {
            addView(i);
        }
        mEmotionAdapter = new GiftPagerAdapter(mViewList);
        mViewPager.setAdapter(mEmotionAdapter);
        mViewPager.setCurrentItem(0);
        showCircle(mViewList.size());


        setWidth(RelativeLayout.LayoutParams.FILL_PARENT);
        setHeight(RelativeLayout.LayoutParams.FILL_PARENT);
        setBackgroundDrawable(new BitmapDrawable());
        setFocusable(true);
        setOutsideTouchable(true);
        setContentView(view);
        showAtLocation(parent, Gravity.BOTTOM, 0, 0);
        update();
    }


    /**
     * 添加表情滑动控件
     * @param i	添加的位置
     */
    private void addView(final int i){
        View view = LayoutInflater.from(mContext).inflate(R.layout.gifttion_gridview, null);
        GridView gridView = (GridView) view.findViewById(R.id.gift_grid);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                ImageView imageView = (ImageView)view.findViewById(R.id.gifttion);
                if(imageView != null){
                    Drawable drawable = imageView.getDrawable();
                    if(drawable instanceof BitmapDrawable){
                        GiftBean mGiftBean = mGiftList.get(i).get(position);
                        String fileName = mGiftBean.fileName;
                        mGiftList.get(selectI).get(selectJ).sign = 0;
                        mScrawlAdapterList.get(selectI).notifyDataSetChanged();

                        mGiftList.get(i).get(position).sign = 1;
                        selectI = i;
                        selectJ = position;
                        LogUtils.i("-----","点击：" + fileName);
                        mScrawlAdapterList.get(i).notifyDataSetChanged();
//                        mPopupWindowScrawlListener.gift(mGiftBean);
                        mScrawlView.setGiftBean(mGiftBean);
                    }
                }
            }
        });
        GiftAdapter mScrawlAdapter = new GiftAdapter(mContext, mGiftList.get(i), screenWidth);
        gridView.setAdapter(mScrawlAdapter);
        mScrawlAdapterList.add(mScrawlAdapter);
        mViewList.add(view);
    }


    /**
     * 显示表情处于第几页标志
     * @param size
     */
    private void showCircle(int size){
        mLayoutCircle.removeAllViews();

        for( int i = 0; i < size; i++){
            ImageView img = new ImageView(mContext);
            img.setLayoutParams(new LinearLayout.LayoutParams(dip2px(mContext, 5), dip2px(mContext, 5)));
            LinearLayout layout = new LinearLayout(mContext);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            int margin = dip2px(mContext, 5);
            params.setMargins(margin, 0, margin, 0);
            layout.setLayoutParams(params);
            layout.addView(img);
            //img.setLayoutParams()
            if ( mPageIndxe == i){
                img.setImageResource(R.mipmap.guide_page_dot_checked);
            } else{
                img.setImageResource(R.mipmap.guide_page_dot_unchecked);
            }
            mLayoutCircle.addView(layout);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mPageIndxe = position;
        showCircle(mViewList.size());
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public int dip2px(Context context, int dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.id_clear_view:
//                mPopupWindowScrawlListener.clear();
                mScrawlView.clear();
                break;
            case R.id.id_hide_view:
                mDismiss();
                break;
            case R.id.id_chongzhi_view:
                LogUtils.i("-----","充值...");
                mPopupWindowScrawlListener.chongZhiYouBi();
                mDismiss();
                break;
            case R.id.id_send_view:
//                mPopupWindowScrawlListener.send();
                List<GiftBean> list = new ArrayList<>();
                List<GiftBean> _list = mScrawlView.getListBitmap();
                for (GiftBean bean : _list) {
                    list.add(bean);
                }
                mScrawlView.clear();
//                if(list != null && list.size() > 0){
//                    mScrawlView.setListBitmap(list);
//                }

                mPopupWindowScrawlListener.send(list,mScrawlView.getScreenWidth(), mScrawlView.getScreenHeight(),totalPrice);
                mDismiss();
                break;
        }
    }

    private void mDismiss(){
        mGiftList.get(selectI).get(selectJ).sign = 0;
        mGiftList.get(0).get(0).sign = 1;
        dismiss();
    }

    private ScrawlView.OnScrawlChangedListener onScrawlChangedListener = new ScrawlView.OnScrawlChangedListener(){
        @Override
        public void onChanged(List<GiftBean> listGift) {
            int arg1 = 0;
            if(listGift != null && listGift.size() > 0){
                for (GiftBean bean:listGift) {
                    arg1 += bean.price;
                }
            }
            Message msg = new Message();
            msg.what = 0;
            msg.arg1 = arg1;
            mHandler.dispatchMessage(msg);
        }
    };

    public interface PopupWindowScrawlListener{
        //礼物集合 。 画板宽高  --- 用以计算    在不同屏幕尺寸上播放动画
        public void send(List<GiftBean> list, int screenWidth, int screenHeight, int totalPrice);
        //充值钱币
        public void chongZhiYouBi();
    }

}
