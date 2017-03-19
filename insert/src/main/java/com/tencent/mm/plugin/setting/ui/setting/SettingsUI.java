package com.tencent.mm.plugin.setting.ui.setting;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.tencent.mm.ui.base.preference.Preference;

/**
 * Created by HuangChengHua on 17/3/19.
 */

public class SettingsUI extends Activity {

    private com.tencent.mm.ui.base.preference.f ioz;

    protected final void On() {
        addLucky();
    }

    private void addLucky() {
        Preference lucky = new Preference(this);
        lucky.setKey("wx_lucky");
        lucky.setTitle("抢红包");
        ioz.a(lucky, 1);
    }

    private void gotoLuckySetting() {
        try {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setClassName("com.tencent.mm.luckymoney", "com.tencent.mm.luckymoney.MainActivity");
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "请安装抢红包插件!", Toast.LENGTH_LONG).show();
        }
    }

    public final boolean a(com.tencent.mm.ui.base.preference.f paramf, Preference paramPreference) {
        String key = paramPreference.ifq;
        if ("wx_lucky".equals(key)) {
            gotoLuckySetting();
            return true;
        }
        return true;
    }
}
