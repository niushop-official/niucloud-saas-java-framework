<template>
    <view :style="themeColor()">
        <view class="bg-[var(--page-bg-color)] min-h-[100vh]" v-if="orderData">
			<view class="pt-[30rpx] sidebar-margin payment-bottom">
                <!-- 配送方式 -->
                <view class="mb-[var(--top-m)] rounded-[var(--rounded-big)] bg-white" v-if="orderData.basic.has_goods_types.includes('real') && delivery_type_list.length">
                    <view class="rounded-tl-[var(--rounded-big)] rounded-tr-[var(--rounded-big)] head-tab flex items-center w-full bg-[#F1F1F1]" v-if="delivery_type_list.length > 1">
                        <view v-for="(item, index) in delivery_type_list" :key="index"  class="head-tab-item flex-1 relative" :class="{'active': index === activeIndex}">
                            <view class="h-[74rpx] relative z-10 text-center leading-[74rpx] text-[28rpx]" @click="switchDeliveryType(item.key, index)">
                                {{ item.name }}
                            </view>
                            <image v-if="index === activeIndex && delivery_type_list.length == 3" class="tab-image absolute bottom-[-2rpx] h-[94rpx] w-[240rpx]" :src="img(`addon/shop/payment/tab_${index}.png`)" mode="aspectFit"></image>
                            <image v-else-if="index === activeIndex && delivery_type_list.length == 2" class="tab-img absolute  bottom-[-2rpx]  h-[95rpx] w-[354rpx]" :src="img(`addon/shop/payment/tabstyle_${index}.png`)" mode="aspectFit"></image>
                        </view>
                    </view>
                    <view class="min-h-[140rpx] flex items-center px-[30rpx]">
                        <!-- 收货地址 -->
                        <view class="w-full" v-if="['express', 'local_delivery'].includes(createData.delivery.delivery_type)" @click="toSelectAddress">
                            <view v-if="!$u.test.isEmpty(orderData.delivery.take_address)" class="pt-[20rpx] pb-[30rpx] flex items-center">
								<image class="w-[60rpx] h-[60rpx] mr-[20rpx] flex-shrink-0" :src="img('addon/shop/payment/position_01.png')" mode="aspectFit"></image>
								<view class="flex flex-col overflow-hidden">
									<text class="text-[26rpx] text-[var(--text-color-light9)] mt-[16rpx] truncate max-w-[536rpx]">{{orderData.delivery.take_address.full_address.split(orderData.delivery.take_address.address)[0]}}</text>
									<text class="font-500 text-[30rpx] mt-[14rpx] text-[#333] truncate max-w-[536rpx]">{{orderData.delivery.take_address.address}}</text>
									<view class="flex items-center text-[26rpx] text-[var(--text-color-light6)] mt-[16rpx]">
										<text class="mr-[16rpx]">{{ orderData.delivery.take_address.name }}</text>
										<text>{{ mobileHide(orderData.delivery.take_address.mobile) }}</text>
									</view>
								</view>
								<text class="ml-auto nc-iconfont nc-icon-youV6xx text-[26rpx] text-[var(--text-color-light9)]"></text>
                            </view>
                            <view v-else class="flex items-center">
                                <image class="w-[26rpx] h-[30rpx] mr-[10rpx]" :src="img('addon/shop/payment/position_02.png')" mode="aspectFit"></image>
                                <text class="text-[28rpx]">添加收货地址</text>
                                <text class="nc-iconfont nc-icon-youV6xx text-[26rpx] text-[var(--text-color-light9)] ml-auto"></text>
                            </view>
                        </view>

                        <!-- 自提点 -->
                        <view class="flex items-center w-full" v-if="createData.delivery.delivery_type == 'store'" @click="openSelectStore()">
                            <view v-if="!$u.test.isEmpty(orderData.delivery.take_store)" class="pt-[40rpx] pb-[30rpx] w-full flex items-center">
								<view class="flex flex-col">
									<view class="text-[30rpx] font-500 text-[#303133] mb-[20rpx]">{{ orderData.delivery.take_store.store_name }}</view>
									<view class="text-[24rpx] text-[var(--text-color-light6)] mb-[20rpx] leading-[1.4] flex">
                                        <text class="flex-shrink-0">门店地址：</text>
                                        <text class="max-w-[490rpx]">{{ orderData.delivery.take_store.full_address }}</text>
                                    </view>
									<view class="text-[24rpx] text-[var(--text-color-light6)] mb-[20rpx]">
                                        <text>联系电话：</text>
                                        <text>{{ orderData.delivery.take_store.store_mobile }}</text>
                                    </view>
									<view class="text-[24rpx] text-[var(--text-color-light6)]">
                                        <text>营业时间：</text>
                                        <text>{{ orderData.delivery.take_store.trade_time }}</text>
                                    </view>
								</view>
								<text class="ml-auto nc-iconfont nc-icon-youV6xx text-[26rpx] text-[var(--text-color-light9)]"></text>
                            </view>
                            <view v-else class="flex items-center w-full">
                                <image class="w-[26rpx] h-[30rpx] mr-[10rpx]" :src="img('addon/shop/payment/position_02.png')" mode="aspectFit"></image>
                                <text class="text-[28rpx]">请选择自提点</text>
                                <text class="ml-auto nc-iconfont nc-icon-youV6xx text-[26rpx] text-[var(--text-color-light9)]"></text>
                            </view>
                        </view>
                    </view>
                </view>
                <view v-if="orderData.basic.has_goods_types.includes('real') && !delivery_type_list.length" class="mb-[var(--top-m)] card-template h-[100rpx] flex items-center">
                    <p class="text-[28rpx] text-[var(--primary-color)]">商家尚未配置配送方式</p>
                </view>

                <view class="mb-[var(--top-m)] card-template p-[0] pb-[var(--pad-top-m)]">
					<view class="pt-[var(--pad-top-m)] pb-[14rpx]">
						<view class="px-[var(--pad-sidebar-m)]" :class="{'mb-[20rpx]': (index+1) != orderData.goods.length}" v-for="(item, index) in orderData.goods" :key="index">
							<view class="flex">
								<u--image radius="var(--goods-rounded-big)" width="180rpx" height="180rpx" :src="img(item.sku_image)" model="aspectFill">
									<template #error>
										<image class="w-[180rpx] h-[180rpx] rounded-[var(--goods-rounded-big)] overflow-hidden" :src="img('static/resource/images/diy/shop_default.jpg')" mode="aspectFill"></image>
									</template>
								</u--image>
								<view class="flex flex-1 w-0 flex-col justify-between ml-[20rpx] py-[6rpx]">
									<view class="line-normal">
										<view class="truncate text-[#303133] text-[28rpx] leading-[32rpx]">
											{{ item.goods.goods_name }}
										</view>
										<view class="mt-[14rpx] flex" v-if="item.sku_name">
											<text class="truncate text-[24rpx] text-[var(--text-color-light9)] leading-[28rpx]">{{ item.sku_name }}</text>
										</view>
									</view>
									<view v-if="item.manjian_info && Object.keys(item.manjian_info).length" class="flex items-center mt-[10rpx] mb-[auto]" @click.stop="manjianOpenFn(item.manjian_info)">
										<view class="bg-[var(--primary-color-light)] text-[var(--primary-color)] rounded-[6rpx] text-[20rpx] flex items-center justify-center w-[88rpx] h-[36rpx] mr-[6rpx]">满减送</view>
										<text class="text-[22rpx] text-[#999]">{{item.manjian_info.manjian_name}}</text>
									</view>
									<view class="mb-auto" :class="{'mt-[6rpx]': !item.sku_name}" v-if="item.not_support_delivery">
										<u-alert type="error" description="该商品不支持当前所选配送方式" class="leading-[30rpx] !inline-block" fontSize="11"></u-alert>
									</view>
									<view class="flex justify-between items-baseline">
										<view class="text-[var(--price-text-color)] flex items-baseline  price-font">
											<text class="text-[24rpx] font-500 mr-[4rpx]">￥</text>
											<text class="text-[40rpx] font-500">{{ parseFloat(item.price).toFixed(2).split('.')[0] }}</text>
											<text class="text-[24rpx] font-500">.{{ parseFloat(item.price).toFixed(2).split('.')[1] }}</text>
										</view>
										<view class="font-400 text-[28rpx] text-[#303133]">
											<text>x</text>
											<text>{{ item.num }}</text>
										</view>
									</view>
								</view>
							</view>
							<view class="flex items-center mt-[8rpx]" :class="{'pb-[40rpx]': (index + 1) != Object.keys(orderData.goods_data).length}" v-if="item.is_newcomer && item.newcomer_price != item.price && item.num>1">
								<image class="h-[24rpx] w-[56rpx]" :src="img('addon/shop/newcomer.png')" mode="heightFix" />
								<view class="text-[24rpx] text-[#FFB000] leading-[34rpx] ml-[8rpx]"
							>第1{{item.goods.unit}}，￥{{parseFloat(item.newcomer_price).toFixed(2)}}/{{item.goods.unit}}；第{{item.num>2?'2~'+item.num:'2'}}{{item.goods.unit}}，￥{{parseFloat(item.price).toFixed(2)}}/{{item.goods.unit}}</view>
							</view>
							<view class="card-template !p-[0]" v-if="item.goods.form_id">
								<diy-form ref="diyFormGoodsRef" :form_id="item.goods.form_id" :relate_id="item.sku_id" :storage_name="'diyFormStorageByGoodsDetail_' + item.sku_id" form_border="none" />
							</view>
						</view>
						<!-- 赠品 -->
						<view v-if="orderData.gift_goods && Object.keys(orderData.gift_goods).length" class="pt-[20rpx] mb-[10rpx] bg-[#f9f9f9] mt-[24rpx] mx-[var(--pad-sidebar-m)] rounded-[30rpx]">
							<view v-for="(item, key, index) in orderData.gift_goods" :key="index" class="flex px-[var(--pad-sidebar-m)] pb-[20rpx]">
								<u--image radius="var(--goods-rounded-big)" width="120rpx" height="120rpx" :src="img(item.sku_image)" model="aspectFill">
									<template #error>
										<image class="w-[120rpx] h-[120rpx] rounded-[var(--goods-rounded-big)] overflow-hidden" :src="img('static/resource/images/diy/shop_default.jpg')" mode="aspectFill"></image>
									</template>
								</u--image>
								<view class="ml-[16rpx] py-[8rpx] flex flex-1 flex-col justify-between">
									<view class="flex items-center">
										<view class="bg-[var(--primary-color-light)] whitespace-nowrap text-[var(--primary-color)] rounded-[6rpx] text-[22rpx] flex items-center justify-center w-[64rpx] h-[34rpx] mr-[6rpx]">赠品</view>
										<view class="text-[26rpx] max-w-[400rpx] truncate leading-[40rpx] text-[#333]">{{ item.goods.goods_name }}</view>
									</view>
									<view class="flex items-center">
										<view v-if="item.sku_name" class="text-[22rpx] text-[var(--text-color-light9)] truncate max-w-[400rpx] leading-[28rpx]">
											{{ item.sku_name }}
										</view>
										<view class="ml-[auto] font-400 text-[26rpx] text-[#303133]">
											<text>x</text>
											<text>{{ item.num }}</text>
										</view>
									</view>
								</view>
							</view>
						</view>
					</view>
                    <!-- 买家留言 -->
                    <view class="bg-white flex items-center leading-[30rpx] px-[var(--pad-sidebar-m)]">
                        <view class="text-[28rpx] w-[150rpx] text-[#303133]">买家留言</view>
                        <view class="flex-1 text-[#303133]">
                            <input type="text" v-model="createData.member_remark" class="text-right text-[#333] text-[28rpx]" maxlength="50" placeholder="请输入留言信息给卖家" placeholder-class="text-[var(--text-color-light9)] text-[28rpx]">
                        </view>
                    </view>
                    <!-- 发票 -->
                    <view v-if="invoiceRef && invoiceRef.invoiceOpen" class="flex items-center text-[#303133] leading-[30rpx] mt-[30rpx] px-[var(--pad-sidebar-m)]" @click="invoiceRef.open()">
                        <view class="text-[28rpx] w-[150rpx] text-[#303133]">发票信息</view>
                        <view class="flex-1 w-0 text-right truncate">
                            <text class="text-[28rpx] text-[#333]">{{ createData.invoice.header_name || '不需要发票' }}</text>
                        </view>
                        <text class="nc-iconfont nc-icon-youV6xx text-[26rpx] text-[var(--text-color-light9)]"></text>
                    </view>

                </view>

                <view class="mb-[var(--top-m)] card-template" v-if="couponRef && couponList.length">
                    <!-- 优惠券 -->
                    <view class="flex items-center h-[40rpx] leading-[40rpx]" @click="couponRef.open(createData.discount.coupon_id)" v-if="couponList.length">
                        <view class="text-[28rpx] w-[150rpx] text-[#303133] flex-shrink-0">优惠券</view>
                        <view class="flex-1 flex justify-end truncate">
                            <text v-if="orderData.discount && orderData.discount.coupon" class="text-[var(--primary-color)] text-[28rpx] truncate ">
                                {{ orderData.discount.coupon.title }}
                            </text>
                            <text class="text-[28rpx] text-gray-subtitle" v-else>请选择优惠券</text>
                        </view>
                        <text class="nc-iconfont nc-icon-youV6xx -mb-[2rpx] text-[26rpx] text-[var(--text-color-light9)]"></text>
                    </view>
                </view>

				<view class="card-template py-[10rpx] mb-[var(--top-m)]" v-if="orderData.form_id">
					<diy-form ref="diyFormRef" :form_id="orderData.form_id" :storage_name="'diyFormStorageByOrderPayment'" />
				</view>

                <view class="card-template">
                    <view class="title">价格明细</view>
                    <view class="card-template-item">
                        <view class="text-[28rpx] w-[150rpx] leading-[30rpx] text-[#303133]">商品金额</view>
                        <view class="flex-1 w-0 text-right  price-font text-[#333] text-[32rpx]">
							￥{{ parseFloat(orderData.basic.goods_money).toFixed(2) }}
                        </view>
                    </view>
                    <view class="card-template-item" v-if="parseFloat(orderData.basic.delivery_money)">
                        <view class="text-[28rpx] w-[150rpx] leading-[30rpx] text-[#303133]">配送费用</view>
                        <view class="flex-1 w-0 text-right price-font text-[#333] text-[32rpx]">
                            ￥{{ parseFloat(orderData.basic.delivery_money).toFixed(2) }}
                        </view>
                    </view>
                    <!-- <view class="card-template-item" v-if="orderData.basic.discount_money">
                        <view class="text-[28rpx] w-[150rpx] leading-[30rpx] text-[#303133]">优惠金额</view>
                        <view class="flex-1 w-0 text-right text-[var(--price-text-color)] text-[32rpx] price-font leading-[1]">
                            -￥{{parseFloat(orderData.basic.discount_money)}}
                        </view>
                    </view> -->
					<view class="card-template-item" v-if="parseFloat(orderData.basic.coupon_money)">
					    <view class="text-[28rpx] w-[170rpx] leading-[30rpx] text-[#303133]">优惠券优惠</view>
					    <view class="flex-1 w-0 text-right text-[var(--price-text-color)] text-[32rpx] price-font leading-[1]">
					        -￥{{parseFloat(orderData.basic.coupon_money).toFixed(2)}}
					    </view>
					</view>
					<view class="card-template-item" v-if="parseFloat(orderData.basic.manjian_discount_money)">
					    <view class="text-[28rpx] w-[170rpx] leading-[30rpx] text-[#303133]">满减优惠</view>
					    <view class="flex-1 w-0 text-right text-[var(--price-text-color)] text-[32rpx] price-font leading-[1]">
					        -￥{{parseFloat(orderData.basic.manjian_discount_money).toFixed(2)}}
					    </view>
					</view>
                </view>
            </view>
            <u-tabbar :fixed="true" :placeholder="true" :safeAreaInsetBottom="true" zIndex="10">
                <view class="flex-1 flex items-center justify-between pl-[30rpx] pr-[20rpx]">
                    <view class="flex items-baseline">
                        <text class="text-[26rpx] text-[#333] leading-[32rpx]">合计：</text>
						<view class="inline-block">
							<text class="text-[26rpx] font-500 text-[var(--price-text-color)] price-font leading-[30rpx]">￥</text>
							<text class="text-[44rpx]  font-500  text-[var(--price-text-color)] price-font leading-[46rpx]">{{ parseFloat(orderData.basic.order_money).toFixed(2).split('.')[0] }}</text>
							<text class="text-[26rpx]  font-500  text-[var(--price-text-color)] price-font leading-[46rpx]">.{{ parseFloat(orderData.basic.order_money).toFixed(2).split('.')[1] }}</text>
						</view>
                    </view>
                    <button class="w-[196rpx]  h-[70rpx] font-500 text-[26rpx] leading-[70rpx] !text-[#fff] m-0  rounded-full  primary-btn-bg remove-border" hover-class="none" @click="create">提交订单</button>
                </view>
            </u-tabbar>

            <!-- 选择优惠券 -->
            <select-coupon :order-key="createData.order_key" ref="couponRef" @confirm="confirmSelectCoupon" />
        </view>

		<!-- 选择自提点 -->
		<select-store ref="storeRef" @confirm="confirmSelectStore" v-if="orderData && orderData.basic.has_goods_types.includes('real')" />
		<!-- 发票 -->
		<invoice ref="invoiceRef" @confirm="confirmInvoice" />
		<!-- 地址 -->
		<address-list ref="addressRef" @confirm="confirmAddress" />
		<!-- 满减 -->
		<ns-goods-manjian ref="manjianShowRef"></ns-goods-manjian>
		<pay ref="payRef" @close="payClose" />

    </view>
