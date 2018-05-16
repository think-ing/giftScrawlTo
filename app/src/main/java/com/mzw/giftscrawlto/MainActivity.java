package com.mzw.giftscrawlto;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

//入口类
public class MainActivity extends Activity {

    private Context mContext;
    private ImageView gift_btn;
    private LinearLayout mScrawlPlayView_layout;
    private ScrawlPlayView mScrawlPlayView;
    private LinearLayout gift_broadcast_layout;// 礼物layout

    private RoundRectImageView broadcast_user_pic;//送礼物人图片
    private TextView broadcast_user_name;//送礼物人名
    private TextView broadcast_gift_info;//礼物名称
    private ImageView broadcast_gift_icon;//礼物图片
    private TextView broadcast_gift_num;//礼物数量
    private Animation myAnimationIn;
    private Animation scaleAnimation;

    private SyncThread syncThread;

    private List<List<GiftBean>> mGiftList = new ArrayList<List<GiftBean>>();

    private int myCoins = 100;//测试使用  用户余额

    private boolean isScaleing = false;// 放大执行中
    private int giftNum = 1;
    private String giftFileName_temp = "";// 记录，当改变时 重置数量为1

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case LogUtils.B:
                    mScrawlPlayView.clear();
                    //打开 礼物播放
                    mScrawlPlayView_layout.setVisibility(View.VISIBLE);
                    break;
                case LogUtils.C:
                    //关闭播报
                    giftNum = 1;
                    gift_broadcast_layout.setVisibility(View.GONE);
                    //关闭 礼物播放
                    mScrawlPlayView_layout.setVisibility(View.GONE);
                    break;
                case LogUtils.D:
                    // 打开播报
//                    GiftBean mGiftBean = (GiftBean)msg.obj;
                    Bundle mBundle = msg.getData();
                    String id = mBundle.getString("id");
                    String name = mBundle.getString("name");
                    String pic = mBundle.getString("pic");

                    String giftFileName = mBundle.getString("giftFileName");
                    String giftName = mBundle.getString("giftName");

                    if(TextUtils.isEmpty(giftFileName) || !giftFileName.equals(giftFileName_temp)){
                        giftFileName_temp = giftFileName;
                        giftNum = 1;
                    }

//                    bitmapUtils.display(broadcast_user_pic,pic);// 加载网络图片
//                    broadcast_user_pic.setImageResource(R.mipmap.ic_launcher);//发送者头像
                    Bitmap mBitmap1 = GiftData.getGiftBitmap(mContext,pic);// 通过文件名反射获取图片
                    broadcast_user_pic.setImageBitmap(mBitmap1);//发送者头像

                    broadcast_user_name.setText(""+name);
                    broadcast_gift_info.setText("送给主播 "+giftName);

                    Bitmap mBitmap = GiftData.getGiftBitmap(mContext,giftFileName);
//                    try{
//                        Field field = R.drawable.class.getDeclaredField(giftFileName);
//                        int resId = field.getInt(null);
//                        Bitmap mBitmap = BitmapFactory.decodeResource(getResources(),resId);
                    broadcast_gift_icon.setImageBitmap(mBitmap);
//                    }catch (Exception e){
//                        e.printStackTrace();
//                    }

                    if(gift_broadcast_layout.getVisibility() == View.GONE){
                        gift_broadcast_layout.setVisibility(View.VISIBLE);
                        gift_broadcast_layout.startAnimation(myAnimationIn);
                    }else{
                        giftNum++;
                    }
                    if(!isScaleing){
                        broadcast_gift_num.setText("x"+giftNum);
                        broadcast_gift_num.startAnimation(scaleAnimation);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;


        gift_btn = (ImageView) findViewById(R.id.id_gift_btn);
        mScrawlPlayView_layout = (LinearLayout) findViewById(R.id.id_ScrawlPlayView_layout);
        mScrawlPlayView = (ScrawlPlayView) findViewById(R.id.id_ScrawlPlayView);
        gift_broadcast_layout = (LinearLayout)findViewById(R.id.id_gift_broadcast_layout);
        broadcast_user_pic = (RoundRectImageView) findViewById(R.id.id_broadcast_user_pic);
        broadcast_gift_icon = (ImageView) findViewById(R.id.id_broadcast_gift_icon);
        broadcast_user_name = (TextView) findViewById(R.id.id_broadcast_user_name);
        broadcast_gift_info = (TextView) findViewById(R.id.id_broadcast_gift_info);
        broadcast_gift_num = (TextView) findViewById(R.id.id_broadcast_gift_num);

        syncThread = new SyncThread(mScrawlPlayView,handler);//刷礼物  播放 线程
        myAnimationIn = AnimationUtils.loadAnimation(mContext, R.anim.slide_left_in);
        scaleAnimation = AnimationUtils.loadAnimation(mContext, R.anim.gift_scale);

        init();
    }

    public void init(){
        mScrawlPlayView_layout.setVisibility(View.GONE);
        gift_broadcast_layout.setVisibility(View.GONE);

        mGiftList = GiftData.getGiftList();
        gift_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
//                ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                gift_btn.setVisibility(View.INVISIBLE);
                new GiftPopupWindow(mContext,gift_btn,mGiftList,mPopupWindowScrawlListener,myCoins).setOnDismissListener(new PopupWindow.OnDismissListener(){
                    @Override
                    public void onDismiss() {
                        gift_btn.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
    }
    private GiftPopupWindow.PopupWindowScrawlListener mPopupWindowScrawlListener = new GiftPopupWindow.PopupWindowScrawlListener(){
        @Override
        public void send(List<GiftBean> list,int screenWidth, int screenHeight,int totalPrice) {
            //发送礼物，将头像url带过去
            if(myCoins >= totalPrice){
                JSONArray jsonArray = GiftData.toJson(list,screenWidth, screenHeight,"ic_launcher");
                if(jsonArray != null){
                    String msgBody = jsonArray.toString();
                    syncThread.setToid("qwe001");//_to  name
                    syncThread.setTo_name("qwe002");//_from  name
                    syncThread.setMsgBody(msgBody);

                    Thread thread = new Thread(syncThread, ""+System.currentTimeMillis());
                    thread.start();

                    //添加并发
//                    Thread thread1 = new Thread(syncThread, ""+System.currentTimeMillis());
//                    thread1.start();
                }
            }else{
                Toast.makeText(mContext,"钱币不足，请先充值！！",Toast.LENGTH_SHORT).show();
            }
        }
        @Override
        public void chongZhiYouBi(){
            Toast.makeText(mContext,"正在研发中...",Toast.LENGTH_SHORT).show();
        }
    };
}
