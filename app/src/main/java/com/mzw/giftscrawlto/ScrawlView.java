package com.mzw.giftscrawlto;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * 涂鸦view
 * 刷礼物
 * Created by think on 2018/4/20.
 */

public class ScrawlView extends View {

    public ScrawlView(Context context) {
        super(context);
        init();
    }

    public ScrawlView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ScrawlView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private GiftBean mGiftBean;// 画笔
    //涂鸦图片
    private Bitmap mBitmap;
    //涂鸦最大尺寸
    private int screenWidth, screenHeight;
    //涂鸦图片尺寸
    private int mBitmapW, mBitmapH;
    //临时记录画笔轨迹
    private float currentX = 0, currentY = 0;
    //涂鸦图片集合
    private List<GiftBean> listGift;

    public List<GiftBean> getListBitmap() {
        return listGift;
    }
    private Handler handler=null;
    private String currentGiftName = "g000";// 自动播放时画笔

    private OnScrawlChangedListener onScrawlChangedListener;


    public void setOnScrawlChangedListener(OnScrawlChangedListener onScrawlChangedListener) {
        this.onScrawlChangedListener = onScrawlChangedListener;
    }

    // 改变画笔
    public void setGiftBean(GiftBean mGiftBean) {
        this.mGiftBean = mGiftBean;
        currentGiftName = mGiftBean.fileName;
        handler.post(mRunnable);
    }
    // 构建Runnable对象，在runnable中更新界面
    private Runnable mRunnable = new  Runnable(){
        @Override
        public void run() {
            //更新界面
            mBitmap = GiftData.getGiftBitmap(getContext(),currentGiftName);
            if(mBitmap != null){
                mBitmapW = mBitmap.getWidth();
                mBitmapH = mBitmap.getHeight();
                invalidate();
            }

//            try {
//                Field field = R.drawable.class.getDeclaredField(currentGiftName);
//                int resId = field.getInt(null);
//                mBitmap = BitmapFactory.decodeResource(getResources(),resId);
//                mBitmapW = mBitmap.getWidth();
//                mBitmapH = mBitmap.getHeight();
//                invalidate();
//            }catch(Exception e){
//                e.printStackTrace();
//            }
        }
    };

    private void init() {
        listGift = new ArrayList<GiftBean>();
        //创建属于主线程的handler
        handler=new Handler();

        mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.g000);
        mGiftBean = GiftData.getGiftList().get(0).get(0);
        mGiftBean.mBitmap = mBitmap;
        this.mBitmapW = mBitmap.getWidth();
        this.mBitmapH = mBitmap.getHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //画
        if(listGift != null && listGift.size() > 0){
            for (GiftBean bean : listGift) {
                //  除以2 是图片中心
                canvas.drawBitmap(bean.mBitmap, bean.currentX - mBitmapW / 2, bean.currentY - mBitmapH / 2, null);
            }
        } else{
            canvas.drawColor(Color.TRANSPARENT);//重置画板
        }
        onScrawlChangedListener.onChanged(listGift);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                currentX = 0;
                currentY = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                break;
        }

        //不可超越画板界限
        if(x > 0 && x < screenWidth && y > 0 && y < screenHeight){
            if(currentX <= 0 && currentY <= 0){
                currentX = x;
                currentY = y;
                listGift.add(new GiftBean(mGiftBean.id,mGiftBean.fileName,mGiftBean.price,currentX,currentY,mBitmap));
            }else{
                //判断
                if(Math.abs(currentX - x) >= Math.abs(currentY - y) ){//横
                    if(Math.abs(currentX - x) - mBitmapW / 3 * 2 > 0){// 滑动距离大于图片宽 记录xy
                        currentX = x;
                        currentY = y;
                        listGift.add(new GiftBean(mGiftBean.id,mGiftBean.fileName,mGiftBean.price,currentX,currentY,mBitmap));
                    }
                }else{//竖
                    if(Math.abs(currentY - y) - mBitmapH / 3 * 2 > 0){// 滑动距离大于图片高 记录xy
                        currentX = x;
                        currentY = y;
                        listGift.add(new GiftBean(mGiftBean.id,mGiftBean.fileName,mGiftBean.price,currentX,currentY,mBitmap));
                    }
                }
            }
            invalidate();
        }
        return true;
    }
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        //确定子View在父View中的位置 后回调 onLayout。所以在此获取画板尺寸
        this.screenWidth = getWidth();
        this.screenHeight = getHeight();
    }
    public void clear(){
        if(listGift != null){
            listGift.clear();
        }
        invalidate();
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    // 涂鸦监听
    public interface OnScrawlChangedListener{
        //涂鸦变化  传入所有涂鸦list   用以计算涂鸦价格
        void onChanged(List<GiftBean> listGift);
    }
}
