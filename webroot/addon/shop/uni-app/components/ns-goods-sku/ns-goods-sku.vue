<template>
	<view @touchmove.prevent.stop>
		<u-overlay :show="goodsSkuPop" @click="closeFn" zIndex="490">
			<u-popup class="popup-type" :show="goodsSkuPop" @close="closeFn" mode="bottom" :overlay="false" zIndex="500">
				<view class="py-[32rpx] relative" v-if="goodsDetail.detail" @touchmove.prevent.stop>
					<view class="flex px-[32rpx] mb-[40rpx]">

						<view class="rounded-[var(--goods-rounded-big)] overflow-hidden w-[180rpx] h-[180rpx]">
							<u--image width="180rpx" height="180rpx" :src="img(goodsDetail.detail.sku_image)" @click="imgListPreview(goodsDetail.detail.sku_image)" model="aspectFill">
								<template #error>
									<image class="w-[180rpx] h-[180rpx]" :src="img('static/resource/images/diy/shop_default.jpg')" mode="aspectFill"></image>
								</template>
							</u--image>
						</view>
						<view class="flex flex-1 flex-col justify-between ml-[24rpx] py-[10rpx]">
							<view class="w-[100%]">
								<view class="text-[var(--price-text-color)] flex items-baseline">
									<text class="text-[32rpx] font-bold price-font">￥</text>
									<text class="text-[48rpx] price-font">{{ parseFloat(goodsPrice).toFixed(2).split('.')[0] }}</text>
									<text class="text-[32rpx] mr-[6rpx] price-font">.{{ parseFloat(goodsPrice).toFixed(2).split('.')[1] }}</text>
								</view>
								<view class="text-[26rpx] leading-[32rpx] text-[var(--text-color-light6)] mt-[12rpx]">库存{{goodsDetail.detail.stock}}{{ goodsDetail.goods.unit }}</view>
							</view>
							<view class="text-[26rpx] leading-[30rpx] text-[var(--text-color-light6)] w-[100%] max-h-[60rpx] multi-hidden" v-if="goodsDetail.goods_spec && goodsDetail.goods_spec.length">已选规格：{{goodsDetail.detail.sku_spec_format}}</view>
	<!-- 						<view v-if="goodsDetail.goods_spec && goodsDetail.goods_spec.length">
								<text>已选规格：{{goodsDetail.detail.sku_spec_format}}</text>
							</view> -->
						</view>
					</view>
					<scroll-view class="h-[500rpx] px-[32rpx] box-border mb-[60rpx]" scroll-y="true">
						<view :class="{'mt-[20rpx]': 0 != index }" v-for="(item,index) in goodsDetail.goods_spec" :key="index">
							<view class="text-[28rpx] leading-[36rpx] mb-[24rpx]">{{item.spec_name}}</view>
							<view class="flex flex-wrap">
								<view class="box-border bg-[var(--temp-bg)] text-[24rpx] px-[44rpx] text-center h-[56rpx] flex-center mr-[20rpx] mb-[20rpx] border-1 border-solid rounded-[50rpx] border-[var(--temp-bg)]"
								 :class="{'!border-[var(--primary-color)] text-[var(--primary-color)] !bg-[var(--primary-color-light)]': subItem.selected}"
								v-for="(subItem,subIndex) in item.values" :key="subIndex" @click="change(subItem, index)">
									{{subItem.name}}
								</view>
							</view>
						</view>
						<view class="flex justify-between items-center mt-[20rpx]">
							<view class="text-[28rpx]">购买数量</view>
							<u-number-box :min="1" :max="goodsDetail.stock" integer :step="1" input-width="68rpx" v-model="buyNum" input-height="52rpx">
								<template #minus>
									<view class="relative w-[30rpx] h-[30rpx]">
										<text class="text-[30rpx] nc-iconfont nc-icon-jianV6xx font-500 absolute flex items-center justify-center -left-[8rpx] -bottom-[8rpx] -right-[8rpx] -top-[8rpx]" :class="{ '!text-[var(--text-color-light9)]': buyNum <= 1 }"></text>
									</view>
								</template>
								<template #input>
									<input class="text-[#303133] text-[28rpx] mx-[10rpx] w-[80rpx] h-[44rpx] bg-[var(--temp-bg)] leading-[44rpx] text-center rounded-[6rpx]" type="number" @input="goodsSkuInputFn" @blur="goodsSkuBlurFn" v-model="buyNum"  />
								</template>
								<template #plus>
									<view class="relative w-[30rpx] h-[30rpx]">
										<text class="text-[30rpx] nc-iconfont nc-icon-jiahaoV6xx font-500 absolute flex items-center justify-center -left-[8rpx] -bottom-[8rpx] -right-[8rpx] -top-[8rpx]" :class="{ '!text-[var(--text-color-light9)]': buyNum >= goodsDetail.stock }"></text>
									</view>
								</template>
							</u-number-box>
						</view>
					</scroll-view>
					<view class="px-[20rpx]">

						<!-- #ifdef H5 -->
						<button v-if="goodsDetail.detail.stock > 0" hover-class="none" class="!h-[80rpx] leading-[80rpx] text-[26rpx] font-500 rounded-[50rpx] primary-btn-bg" type="primary" @click="confirm">确定</button>
						<!-- #endif -->

						<!-- #ifdef MP-WEIXIN -->
						<template v-if="goodsDetail.detail.stock > 0">
							<button v-if="isBindMobile && userInfo && !userInfo.mobile" hover-class="none" class="!h-[80rpx] leading-[80rpx] text-[26rpx] font-500 rounded-[50rpx] primary-btn-bg" type="primary" open-type="getPhoneNumber" @getphonenumber="memberStore.bindMobile">确定</button>
							<button v-else hover-class="none" class="!h-[80rpx] leading-[80rpx] text-[26rpx] font-500 rounded-[50rpx] primary-btn-bg" type="primary" @click="confirm">确定</button>
						</template>
						<!-- #endif -->

						<button hover-class="none" v-else class="!h-[80rpx] leading-[80rpx] text-[26rpx] font-500 text-[#fff] bg-[#ccc] rounded-[50rpx]">已售罄</button>
					</view>
				</view>
			</u-popup>
		</u-overlay>
		 <!-- 强制绑定手机号 -->
		<bind-mobile ref="bindMobileRef" /> 
	</view>
