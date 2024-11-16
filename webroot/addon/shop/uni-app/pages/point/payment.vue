<template>
    <view :style="themeColor()">
        <view class="bg-[var(--page-bg-color)] min-h-[100vh]" v-if="orderData">
            <view class="pt-[30rpx] sidebar-margin payment-bottom">
                <!-- 配送方式 -->
                <view class="mb-[var(--top-m)] rounded-[var(--rounded-big)] bg-white" v-if="orderData.basic.has_goods_types.includes('real') && delivery_type_list.length">
                    <view class="rounded-tl-[var(--rounded-big)] rounded-tr-[var(--rounded-big)] head-tab flex items-center w-full bg-[#f1f1f1]" v-if="delivery_type_list.length > 1">
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
                        <view class="flex items-center w-full flex items-center" v-if="createData.delivery.delivery_type == 'store'" @click="storeRef.open()">
                            <view v-if="!$u.test.isEmpty(orderData.delivery.take_store)" class="pt-[26rpx] pb-[30rpx] w-full flex items-centerv">
                                <view class="flex flex-col">
									<view class="text-[30rpx] font-500 text-[#303133] mb-[20rpx]">{{ orderData.delivery.take_store.store_name }}</view>
									<view class="text-[24rpx] text-[var(--text-color-light6)] mb-[14rpx]">门店地址：{{ orderData.delivery.take_store.full_address }}</view>
                                    <view class="text-[24rpx] text-[var(--text-color-light6)] mb-[14rpx]">联系电话：{{ orderData.delivery.take_store.store_mobile }}</view>
                                    <view class="text-[24rpx] text-[var(--text-color-light6)]">营业时间：{{ orderData.delivery.take_store.trade_time }}</view>
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
                <view class="mb-[var(--top-m)] card-template h-[100rpx] flex items-center" v-if="orderData.basic.has_goods_types.includes('real') && !delivery_type_list.length">
                    <p class="text-[28rpx] text-[var(--primary-color)]">商家尚未配置配送方式</p>
                </view>

                <view class="mb-[var(--top-m)] card-template">
                    <view class="mb-[30rpx]">
                        <view class="flex" v-for="(item, key, index) in orderData.goods_data" :key="index" :class="{'pb-[40rpx]': (index + 1) != Object.keys(orderData.goods_data).length}">
                            <u--image radius="var(--goods-rounded-big)" width="180rpx" height="180rpx" :src="img(item.sku_image.split(',')[0])" model="aspectFill">
                                <template #error>
									<image class="w-[180rpx] h-[180rpx] rounded-[var(--goods-rounded-big)] overflow-hidden" :src="img('static/resource/images/diy/shop_default.jpg')" mode="aspectFill"/>
                                </template>
                            </u--image>
                            <view class="flex flex-1 w-0 flex-col justify-between ml-[20rpx] py-[6rpx]">
                                <view class="line-normal">
                                    <view class="truncate text-[#303133] text-[28rpx] leading-[32rpx]">
                                        {{ item.goods.goods_name }}
                                    </view>
                                    <view v-if="item.sku_name" class="mt-[14rpx] flex">
                                        <text class="truncate text-[24rpx] text-[var(--text-color-light9)] leading-[28rpx]">{{ item.sku_name }}</text>
                                    </view>
                                </view>
                                <view class="mb-auto" :class="{'mt-[6rpx]': !item.sku_name}"  v-if="item.not_support_delivery">
                                    <u-alert type="error" description="该商品不支持当前所选配送方式" class="leading-[30rpx] !inline-block" fontSize="11"></u-alert>
                                </view>
                                <view class="flex justify-between items-baseline">
                                    <view class="text-[var(--price-text-color)] flex items-baseline  price-font">
										<view class="flex items-baseline price-font">
											<text class="text-[40rpx] font-200">{{item.exchange_info.point}}</text>
											<text class="text-[32rpx]">积分</text>
										</view>
										<block v-if="parseFloat(item.price)">
											<text class="mx-[4rpx] text-[32rpx]">+</text>
											<view class="flex items-baseline price-font">
												<text class="text-[40rpx] font-200">{{ parseFloat(item.price).toFixed(2) }}</text>
												<text class="text-[32rpx]">元</text>
											</view>
										</block>
                                    </view>
                                    <view class="font-400 text-[28rpx] text-[#303133]">
                                        <text>x</text>
                                        <text>{{ item.num }}</text>
                                    </view>
                                </view>
                            </view>
                        </view>
                    </view>

                    <!-- 买家留言 -->
                    <view class="bg-white flex items-center leading-[30rpx]">
                        <view class="text-[28rpx] w-[150rpx] text-[#303133]">买家留言</view>
                        <view class="flex-1 text-[#303133]">
                            <input type="text" v-model="createData.member_remark" class="text-right text-[#333] text-[28rpx]" maxlength="50" placeholder="请输入留言信息给卖家" placeholder-class="text-[var(--text-color-light9)] text-[28rpx]">
                        </view>
                        <!-- <text class="nc-iconfont nc-icon-youV6xx text-[26rpx] text-[var(--text-color-light9)]"></text> -->
                    </view>
                    <!-- 发票 -->
                    <view class="flex items-center text-[#303133] leading-[30rpx] mt-[30rpx]" @click="invoiceRef.open()" v-if="invoiceRef && invoiceRef.invoiceOpen">
                        <view class="text-[28rpx] w-[150rpx] text-[#303133]">发票信息</view>
                        <view class="flex-1 w-0 text-right truncate">
                            <text class="text-[28rpx] text-[#333]">{{ createData.invoice.header_name || '不需要发票' }}</text>
                        </view>
                        <text class="nc-iconfont nc-icon-youV6xx text-[26rpx] text-[var(--text-color-light9)]"></text>
                    </view>
                </view>

                <view class="card-template">
                    <view class="title">价格明细</view>
                    <view class="card-template-item">
                        <view class="text-[28rpx] w-[150rpx] leading-[30rpx] text-[#303133]">商品金额</view>
                        <view class="flex-1 w-0 text-right  price-font text-[#333] text-[32rpx]">
							<view class="inline-block">
								<text class="text-[32rpx] mr-[2rpx]">{{ orderData.basic.point_sum }}</text>
								<text class="text-[30rpx]">积分</text>
							</view>
							<block v-if="orderData.basic && parseFloat(orderData.basic.goods_money)">
								<text class="text-[28rpx] mx-[4rpx]">+</text>
								<view class="inline-block">
									<text class="text-[32rpx] mr-[2rpx]">{{ parseFloat(orderData.basic.goods_money).toFixed(2) }}</text>
									<text class="text-[30rpx]">元</text>
								</view>
							</block>
                        </view>
                    </view>
                    <view class="card-template-item" v-if="orderData.basic.delivery_money">
                        <view class="text-[26rpx] w-[150rpx] leading-[30rpx] text-[#303133]">配送费用</view>
                        <view class="flex-1 w-0 text-right price-font text-[#333] text-[32rpx]">
                            ￥{{ parseFloat(orderData.basic.delivery_money) }}
                        </view>
                    </view>
                    <view class="card-template-item" v-if="orderData.basic.discount_money">
                        <view class="text-[26rpx] w-[150rpx] leading-[30rpx] text-[#303133]">优惠金额</view>
                        <view class="flex-1 w-0 text-right text-[var(--price-text-color)] text-[32rpx] price-font leading-[1]">
                            -￥{{ parseFloat(orderData.basic.discount_money) }}
                        </view>
                    </view>
                </view>
            </view>
            <u-tabbar :fixed="true" :placeholder="true" :safeAreaInsetBottom="true">
                <view class="flex-1 flex items-center justify-between pl-[30rpx] pr-[20rpx]">
                    <view class="flex items-baseline">
                        <text class="text-[26rpx] text-[#333] leading-[32rpx]">合计：</text>
						<text class="text-[var(--price-text-color)] price-font inline-block">
							<text class="text-[44rpx]">{{ orderData.basic.point_sum }}</text>
							<text class="text-[38rpx]">积分</text>
						</text>
						<block v-if="orderData.basic && parseFloat(orderData.basic.goods_money)">
							<text class="text-[38rpx] text-[var(--price-text-color)] price-font mx-[4rpx]">+</text>
							<view class="inline-block">
								<text class="text-[44rpx] text-[var(--price-text-color)] price-font">{{ parseFloat(orderData.basic.order_money).toFixed(2) }}</text>
								<text class="text-[38rpx] text-[var(--price-text-color)] price-font">元</text> 
							</view>
						</block>
                    </view>
                    <button class="primary-btn-bg w-[196rpx] h-[70rpx] font-500 text-[26rpx] leading-[70rpx] !text-[#fff] !border-[0] rounded-[100rpx] m-0" hover-class="none" @click="create">提交订单</button>
                </view>
            </u-tabbar>
            <!-- 选择自提点 -->
            <select-store ref="storeRef" @confirm="confirmSelectStore" />
            <!-- 发票 -->
            <invoice ref="invoiceRef" @confirm="confirmInvoice" />
			<!-- 地址 -->
			<address-list ref="addressRef" @confirm="confirmAddress" />

            <pay ref="payRef" @close="payClose" />
        </view>
    </view>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { orderCreateCalculate, orderCreate } from '@/addon/shop/api/point'