</template>

<script setup lang="ts">
import { ref, computed, watch, nextTick } from 'vue'
import { orderCreateCalculate, orderCreate } from '@/addon/shop/api/order'
import { redirect, img, moneyFormat, mobileHide } from '@/utils/common'
import selectCoupon from './components/select-coupon/select-coupon'
import selectStore from './components/select-store/select-store'
import addressList from './components/address-list/address-list'
import invoice from './components/invoice/invoice'
import nsGoodsManjian from '@/addon/shop/components/ns-goods-manjian/ns-goods-manjian.vue';
import { useSubscribeMessage } from '@/hooks/useSubscribeMessage'
import { onShow } from '@dcloudio/uni-app'
import { cloneDeep } from 'lodash-es'
import diyForm from '@/addon/components/diy-form/index.vue'

const createData: any = ref({
	order_key: '',
	member_remark: '',
	discount: {},
	invoice: {},
	delivery: {
		delivery_type: ''
	},
	extend_data: {}, // 扩展数据，目前礼品卡用到
	form_data: {} // 万能表单数据（商品+待付款订单）
})
const manjianShowRef: any = ref(null); //满减送

const orderData: any = ref(null)
const couponRef = ref()
const storeRef = ref()
const payRef = ref()
const addressRef = ref()
const invoiceRef = ref()
const createLoading = ref(false)
const activeIndex = ref(0)//配送方式激活
const delivery_type_list = ref([])
uni.getStorageSync('orderCreateData') && Object.assign(createData.value, uni.getStorageSync('orderCreateData'))

