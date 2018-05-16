package com.mzw.giftscrawlto;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by think on 2018/4/15.
 */

public class GiftData {

    private static int count = 10;
    private static String giftFileName = "";
    private static Bitmap mBitmap = null;
    /**
     * 获取礼物列表 数据  图片不可删除，用于记录收到和送出流水，
     */
    public static List<GiftBean> getGiftData() {
        List<GiftBean> list = new ArrayList<GiftBean>();
        list.add(new GiftBean(0,"g000",1,1,"棒棒糖"));
        list.add(new GiftBean(1,"g001",2,0,"奶瓶儿"));
        list.add(new GiftBean(2,"g002",3,0,"啤酒"));
        list.add(new GiftBean(3,"g003",4,0,"咖啡"));
        list.add(new GiftBean(4,"g004",5,0,"兰州拉面"));
        list.add(new GiftBean(5,"g005",6,0,"生日蛋糕"));
        list.add(new GiftBean(6,"g006",7,0,"大礼包"));
        list.add(new GiftBean(7,"g007",8,0,"新年爆竹"));
        list.add(new GiftBean(8,"g008",9,0,"发发发"));
        list.add(new GiftBean(9,"g009",10,0,"猪头"));
        list.add(new GiftBean(10,"g010",11,0,"麦克风"));
        list.add(new GiftBean(11,"g011",12,0,"炸弹"));
        list.add(new GiftBean(12,"g012",13,0,"鄙视你"));
        list.add(new GiftBean(13,"g013",14,0,"玫瑰花"));
        list.add(new GiftBean(14,"g014",15,0,"万花筒"));
        return list;
    }


    //获取礼物列表数据 分组
    public static List<List<GiftBean>> getGiftList() {
        List<GiftBean> list = getGiftData();

        List<List<GiftBean>> totalList = new ArrayList<List<GiftBean>>();

        int page = list.size() % count ==0 ? list.size() / count : list.size() / count + 1;
        for (int i = 0; i < page; i++) {
            int startIndex = i * count;
            List<GiftBean> singleList = new ArrayList<GiftBean>();
            if(singleList != null){
                singleList.clear();
            }
            int endIndex = 0;
            if(i < page - 1){
                endIndex = startIndex + count;
            }else if(i == page - 1){
                endIndex = list.size();
            }
            singleList.addAll(list.subList(startIndex, endIndex));
            totalList.add(singleList);
        }
        return totalList;
    }

    public static JSONArray toJson(List<GiftBean> list,int screenWidth, int screenHeight,String uPic){

        try{
            if(list != null && list.size() > 0){
                //将礼物发出去
                JSONArray jsonArray = new JSONArray();
                JSONObject json = null;
                if(screenWidth > 0 || screenHeight > 0){//添加 送礼物人信息
                    json = new JSONObject();
                    json.put("id",-1);//
                    json.put("fileName",uPic);// 送礼物人头像 地址
                    json.put("currentX",screenWidth);//送礼物人 手机屏幕宽高
                    json.put("currentY",screenHeight);
                    json.put("price",0);
                    jsonArray.put(json);
                }
                for(int i = 0; i < list.size(); i++){// 将礼物 转为 json
                    GiftBean bean = list.get(i);
                    json = new JSONObject();
                    json.put("id",bean.id);
                    json.put("fileName",bean.fileName);//礼物文件名
                    json.put("currentX",bean.currentX);//礼物 在屏幕上的坐标
                    json.put("currentY",bean.currentY);
                    json.put("price",bean.price);// 礼物单价
                    jsonArray.put(json);
                }
                return jsonArray;
//                handler.sendEmptyMessage(QianMiGoCommon.B);
//                mScrawlPlayView.setListBitmap(list);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static List<GiftBean> toList(String str){
        Map<String, GiftBean> map = getGiftMap();//
        try{
            List<GiftBean> list = new ArrayList<GiftBean>();
            if(!TextUtils.isEmpty(str) && str.startsWith("[")){
                JSONArray jsonArray =  new JSONArray(str);
                if(jsonArray != null && jsonArray.length()>0){
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject json = jsonArray.getJSONObject(i);
                        int id = 0;
                        String name = "礼物";
                        String fileName = "";
                        int price = 0;
                        float currentX = 0;
                        float currentY = 0;
                        if(json != null){
                            if(!json.isNull("id")){
                                id = json.getInt("id");
                            }
                            if(!json.isNull("fileName")){
                                fileName = json.getString("fileName");
                            }
                            if(!json.isNull("price")){
                                price = json.getInt("price");
                            }
                            if(!json.isNull("currentX")){
                                currentX = json.getInt("currentX");
                            }
                            if(!json.isNull("currentY")){
                                currentY = json.getInt("currentY");
                            }
                        }
                        if(!TextUtils.isEmpty(fileName)){// 礼物信息传递时  为了节省网络开销，没有传递 礼物中文名称，在此获取
                            GiftBean bean = map.get(fileName);
                            if(bean != null){
                                name = bean.name;
                            }
                        }
                        // 封装
                        GiftBean _GiftBean = new GiftBean(id,fileName,price,currentX,currentY,name);
                        list.add(_GiftBean);
                    }
                }
            }
            if(list != null){
                return list;
            }
        }catch (Exception e){e.printStackTrace();}

        return null;
    }

    // 当数据字典使用，key为fileName
    public static Map<String, GiftBean> getGiftMap() {
        List<GiftBean> list = getGiftData();
        Map<String, GiftBean> map = new HashMap<String, GiftBean>();
        for (GiftBean bean:list) {
            map.put(bean.fileName,bean);
        }
        return map;
    }

    //避免 反复加载相同图片
    public static Bitmap getGiftBitmap(Context mContext, String _giftFileName) {
        if(!TextUtils.isEmpty(_giftFileName)){
            if(mBitmap != null && _giftFileName.equals(giftFileName)){
                return mBitmap;
            }else{
                try{
                    Field field = R.mipmap.class.getDeclaredField(_giftFileName);
                    int resId = field.getInt(null);
                    mBitmap = BitmapFactory.decodeResource(mContext.getResources(),resId);
                    giftFileName = _giftFileName;
                    return mBitmap;
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
