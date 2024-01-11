<?php

return [
    'DIY_SHOP_INDEX' => [
        'shop_index' => [ // 页面标识
            "title" => "商城首页", // 页面名称
            'cover' => 'addon/shop/diy/template/shop_index_cover.png', // 页面封面图
            'preview' => '', // 页面预览图
            'desc' => '', // 页面描述
            'mode' => 'diy', // 页面模式：diy：自定义，fixed：固定
            // 页面数据源
            "data" => [
                "global" => [
                    "title" => "商城首页",
                    "pageBgColor" => "rgba(255, 255, 255, 1)",
                    "bgUrl" => "addon/shop/diy/index/bg.png",
                    "imgWidth" => 750,
                    "imgHeight" => 1485,
                    "bottomTabBarSwitch" => true,
                    "template" => [
                        'textColor' => "#303133",
                        "pageBgColor" => "",
                        "componentBgColor" => "",
                        "topRounded" => 0,
                        "bottomRounded" => 0,
                        "elementBgColor" => "",
                        "topElementRounded" => 0,
                        "bottomElementRounded" => 0,
                        "margin" => [
                            "top" => 0,
                            "bottom" => 0,
                            "both" => 12
                        ]
                    ],
                    'topStatusBar' => [
                        "bgColor" => "#ffffff",
                        "isTransparent" => false,
                        "isShow" => true,
                        "style" => "style-1",
                        "textColor" => "#333333",
                        "textAlign" => "center"
                    ],
                    'popWindow' => [
                        'imgUrl' => "",
                        'imgWidth' => '',
                        'imgHeight' => '',
                        'count' => -1,
                        'show' => 0,
                        'link' => [
                            'name' => ""
                        ],
                    ]
                ],
                "value" => [
                    [
                        "path" => "edit-shop-search",
                        "uses" => 1,
                        "id" => "245qvzv4w8gw",
                        "componentName" => "ShopSearch",
                        "componentTitle" => "搜索",
                        "ignore" => [],
                        "searchStyle" => "style-1",
                        "searchLink" => [
                            "name" => ""
                        ],
                        "iconType" => "img",
                        "imageUrl" => "",
                        "textColor" => "#303133",
                        "pageBgColor" => "",
                        "componentBgColor" => "",
                        "topRounded" => 0,
                        "bottomRounded" => 0,
                        "elementBgColor" => "",
                        "topElementRounded" => 0,
                        "bottomElementRounded" => 0,
                        "margin" => [
                            "top" => 20,
                            "bottom" => 15,
                            "both" => 12
                        ]
                    ],
                    [
                        "path" => "edit-image-ads",
                        "uses" => 0,
                        "id" => "6jdfnu4e36g0",
                        "componentName" => "ImageAds",
                        "componentTitle" => "图片广告",
                        "ignore" => [],
                        "imageHeight" => 160,
                        "list" => [
                            [
                                "link" => [
                                    "name" => ""
                                ],
                                "imageUrl" => "addon/shop/diy/index/banner.png",
                                "imgWidth" => 702,
                                "imgHeight" => 320,
                                "id" => "7ao3t56ug4o0",
                                "width" => 375,
                                "height" => 170.94
                            ]
                        ],
                        "textColor" => "#303133",
                        "pageBgColor" => "",
                        "componentBgColor" => "",
                        "topRounded" => 0,
                        "bottomRounded" => 0,
                        "elementBgColor" => "",
                        "topElementRounded" => 0,
                        "bottomElementRounded" => 0,
                        "margin" => [
                            "top" => 0,
                            "bottom" => 10,
                            "both" => 12
                        ]
                    ],
                    [
                        "path" => "edit-notice",
                        "uses" => 0,
                        "id" => "1xpv3z7vvtds",
                        "componentName" => "Notice",
                        "componentTitle" => "公告",
                        "ignore" => [],
                        "list" => [
                            "link" => [
                                "name" => ""
                            ],
                            "text" => "最新公告：欢迎来到小店参观！"
                        ],
                        "iconType" => "system",
                        "systemIcon" => "style_01",
                        "showType" => "popup",
                        "imageUrl" => "",
                        "textColor" => "#303133",
                        "pageBgColor" => "",
                        "componentBgColor" => "rgba(255, 255, 255, 1)",
                        "topRounded" => 9,
                        "bottomRounded" => 9,
                        "elementBgColor" => "",
                        "topElementRounded" => 0,
                        "bottomElementRounded" => 0,
                        "margin" => [
                            "top" => 0,
                            "bottom" => 10,
                            "both" => 12
                        ],
                        "fontSize" => 12
                    ],
                    [
                        "path" => "edit-graphic-nav",
                        "uses" => 0,
                        "id" => "5tnh9iiuxv40",
                        "componentName" => "GraphicNav",
                        "componentTitle" => "图文导航",
                        "ignore" => [],
                        "layout" => "horizontal",
                        "navTitle" => "",
                        "subNavTitle" => "",
                        "subNavTitleLink" => [
                            "name" => ""
                        ],
                        "subNavColor" => "#999999",
                        "mode" => "graphic",
                        "showStyle" => "fixed",
                        "rowCount" => 5,
                        "pageCount" => 2,
                        "carousel" => [
                            "type" => "circle",
                            "color" => "#FFFFFF"
                        ],
                        "imageSize" => 26,
                        "aroundRadius" => 0,
                        "font" => [
                            "size" => 14,
                            "weight" => "normal",
                            "color" => "#303133"
                        ],
                        "list" => [
                            [
                                "title" => "首页",
                                "link" => [
                                    "parent" => "SYSTEM_LINK",
                                    "title" => "首页",
                                    "url" => "/app/pages/index/index",
                                    "name" => "INDEX"
                                ],
                                "imageUrl" => "addon/shop/diy/index/nav_notice.png",
                                "label" => [
                                    "control" => false,
                                    "text" => "热门",
                                    "textColor" => "#FFFFFF",
                                    "bgColorStart" => "#F83287",
                                    "bgColorEnd" => "#FE3423"
                                ],
                                "id" => "5b46sybkhkg0",
                                "imgWidth" => 50,
                                "imgHeight" => 50
                            ],
                            [
                                "title" => "商品",
                                "link" => [
                                    "parent" => "SHOP_LINK",
                                    "title" => "商品列表",
                                    "url" => "/addon/shop/pages/goods/list",
                                    "name" => "SHOP_GOODS_LIST"
                                ],
                                "imageUrl" => "addon/shop/diy/index/nav_supermarket.png",
                                "label" => [
                                    "control" => false,
                                    "text" => "热门",
                                    "textColor" => "#FFFFFF",
                                    "bgColorStart" => "#F83287",
                                    "bgColorEnd" => "#FE3423"
                                ],
                                "id" => "4dha169dzqe0",
                                "imgWidth" => 50,
                                "imgHeight" => 50
                            ],
                            [
                                "title" => "分类",
                                "link" => [
                                    "parent" => "SHOP_LINK",
                                    "title" => "商品分类",
                                    "url" => "/addon/shop/pages/goods/category",
                                    "name" => "SHOP_GOODS_CATEGORY"
                                ],
                                "imageUrl" => "addon/shop/diy/index/nav_article.png",
                                "label" => [
                                    "control" => false,
                                    "text" => "热门",
                                    "textColor" => "#FFFFFF",
                                    "bgColorStart" => "#F83287",
                                    "bgColorEnd" => "#FE3423"
                                ],
                                "id" => "7eh2hqz0w900",
                                "imgWidth" => 50,
                                "imgHeight" => 50
                            ],
                            [
                                "id" => "7fu7atbr9bk0",
                                "title" => "领券",
                                "imageUrl" => "addon/shop/diy/index/nav_coupon.png",
                                "imgWidth" => 50,
                                "imgHeight" => 50,
                                "link" => [
                                    "parent" => "SHOP_LINK",
                                    "title" => "优惠券列表",
                                    "url" => "/addon/shop/pages/coupon/list",
                                    "name" => "SHOP_COUPON_LIST"
                                ],
                                "label" => [
                                    "control" => false,
                                    "text" => "热门",
                                    "textColor" => "#FFFFFF",
                                    "bgColorStart" => "#F83287",
                                    "bgColorEnd" => "#FE3423"
                                ]
                            ],
                            [
                                "title" => "拼团",
                                "link" => [
                                    "name" => ""
                                ],
                                "imageUrl" => "addon/shop/diy/index/nav_pintuan.png",
                                "label" => [
                                    "control" => false,
                                    "text" => "热门",
                                    "textColor" => "#FFFFFF",
                                    "bgColorStart" => "#F83287",
                                    "bgColorEnd" => "#FE3423"
                                ],
                                "id" => "5q9ogqz2vjc0",
                                "imgWidth" => 50,
                                "imgHeight" => 50
                            ],
                            [
                                "id" => "2cc20cawafr4",
                                "title" => "秒杀",
                                "imageUrl" => "addon/shop/diy/index/nav_seckill.png",
                                "imgWidth" => 50,
                                "imgHeight" => 50,
                                "link" => [
                                    "name" => ""
                                ],
                                "label" => [
                                    "control" => false,
                                    "text" => "热门",
                                    "textColor" => "#FFFFFF",
                                    "bgColorStart" => "#F83287",
                                    "bgColorEnd" => "#FE3423"
                                ]
                            ],
                            [
                                "id" => "532sx2po4do0",
                                "title" => "签到",
                                "imageUrl" => "addon/shop/diy/index/nav_signin.png",
                                "imgWidth" => 50,
                                "imgHeight" => 50,
                                "link" => [
                                    "name" => ""
                                ],
                                "label" => [
                                    "control" => false,
                                    "text" => "热门",
                                    "textColor" => "#FFFFFF",
                                    "bgColorStart" => "#F83287",
                                    "bgColorEnd" => "#FE3423"
                                ]
                            ],
                            [
                                "id" => "13itkrdjqteo",
                                "title" => "邀请好友",
                                "imageUrl" => "addon/shop/diy/index/nav_invite_friends.png",
                                "imgWidth" => 50,
                                "imgHeight" => 50,
                                "link" => [
                                    "name" => ""
                                ],
                                "label" => [
                                    "control" => false,
                                    "text" => "热门",
                                    "textColor" => "#FFFFFF",
                                    "bgColorStart" => "#F83287",
                                    "bgColorEnd" => "#FE3423"
                                ]
                            ],
                            [
                                "id" => "6sisp3zd0w80",
                                "title" => "公告",
                                "imageUrl" => "addon/shop/diy/index/nav_sys_message.png",
                                "imgWidth" => 50,
                                "imgHeight" => 50,
                                "link" => [
                                    "name" => ""
                                ],
                                "label" => [
                                    "control" => false,
                                    "text" => "热门",
                                    "textColor" => "#FFFFFF",
                                    "bgColorStart" => "#F83287",
                                    "bgColorEnd" => "#FE3423"
                                ]
                            ],
                            [
                                "id" => "6qoc0ssgvhc0",
                                "title" => "积分",
                                "imageUrl" => "addon/shop/diy/index/nav_point.png",
                                "imgWidth" => 50,
                                "imgHeight" => 50,
                                "link" => [
                                    "name" => ""
                                ],
                                "label" => [
                                    "control" => false,
                                    "text" => "热门",
                                    "textColor" => "#FFFFFF",
                                    "bgColorStart" => "#F83287",
                                    "bgColorEnd" => "#FE3423"
                                ]
                            ]
                        ],
                        "textColor" => "#303133",
                        "pageBgColor" => "",
                        "componentBgColor" => "rgba(255, 255, 255, 0.87)",
                        "topRounded" => 9,
                        "bottomRounded" => 9,
                        "elementBgColor" => "",
                        "topElementRounded" => 0,
                        "bottomElementRounded" => 0,
                        "margin" => [
                            "top" => 0,
                            "bottom" => 12,
                            "both" => 12
                        ]
                    ],
                    [
                        "path" => "edit-hot-area",
                        "uses" => 0,
                        "id" => "53fen2lf6l80",
                        "componentName" => "HotArea",
                        "componentTitle" => "热区",
                        "ignore" => [],
                        "imageUrl" => "addon/shop/diy/index/hot_area.png",
                        "imgWidth" => 698,
                        "imgHeight" => 466,
                        "heatMapData" => [],
                        "textColor" => "#303133",
                        "pageBgColor" => "",
                        "componentBgColor" => "",
                        "topRounded" => 0,
                        "bottomRounded" => 0,
                        "elementBgColor" => "",
                        "topElementRounded" => 0,
                        "bottomElementRounded" => 0,
                        "margin" => [
                            "top" => 0,
                            "bottom" => 12,
                            "both" => 12
                        ]
                    ],
                    [
                        "path" => "edit-text",
                        "uses" => 0,
                        "id" => "5lojzvbvg6o0",
                        "componentName" => "Text",
                        "componentTitle" => "标题",
                        "ignore" => [],
                        "style" => "style-1",
                        "styleName" => "风格1",
                        "text" => "商品推荐",
                        "link" => [
                            "name" => ""
                        ],
                        "textColor" => "#303133",
                        "fontSize" => 16,
                        "fontWeight" => "normal",
                        "textAlign" => "center",
                        "subTitle" => [
                            "text" => "副标题",
                            "color" => "#999999",
                            "fontSize" => 14,
                            "control" => false,
                            "fontWeight" => "normal"
                        ],
                        "more" => [
                            "text" => "查看更多",
                            "control" => false,
                            "isShow" => true,
                            "link" => [
                                "name" => ""
                            ],
                            "color" => "#999999"
                        ],
                        "pageBgColor" => "",
                        "componentBgColor" => "",
                        "topRounded" => 0,
                        "bottomRounded" => 0,
                        "elementBgColor" => "",
                        "topElementRounded" => 0,
                        "bottomElementRounded" => 0,
                        "margin" => [
                            "top" => 0,
                            "bottom" => 12,
                            "both" => 0
                        ]
                    ],
                    [
                        "path" => "edit-goods-list",
                        "uses" => 0,
                        "id" => "50pj7dmvbvo0",
                        "componentName" => "GoodsList",
                        "componentTitle" => "商品列表",
                        "ignore" => [],
                        "style" => "style1",
                        "source" => "all",
                        "num" => 10,
                        "goods_category" => "",
                        "goods_ids" => [],
                        "textColor" => "#303133",
                        "pageBgColor" => "",
                        "componentBgColor" => "",
                        "topRounded" => 0,
                        "bottomRounded" => 0,
                        "elementBgColor" => "",
                        "topElementRounded" => 0,
                        "bottomElementRounded" => 0,
                        "margin" => [
                            "top" => 0,
                            "bottom" => 0,
                            "both" => 12
                        ]
                    ]
                ]
            ]
        ]
    ],
    'DIY_SHOP_MEMBER_INDEX' => [
        'shop_member_index' => [
            "title" => "商城个人中心", // 页面名称
            'cover' => 'addon/shop/diy/template/member_index_cover.png', // 页面封面图
            'preview' => '', // 页面预览图
            'desc' => '商城个人中心', // 页面描述
            'mode' => 'diy', // 页面模式：diy：自定义，fixed：固定
            'data' => [
                "global" => [
                    "title" => "商城个人中心",
                    "pageBgColor" => "rgba(246, 247, 252, 1)",
                    "bgUrl" => "",
                    "imgWidth" => "",
                    "imgHeight" => "",
                    "bottomTabBarSwitch" => true,
                    "template" => [
                        "textColor" => "#303133",
                        "pageBgColor" => "",
                        "componentBgColor" => "",
                        "topRounded" => 0,
                        "bottomRounded" => 0,
                        "elementBgColor" => "",
                        "topElementRounded" => 0,
                        "bottomElementRounded" => 0,
                        "margin" => [
                            "top" => 0,
                            "bottom" => 0,
                            "both" => 12
                        ]
                    ],
                    "topStatusBar" => [
                        "bgColor" => "#ffffff",
                        "isTransparent" => false,
                        "isShow" => true,
                        "style" => "style-1",
                        "textColor" => "#333333",
                        "textAlign" => "center"
                    ],
                    "popWindow" => [
                        "imgUrl" => "",
                        "imgWidth" => "",
                        "imgHeight" => "",
                        "count" => -1,
                        "show" => 0,
                        "link" => [
                            "name" => ""
                        ]
                    ]
                ],
                "value" => [
                    [
                        "path" => "edit-member-info",
                        "id" => "40w5ay4dep00",
                        "componentName" => "MemberInfo",
                        "componentTitle" => "会员信息",
                        "uses" => 1,
                        "ignore" => [],
                        "pageBgColor" => "",
                        "componentBgColor" => "",
                        "topRounded" => 0,
                        "bottomRounded" => 0,
                        "elementBgColor" => "",
                        "topElementRounded" => 0,
                        "bottomElementRounded" => 0,
                        "margin" => [
                            "top" => 0,
                            "bottom" => 0,
                            "both" => 0
                        ],
                        "textColor" => "#303133",
                        "style" => "style-1",
                        "styleName" => "风格1",
                        "bgUrl" => "",
                        "bgColorStart" => "",
                        "bgColorEnd" => ""
                    ],
                    [
                        "path" => "edit-graphic-nav",
                        "uses" => 0,
                        "id" => "42bjn10l0ak0",
                        "componentName" => "GraphicNav",
                        "componentTitle" => "图文导航",
                        "ignore" => [],
                        "list" => [
                            [
                                "link" => [
                                    "parent" => "DIY_LINK",
                                    "title" => "待付款",
                                    "url" => "/addon/shop/pages/order/list?status=1",
                                    "name" => "DIY_LINK"
                                ],
                                "imageUrl" => "addon/shop/diy/member/nav_wait_pay_order.png",
                                "imgWidth" => 40,
                                "imgHeight" => 40,
                                "id" => "ogkw2mqwvow",
                                "title" => "待付款",
                                "label" => [
                                    "control" => false,
                                    "text" => "热门",
                                    "textColor" => "#FFFFFF",
                                    "bgColorStart" => "#F83287",
                                    "bgColorEnd" => "#FE3423"
                                ]
                            ],
                            [
                                "title" => "待发货",
                                "link" => [
                                    "parent" => "DIY_LINK",
                                    "title" => "待发货",
                                    "url" => "/addon/shop/pages/order/list?status=2",
                                    "name" => "DIY_LINK"
                                ],
                                "imageUrl" => "addon/shop/diy/member/nav_wait_delivery_order.png",
                                "label" => [
                                    "control" => false,
                                    "text" => "热门",
                                    "textColor" => "#FFFFFF",
                                    "bgColorStart" => "#F83287",
                                    "bgColorEnd" => "#FE3423"
                                ],
                                "id" => "37hnqfvx5va0",
                                "imgWidth" => 40,
                                "imgHeight" => 40
                            ],
                            [
                                "title" => "待收货",
                                "link" => [
                                    "parent" => "DIY_LINK",
                                    "title" => "待收货",
                                    "url" => "/addon/shop/pages/order/list?status=3",
                                    "name" => "DIY_LINK"
                                ],
                                "imageUrl" => "addon/shop/diy/member/nav_wait_take_order.png",
                                "label" => [
                                    "control" => false,
                                    "text" => "热门",
                                    "textColor" => "#FFFFFF",
                                    "bgColorStart" => "#F83287",
                                    "bgColorEnd" => "#FE3423"
                                ],
                                "id" => "63n62guoiug0",
                                "imgWidth" => 40,
                                "imgHeight" => 40
                            ],
                            [
                                "title" => "待评价",
                                "link" => [
                                    "name" => ""
                                ],
                                "imageUrl" => "addon/shop/diy/member/nav_wait_evaluate_order.png",
                                "label" => [
                                    "control" => false,
                                    "text" => "热门",
                                    "textColor" => "#FFFFFF",
                                    "bgColorStart" => "#F83287",
                                    "bgColorEnd" => "#FE3423"
                                ],
                                "id" => "4wblhzd14uw0",
                                "imgWidth" => 40,
                                "imgHeight" => 40
                            ],
                            [
                                "id" => "fl2bqfimnuo",
                                "title" => "售后退款",
                                "imageUrl" => "addon/shop/diy/member/nav_refund_order.png",
                                "imgWidth" => 40,
                                "imgHeight" => 40,
                                "link" => [
                                    "parent" => "SHOP_LINK",
                                    "title" => "退款列表",
                                    "url" => "/addon/shop/pages/refund/list",
                                    "name" => "SHOP_REFUND_LIST"
                                ],
                                "label" => [
                                    "control" => false,
                                    "text" => "热门",
                                    "textColor" => "#FFFFFF",
                                    "bgColorStart" => "#F83287",
                                    "bgColorEnd" => "#FE3423"
                                ]
                            ]
                        ],
                        "textColor" => "#303133",
                        "pageBgColor" => "",
                        "componentBgColor" => "rgba(255, 255, 255, 1)",
                        "topRounded" => 9,
                        "bottomRounded" => 9,
                        "elementBgColor" => "",
                        "topElementRounded" => 0,
                        "bottomElementRounded" => 0,
                        "margin" => [
                            "top" => 0,
                            "bottom" => 12,
                            "both" => 16
                        ],
                        "layout" => "horizontal",
                        "navTitle" => "我的订单",
                        "subNavTitle" => "全部",
                        "subNavTitleLink" => [
                            "parent" => "SHOP_LINK",
                            "title" => "订单列表",
                            "url" => "/addon/shop/pages/order/list",
                            "name" => "SHOP_ORDER_LIST"
                        ],
                        "subNavColor" => "#999999",
                        "mode" => "graphic",
                        "showStyle" => "fixed",
                        "rowCount" => 5,
                        "pageCount" => 2,
                        "carousel" => [
                            "type" => "circle",
                            "color" => "#FFFFFF"
                        ],
                        "imageSize" => 20,
                        "aroundRadius" => 0,
                        "font" => [
                            "size" => 14,
                            "weight" => "normal",
                            "color" => "rgba(32, 15, 51, 1)"
                        ]
                    ],
                    [
                        "path" => "edit-graphic-nav",
                        "uses" => 0,
                        "id" => "1swylyl5fvb4",
                        "componentName" => "GraphicNav",
                        "componentTitle" => "图文导航",
                        "ignore" => [],
                        "list" => [
                            [
                                "title" => "签到",
                                "link" => [
                                    "name" => ""
                                ],
                                "imageUrl" => "addon/shop/diy/member/nav_signin.png",
                                "label" => [
                                    "control" => false,
                                    "text" => "热门",
                                    "textColor" => "#FFFFFF",
                                    "bgColorStart" => "#F83287",
                                    "bgColorEnd" => "#FE3423"
                                ],
                                "imgWidth" => 58,
                                "imgHeight" => 58
                            ],
                            [
                                "title" => "个人资料",
                                "link" => [
                                    "parent" => "MEMBER_LINK",
                                    "title" => "个人资料",
                                    "url" => "/app/pages/member/personal",
                                    "name" => "MEMBER_PERSONAL"
                                ],
                                "imageUrl" => "addon/shop/diy/member/nav_info.png",
                                "label" => [
                                    "control" => false,
                                    "text" => "热门",
                                    "textColor" => "#FFFFFF",
                                    "bgColorStart" => "#F83287",
                                    "bgColorEnd" => "#FE3423"
                                ],
                                "imgWidth" => 58,
                                "imgHeight" => 58
                            ],
                            [
                                "title" => "收货地址",
                                "link" => [
                                    "parent" => "MEMBER_LINK",
                                    "title" => "收货地址",
                                    "url" => "/app/pages/member/address",
                                    "name" => "MEMBER_ADDRESS"
                                ],
                                "imageUrl" => "addon/shop/diy/member/nav_address.png",
                                "label" => [
                                    "control" => false,
                                    "text" => "热门",
                                    "textColor" => "#FFFFFF",
                                    "bgColorStart" => "#F83287",
                                    "bgColorEnd" => "#FE3423"
                                ],
                                "imgWidth" => 58,
                                "imgHeight" => 58
                            ],
                            [
                                "title" => "优惠券",
                                "link" => [
                                    "parent" => "SHOP_LINK",
                                    "title" => "我的优惠券",
                                    "url" => "/addon/shop/pages/member/my_coupon",
                                    "name" => "SHOP_MY_COUPON"
                                ],
                                "imageUrl" => "addon/shop/diy/member/nav_coupon.png",
                                "label" => [
                                    "control" => false,
                                    "text" => "热门",
                                    "textColor" => "#FFFFFF",
                                    "bgColorStart" => "#F83287",
                                    "bgColorEnd" => "#FE3423"
                                ],
                                "imgWidth" => 58,
                                "imgHeight" => 58
                            ],
                            [
                                "id" => "su0vf82y200",
                                "title" => "我的拼单",
                                "imageUrl" => "addon/shop/diy/member/nav_my_pintuan.png",
                                "imgWidth" => 58,
                                "imgHeight" => 58,
                                "link" => [
                                    "name" => ""
                                ],
                                "label" => [
                                    "control" => false,
                                    "text" => "热门",
                                    "textColor" => "#FFFFFF",
                                    "bgColorStart" => "#F83287",
                                    "bgColorEnd" => "#FE3423"
                                ]
                            ],
                            [
                                "id" => "2b0att2b3pq8",
                                "title" => "我的礼物",
                                "imageUrl" => "addon/shop/diy/member/nav_gift.png",
                                "imgWidth" => 58,
                                "imgHeight" => 58,
                                "link" => [
                                    "name" => ""
                                ],
                                "label" => [
                                    "control" => false,
                                    "text" => "热门",
                                    "textColor" => "#FFFFFF",
                                    "bgColorStart" => "#F83287",
                                    "bgColorEnd" => "#FE3423"
                                ]
                            ],
                            [
                                "id" => "z8x7ci5wik0",
                                "title" => "我的足迹",
                                "imageUrl" => "addon/shop/diy/member/nav_footmark.png",
                                "imgWidth" => 58,
                                "imgHeight" => 58,
                                "link" => [
                                    "name" => ""
                                ],
                                "label" => [
                                    "control" => false,
                                    "text" => "热门",
                                    "textColor" => "#FFFFFF",
                                    "bgColorStart" => "#F83287",
                                    "bgColorEnd" => "#FE3423"
                                ]
                            ],
                            [
                                "id" => "1sejr0g3h4zk",
                                "title" => "积分兑换",
                                "imageUrl" => "addon/shop/diy/member/nav_point_change.png",
                                "imgWidth" => 58,
                                "imgHeight" => 58,
                                "link" => [
                                    "name" => ""
                                ],
                                "label" => [
                                    "control" => false,
                                    "text" => "热门",
                                    "textColor" => "#FFFFFF",
                                    "bgColorStart" => "#F83287",
                                    "bgColorEnd" => "#FE3423"
                                ]
                            ]
                        ],
                        "textColor" => "#303133",
                        "pageBgColor" => "",
                        "componentBgColor" => "rgba(255, 255, 255, 1)",
                        "topRounded" => 9,
                        "bottomRounded" => 9,
                        "elementBgColor" => "",
                        "topElementRounded" => 0,
                        "bottomElementRounded" => 0,
                        "margin" => [
                            "top" => 0,
                            "bottom" => 0,
                            "both" => 16
                        ],
                        "layout" => "horizontal",
                        "navTitle" => "常用工具",
                        "subNavTitle" => "",
                        "subNavTitleLink" => [
                            "name" => ""
                        ],
                        "subNavColor" => "#999999",
                        "mode" => "graphic",
                        "showStyle" => "fixed",
                        "rowCount" => 4,
                        "pageCount" => 2,
                        "carousel" => [
                            "type" => "circle",
                            "color" => "#FFFFFF"
                        ],
                        "imageSize" => 29,
                        "aroundRadius" => 0,
                        "font" => [
                            "size" => 14,
                            "weight" => "normal",
                            "color" => "#303133"
                        ]
                    ]
                ]
            ]
        ]
    ]
];