const diyFormRef: any = ref(null)
const diyFormGoodsRef: any = ref(null)

onShow(() => {
	nextTick(()=>{
		if(storeRef.value){
			storeRef.value.getData((data:any)=>{
				if(data.length){
					createData.value.delivery.take_store_id = ((data[0] && data[0].store_id) ? data[0].store_id: 0)
				}
			});
		}
	})
})

const openSelectStore = () => {
    if(storeRef.value){
        if (!createData.value.delivery.take_store_id) {
            storeRef.value.getData((data:any)=>{
                if(data.length){
                    createData.value.delivery.take_store_id = ((data[0] && data[0].store_id) ? data[0].store_id: 0)
                }
           }); 
        }
    }
    storeRef.value.open()
}

// 选择地址之后跳转回来
const selectAddress = uni.getStorageSync('selectAddressCallback')
if (selectAddress) {
    createData.value.order_key = ''
    createData.value.delivery.delivery_type = selectAddress.delivery
    createData.value.delivery.take_address_id = selectAddress.address_id
    uni.removeStorage({ key: 'selectAddressCallback' })
}

// 切换配送方式
const switchDeliveryType = (type: string, index: number) => {
    if (createData.value.delivery.delivery_type != type) {
        activeIndex.value = index
        createData.value.order_key = ''
        createData.value.delivery.delivery_type = type
        createData.value.delivery.take_address_id = 0
        calculate()
    }
}

