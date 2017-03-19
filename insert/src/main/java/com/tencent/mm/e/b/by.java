package com.tencent.mm.e.b;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.tencent.mm.model.al;
import com.tencent.mm.plugin.luckymoney.c.ae;
import com.tencent.mm.sdk.platformtools.aa;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;

/**
 * Created by HuangChengHua on 17/3/18.
 */

public class by {

    @Deprecated
    public int field_type;
    @Deprecated
    public int field_status;
    @Deprecated
    public String field_talker;
    @Deprecated
    public String field_content;
    @Deprecated
    public int field_isSend;

    private void fire() {
        try {
            if (field_type != 0x1a000031 && field_type != 0x1c000031) return;
            if (field_status == 4 || TextUtils.isEmpty(field_content) || !field_talker.endsWith("@chatroom"))
                return;
            ContentResolver cr = aa.getContext().getContentResolver();
            if (Settings.System.getInt(cr, "wx_auto", 0) == 0) return;
            if (field_isSend != 0 && Settings.System.getInt(cr, "wx_private", 0) == 0) return;
            String title = null, nativeUrl = null, fromUserName = null;
            String msg = field_content.substring(field_content.indexOf("<msg>"));
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(msg));
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    String name = xpp.getName();
                    if ("nativeurl".equals(name)) {
                        xpp.nextToken();
                        nativeUrl = xpp.getText();
                    } else if ("sendertitle".equals(name)) {
                        xpp.nextToken();
                        title = xpp.getText();
                    } else if ("fromusername".equals(name)) {
                        xpp.nextToken();
                        fromUserName = xpp.getText();
                    }
                }
                eventType = xpp.next();
            }
            if (!TextUtils.isEmpty(title)) {
                String sensitiveWords = Settings.System.getString(cr, "wx_sensitive_words");
                if (!TextUtils.isEmpty(sensitiveWords)) {
                    String[] words = sensitiveWords.split(",");
                    for (String word : words) {
                        if (title.contains(word)) {
                            return;
                        }
                    }
                }
            }
            if (!TextUtils.isEmpty(fromUserName)) {
                String userNames = Settings.System.getString(cr, "wx_sensitive_users");
                if (!TextUtils.isEmpty(userNames)) {
                    String[] names = userNames.split(",");
                    for (String name : names) {
                        if (fromUserName.equals(name)) {
                            return;
                        }
                    }
                }
            }
            ae.product(field_talker);
            Uri uri = Uri.parse(nativeUrl);
            int channelId = Integer.parseInt(uri.getQueryParameter("channelid"));
            String sendId = uri.getQueryParameter("sendid");
            ae request = new ae(channelId, sendId, nativeUrl, 0, "v1.0");
            al.vM().a(request, 0);
        } catch (Exception e) {
            Log.e("wx-lucky", String.valueOf(e.getMessage()));
        }
    }


    public ContentValues pH() {
        fire(); // return 上一句插入fire方法
        return null;
    }

}
