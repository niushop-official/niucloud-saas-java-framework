package com.niu.core.enums.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum CommonActiveEnum {

    IMPULSE_BUY("impulse_buy", "顺手买", "顺", "#FF7700"),
    GIFTCARD("giftcard", "礼品卡", "礼", "#F00000"),
    DISCOUNT("discount", "限时折扣", "折", "#FFA322"),
    EXCHANGE("exchange", "积分商城", "积", "#00C441"),
    MANJIANSONG("manjiansong", "满减送", "满减", "#249DE9"),
    NEWCOMER_DISCOUNT("newcomer_discount", "新人专享", "新", "#BB27FF"),
    SECKILL("seckill", "秒杀", "秒", "#F606CA"),
    PINTUAN("pintuan", "拼团", "拼", "#FF1C77");

    private final String code;
    private final String shortName;
    private final String name;
    private final String bgColor;

    CommonActiveEnum(String code, String name, String shortName, String bgColor) {
        this.code = code;
        this.name = name;
        this.shortName = shortName;
        this.bgColor = bgColor;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getShortName() {
        return shortName;
    }

    public String getBgColor() {
        return bgColor;
    }

    public static Map<String, Map<String, Object>> getActiveShort() {
        Map<String, Map<String, Object>> data = new HashMap<>();
        for (CommonActiveEnum active : CommonActiveEnum.values()) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", active.getShortName());
            item.put("active_name", active.getName());
            item.put("bg_color", active.getBgColor());
            data.put(active.getCode(), item);
        }
        return data;
    }

    public static Map<String, Object> getActiveShort(String code) {
        for (CommonActiveEnum active : CommonActiveEnum.values()) {
            if (active.getCode().equals(code)) {
                Map<String, Object> item = new HashMap<>();
                item.put("name", active.getShortName());
                item.put("active_name", active.getName());
                item.put("bg_color", active.getBgColor());
                return item;
            }
        }
        return null;
    }

    // 活动背景特效
    public static final Map<String, Map<String, Object>> ACTIVE_BG_EFFECT = Map.of(
            "normal", Map.of(
                    "title", "无特效",
                    "child_list", List.of()
            ),
            "promotion", Map.of(
                    "title", "促销",
                    "child_list", Map.of(
                            "red_packet", "红包",
                            "gift_box", "礼盒",
                            "big_wing", "大元宝"
                    )
            ),
            "festival", Map.of(
                    "title", "节庆",
                    "child_list", Map.of(
                            "blessing", "花瓣",
                            "firecrackers", "爆竹",
                            "i_love_you", "我爱你",
                            "kongmin_light", "孔明灯"
                    )
            )
    );

    // 背景音乐模板
    public static final Map<String, Map<String, Object>> ACTIVE_BG_MUSIC = Map.of(
            "popular", Map.of(
                    "title", "流行",
                    "child_list", Map.of(
                            "quiet_guitar_soundtrack", Map.of(
                                    "title", "安静吉他配乐",
                                    "url", "static/resource/audio/quiet_guitar_soundtrack.mp3"
                            ),
                            "city_of_hope", Map.of(
                                    "title", "City Of Hope",
                                    "url", "static/resource/audio/city_of_hope.mp3"
                            ),
                            "intro", Map.of(
                                    "title", "Intro",
                                    "url", "static/resource/audio/intro.mp3"
                            ),
                            "dreams_exploring_world", Map.of(
                                    "title", "探索世界的梦想",
                                    "url", "static/resource/audio/dreams_exploring_world.mp3"
                            )
                    )
            ),
            "holiday", Map.of(
                    "title", "节日",
                    "child_list", Map.of(
                            "spring_festival_overture", Map.of(
                                    "title", "过年春节序曲",
                                    "url", "static/resource/audio/spring_festival_overture.mp3"
                            )
                    )
            ),
            "classical", Map.of(
                    "title", "古典",
                    "child_list", Map.of(
                            "elegant_chinese_style", Map.of(
                                    "title", "古典高雅中国风",
                                    "url", "static/resource/audio/elegant_chinese_style.mp3"
                            ),
                            "city_hope_soundtrack", Map.of(
                                    "title", "希望之城配乐",
                                    "url", "static/resource/audio/city_hope_soundtrack.mp3"
                            ),
                            "ink_painting_chinese_style", Map.of(
                                    "title", "墨水画中国风",
                                    "url", "static/resource/audio/ink_painting_chinese_style.mp3"
                            )
                    )
            ),
            "epic", Map.of(
                    "title", "史诗",
                    "child_list", Map.of(
                            "destiny_honor", Map.of(
                                    "title", "Destiny & Honor",
                                    "url", "static/resource/audio/destiny_honor.mp3"
                            ),
                            "electric_romeo", Map.of(
                                    "title", "Electric Romeo",
                                    "url", "static/resource/audio/electric_romeo.mp3"
                            )
                    )
            ),
            "piano", Map.of(
                    "title", "钢琴",
                    "child_list", Map.of(
                            "tapeworm_compilation", Map.of(
                                    "title", "Tapeworm Compilation I",
                                    "url", "static/resource/audio/tapeworm_compilation.mp3"
                            )
                    )
            ),
            "happy", Map.of(
                    "title", "欢快",
                    "child_list", Map.of(
                            "laugh_smile_no_vox", Map.of(
                                    "title", "A Laugh And A Smile No Vox",
                                    "url", "static/resource/audio/laugh_smile_no_vox.mp3"
                            ),
                            "easy_run_soundtrack", Map.of(
                                    "title", "轻松奔跑配乐",
                                    "url", "static/resource/audio/easy_run_soundtrack.mp3"
                            ),
                            "theme_happy_no_1", Map.of(
                                    "title", "Theme-Happy No.1",
                                    "url", "static/resource/audio/theme_happy_no_1.mp3"
                            ),
                            "sunny_jim", Map.of(
                                    "title", "Sunny Jim",
                                    "url", "static/resource/audio/sunny_jim.mp3"
                            ),
                            "energetic_warm_soundtrack", Map.of(
                                    "title", "活力温暖配乐",
                                    "url", "static/resource/audio/energetic_warm_soundtrack.mp3"
                            ),
                            "light_soothing_soundtrack", Map.of(
                                    "title", "轻快舒缓配乐",
                                    "url", "static/resource/audio/light_soothing_soundtrack.mp3"
                            ),
                            "inspirational_bright_music", Map.of(
                                    "title", "励志明亮配乐",
                                    "url", "static/resource/audio/inspirational_bright_music.mp3"
                            ),
                            "sharp_rhythm_whistling", Map.of(
                                    "title", "节奏鲜明口哨",
                                    "url", "static/resource/audio/sharp_rhythm_whistling.mp3"
                            ),
                            "cheerful_korean_music", Map.of(
                                    "title", "欢快韩风配乐",
                                    "url", "static/resource/audio/cheerful_korean_music.mp3"
                            )
                    )
            )
    );
}
