<template>
	<view :style="themeColor()">
		<view class="flex items-center px-[20rpx] h-[120rpx]">
			<view class="h-[68rpx] bg-[var(--temp-bg)] px-[30rpx] flex items-center rounded-[100rpx] flex-1">	
				<text class="nc-iconfont nc-icon-sousuo-duanV6xx1 text-[var(--text-color-light9)] text-[26rpx] mr-[18rpx]"></text>
				<input class="text-[28rpx] flex-1" maxlength="50" type="text" v-model="inputValue"  placeholder="请搜索您想要的商品" confirm-type="search" placeholderClass="text-[var(--text-color-light9)] text-[28rpx]" @confirm="search">
				<text v-if="inputValue" class="nc-iconfont nc-icon-cuohaoV6xx1 text-[24rpx] text-[var(--text-color-light9)]" @click="inputValue=''"></text>
			</view>
			<text @click.stop="search()" class="text-[28rpx] ml-[32rpx] -mb-[2rpx]">搜索</text>
		</view>

		<view class="search-content">
			<!-- 历史搜索 -->
			<view class="history" v-if="historyList.length">
				<view class="history-box">
					<view class="history-top">
						<view class="title font-500">历史搜索</view>
						<view class="icon nc-iconfont nc-icon-shanchu-yuangaizhiV6xx !text-[24rpx] text-[var(--text-color-light6)]" @click="deleteHistoryList"></view>
					</view>
					<view class="history-bottom " id="history-list" :style="{ maxHeight: !isAllHistory ? '100%' : '168rpx' }">
						<view class="history-li" v-for="(item, index) in historyList" :key="index" @click="otherSearch(item)">
							<view>{{ item }}</view>
						</view>
						<view class="history-li history_more" v-if="isAllHistory" @click="isAllHistory = false">
							<view>
								<text class="text-[30rpx] nc-iconfont nc-icon-xiaV6xx"></text>
							</view>
						</view>
					</view>
				</view>
			</view>
		</view>

	</view>
</template>
<script setup lang="ts">
import { onLoad, onShow } from '@dcloudio/uni-app'
import { ref, nextTick } from 'vue';
import { redirect } from '@/utils/common';
import useConfigStore from "@/stores/config";

const inputValue = ref('') //搜索框的值
const historyList = ref([]) //历史搜索记录
const isAllHistory = ref(false)

onLoad((options:any) => {
    uni.getStorageSync('goodsSearchHistory') ? '' : uni.setStorageSync('goodsSearchHistory', []);
});

onShow(() => {
    findHistoryList()
    nextTick(()=> {
        getHistoryHeight();
    });
})

//获取历史搜索记录
const findHistoryList =()=> {
    historyList.value = uni.getStorageSync('goodsSearchHistory').reverse();
}

// 删除所有历史记录
const deleteHistoryList =()=> {
    uni.showModal({
        title: '提示',
        content: '确认删除全部历史记录？',
        confirmColor: useConfigStore().themeColor['--primary-color'],
        success: res => {
            if (res.confirm) {
                uni.setStorageSync('goodsSearchHistory', []);
                findHistoryList();
            }
        }
    });
}

//搜索
const search = ()=> {
    if (inputValue.value.trim() != '') {

        // 对历史搜索处理,判断有无,最近搜索显示在最前
        let historyList = uni.getStorageSync('goodsSearchHistory');
        let array = [];
        if (historyList.length) {
            array = historyList.filter(v => {
                return v != inputValue.value.trim();
            });
            array.push(inputValue.value.trim());
        } else {
            array.push(inputValue.value.trim());
        }
        uni.setStorageSync('goodsSearchHistory', array);

        redirect({ url: '/addon/shop/pages/goods/list', param: { goods_name: encodeURIComponent(inputValue.value) }, mode: 'navigateTo' })
    }
}

// 点击历史搜索
const otherSearch = (e:any)=> {
    inputValue.value = e;
    search();
}

// 获取元素高度
const getHistoryHeight =()=> {
    const query = uni.createSelectorQuery().in(this);
    query.select('#history-list').boundingClientRect((data: any) => {
        if (data && data.height > uni.upx2px(70) * 2 + uni.upx2px(35) * 2) {
            isAllHistory.value = true;
        }
    }).exec();
}

</script>
<style lang="scss" scoped>
	.content {
		width: 100vw;
		/* #ifdef MP */
		height: 100vh;
		/* #endif */
		/* #ifdef H5 */
		height: calc(100vh - env(safe-area-inset-bottom) - var(--status-bar-height));
		height: calc(100vh - constant(safe-area-inset-bottom) - var(--status-bar-height));
		/* #endif */
		/* #ifdef APP-PLUS */
		height: calc(100vh - 44px - env(safe-area-inset-bottom));
		height: calc(100vh - 44px - constant(safe-area-inset-bottom));
		/* #endif */
		background: #ffffff;
	}

	.search-content {
		box-sizing: border-box;
		background: #ffffff;
	}

	.history {
		width: 100%;
		box-sizing: border-box;

		.history-box {
			width: 100%;
			height: 100%;
			background: #ffffff;
			padding: 16rpx 20rpx 0rpx 20rpx;

			box-sizing: border-box;
			overflow: hidden;

			.history-top {
				width: 100%;
				display: flex;
				justify-content: space-between;
				align-items: center;

				.title {
					font-size: 28rpx;
				}

				.iconfont {
					color: var(--text-color-light9);
					font-size: 28rpx;
				}
			}

			.history-bottom {
				width: 100%;
				padding-top: 20rpx;
				position: relative;

				.history-li {
					display: inline-block;
					margin-right: 20rpx;
					margin-bottom: 15rpx;
					max-width: 100%;

					view {
						line-height: 56rpx;
						background: var(--temp-bg) !important;
						height: 56rpx;
						color: #333 !important;
						margin: 0 0rpx 4rpx 0 !important;
						padding: 0 24rpx;
						overflow: hidden;
						white-space: nowrap;
						text-overflow: ellipsis;
						border-radius: 100rpx;
						font-size: 24rpx;
					}

					&.history_more {
						margin-right: 0;
						position: absolute;
						bottom: 0;
						right: 0;
					}
				}
			}
		}
	}

</style>
