<template>
	<view :style="themeColor()">

		<loading-page :loading="diyForm.getLoading()"></loading-page>

		<view v-show="!diyForm.getLoading()">

			<!-- 自定义模板渲染 -->
			<view class="diyForm-template-wrap bg-index" :style="diyForm.pageStyle()">

				<diy-group ref="diyGroupRef" :data="diyForm.data" :pullDownRefreshCount="diyForm.pullDownRefreshCount" />

			</view>


		</view>

		<!-- #ifdef MP-WEIXIN -->
		<!-- 小程序隐私协议 -->
		<wx-privacy-popup ref="wxPrivacyPopupRef"></wx-privacy-popup>
		<!-- #endif -->

	</view>
</template>

<script setup lang="ts">
    import {ref,nextTick} from 'vue';
    import {useDiyForm} from '@/hooks/useDiyForm'
    import {useShare} from '@/hooks/useShare'
    import diyGroup from '@/addon/components/diy/group/index.vue'

    const {setShare} = useShare()

    const diyForm = useDiyForm({})

    const diyGroupRef = ref(null)

    const wxPrivacyPopupRef:any = ref(null)

    // 监听页面加载
    diyForm.onLoad();

    // 监听页面显示
    diyForm.onShow((data: any) => {
        let share = data.share ? JSON.parse(data.share) : null;
        setShare(share);
        diyGroupRef.value?.refresh();
	    // #ifdef MP
	    nextTick(()=>{
		    if(wxPrivacyPopupRef.value) wxPrivacyPopupRef.value.proactive();
	    })
	    // #endif
    });

    // 监听页面隐藏
    diyForm.onHide();

	// 监听页面卸载
	diyForm.onUnload();

    // 监听下拉刷新事件
    diyForm.onPullDownRefresh()

    // 监听滚动事件
    diyForm.onPageScroll()
</script>
<style lang="scss" scoped>
	@import '@/styles/diy.scss';
</style>
<style lang="scss">
.diy-template-wrap {
  /* #ifdef MP */
  .child-diy-template-wrap {
    ::v-deep .diy-group {
      > .draggable-element.top-fixed-diy {
        display: block !important;
      }
    }
  }
  /* #endif */
}
</style>
