<template>
    <scroll-view scroll-y="true" class="bg-page h-screen" v-if="orderData">
        <view class="py-[20rpx] px-[24rpx]" style="background: linear-gradient(var(--primary-color) 0%, var(--primary-color-disabled) 7%, #F4F4F6 10%)">
            <!-- 配送方式 -->
            <view class="mb-[20rpx] rounded-[16rpx] bg-white" v-if="orderData.basic.has_goods_types.includes('real') && delivery_type_list.length">
                <view class="flex items-center rounded-tl-[16rpx] rounded-tr-[16rpx] overflow-hidden w-full" v-if="delivery_type_list.length > 1">
                    <view v-for="(item, index) in delivery_type_list" class="flex-1"
                        :class="{'bg-[#fff]' : index === (activeIndex - 1) || index === (activeIndex + 1),'bg-[var(--primary-color-disabled)]' : !(index === (activeIndex - 1) || index === (activeIndex + 1))}"
                        :key="index">
                        <view class="h-[74rpx] text-center leading-[74rpx] text-[30rpx] font-500"
							:class="{'bg-[var(--primary-color-disabled)]':index === (activeIndex - 1) || index === (activeIndex + 1),
							'rounded-br-[16rpx]': index === (activeIndex - 1),
							'rounded-tr-[16rpx] rounded-tl-[16rpx] bg-[#fff] text-[var(--primary-color)]':index === activeIndex,
							'rounded-bl-[16rpx]':index === (activeIndex + 1)}"
                            @click="switchDeliveryType(item.key, index)">
                            <text>{{ item.name }}
                            </text>
                        </view>
                    </view>
                </view>
                <view class="p-[24rpx]">
                    <!-- 收货地址 -->
                    <view class="flex items-center pt-[24rpx] pb-[10rpx]"
                        v-if="['express', 'local_delivery'].includes(createData.delivery.delivery_type)"
                        @click="toSelectAddress">
                        <view class="flex-1 w-0">
                            <view v-if="!$u.test.isEmpty(orderData.delivery.take_address)">
                                <view class="font-500 text-[30rpx] mb-[10rpx]">
                                    {{ orderData.delivery.take_address.name }}
                                    <text class="text-[30rpx]">{{ mobileHide(orderData.delivery.take_address.mobile)
                                    }}</text>
                                </view>
                                <view class="text-[26rpx] text-gray-subtitle mt-[10rpx]">{{
                                    orderData.delivery.take_address.full_address }}</view>
                            </view>
                            <view v-else class="text-[26rpx]">
                                添加收货地址
                            </view>
                        </view>
                        <text class="iconfont iconxiangyoujiantou text-[26rpx] text-gray-subtitle"></text>
                    </view>

                    <!-- 自提点 -->
                    <view class="flex items-center pt-[24rpx] pb-[10rpx]"
                        v-if="createData.delivery.delivery_type == 'store'" @click="storeRef.open()">
                        <view class="flex-1 w-0">
                            <view v-if="!$u.test.isEmpty(orderData.delivery.take_store)">
                                <view class="font-500 text-[30rpx] mb-[10rpx]">
                                    {{ orderData.delivery.take_store.store_name }}
                                    <text class="text-[26rpx]">{{ orderData.delivery.take_store.store_mobile }}</text>
                                </view>
                                <view class="text-[26rpx] text-gray-subtitle mt-[10rpx]">{{
                                    orderData.delivery.take_store.full_address }}</view>
                                <view class="text-[26rpx] text-gray-subtitle mt-[16rpx]">营业时间：{{
                                    orderData.delivery.take_store.trade_time }}</view>
                            </view>
                            <view v-else class="text-[26rpx]">
                                请选择自提点
                            </view>
                        </view>
                        <text class="iconfont iconxiangyoujiantou text-[26rpx] text-gray-subtitle"></text>
                    </view>
                </view>
            </view>
            <view class="mb-[20rpx] rounded-[16rpx] bg-white p-[24rpx] py-[40rpx]" v-if="orderData.basic.has_goods_types.includes('real') && !delivery_type_list.length">
                <p class="text-[26rpx] text-gray-subtitle">商家尚未配置配送方式</p>
            </view>

            <view class="mb-[20rpx] px-[24rpx] rounded-md bg-white">
                <view class="flex py-[30rpx] border-0 !border-b !border-[#f5f5f5] border-solid"
                    v-for="(item, index) in orderData.goods_data">
                    <u--image width="168rpx" height="168rpx" :src="img(item.sku_image)" model="aspectFill">
                        <template #error>
                            <u-icon name="photo" color="#999" size="50"></u-icon>
                        </template>
                    </u--image>
                    <view class="flex flex-1 w-0 flex-col justify-between ml-[20rpx]">
                        <view>
                            <view class="text-ellipsis text-[#333] text-[26rpx] leading-normal font-500">
                                {{ item.goods.goods_name }}
                            </view>
                            <view class="mt-[10rpx] text-[26rpx] text-gray-subtitle">{{ item.sku_name }}</view>
                        </view>
                        <u-alert type="error" description="该商品不支持当前所选配送方式" fontSize="12"
                            v-if="item.not_support_delivery"></u-alert>
                        <view class="flex justify-between">
                            <view class="text-[var(--price-text-color)] font-500 price-font">
                                <text class="text-xs">￥</text>
                                <text>{{ moneyFormat(item.price) }}</text>
                            </view>
                            <view class="font-500 text-sm">
                                <text class="text-[26rpx]">x</text>{{ item.num }}
                            </view>
                        </view>
                    </view>
                </view>
            </view>

            <!-- 买家留言 -->
            <view class="mb-[20rpx] p-[24rpx] rounded-md bg-white flex">
                <view class="text-[28rpx] font-500 w-[150rpx]">买家留言</view>
                <view class="flex-1">
                    <input type="text" v-model="createData.member_remark" class="text-right text-[28rpx]" maxlength="50"
                        placeholder="请输入留言信息给卖家">
                </view>
            </view>

            <view class="rounded-md bg-white mb-[24rpx] overflow-hidden">
                <!-- 优惠券 -->
                <view class="p-[24rpx] flex items-center" @click="couponRef.open()" v-if="couponList.length">
                    <view class="text-[28rpx] font-500 w-[150rpx]">优惠券</view>
                    <view class="flex-1 w-0 text-right">
                        <text v-if="orderData.discount && orderData.discount.coupon" class="text-red font-500 text-[28rpx]">
                            {{ orderData.discount.coupon.title }}
                        </text>
                        <text class="text-[28rpx] text-gray-subtitle" v-else>请选择优惠券</text>
                    </view>
                    <text class="iconfont iconxiangyoujiantou text-[28rpx] text-gray-subtitle"></text>
                </view>

                <!-- 发票 -->
                <view class="p-[24rpx] flex items-center" @click="invoiceRef.open()"
                    v-if="invoiceRef && invoiceRef.invoiceOpen">
                    <view class="text-[28rpx] font-500 w-[150rpx]">发票信息</view>
                    <view class="flex-1 w-0 text-right truncate">
                        <text class="text-[28rpx] text-gray-subtitle">{{ createData.invoice.header_name || '不需要发票' }}</text>
                    </view>
                    <text class="iconfont iconxiangyoujiantou text-[28rpx] text-gray-subtitle"></text>
                </view>
            </view>

            <view class=" mt-0 p-[24rpx] rounded-md bg-white">
                <view class="flex font-500 py-[10rpx]">
                    <view class="text-[28rpx] w-[150rpx]">商品金额</view>
                    <view class="flex-1 w-0 text-right text-[var(--price-text-color)] price-font">
                        <text class="text-[24rpx]">￥</text>
                        <text>{{ moneyFormat(orderData.basic.goods_money) }}</text>
                    </view>
                </view>
                <view class="flex font-500 py-[10rpx]" v-if="orderData.basic.delivery_money">
                    <view class="text-[28rpx] w-[150rpx]">运费</view>
                    <view class="flex-1 w-0 text-right text-[var(--price-text-color)] price-font">
                        <text class="text-[24rpx]">￥</text>
                        <text>{{ moneyFormat(orderData.basic.delivery_money) }}</text>
                    </view>
                </view>
                <view class="flex font-500 py-[10rpx]" v-if="orderData.basic.discount_money">
                    <view class="text-[28rpx] w-[150rpx]">优惠金额</view>
                    <view class="flex-1 w-0 text-right text-[var(--price-text-color)] price-font">
                        <text class="text-[24rpx]">-￥</text>
                        <text>{{ moneyFormat(orderData.basic.discount_money) }}</text>
                    </view>
                </view>
            </view>
        </view>
        <u-tabbar :fixed="true" :placeholder="true" :safeAreaInsetBottom="true">
            <view class="flex-1 flex items-center justify-between">
                <view class="whitespace-nowrap px-[30rpx] text-color font-600 leading-[45rpx]">
                    <text class="text-[#333333] text-[26rpx]">合计：</text>
                    <text class="text-[24rpx] font-500 text-[var(--price-text-color)] price-font">￥</text>
                    <text class="text-[34rpx] mr-[10rpx]  font-500  text-[var(--price-text-color)] price-font">{{
                        moneyFormat(orderData.basic.order_money) }}</text>
                </view>
                <button class="!w-[204rpx] !h-[80rpx] text-[32rpx] mr-[30rpx] leading-[80rpx] rounded-full text-white bg-[var(--primary-color)] remove-border"
                    :loading="createLoading" @click="create">提交订单</button>
            </view>
        </u-tabbar>

        <!-- 选择优惠券 -->
        <select-coupon :order-key="createData.order_key" ref="couponRef" @confirm="confirmSelectCoupon" />
        <!-- 选择自提点 -->
        <select-store ref="storeRef" @confirm="confirmSelectStore" />
        <!-- 发票 -->
        <invoice ref="invoiceRef" @confirm="confirmInvoice" />

        <pay ref="payRef" @close="payClose" />
    </scroll-view>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { orderCreateCalculate, orderCreate } from '@/addon/shop/api/order'
