<template>
	<view class="bg-page h-screen overflow-hidden flex flex-col" v-if="!loading">
		<view v-if="!info" class="flex-1 flex flex-col justify-center">
			<u-empty mode="data" text="未登录">
				<button shape="circle" class="!w-[200rpx] mt-[20rpx] text-[30rpx] leading-[70rpx] text-[#fff] rounded-full bg-color remove-border" @click="toLogin">去登录</button>
			</u-empty>
		</view>
		<view v-else-if="!cartList.length" class="flex-1 flex flex-col justify-center">
			<u-empty mode="car"></u-empty>
			<button shape="circle" class="!w-[200rpx] mt-[20rpx] text-[30rpx] leading-[70rpx] text-[#fff] rounded-full bg-color remove-border" @click="redirect({ url: '/addon/shop/pages/goods/list' })">去逛逛</button>
		</view>
		<block v-else>
			<view class="flex justify-between px-[32rpx] leading-[84rpx] font-500 text-[#666] text-[26rpx] bg-[#fff]">
				<text>共{{ cartList.length }}件宝贝</text>
				<text @click="isEdit = !isEdit">{{ isEdit ? '完成' : '编辑' }}</text>
			</view>
			<view class="flex-1 h-0">
				<scroll-view class="scroll-height box-border" :scroll-y="true">
					<view class="py-[20rpx] px-[24rpx]">
						<u-swipe-action>
							<view v-for="(item, index) in cartList" :class="['bg-[#fff] py-[30rpx] rounded-[18rpx] overflow-hidden', index ? 'mt-[20rpx]' : '']">
								<u-swipe-action-item :options="cartOptions" @click="swipeClick($event,'cart')">
									<view :class="['flex px-[24rpx]']">
										<text
											:class="['self-center iconfont text-color text-[37rpx] mr-[24rpx]', item.checked ? 'iconxuanze1' : 'iconcheckbox_nol']"
											@click="item.checked = !item.checked">
										</text>
										<u--image width="168rpx" height="168rpx" @click="toDetail(item)" :src="img(item.goodsSku.sku_image_thumb_mid)" model="aspectFill">
											<template #error>
												<u-icon name="photo" color="#999" size="50"></u-icon>
											</template>
										</u--image>
										<view class="flex flex-1 flex-wrap ml-[20rpx]">
											<view class="w-[100%]">
												<view class="w-[404rpx] multi-hidden text-[#333] text-[26rpx] leading-[36rpx] text-ellipsis">
													{{ item.goods.goods_name }}
												</view>
												<view class="w-[404rpx] mt-[12rpx] truncate text-[#888] text-[24rpx] leading-[32rpx] font-500">
													{{ item.goodsSku.sku_spec_format }}
												</view>
											</view>
											<view class="flex justify-between items-end self-end w-[100%]">
												<view class=" text-[var(--price-text-color)] leading-[40rpx] price-font">
													<text class="text-[24rpx] font-500">￥</text>
													<text class="text-[32rpx] mr-[10rpx] font-500">{{ item.goodsSku.sale_price }}</text>
												</view>
												<u-number-box v-model="item.num" :min="numLimit(item).min"
													:max="numLimit(item).max" integer :step="1" input-width="68rpx"
													input-height="52rpx" button-size="52rpx" disabledInput
													@change="numChange($event, index)">
													<template #minus>
														<text
															:class="{ 'text-[#c8c9cc]': item.num === numLimit(item).min, 'text-[var(--primary-color)]': item.num !== numLimit(item).min }"
															class="text-[44rpx]  iconfont iconjianhao"></text>
													</template>
													<template #input>
														<text class="text-[#333] fext-[23rpx] font-500 mx-[16rpx]">{{ item.num }}</text>
													</template>
													<template #plus>
														<text
															:class="{ 'text-[#c8c9cc]': item.num === numLimit(item).max, ' text-[var(--primary-color)]': item.num !== numLimit(item).max }"
															class="text-[44rpx] text-[var(--primary-color)] iconfont iconjiahao2fill"></text>
													</template>
												</u-number-box>
											</view>
										</view>
									</view>
								</u-swipe-action-item>
							</view>
							<view v-for="(item, index) in invalidList" :class="['bg-[#fff] py-[30rpx] rounded-[18rpx] overflow-hidden mt-[20rpx]']">
								<u-swipe-action-item :options="cartOptions" @click="swipeClick($event,'cart')">
									<view :class="['flex px-[24rpx]']">
										<text class="self-center iconfont text-[37rpx] mr-[24rpx] iconcheckbox_nol bg-[#f5f5f5] text-[#eee] rounded-[50%]"></text>
										<view class="relative w-[168rpx] h-[168rpx]">
											<u--image width="168rpx" height="168rpx" :src="img(item.goodsSku.sku_image_thumb_mid)" model="aspectFill">
												<template #error>
													<u-icon name="photo" color="#999" size="50"></u-icon>
												</template>
											</u--image>
											<view class="absolute left-0 top-0  w-[168rpx] h-[168rpx]  leading-[168rpx] text-center " style="background-color: rgba(243,244,246,0.5);">
												<text class="text-[#333] text-[24rpx] font-500">已失效</text>
											</view>
										</view>
										<view class="flex flex-1 flex-wrap ml-[20rpx]">
											<view class="w-[100%]">
												<view class="w-[404rpx] multi-hidden text-[#333] text-[26rpx] leading-[36rpx] text-ellipsis">
													{{ item.goods.goods_name }}
												</view>
												<view class="w-[404rpx] mt-[12rpx] truncate text-[#888] text-[24rpx] leading-[32rpx] font-500">
													{{ item.goodsSku.sku_spec_format }}
												</view>
											</view>
											<view class="flex justify-between items-end self-end w-[100%]">
												<view class="text-[var(--price-text-color)] leading-[40rpx] price-font">
													<text class="text-[24rpx] font-500">￥</text>
													<text class="text-[32rpx] mr-[10rpx] font-500">{{ item.goodsSku.sale_price }}</text>
												</view>
											</view>
										</view>
									</view>
								</u-swipe-action-item>
							</view>
						</u-swipe-action>
					</view>
				</scroll-view>
			</view>
		</block>
	</view>
	<!--  #ifdef  H5 -->
	<view v-if="cartList.length"
		class="flex h-[100rpx] items-center bg-[#fff] fixed left-0 right-0 bottom-[50px] box-solid mb-[env(safe-area-inset-bottom)]">
		<view class="flex items-center px-[30rpx]" @click="selectAll">
			<text class="iconfont text-color text-[34rpx] mr-[12rpx]" :class="cartList.length == checkedNum ? 'iconxuanze1' : 'iconcheckbox_nol'"></text>
			<text class="font-500 text-[#676767] text-[25rpx]">全选</text>
		</view>
		<view class="flex-1 flex items-center justify-between" v-if="!isEdit">
			<text class="whitespace-nowrap mr-[24rpx] text-[var(--price-text-color)] font-500  leading-[45rpx]">
				<text class="text-[#333333] text-[32rpx]">合计：</text>
				<text class="text-[24rpx] price-font">￥</text>
				<text class="text-[34rpx] mr-[10rpx] price-font">{{ total }}</text>
			</text>
			<button
				class="!w-[204rpx] !h-[80rpx] text-[#fff]  text-[30rpx] !leading-[80rpx] !text-[32rpx] mr-[30rpx] bg-color"
				shape="circle" @click="settlement">结算</button>
		</view>
		<view class="flex-1 flex items-center justify-end" v-else>
			<button
				class="!w-[204rpx] !h-[80rpx] text-[#fff]  text-[30rpx] !leading-[80rpx] !text-[32rpx] mr-[30rpx]  bg-color"
				shape="circle" @click="deleteCartFn">删除</button>
		</view>
	</view>
	<!--  #endif -->
	<!--  #ifndef  H5 -->
	<view v-if="cartList.length" class="flex h-[100rpx] items-center bg-[#fff] fixed left-0 right-0 bottom-[100rpx] box-solid mb-[env(safe-area-inset-bottom)]">
		<view class="flex items-center px-[30rpx]" @click="selectAll">
			<text class="iconfont text-color text-[34rpx] mr-[12rpx]" :class="cartList.length == checkedNum ? 'iconxuanze1' : 'iconcheckbox_nol'"></text>
			<text class="font-500 text-[#676767] text-[25rpx]">全选</text>
		</view>
		<view class="flex-1 flex items-center justify-between" v-if="!isEdit">
			<text class="whitespace-nowrap mr-[24rpx] text-[var(--price-text-color)] font-500  leading-[45rpx]">
				<text class="text-[#333333] text-[32rpx]">合计：</text>
				<text class="text-[24rpx] price-font">￥</text>
				<text class="text-[34rpx] mr-[10rpx] price-font">{{ total }}</text>
			</text>
			<button class="!w-[204rpx] !h-[80rpx] text-[#fff]  text-[30rpx] !leading-[80rpx] !text-[32rpx] mr-[30rpx] bg-color rounded-full" shape="circle" @click="settlement">结算</button>
		</view>
		<view class="flex-1 flex items-center justify-end" v-else>
			<button class="!w-[204rpx] !h-[80rpx] text-[#fff]  text-[30rpx] !leading-[80rpx] !text-[32rpx] mr-[30rpx]  bg-color" shape="circle" @click="deleteCartFn">删除</button>
		</view>
	</view>
	<!--  #endif -->
	<u-loading-page :loading="optionLoading" loading-text="" bg-color="none" loadingColor="var(--primary-color)" iconSize="35"></u-loading-page>
	<u-loading-page :loading="loading" loading-text="" bg-color="#f7f7f7" loadingColor="var(--primary-color)" iconSize="35"></u-loading-page>
	<tabbar addon="shop"/>
