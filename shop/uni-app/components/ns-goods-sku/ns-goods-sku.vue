<template>
	<view @touchmove.prevent.stop>
		<u-popup :show="goodsSkuPop" @close="closeFn" mode="bottom">
			<view class="rounded-t-[20rpx] overflow-hidden bg-[#fff] p-[32rpx] relative">
				<view class="absolute right-[37rpx]  iconfont iconguanbi text-[50rpx]" @click="closeFn"></view>
				<view class="flex mb-[58rpx]">

					<view class="rounded-[8rpx] overflow-hidden">
						<u--image width="204rpx" height="204rpx" :src="img(goodsDetail.detail.sku_image)" model="aspectFill">
							<template #error>
								<u-icon name="photo" color="#999" size="50"></u-icon>
							</template>
						</u--image>
					</view>
					<view class="flex flex-1 flex-col justify-between ml-[20rpx]">
						<view class="w-[100%]">
							<view class=" text-[var(--price-text-color)]">
								<text class="text-[28rpx] font-bold">￥</text><text
									class="text-[42rpx] mr-[10rpx]  font-bold">{{goodsDetail.detail.sale_price}}</text>
							</view>
							<view class="text-[24rpx] leading-[32rpx] text-[#666] mt-[12rpx]">库存{{goodsDetail.detail.stock}}{{ goodsDetail.goods.unit }}</view>
						</view>
						<view class="w-[100%]"  style="max-height: calc(204rpx - 126rpx); overflow: hidden;"  v-if="goodsDetail.goodsSpec && goodsDetail.goodsSpec.length">
							<text class="text-[24rpx] leading-[32rpx] text-[#666]">已选规格：{{goodsDetail.detail.sku_spec_format}}</text>
						</view>
					</view>
				</view>
				<scroll-view class="h-[500rpx]" scroll-y="true">
					<view class="mb-[20rpx]" v-for="(item,index) in goodsDetail.goodsSpec" :key="index">
						<view class="text-[26rpx] leading-[36rpx] mb-[30rpx]">{{item.spec_name}}</view>
						<view class="flex flex-wrap">
							<view
							class="box-border  min-w-[96rpx] text-[24rpx] px-[15rpx] text-center h-[52rpx] leading-[52rpx] mr-[20rpx] mb-[20rpx] border-1 border-solid rounded-[8rpx] border-[#888]"
							 :class="{'!border-[var(--primary-color)] text-[var(--primary-color)] bg-[var(--primary-color-light)]': subItem.selected}"
							v-for="(subItem,subIndex) in item.values" :key="subIndex"
							@click="change(subItem, index)">
								{{subItem.name}}
							</view>
						</view>
					</view>
					<view class="flex justify-between">
						<view class="text-[26rpx] leading-[36rpx] mb-[30rpx]">购买数量</view>
						<u-number-box :min="1" :max="goodsDetail.stock" integer :step="1" input-width="98rpx" v-model="buyNum" input-height="54rpx">
							<template #minus>
								<text class="text-[44rpx] iconfont iconjianhao text-[var(--primary-color)]"
									:class="{ '!text-[#c8c9cc]': buyNum === 1 }"></text>
							</template>
							<template #input>
								<text class="text-[#333] fext-[23rpx] font-500 mx-[16rpx]">{{ buyNum }}</text>
							</template>
							<template #plus>

									<text class="text-[44rpx] iconfont iconjiahao2fill text-[var(--primary-color)]"
									:class="{ '!text-[#c8c9cc]': buyNum === goodsDetail.stock }"></text>
							</template>
						</u-number-box>
					</view>
				</scroll-view>
				<u-button class="!h-[80rpx] !text-[30rpx] !m-0 !mt-[30rpx]" type="primary" shape="circle" @click="confirm">确定</u-button>
			</view>
		</u-popup>
	</view>
</template>

<script setup lang="ts">
	import { ref, reactive, computed, watch, toRaw } from 'vue';
	import { img, redirect } from '@/utils/common'
	import useCartStore from '@/addon/shop/stores/cart'
	import { useLogin } from '@/hooks/useLogin'
	import useMemberStore from '@/stores/member'

	const props = defineProps(['goodsDetail']);
	let goodsSkuPop = ref(false);
	let callback:any = ref(null);
	let currSpec = ref({
		skuId: "",
		name: []
	})
	let openType = ref("");
	let buyNum = ref(1)

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
	const closeFn = ()=>{
		goodsSkuPop.value = false
	}

	const goodsDetail = computed(() => {
		let data = JSON.parse(JSON.stringify(props.goodsDetail));

		// 重组数据结构
		if(Object.keys(data).length){

			if(!Object.keys(currSpec.value.name).length) currSpec.value.name = data.sku_spec_format.split(",");

			data.goodsSpec.forEach((item,index)=>{
				let specName = item.spec_values.split(",");
				item.values = [];
				specName.forEach((specItem, specIndex)=>{
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
			if(data.skuList && Object.keys(data.skuList).length){
				data.skuList.forEach((idItem, idIndex)=>{
					if(idItem.sku_id == currSpec.value.skuId){
						data.detail = idItem;
					}
				})
			}
		}
		return data;
	})

	const change = (data, index)=>{
		currSpec.value.name[index] = data.name;
		getSkuId();
	}

	const emits = defineEmits(['change'])
	const getSkuId = ()=>{
		props.goodsDetail.skuList.forEach((skuItem, skuIndex)=>{
			if(skuItem.sku_spec_format == currSpec.value.name.toString()){
				currSpec.value.skuId = skuItem.sku_id
				emits('change',skuItem.sku_id)
			}
		})
	}

	//提交
	const confirm = ()=>{
		// 检测是否登录
		if (!userInfo.value) {
			useLogin().setLoginBack({ url: '/addon/shop/pages/goods/detail', param: {sku_id: goodsDetail.value.sku_id} })
			return false
		}

		if(openType.value == 'join_cart'){ // 加入购物车
			let num = 0;
			let cartId = "";

			if(cartList.value['goods_'+goodsDetail.value.goods_id]&&cartList.value['goods_'+goodsDetail.value.goods_id]['sku_'+goodsDetail.value.sku_id]){
				num = toRaw(cartList.value['goods_'+goodsDetail.value.goods_id]['sku_'+goodsDetail.value.sku_id].num);
				cartId = toRaw(cartList.value['goods_'+goodsDetail.value.goods_id]['sku_'+goodsDetail.value.sku_id].id)
			}

			num += buyNum.value;

			cartStore.increase({
				id: cartId||'',
				goods_id: goodsDetail.value.goods_id,
				sku_id: goodsDetail.value.sku_id,
				stock: goodsDetail.value.stock,
				sale_price: goodsDetail.value.sale_price,
				num: num
			},0,()=>{
				uni.showToast({
					title: '加入购物车成功',
					icon: 'none'
				});
			});

		}else if(openType.value == 'buy_now'){ // 立即购买
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
	defineExpose({
		open
	})
</script>

<style lang="scss" scoped>
	@import '@/addon/shop/styles/common.scss';
</style>