import { redirect, img, moneyFormat, mobileHide } from '@/utils/common'
import selectStore from './../order/components/select-store/select-store'
import invoice from './../order/components/invoice/invoice'
import addressList from './components/address-list/address-list'
import { useSubscribeMessage } from '@/hooks/useSubscribeMessage'

const createData: any = ref({
    order_key: '',
    member_remark: '',
    discount: {},
    invoice: {},
    delivery: {
        delivery_type: ''
    }
})

const orderData = ref(null)
const storeRef = ref()
const payRef = ref()
const invoiceRef = ref()
const createLoading = ref(false)
const activeIndex = ref(0)//配送方式激活
const delivery_type_list = ref([])
const addressRef = ref()
uni.getStorageSync('orderCreateData') && Object.assign(createData.value, uni.getStorageSync('orderCreateData'))

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

/**
 * 订单计算
 */
const calculate = () => {
    orderCreateCalculate(createData.value).then(({data}) => {
        orderData.value = data
        createData.value.order_key = data.order_key
        if (orderData.value.delivery.delivery_type_list) {
            delivery_type_list.value = Object.values(orderData.value.delivery.delivery_type_list)
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
			delivery_type_list.value.forEach((item: any,index: any)=>{
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
    createLoading.value = true

    useSubscribeMessage().request('shop_order_pay,shop_order_delivery')

    orderCreate(createData.value).then(({ data }) => {
        orderId = data.order_id
        if (orderData.value.basic.order_money == 0) {
            redirect({ url: '/addon/shop/pages/order/detail', param: { order_id: orderId }, mode: 'redirectTo' })
        } else {
            payRef.value?.open(data.trade_type, data.order_id, `/addon/shop/pages/order/detail?order_id=${ data.order_id }`)
        }
    }).catch(() => {
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

const confirmAddress = (data:object) => {
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
.payment-bottom{
	padding-bottom: calc(20rpx + constant(safe-area-inset-bottom));
	padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
}
</style>