// 满减
const manjianOpenFn = (data:any) =>{
	let obj = {};
	obj.condition_type = cloneDeep(data).condition_type;
	obj.rule_json = [cloneDeep(data).rule];
	obj.name = cloneDeep(data).manjian_name;
	manjianShowRef.value.open(obj);
}

/**
 * 订单计算
 */
const calculate = () => {
	orderCreateCalculate(createData.value).then(({ data }) => {
		orderData.value = cloneDeep(data);

		orderData.value.goods = []; //购买商品
		if (orderData.value.goods_data && Object.values(orderData.value.goods_data).length) {
			Object.values(orderData.value.goods_data).forEach((item: any, index) => {
				orderData.value.goods.push(item);
			})
		}

		createData.value.order_key = data.order_key
		if (orderData.value.delivery.delivery_type_list) {
			delivery_type_list.value = Object.values(orderData.value.delivery.delivery_type_list)
		}
		if (orderData.value.discount && orderData.value.discount.manjian) {
			orderData.value.manjian = orderData.value.discount.manjian
		}

		if (selectAddress) activeIndex.value = delivery_type_list.value.findIndex(el => el.key === orderData.value.delivery.delivery_type)
		!createData.value.delivery.delivery_type && data.delivery.delivery_type && (createData.value.delivery.delivery_type = data.delivery.delivery_type)

	}).catch()
}

