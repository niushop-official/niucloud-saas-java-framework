<template>
	<view>
		<category-template-one-one class="category" v-if="config.level===1&&config.template === 'style-1'" :config="config" />
		<category-template-two-one v-if="config.level===2&&config.template === 'style-1'" :config="config" />
		<category-template-two-two class="category" v-if="config.level===2&&config.template === 'style-2'" :config="config" />
	</view>
</template>
<script setup lang="ts">
import { onLoad, onShow } from '@dcloudio/uni-app'
import { ref, computed } from 'vue';
import categoryTemplateTwoOne from '@/addon/shop/pages/goods/components/category-template-two-one.vue';
import categoryTemplateOneOne from '@/addon/shop/pages/goods/components/category-template-one-one.vue';
import categoryTemplateTwoTwo from '@/addon/shop/pages/goods/components/category-template-two-two.vue';
import { getGoodsCategoryConfig } from '@/addon/shop/api/goods';
import useCartStore from '@/addon/shop/stores/cart'

const cartStore = useCartStore();

const config = ref({})
const getGoodsCategoryConfigFn = () => {
	getGoodsCategoryConfig().then(res => {
		config.value = res.data
	})
}

onLoad((option) => {
	getGoodsCategoryConfigFn()
});

onShow(() => {
	// 查询购物车列表
	cartStore.getList();
})

</script>
<style>
@import '@/addon/shop/styles/common.scss';
/*  #ifdef  H5  */
:deep(.category .detail .mescroll-bod)y {
	padding-bottom: 50px !important;
}

:deep(.category .cart .mescroll-body){
	padding-bottom: calc(98rpx + 50px) !important;
}
/*  #endif  */
/*  #ifndef  H5  */
.category .detail .mescroll-body {
	padding-bottom: calc(100rpx + env(safe-area-inset-bottom)) !important;
}

.category .cart .mescroll-body {
	padding-bottom: calc(198rpx + env(safe-area-inset-bottom)) !important;
}
/*  #endif  */
.category .labelPopup :deep(.u-transition) {
	top: 92rpx !important;
	left: 182rpx !important;
	z-index: 8 !important;
}
.category .labelPopup.active :deep(.u-transition) {
	top: 198rpx !important;
}
</style>
<style lang="scss" scoped>
:deep(.mescroll-upwarp) {
	box-sizing: border-box;
	padding-left: 182rpx;
}

:deep(.tab-bar-placeholder) {
	display: none !important;
}

:deep(.u-tabbar__placeholder) {
	display: none !important;
}
button::after{
	border: 0 !important;

}
</style>
