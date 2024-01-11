<template>
    <u-popup :show="show" @close="show = false" mode="bottom" :round="10" :closeable="true">
        <view class="text-center p-[30rpx]">请选择自提点</view>
        <scroll-view scroll-y="true" class="h-[50vh]">
            <view class="p-[30rpx] pt-0 text-sm">
                <view class="mt-[30rpx] p-[30rpx] border-1 !border-[#eee] border-solid rounded-[20rpx]"
                    :class="{'!border-primary bg-primary-light': store && store.store_id == item.store_id}"
                    v-for="item in storeList"
                    @click="selectStore(item)">
                    <view class="font-bold flex">
                        <view class="flex-1 w-0">
                            <text>{{ item.store_name }}</text>
                            <text class="text-[26rpx] ml-[20rpx]">{{ item.store_mobile }}</text>
                        </view>
                        <view v-if="item.distance">
                            <text class="text-red text-xs font-normal">{{ distanceFormat(item.distance) }}</text>
                        </view>
                    </view>
                    <view class="mt-[16rpx] text-[26rpx]">{{ item.full_address }}</view>
                    <view class="mt-[16rpx] text-[26rpx]">营业时间：{{ item.trade_time }}</view>
                </view>
            </view>
            <view class="h-[50vh] flex items-center flex-col justify-center" v-if="loading">
                <u-loading-icon :vertical="true" ></u-loading-icon>
            </view>
            <view class="h-[50vh] flex items-center flex-col justify-center" v-if="!loading && !storeList.length">
                <u-empty text="没有可选择的自提点" :icon="img('static/resource/images/empty.png')"/>
            </view>
        </scroll-view>
        <view class="p-[30rpx]">
            <u-button type="primary" shape="circle" @click="confirm">确认</u-button>
        </view>
    </u-popup>
</template>

<script setup lang="ts">
    import { ref } from 'vue'
    import { getStoreList } from '@/addon/shop/api/order'
    import { img } from '@/utils/common'

    const show = ref(false)
    const loaded = ref(false)
    const loading = ref(true)
    const storeList = ref<object[]>([])
    const store = ref<null | object>(null)

    const open = ()=> {
        if (!loaded.value) {
            loaded.value = true

            const latlng = {}
            uni.getLocation({
            	success: (res) => {
                    latlng.lat = res.latitude
                    latlng.lng = res.longitude
            	}
            });

            setTimeout(() => {
                getStoreList({ latlng })
                .then(({ data }) => {
                    storeList.value = data
                    loading.value = false
                })
                .catch(() => {
                    loading.value = false
                })
            }, 1500)
        }
        show.value = true
    }

    const selectStore = (data: object)=> {
        if (store.value) {
            store.value = store.value.store_id != data.store_id ? data : null
        } else {
            store.value = data
        }
    }

    const emits = defineEmits(['confirm'])

    const confirm = ()=> {
        emits('confirm', store.value)
        show.value = false
    }

    const distanceFormat = (distance: number | string) => {
        distance = parseFloat(distance)
        if (distance < 1000) {
            return `${distance}m`;
        } else {
            const km = (distance / 1000).toFixed(2)
            return `${km}km`;
        }
    }

    defineExpose({
    	open
    })
</script>

<style lang="scss" scoped></style>
