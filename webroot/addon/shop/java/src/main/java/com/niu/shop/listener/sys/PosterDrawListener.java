package com.niu.shop.listener.sys;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import com.freewayso.image.combiner.ImageCombiner;
import com.freewayso.image.combiner.element.ImageElement;
import com.freewayso.image.combiner.element.RectangleElement;
import com.freewayso.image.combiner.element.TextElement;
import com.freewayso.image.combiner.enums.LineAlign;
import com.freewayso.image.combiner.enums.ZoomMode;
import com.niu.core.common.annotation.EventListen;
import com.niu.core.common.component.context.WebAppEnvs;
import com.niu.core.common.component.context.listener.AbstractListener;
import com.niu.core.common.utils.image.ImageUtils;
import com.niu.core.event.sys.PosterDrawEvent;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Map;

@EventListen("shop")
@Component
public class PosterDrawListener extends AbstractListener {

    public void handleEvent(PosterDrawEvent event) {
        JSONObject poster = event.getPoster();
        ImageCombiner combiner = event.getCombiner();
        Map<String, Object> posterData = event.getPosterData();
        String type = poster.getStr("type", "").toLowerCase();

        switch (type) {
            case "strikethrough":
                String testStr = poster.getStr("value");
                if (testStr.isEmpty() && !poster.getStr("relate").isEmpty()) {
                    testStr = (String) posterData.getOrDefault(poster.getStr("relate"), "");
                }

                int fontStyle = poster.getBool("weight") ? Font.BOLD : Font.PLAIN;

                Font customFont = null;
                String defaultFont = "static/font/SourceHanSansCN-Regular.ttf";
                File fontFile = new File(WebAppEnvs.get().webRootDownResource, ObjectUtil.defaultIfEmpty(poster.getStr("fontFamily", defaultFont), defaultFont));

                try {
                    customFont = Font.createFont(Font.TRUETYPE_FONT, fontFile);
                    customFont = customFont.deriveFont(fontStyle, poster.getInt("fontSize"));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                TextElement text = new TextElement(testStr, fontStyle, poster.getInt("fontSize"), poster.getInt("x"), poster.getInt("y"));
                // 设置字体颜色
                text.setColor(Color.decode(poster.getStr("fontColor")));
                // 设置换行
                text.setAutoBreakLine(poster.getInt("width"), poster.getInt("height") / poster.getInt("fontSize"), LineAlign.Left);
                text.setStrikeThrough(true);
                if (customFont != null) text.setFont(customFont);
                combiner.addElement(text);
                break;
        }
    }
}
