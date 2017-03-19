package com.tencent.mm.plugin.luckymoney.c;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.tencent.mm.model.al;
import com.tencent.mm.u.k;

import org.json.JSONObject;

/**
 * Created by HuangChengHua on 17/3/18.
 */


public class ae extends k {

    @Deprecated
    public String hrK;

    private static String talker;

    @Deprecated
    public ae(int p1, String p2, String p3, int p4, String p5) {

    }

    public void fire(JSONObject object) {
        try {
            int receiveStatus = object.optInt("receiveStatus");
            if (receiveStatus != 0) return;
            int hbStatus = object.optInt("hbStatus");
            if (hbStatus == 4 || hbStatus == 5) {
                return;
            }
            String talker = consume();
            if (TextUtils.isEmpty(talker))
                return;
            String timingIdentifier = object.optString("timingIdentifier");
            String sendHeadImg = object.optString("sendHeadImg");
            String sendUserName = object.optString("sendUserName");
            String nativeurl = hrK;
            Uri nativeUrl = Uri.parse(nativeurl);
            String ver = "v1.0";
            int msgType = Integer.parseInt(nativeUrl.getQueryParameter("msgtype"));
            int channelId = Integer.parseInt(nativeUrl.getQueryParameter("channelid"));
            String sendId = nativeUrl.getQueryParameter("sendid");
            ab request = new ab(msgType, channelId, sendId, nativeurl, sendHeadImg, sendUserName, talker, ver, timingIdentifier);
            al.vM().a(request, 500);
        } catch (Exception e) {
            Log.e("wx-lucky", String.valueOf(e.getMessage()));
        }

    }

    public static void product(String t) {
        talker = t;
    }

    public static String consume() {
        String t = talker;
        talker = null;
        return t;
    }


    public void a(int p1, String p2, JSONObject p3) {
        fire(p3); // 第一句插入fire方法
    }

}
