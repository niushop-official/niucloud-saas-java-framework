package com.niu.core.listener.sys;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import com.freewayso.image.combiner.ImageCombiner;
import com.freewayso.image.combiner.element.ImageElement;
import com.freewayso.image.combiner.element.RectangleElement;
import com.freewayso.image.combiner.element.TextElement;
import com.freewayso.image.combiner.enums.BaseLine;
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

@EventListen("core")
@Component
public class PosterDrawListener extends AbstractListener {

    public void handleEvent(PosterDrawEvent event) {
        JSONObject poster = event.getPoster();
        ImageCombiner combiner = event.getCombiner();
        Map<String, Object> posterData = event.getPosterData();
        String type = poster.getStr("type", "").toLowerCase();

        switch (type) {
            case "image":
                String imgPath = poster.getStr("value", "");
                if (imgPath.isEmpty() && !poster.getStr("relate").isEmpty()) {
                    imgPath = (String) posterData.getOrDefault(poster.getStr("relate"), "");
                }
                BufferedImage img = ImageUtils.readImage(imgPath, "static/resource/images/diy/crack_figure.png");

                ImageElement imgElement = new ImageElement(img, poster.getInt("x"), poster.getInt("y"), poster.getInt("width"), poster.getInt("height"), ZoomMode.Width);
                if (poster.getStr("shape", "").equals("circle")) {
                    imgElement.setRoundCorner(poster.getInt("width"));
                }
                combiner.addElement(imgElement);
                break;
            case "text":
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

                TextElement text = new TextElement(testStr, fontStyle, poster.getInt("fontSize"), poster.getInt("x", 0), poster.getInt("y"));
                // 设置居中
                if (poster.getStr("x", "").equals("center")) text.setCenter(true);
                // 设置字体颜色
                text.setColor(Color.decode(poster.getStr("fontColor")));
                // 设置换行
                text.setAutoBreakLine(poster.getInt("width"), poster.getInt("height") / poster.getInt("fontSize"), LineAlign.Left);
                if (customFont != null) text.setFont(customFont);
                combiner.addElement(text);
                break;
            case "qrcode":
                String qrcodePath = (String) posterData.getOrDefault(poster.getStr("relate"), "static/resource/images/diy/qrcode.png");
                BufferedImage qrcode = ImageUtils.readImage(qrcodePath, "static/resource/images/diy/qrcode.png");

                ImageElement qrcodeElement = new ImageElement(qrcode, poster.getInt("x"), poster.getInt("y"), poster.getInt("width"), poster.getInt("height"), ZoomMode.Width);
                combiner.addElement(qrcodeElement);
                break;
            case "draw":
                RectangleElement rectangle = new RectangleElement(poster.getInt("x"), poster.getInt("y"), poster.getInt("width"), poster.getInt("height"));
                rectangle.setColor(Color.decode(poster.getStr("bgColor")));
                combiner.addElement(rectangle);
                break;
        }
    }
}