</template>

<script setup lang="ts">
	import { ref, computed, toRaw } from 'vue';
	import { img, redirect, getToken } from '@/utils/common'
	import useCartStore from '@/addon/shop/stores/cart'
	import { useLogin } from '@/hooks/useLogin'
	import useMemberStore from '@/stores/member'
	import bindMobile from '@/components/bind-mobile/bind-mobile.vue';
	import { cloneDeep } from 'lodash-es'
	import { t } from '@/locale'

	const props = defineProps(['goodsDetail']);
	const goodsSkuPop = ref(false);
	const callback:any = ref(null);
	const currSpec = ref({
		skuId: "",
		name: []
	})
	const openType = ref("");
	const buyNum = ref(1)
	
	// 商品价格
	const goodsPrice = computed(() => {
        let price = "0.00";
        if (Object.keys(goodsDetail.value).length && Object.keys(goodsDetail.value.goods).length && goodsDetail.value.goods.is_discount && goodsDetail.value.sale_price != goodsDetail.value.price) {
            price = goodsDetail.value.sale_price // 折扣价
        } else if (Object.keys(goodsDetail.value).length && Object.keys(goodsDetail.value.goods).length && goodsDetail.value.goods.member_discount && getToken() && goodsDetail.value.member_price != goodsDetail.value.price) {
            price = goodsDetail.value.member_price // 会员价
        } else {
            price = goodsDetail.value.price
        }
        return price;
    })

	// 会员信息
	const memberStore = useMemberStore()
	const userInfo = computed(() => memberStore.info)

	// 购物车数量
	const cartStore = useCartStore();
	cartStore.getList();
	const cartList = computed(() => cartStore.cartList)

	const open = (type="",fn = "")=>{
		openType.value = type;
		goodsSkuPop.value = true;
		callback.value = fn;
	}
	
	const goodsSkuInputFn = ()=>{
		setTimeout(() => {
			if(buyNum.value >= goodsDetail.value.stock){
				buyNum.value = goodsDetail.value.stock;
			}
		},0)
	}
	const goodsSkuBlurFn = ()=>{
		setTimeout(() => {
			if(!buyNum.value || buyNum.value <= 0 ){
				buyNum.value = 1;
			}
			if(buyNum.value >= goodsDetail.value.stock){
				buyNum.value = goodsDetail.value.stock;
			}
		},0)
	}

	const closeFn = ()=>{
		goodsSkuPop.value = false
	}

	const goodsDetail = computed(() => {
		let data = cloneDeep(props.goodsDetail);

		// 重组数据结构
		if(Object.keys(data).length){

			if(!Object.keys(currSpec.value.name).length) currSpec.value.name = data.sku_spec_format.split(",");

			data.goods_spec.forEach((item: any,index: any)=>{
				let specName = item.spec_values.split(",");
				item.values = [];
				specName.forEach((specItem: any, specIndex: any)=>{
					item.values[specIndex] = {};
					item.values[specIndex].name = specItem;
					item.values[specIndex].selected = false;
					item.values[specIndex].disabled = false;

					// 选中规格
					currSpec.value.name.forEach((currSpecItem, currSpecIndex)=>{
						if(currSpecIndex == index && currSpecItem == specItem){
							item.values[specIndex].selected = true;
						}
					})
				})
			})
			getSkuId();

			// 当前详情内容
			if(data.sku_list && Object.keys(data.sku_list).length){
				data.sku_list.forEach((idItem: any, idIndex: any)=>{
					if(idItem.sku_id == currSpec.value.skuId){
						data.detail = idItem;
					}
				})
			}
		}
		return data;
	})

	const change = (data: any, index: any)=>{
		currSpec.value.name[index] = data.name;
		buyNum.value = 1
		getSkuId();
	}

	const emits = defineEmits(['change'])
	const getSkuId = ()=>{
		props.goodsDetail.sku_list.forEach((skuItem: any, skuIndex: any)=>{
			if(skuItem.sku_spec_format == currSpec.value.name.toString()){
				currSpec.value.skuId = skuItem.sku_id
				emits('change',skuItem.sku_id)
			}
		})
	}

	//强制绑定手机号
	const bindMobileRef: any = ref(null)
	const isBindMobile = ref(uni.getStorageSync('isbindmobile'))

	//提交
	const confirm = ()=> {
        // 检测是否登录
        if (!userInfo.value) {
            useLogin().setLoginBack({
                url: '/addon/shop/pages/goods/detail',
                param: { sku_id: goodsDetail.value.sku_id }
            })
            return false
        }

        // #ifdef H5
        // 绑定手机号
        if (uni.getStorageSync('isbindmobile')) {
            bindMobileRef.value.open()
            return false
        }
        // #endif

        // 加入购物车
        if (openType.value == 'join_cart') {
            let num = 0;
            let cartId = "";

            if (cartList.value['goods_' + goodsDetail.value.goods_id] && cartList.value['goods_' + goodsDetail.value.goods_id]['sku_' + goodsDetail.value.sku_id]) {
                num = toRaw(cartList.value['goods_' + goodsDetail.value.goods_id]['sku_' + goodsDetail.value.sku_id].num);
                cartId = toRaw(cartList.value['goods_' + goodsDetail.value.goods_id]['sku_' + goodsDetail.value.sku_id].id)
            }

            num += Number(buyNum.value);
            cartStore.increase({
                id: cartId || '',
                goods_id: goodsDetail.value.goods_id,
                sku_id: goodsDetail.value.sku_id,
                stock: goodsDetail.value.stock,
                sale_price: goodsDetail.value.sale_price,
                num: num
            }, 0, () => {
                uni.showToast({
                    title: '加入购物车成功',
                    icon: 'none'
                });
            });

        } else if (openType.value == 'buy_now') {
            // 立即购买
            var data = {
                sku_id: goodsDetail.value.sku_id,
                num: buyNum.value
            };

            uni.setStorage({
                key: 'orderCreateData',
                data: {
                    sku_data: [
                        data
                    ]
                },
                success: () => {
                    redirect({ url: '/addon/shop/pages/order/payment' })
                }
            });
        }

        closeFn();
    }

	//预览图片
	const imgListPreview = (item: any) => {
		if (item === '') return false
		var urlList = []
		urlList.push(img(item))  //push中的参数为 :src="item.img_url" 中的图片地址
		uni.previewImage({
			indicator: "number",
			loop: true,
			urls: urlList
		})
	}
	defineExpose({
		open
	})
</script>

<style lang="scss" scoped>
	::v-deep .u-number-box .u-number-box__slot{
		display: flex;
		align-items: center;
	}
</style>