</template>


<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import useMemberStore from '@/stores/member'
import { useLogin } from '@/hooks/useLogin'
import { onShow } from '@dcloudio/uni-app'
import { img, redirect } from '@/utils/common'
import useCartStore from '@/addon/shop/stores/cart'
import { getCartGoodsList } from '@/addon/shop/api/cart'
import {t} from "@/locale";

const memberStore = useMemberStore()
const info = computed(() => memberStore.info)
const loading = ref(true)
const optionLoading = ref(false)
const total = ref('0.00')
const cartList = ref<object[]>([])
const invalidList = ref<object[]>([]) //  失效商品：已下架、已删除
const isEdit = ref(false)

const cartStore = useCartStore();

const getCartGoodsListFn = () => {
    getCartGoodsList({}).then(({data}) => {
        cartList.value = []
        invalidList.value = []

        data.forEach(item => {
            item.checked = false
            if (item.status && item.goods.delete_time == 0) {
                cartList.value.push(item)
            } else {
                invalidList.value.push(item)
            }
        })

        loading.value = false
    }).catch()
}

onShow(() => {
	if (info.value) {
		getCartGoodsListFn()
		cartStore.getList();
	} else {
		loading.value = false
	}
})

watch(() => info.value, () => {
	if (info.value) {
		getCartGoodsListFn()
	}
})