calculate()

// 改变配送方式
watch(
	() => delivery_type_list.value.length,
	(newValue, oldValue) => {
		if(delivery_type_list.value.length && uni.getStorageSync('distributionType')){
			delivery_type_list.value.forEach((item: any,index)=>{
				if(item.name == uni.getStorageSync('distributionType')){
					activeIndex.value = index;
					switchDeliveryType(item.key, index)
				}
			})
			uni.removeStorage({ key: 'distributionType' })
		}
	}
)

let orderId = 0
/**
 * 订单创建
 */
const create = () => {
	if (!verify() || createLoading.value) return
	if (diyFormGoodsRef.value) {
		let pass = true;
		for (let i = 0; i < diyFormGoodsRef.value.length; i++) {
			if (!diyFormGoodsRef.value[i].verify()) {
				pass = false;
				break;
			}
		}
		if (!pass) return;
	}

	if (diyFormRef.value && !diyFormRef.value.verify()) return;
	createLoading.value = true

	useSubscribeMessage().request('shop_order_pay,shop_order_delivery')

	createData.value.form_data.order = {};
	createData.value.form_data.goods = {};
	if (diyFormRef.value) {
		createData.value.form_data.form_id = orderData.value.form_id;
		createData.value.form_data.order = diyFormRef.value.getData();
	}
	if (diyFormGoodsRef.value) {
		orderData.value.goods.forEach((item: any) => {
			if (item.goods.form_id) {
				for (let i = 0; i < diyFormGoodsRef.value.length; i++) {
					let formItem = diyFormGoodsRef.value[i].getData();
					if (formItem.relate_id == item.sku_id && item.goods.form_id == formItem.form_id) {
						createData.value.form_data.goods[item.sku_id] = formItem;
					}
				}
			}
		})
	}

	orderCreate(createData.value).then(({ data }) => {
		orderId = data.order_id
		if (diyFormRef.value) diyFormRef.value.clearStorage();
		if (diyFormGoodsRef.value) {
			for (let i = 0; i < diyFormGoodsRef.value.length; i++) {
				diyFormGoodsRef.value[i].clearStorage()
			}
		}
		createData.value.form_data = {}
		if (orderData.value.basic.order_money == 0) {
			redirect({ url: '/addon/shop/pages/order/detail', param: { order_id: orderId }, mode: 'redirectTo' })
		} else {
			payRef.value?.open(data.trade_type, data.order_id, `/addon/shop/pages/order/detail?order_id=${ data.order_id }`)
		}
	}).catch(() => {
		createData.value.form_data = {}
		createLoading.value = false
	})
}

