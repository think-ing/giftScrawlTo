package com.mzw.giftscrawlto;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.util.List;

/**
 * 刷礼物 播放  一个一个画。调用者控制 并保证 一组画完在画下一组
 * Created by think on 2018/4/21.
 */

public class SyncThread implements Runnable {
    private ScrawlPlayView mScrawlPlayView;
    private Handler handler;

    private String toid;
    private String to_pic;
    private String to_name;
    private String msgBody;

    public void setToid(String toid) {
        this.toid = toid;
    }

    public void setTo_name(String to_name) {
        this.to_name = to_name;
    }

    public void setMsgBody(String msgBody) {
        this.msgBody = msgBody;
    }

    public SyncThread(ScrawlPlayView mScrawlPlayView, Handler handler) {
        this.mScrawlPlayView = mScrawlPlayView;
        this.handler = handler;
    }

    public  void run() {
        synchronized(this) { // 同步的
            try{
//                String msgBody = Thread.currentThread().getName();
                LogUtils.i("-----","msgBody : " + msgBody);
                LogUtils.i("-----","from : " + to_name);
                List<GiftBean> list = GiftData.toList(msgBody);
                if(list != null && list.size() > 0){
                    float screenWidth = mScrawlPlayView.getScreenWidth(), screenHeight = mScrawlPlayView.getScreenHeight();
                    for (GiftBean bean:list) {
                        if(bean.id == -1){
                            to_pic = bean.fileName;
                            screenWidth = bean.currentX;
                            screenHeight = bean.currentY;
                            break;
                        }
                    }
                    handler.sendEmptyMessage(LogUtils.B);
                    for(int i = 0; i < list.size(); i++){
                        GiftBean mGiftBean = list.get(i);
                        if(mGiftBean.id >= 0){
                            Thread.sleep(100);
                            Bundle mBundle = new Bundle();
                            mBundle.putString("id",toid);
                            mBundle.putString("name",to_name);
                            mBundle.putString("pic",to_pic);

                            mBundle.putString("giftName",mGiftBean.name);
                            mBundle.putString("giftFileName",mGiftBean.fileName);

                            Message msg = new Message();
//                            msg.obj = mGiftBean;
                            msg.what = LogUtils.D;
                            msg.setData(mBundle);
                            handler.sendMessage(msg);
                            mScrawlPlayView.setGiftBean(mGiftBean,screenWidth,screenHeight);
                        }
                        if(i == list.size() - 1){// 播放完 等待1.5秒 关闭播放
                            Thread.sleep(1500);
                            handler.sendEmptyMessage(LogUtils.C);
                        }
                    }
                }
                Thread.sleep(1000);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
