<?php
return [
    'pages' => <<<EOT
        // PAGE_BEGIN
		// *********************************** 商城 ***********************************
		{
			"path": "addon/{{addon_name}}/pages/index",
			"style": {
				"navigationBarTitleText": "%{{addon_name}}.pages.index%",
				"enablePullDownRefresh": true
			}
		},
		{
			"path": "addon/{{addon_name}}/pages/coupon/list",
			"style": {
				"navigationBarTitleText": "%{{addon_name}}.pages.coupon.list%"
			}
		},
		{
			"path": "addon/{{addon_name}}/pages/coupon/detail",
			"style": {
				"navigationBarTitleText": "%{{addon_name}}.pages.coupon.detail%"
			}
		},
		{
			"path": "addon/{{addon_name}}/pages/evaluate/list",
			"style": {
				"navigationBarTitleText": "%{{addon_name}}.pages.evaluate.list%"
			}
		},
		{
			"path": "addon/{{addon_name}}/pages/evaluate/order_evaluate",
			"style": {
				"navigationBarTitleText": "%{{addon_name}}.pages.evaluate.order_evaluate%"
			}
		},
		{
			"path": "addon/{{addon_name}}/pages/evaluate/order_evaluate_view",
			"style": {
				"navigationBarTitleText": "%{{addon_name}}.pages.evaluate.order_evaluate_view%"
			}
		},
		{
			"path": "addon/{{addon_name}}/pages/member/my_coupon",
			"style": {
				"navigationBarTitleText": "%{{addon_name}}.pages.member.my_coupon%"
			},
            "needLogin": true
		},
		{
			"path": "addon/{{addon_name}}/pages/member/index",
			"style": {
				"navigationBarTitleText": "%{{addon_name}}.pages.member.index%",
				"enablePullDownRefresh": true
			}
		},
		{
			"path": "addon/{{addon_name}}/pages/goods/list",
			"style": {
				"navigationBarTitleText": "%{{addon_name}}.pages.goods.list%"
			}
		},
		{
			"path": "addon/{{addon_name}}/pages/goods/detail",
			"style": {
				"navigationBarTitleText": "%{{addon_name}}.pages.goods.detail%"
			}
		},
		{
			"path": "addon/{{addon_name}}/pages/goods/cart",
			"style": {
				"navigationBarTitleText": "%{{addon_name}}.pages.goods.cart%"
			}
		},
		{
			"path": "addon/{{addon_name}}/pages/goods/category",
			"style": {
				"navigationBarTitleText": "%{{addon_name}}.pages.goods.category%"
			}
		},
		{
			"path": "addon/{{addon_name}}/pages/order/detail",
			"style": {
				"navigationBarTitleText": "%{{addon_name}}.pages.order.detail%"
			},
            "needLogin": true
		},
		{
			"path": "addon/{{addon_name}}/pages/order/list",
			"style": {
				"navigationBarTitleText": "%{{addon_name}}.pages.order.list%"
			},
            "needLogin": true
		},
        {
        	"path": "addon/{{addon_name}}/pages/order/payment",
        	"style": {
        		"navigationBarTitleText": "%{{addon_name}}.pages.order.payment%"
        	},
            "needLogin": true
        },
        {
        	"path": "addon/{{addon_name}}/pages/refund/apply",
        	"style": {
        		"navigationBarTitleText": "%{{addon_name}}.pages.refund.apply%"
        	},
            "needLogin": true
        },
        {
        	"path": "addon/{{addon_name}}/pages/refund/edit_apply",
        	"style": {
        		"navigationBarTitleText": "%{{addon_name}}.pages.refund.edit_apply%"
        	},
            "needLogin": true
        },
        {
        	"path": "addon/{{addon_name}}/pages/refund/list",
        	"style": {
        		"navigationBarTitleText": "%{{addon_name}}.pages.refund.list%"
        	},
            "needLogin": true
        },
        {
        	"path": "addon/{{addon_name}}/pages/refund/detail",
        	"style": {
        		"navigationBarTitleText": "%{{addon_name}}.pages.refund.detail%"
        	},
            "needLogin": true
        },
        {
        	"path": "addon/{{addon_name}}/pages/refund/log",
        	"style": {
        		"navigationBarTitleText": "%{{addon_name}}.pages.refund.log%"
        	},
            "needLogin": true
        }
        // PAGE_END
EOT
];