/**
 * 下单校验
 */
const verify = () => {
    const data = createData.value
    let verify = true

    if (orderData.value.basic.has_goods_types.includes('real')) {
        if (['express', 'local_delivery'].includes(data.delivery.delivery_type) && !orderData.value.delivery.take_address) {
            uni.showToast({ title: '请选择收货地址', icon: 'none' })
            return false
        }

        if (data.delivery.delivery_type == 'store' && !data.delivery.take_store_id) {
            uni.showToast({ title: '请选择自提点', icon: 'none' })
            return false
        }
    }

    return verify
}

/**
 * 支付弹窗关闭
 */
const payClose = () => {
    redirect({ url: '/addon/shop/pages/order/detail', param: { order_id: orderId }, mode: 'redirectTo' })
}

/**
 * 选择地址
 */
const toSelectAddress = () => {
	let data: any = {};
	data.delivery = createData.value.delivery.delivery_type;
	data.type = createData.value.delivery.delivery_type == 'local_delivery' ? 'location_address' : 'address';
	data.id = orderData.value.delivery.take_address.id;
	addressRef.value.open(data);
}

const couponList = computed(() => {
    return couponRef.value?.couponList || []
})

/**
 * 选择优惠券
 */
const confirmSelectCoupon = (coupon: any) => {
    createData.value.discount.coupon_id = coupon ? coupon.id : 0
    calculate()
}