import { redirect, img, moneyFormat, mobileHide } from '@/utils/common'
import selectCoupon from './components/select-coupon/select-coupon'
import selectStore from './components/select-store/select-store'
import invoice from './components/invoice/invoice'

const createData = ref({
    order_key: '',
    member_remark: '',
    discount: {},
    invoice: {},
    delivery: {
        delivery_type: ''
    }
})

const orderData = ref(null)
const couponRef = ref()
const storeRef = ref()
const payRef = ref()
const invoiceRef = ref()
const createLoading = ref(false)
const activeIndex = ref(0)//配送方式激活
const delivery_type_list = ref([])
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
    orderCreateCalculate(createData.value)
        .then(({ data }) => {
            orderData.value = data
            createData.value.order_key = data.order_key
            delivery_type_list.value = Object.values(orderData.value.delivery.delivery_type_list)
            if (selectAddress) activeIndex.value = delivery_type_list.value.findIndex(el => el.key === orderData.value.delivery.delivery_type)
            !createData.value.delivery.delivery_type && data.delivery.delivery_type && (createData.value.delivery.delivery_type = data.delivery.delivery_type)
        })
        .catch()
}
calculate()

let orderId = 0
/**
 * 订单创建
 */
const create = () => {
    if (!verify() || createLoading.value) return
    createLoading.value = true

    orderCreate(createData.value)
        .then(({ data }) => {
            orderId = data.order_id
            if (orderData.value.basic.order_money == 0) {
                redirect({ url: '/addon/shop/pages/order/detail', param: { order_id: orderId }, mode: 'redirectTo' })
            } else {
                payRef.value?.open(data.trade_type, data.order_id, `/addon/shop/pages/order/detail?order_id=${data.order_id}`)
            }
        })
        .catch(() => {
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
    uni.setStorage({
        key: 'selectAddressCallback',
        data: {
            back: '/addon/shop/pages/order/payment',
            delivery: createData.value.delivery.delivery_type
        },
        success() {
            redirect({ url: '/app/pages/member/address', param: { type: createData.value.delivery.delivery_type == 'local_delivery' ? 'location_address' : 'address' } })
        }
    })
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
    createData.value.delivery.take_store_id = store.store_id || 0
    calculate()
}

const confirmInvoice = (invoice: object) => {
    createData.value.invoice = invoice
}
</script>

<style lang="scss" scoped>
:deep(.u-alert) {
    padding: 6rpx 16rpx;
}

.text-ellipsis {
    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 2;
    overflow: hidden;
}
.remove-border{
	&::after{
		border: none;
	}
}
</style>
<style>
	@import '@/addon/shop/styles/common.scss';
</style>