const checkedNum = computed(() => {
	let num = 0
	cartList.value.forEach(item => {
		item.checked && (num += 1)
	})
	return num
})

watch(() => cartList.value, () => {
	let value = 0
	cartList.value.forEach(item => {
		if (item.checked) value += parseFloat(item.goodsSku.sale_price) * item.num
	})
	total.value = value.toFixed(2)
}, { deep: true })

const toLogin = () => {
	useLogin().setLoginBack({ url: '/addon/shop/pages/goods/cart' })
}

const toDetail = (data: any) => {
    redirect({ url: '/addon/shop/pages/goods/detail', param: { goods_id: data.goods_id }, mode: 'navigateTo' })
}

const numChange = (event, index) => {
	uni.$u.debounce((event) => {
		const data = cartList.value[index]

		cartStore.increase({
			id: data.id,
			goods_id: data.goods_id,
			sku_id: data.sku_id,
			stock: data.goodsSku.stock,
			sale_price: data.goodsSku.sale_price,
			num: data.num
		}, 0);
	}, 500)
}

const numLimit = (data) => {
	return {
		min: 1,
		max: data.goodsSku.stock || 1
	}
}

const cartOptions = ref([
    {
        text: t('delete'),
        style: {
            backgroundColor: '#F56C6C'
        }
    }
])

const swipeClick = (event: {}, type: string) => {
	if (optionLoading.value) return
	optionLoading.value = true

    const index = event.index
	const data = cartList.value[index]

	cartStore.delete(data.id, () => {
		type == 'cart' ? cartList.value.splice(index, 1) : invalidList.value.splice(index, 1)
		optionLoading.value = false
	})
}

/**
 * 全选
 */
const selectAll = () => {
	const checked = cartList.value.length == checkedNum.value ? false : true
	cartList.value.forEach(item => {
		item.checked = checked
	})
}

/**
 * 结算
 */
const settlement = () => {
	if (!checkedNum.value) {
		uni.showToast({ title: '还没有选择商品', icon: 'none' })
		return
	}
	const ids = []
	cartList.value.forEach(item => {
		if (item.checked) ids.push(item.id)
	})

	uni.setStorage({
		key: 'orderCreateData',
		data: {
			cart_ids: ids
		},
		success() {
			redirect({ url: '/addon/shop/pages/order/payment' })
		}
	})
}

/**
 * 删除
 */
const deleteCartFn = () => {
	if (!checkedNum.value) {
		uni.showToast({ title: '还没有选择商品', icon: 'none' })
		return
	}
	if (optionLoading.value) return
	optionLoading.value = true

	const ids = []
	cartList.value.forEach(item => {
		if (item.checked) ids.push(item.id)
	})

	cartStore.delete(ids, () => {
		getCartGoodsListFn()
		optionLoading.value = false
	})
}
</script>
<style>
@import '@/addon/shop/styles/common.scss';
</style>
<style lang="scss" scoped>
:deep(uni-page) {
	background: var(--page-bg-color);
}

uni-page-body {
	height: 100%;
}

.text-color {
	color: var(--primary-color);
}

.bg-color {
	background-color: var(--primary-color);
}

:deep(.tab-bar-placeholder) {
	display: none !important;
}

:deep(.u-tabbar__placeholder) {
	display: none !important;
}

/*  #ifdef  H5  */
.scroll-height {
	height: calc(100vh - 184rpx - 50px - env(safe-area-inset-bottom));
	height: calc(100vh - 184rpx - 50px - constant(safe-area-inset-bottom));
}

/*  #endif  */
/*  #ifndef  H5  */
.scroll-height {
	height: calc(100vh - 284rpx - env(safe-area-inset-bottom));
	height: calc(100vh - 284rpx - constant(safe-area-inset-bottom));
}

/*  #endif  */

.text-ellipsis{
    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 2;
    overflow: hidden;
}
</style>
