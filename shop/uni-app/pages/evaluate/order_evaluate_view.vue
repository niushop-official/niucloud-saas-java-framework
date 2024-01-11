<template>
     <view class="bg-[#f8f8f8] min-h-screen">
          <view class="px-[24rpx] py-[20rpx]">
               <template v-for="(item, index)  in info" :key="index">
                    <view class="bg-white py-[20rpx] px-[24rpx] mb-[20rpx] rounded-[16rpx]">
                         <view class="flex mb-[20rpx]">
                              <u--image class="rounded-[10rpx] overflow-hidden" width="200rpx" height="200rpx"
                                   :src="img(item.order_goods.goods_image_thumb_mid ? item.order_goods.goods_image_thumb_mid : '')"
                                   model="aspectFill">
                                   <template #error>
                                        <u-icon name="photo" color="#999" size="50"></u-icon>
                                   </template>
                              </u--image>
                              <view class="flex-1 flex flex-wrap ml-[20rpx]">
                                   <view>
                                        <view
                                             class="text-[26rpx] font-500 h-[80rpx] leading-[40rpx]  multi-hidden mb-[10rpx]">
                                             {{ item.order_goods.goods_name }}
                                        </view>
                                        <view
                                             class="w-[404rpx] mt-[12rpx] truncate text-[#888] text-[24rpx] leading-[32rpx] font-500">
                                             {{ item.order_goods.sku_name }}
                                        </view>
                                   </view>
                                   <view class="mt-auto flex  self-end justify-between w-[100%]">
                                        <view class="flex flex-col">
                                             <text class="text-[28rpx] text-[var(--price-text-color)] price-font">￥{{ item.order_goods.price
                                             }}</text>

                                        </view>
                                        <text class="text--[24rpx] text-[#666]">x{{ item.order_goods.num }}</text>
                                   </view>
                              </view>
                         </view>
                         <view class="pt-[20rpx] flex items-center border-0 border-t-[2rpx] border-solid border-[#ebebec]">
                              <u-rate :count="5" v-model="item.scores" active-color="red" :size="'40rpx'" readonly></u-rate>
                              <text class="ml-[60rpx] text-[26rpx] text-[#888]">{{ item.scores === 1 ? '差评' :
                                   item.scores ===
                                        2 ||
                                        item.scores === 3 ? '中评' : '好评' }}</text>
                         </view>
                         <view class=" text-[28rpx] text-[#888] my-[20rpx] overflow-clip">{{ item.content }}</view>
                         <template v-if="item.image_mid.length === 1">
                            <u--image class="rounded-[8rpx] overflow-hidden mt-[10rpx]" width="420rpx" height="420rpx"
                                :src="img(item.image_mid[0])" model="aspectFill"
                                @click="imgListPreview(item.image_mid[0])">
                                <template #error>
                                    <u-icon name="photo" color="#999" size="50"></u-icon>
                                </template>
                            </u--image>
                        </template>
                        <template v-if="item.image_mid.length === 2">
                            <view class="flex justify-between mt-[10rpx]">
                                <u--image class="rounded-[8rpx] overflow-hidden" width="322rpx" height="322rpx"
                                    :src="img(item.image_mid[0])" model="aspectFill"
                                    @click="imgListPreview(item.image_mid[0])">
                                    <template #error>
                                        <u-icon name="photo" color="#999" size="50"></u-icon>
                                    </template>
                                </u--image>
                                <u--image class="rounded-[8rpx] overflow-hidden" width="322rpx" height="322rpx"
                                    :src="img(item.image_mid[1])" model="aspectFill"
                                    @click="imgListPreview(item.image_mid[1])">
                                    <template #error>
                                        <u-icon name="photo" color="#999" size="50"></u-icon>
                                    </template>
                                </u--image>
                            </view>
                        </template>
                        <template v-if="item.image_mid.length === 3">
                            <view class="flex justify-between mt-[10rpx]">
                                <u--image class="rounded-[8rpx] overflow-hidden" width="430rpx" height="430rpx"
                                    :src="img(item.image_mid[0])" model="aspectFill"
                                    @click="imgListPreview(item.image_mid[0])">
                                    <template #error>
                                        <u-icon name="photo" color="#999" size="50"></u-icon>
                                    </template>
                                </u--image>
                                <view>
                                    <u--image class="rounded-[8rpx] overflow-hidden mb-[20rpx]" width="205rpx"
                                        height="205rpx" :src="img(item.image_mid[1])" model="aspectFill"
                                        @click="imgListPreview(item.image_mid[1])">
                                        <template #error>
                                            <u-icon name="photo" color="#999" size="50"></u-icon>
                                        </template>
                                    </u--image>
                                    <u--image class="rounded-[8rpx] overflow-hidden" width="205rpx" height="205rpx"
                                        :src="img(item.image_mid[2])" model="aspectFill"
                                        @click="imgListPreview(item.image_mid[2])">
                                        <template #error>
                                            <u-icon name="photo" color="#999" size="50"></u-icon>
                                        </template>
                                    </u--image>
                                </view>
                            </view>
                        </template>
                        <template v-if="item.image_mid.length === 4">
                            <view class="flex flex-wrap mt-[10rpx]">
                                <u--image class="rounded-[8rpx] overflow-hidden mr-[15rpx] mb-[15rpx]" width="215rpx"
                                    height="215rpx" :src="img(item.image_mid[0])" model="aspectFill"
                                    @click="imgListPreview(item.image_mid[0])">
                                    <template #error>
                                        <u-icon name="photo" color="#999" size="50"></u-icon>
                                    </template>
                                </u--image>
                                <u--image class="rounded-[8rpx] overflow-hidden mr-[15rpx] mb-[15rpx]" width="215rpx"
                                    height="215rpx" :src="img(item.image_mid[1])" model="aspectFill"
                                    @click="imgListPreview(item.image_mid[1])">
                                    <template #error>
                                        <u-icon name="photo" color="#999" size="50"></u-icon>
                                    </template>
                                </u--image>
                                <u--image class="rounded-[8rpx] overflow-hidden mr-[15rpx]" width="215rpx" height="215rpx"
                                    :src="img(item.image_mid[2])" model="aspectFill"
                                    @click="imgListPreview(item.image_mid[2])">
                                    <template #error>
                                        <u-icon name="photo" color="#999" size="50"></u-icon>
                                    </template>
                                </u--image>
                                <u--image class="rounded-[8rpx] overflow-hidden mr-[15rpx]" width="215rpx" height="215rpx"
                                    :src="img(item.image_mid[3])" model="aspectFill"
                                    @click="imgListPreview(item.image_mid[3])">
                                    <template #error>
                                        <u-icon name="photo" color="#999" size="50"></u-icon>
                                    </template>
                                </u--image>
                            </view>
                        </template>
                        <template v-if="item.image_mid.length > 4">
                            <view class="flex flex-wrap mt-[10rpx]">
                                <u--image v-for="(imageItem, imageIndex) in item.image_mid"
                                    :class="['rounded-[8rpx] overflow-hidden mb-[10rpx]', (imageIndex + 1) % 3 === 0 ? '' : 'mr-[10rpx]']"
                                    width="211rpx" height="211rpx" :src="img(imageItem)" model="aspectFill"
                                    @click="imgListPreview(imageItem)">
                                    <template #error>
                                        <u-icon name="photo" color="#999" size="50"></u-icon>
                                    </template>
                                </u--image>
                            </view>
                        </template>
                        <view v-if="item.explain_first !=''" class="text-[26rpx] !text-[#666] mt-[20rpx] pt-[20rpx] border-0 border-t-[2rpx] border-solid border-[#ebebec] w-[100%] overflow-clip leading-[1.2]">
                            <text class=" text-[var(--primary-color)]">商家回复：</text>{{ item.explain_first }}
                        </view>
                    </view>
               </template>
          </view>
          <u-loading-page bg-color="rgb(248,248,248)" :loading="loading" fontSize="16" color="#333"></u-loading-page>
     </view>
</template>
<script lang="ts" setup>
import { ref } from 'vue';
import { getOrderEvaluate } from '@/addon/shop/api/evaluate';
import { onLoad } from '@dcloudio/uni-app';
import { img } from '@/utils/common';

const info = ref<Array<any>>([])
const loading = ref(false)
onLoad((option) => {
     getOrderEvaluateFn(option.order_id)
})
const getOrderEvaluateFn = (id) => {
     loading.value = true
     getOrderEvaluate(id).then(res => {
          info.value = res.data
          loading.value = false
     }).catch(()=>{
          loading.value = false
     })
}
</script>
<style>
 @import '@/addon/shop/styles/common.scss';
</style>
