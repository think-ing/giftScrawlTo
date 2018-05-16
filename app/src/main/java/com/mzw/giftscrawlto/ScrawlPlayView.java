package com.mzw.giftscrawlto;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * 刷礼物后  自动播放 涂鸦
 * Created by think on 2018/4/20.
 */

public class ScrawlPlayView extends View {

    public ScrawlPlayView(Context context) {
        super(context);
        init();
    }

    public ScrawlPlayView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ScrawlPlayView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    //涂鸦最大尺寸
    private int screenWidth, screenHeight;
    //画笔
    private GiftBean mGiftBean;
    //涂鸦图片
    private Bitmap mBitmap;
    //涂鸦图片尺寸
    private int mBitmapW, mBitmapH;
    //涂鸦图片集合
    private List<GiftBean> listGift;

    private Handler handler=null;
//    private String currentGiftName = "g000";// 自动播放时画笔

    private OnScrawlPlayStateListener onScrawlPlayStateListener;


    public void setOnScrawlPlayStateListener(OnScrawlPlayStateListener onScrawlPlayStateListener) {
        this.onScrawlPlayStateListener = onScrawlPlayStateListener;
    }

    // 一个一个画。调用者控制 并保证 一组画完在画下一组
    public void setGiftBean(GiftBean mGiftBean,float _screenWidth, float _screenHeight) {
        this.mGiftBean = mGiftBean;
        float w = _screenWidth / screenWidth;
        float h = _screenHeight / screenHeight;

        if(mGiftBean .id >= 0){
            mGiftBean.currentX = mGiftBean.currentX / w;
            mGiftBean.currentY = mGiftBean.currentY / h;
            //不可以在子线程中更新UI
            handler.post(mRunnable);
        }
    }

    //一组一组画   有可能一组没画完  又来一组  所以不合适，使用 一个一个画
    public void setListBitmap(final List<GiftBean> _listBitmap, final float _screenWidth, final float _screenHeight) {
        clear();
        new Thread(){
            public void run(){
                //计算  原始画板和目标画板比
                float w = _screenWidth / screenWidth;
                float h = _screenHeight / screenHeight;
                try{
                    //自动播放涂鸦，
                    for(int i = 0; i < _listBitmap.size(); i++){
                        if(i == 0){
                            onScrawlPlayStateListener.onStart();
                        }
                        mGiftBean = _listBitmap.get(i);
                        if(mGiftBean .id >= 0){
                            mGiftBean.currentX = mGiftBean.currentX / w;
                            mGiftBean.currentY = mGiftBean.currentY / h;
                            //不可以在子线程中更新UI
                            handler.post(mRunnable);
                            // 睡眠80毫秒
                            Thread.sleep(50);
                        }
                        if(i == _listBitmap.size() - 1){
                            Thread.sleep(1500);
                            onScrawlPlayStateListener.onEnd();
                        }
                    }
                }catch(Exception e){
                    Log.i("-----","---13---" + e.getMessage());
                }
            }
        }.start();
    }
    // 构建Runnable对象，在runnable中更新界面
    private Runnable mRunnable = new  Runnable(){
        @Override
        public void run() {
            //更新界面
            if(mGiftBean != null){
                mBitmap = GiftData.getGiftBitmap(getContext(),mGiftBean.fileName);
                if(mBitmap != null){
                    mBitmapW = mBitmap.getWidth();
                    mBitmapH = mBitmap.getHeight();
                    mGiftBean.mBitmap = mBitmap;
                    listGift.add(mGiftBean);
                    invalidate();
                }
            }
//            try {
//                if(mGiftBean != null){
//                    Field field = R.drawable.class.getDeclaredField(mGiftBean.fileName);
//                    int resId = field.getInt(null);
//                    mBitmap = BitmapFactory.decodeResource(getResources(),resId);
//                    mBitmapW = mBitmap.getWidth();
//                    mBitmapH = mBitmap.getHeight();
//                    mGiftBean.mBitmap = mBitmap;
//                    listGift.add(mGiftBean);
//
//                    invalidate();
//                }
//            }catch(Exception e){
//                Log.i("-----","" + e.getMessage());
//                e.printStackTrace();
//            }
        }

    };


    private void init() {
        listGift = new ArrayList<GiftBean>();
        //创建属于主线程的handler
        handler=new Handler();

//        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.g000);
//        mGiftBean = new GiftBean(0,currentGiftName,0);
//        mGiftBean.mBitmap = mBitmap;
//        this.mBitmapW = mBitmap.getWidth();
//        this.mBitmapH = mBitmap.getHeight();
    }
    @Override
    protected void onDraw(Canvas canvas) {
        //画
        if(listGift != null && listGift.size() > 0){
            for (GiftBean bean : listGift) {
                //  除以2 是图片中心
//                if(bean.mBitmap != null){
                    canvas.drawBitmap(bean.mBitmap, bean.currentX - mBitmapW / 2, bean.currentY - mBitmapH / 2, null);
//                }else if(mBitmap != null){
//                    canvas.drawBitmap(mBitmap, bean.currentX - mBitmapW / 2, bean.currentY - mBitmapH / 2, null);
//                }
            }
        } else{
            canvas.drawColor(Color.TRANSPARENT);//重置画板
        }
    }

    public void clear(){
        if(listGift != null){
            listGift.clear();
        }
        invalidate();
    }
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        //确定子View在父View中的位置 后回调 onLayout。所以在此获取画板尺寸
        this.screenWidth = getWidth();
        this.screenHeight = getHeight();
    }
    public int getScreenWidth() {
        return screenWidth;
    }
    public int getScreenHeight() {
        return screenHeight;
    }
    // 播放状态回掉
    public interface OnScrawlPlayStateListener{
        //播放开始
        public void onStart();
        //播放完毕
        public void onEnd();
    }
}