/**
 * 选择自提点
 */
const confirmSelectStore = (store: any) => {
    createData.value.delivery.take_store_id = ((store && store.store_id) ? store.store_id: 0)
    calculate()
}

const confirmInvoice = (invoice: object) => {
    createData.value.invoice = invoice
}

const confirmAddress = (data:any) => {
	createData.value.order_key = ''
	createData.value.delivery.delivery_type = data.delivery
	createData.value.delivery.take_address_id = data.address_id
	calculate();
}
</script>

<style lang="scss" scoped>
.head-tab{
	.head-tab-item{
		.tab-image{
            left: 50%;
			transform: translateX(-50%);
		}
		&:nth-child(1).active{
			view{
				padding-right: 40rpx;
			}
		}
		&:nth-child(2){
			.tab-image{
				width: 312rpx;
			}
		}
		&:nth-child(3).active{
			view{
				padding-left: 30rpx;
			}
		}
		&.active{
			view{
                font-weight: bold;
				color: var(--primary-color);
			}
		}
        .tab-img{
            left: 50%;
			transform: translateX(-50%);
        }

	}
}

:deep(.u-alert) {
    padding: 6rpx 16rpx !important;
    display: inline-block !important;
}
.alert-wrap{
    display: inline-block !important;
    :deep(.u-fade-enter-active){
        display: inline-block !important;
    }
}

.text-ellipsis {
    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 2;
    overflow: hidden;
}
.line-normal{
    line-height: normal;
}
.bg-color{
    background: linear-gradient( 94deg, var(--primary-help-color) 0%, var(--primary-color) 69%),var(--primary-color) ;
}
.payment-bottom{
	padding-bottom: calc(20rpx + constant(safe-area-inset-bottom));
	padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
}
</style>
