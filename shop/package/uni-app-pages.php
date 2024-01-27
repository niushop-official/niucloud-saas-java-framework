<?php
return [
    'pages' => <<<EOT
        // PAGE_BEGIN
		// *********************************** 商城 ***********************************
		{
			"path": "addon/shop/pages/index",
			"style": {
				"navigationBarTitleText": "%shop.pages.index%",
				"enablePullDownRefresh": true
			}
		},
		{
			"path": "addon/shop/pages/coupon/list",
			"style": {
				"navigationBarTitleText": "%shop.pages.coupon.list%"
			}
		},
		{
			"path": "addon/shop/pages/coupon/detail",
			"style": {
				"navigationBarTitleText": "%shop.pages.coupon.detail%"
			}
		},
		{
			"path": "addon/shop/pages/evaluate/list",
			"style": {
				"navigationBarTitleText": "%shop.pages.evaluate.list%"
			}
		},
		{
			"path": "addon/shop/pages/evaluate/order_evaluate",
			"style": {
				"navigationBarTitleText": "%shop.pages.evaluate.order_evaluate%"
			}
		},
		{
			"path": "addon/shop/pages/evaluate/order_evaluate_view",
			"style": {
				"navigationBarTitleText": "%shop.pages.evaluate.order_evaluate_view%"
			}
		},
		{
			"path": "addon/shop/pages/member/my_coupon",
			"style": {
				"navigationBarTitleText": "%shop.pages.member.my_coupon%"
			},
            "needLogin": true
		},
		{
			"path": "addon/shop/pages/member/index",
			"style": {
				"navigationBarTitleText": "%shop.pages.member.index%",
				"enablePullDownRefresh": true
			}
		},
		{
			"path": "addon/shop/pages/goods/list",
			"style": {
				"navigationBarTitleText": "%shop.pages.goods.list%"
			}
		},
		{
			"path": "addon/shop/pages/goods/detail",
			"style": {
				"navigationBarTitleText": "%shop.pages.goods.detail%"
			}
		},
		{
			"path": "addon/shop/pages/goods/cart",
			"style": {
				"navigationBarTitleText": "%shop.pages.goods.cart%"
			}
		},
		{
			"path": "addon/shop/pages/goods/category",
			"style": {
				"navigationBarTitleText": "%shop.pages.goods.category%"
			}
		},
		{
			"path": "addon/shop/pages/order/detail",
			"style": {
				"navigationBarTitleText": "%shop.pages.order.detail%"
			},
            "needLogin": true
		},
		{
			"path": "addon/shop/pages/order/list",
			"style": {
				"navigationBarTitleText": "%shop.pages.order.list%"
			},
            "needLogin": true
		},
        {
        	"path": "addon/shop/pages/order/payment",
        	"style": {
        		"navigationBarTitleText": "%shop.pages.order.payment%"
        	},
            "needLogin": true
        },
        {
        	"path": "addon/shop/pages/refund/apply",
        	"style": {
        		"navigationBarTitleText": "%shop.pages.refund.apply%"
        	},
            "needLogin": true
        },
        {
        	"path": "addon/shop/pages/refund/edit_apply",
        	"style": {
        		"navigationBarTitleText": "%shop.pages.refund.edit_apply%"
        	},
            "needLogin": true
        },
        {
        	"path": "addon/shop/pages/refund/list",
        	"style": {
        		"navigationBarTitleText": "%shop.pages.refund.list%"
        	},
            "needLogin": true
        },
        {
        	"path": "addon/shop/pages/refund/detail",
        	"style": {
        		"navigationBarTitleText": "%shop.pages.refund.detail%"
        	},
            "needLogin": true
        },
        {
        	"path": "addon/shop/pages/refund/log",
        	"style": {
        		"navigationBarTitleText": "%shop.pages.refund.log%"
        	},
            "needLogin": true
        }
        // PAGE_END
EOT
];