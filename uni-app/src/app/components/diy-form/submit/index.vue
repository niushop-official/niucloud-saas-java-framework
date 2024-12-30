<template>
	<view :style="warpCss">
		表单提交按钮
	</view>
</template>

<script setup lang="ts">
	// 标题
	import { ref, computed, watch,onMounted,getCurrentInstance } from 'vue';
	import useDiyFormStore from '@/app/stores/diy_form';
    import { img } from '@/utils/common';

	const props = defineProps(['component', 'index']);
	const diyFormStore = useDiyFormStore();

	const diyComponent = computed(() => {
		if (diyFormStore.mode == 'decorate') {
			return diyFormStore.value[props.index];
		} else {
			return props.component;
		}
	})

	const warpCss = computed(() => {
		var style = '';
        style += 'position:relative;';
        if(diyComponent.value.componentStartBgColor) {
            if (diyComponent.value.componentStartBgColor && diyComponent.value.componentEndBgColor) style += `background:linear-gradient(${diyComponent.value.componentGradientAngle},${diyComponent.value.componentStartBgColor},${diyComponent.value.componentEndBgColor});`;
            else style += 'background-color:' + diyComponent.value.componentStartBgColor + ';';
        }

        if(diyComponent.value.componentBgUrl) {
            style += `background-image:url('${ img(diyComponent.value.componentBgUrl) }');`;
            style += 'background-size: cover;background-repeat: no-repeat;';
        }

		if (diyComponent.value.topRounded) style += 'border-top-left-radius:' + diyComponent.value.topRounded * 2 + 'rpx;';
		if (diyComponent.value.topRounded) style += 'border-top-right-radius:' + diyComponent.value.topRounded * 2 + 'rpx;';
		if (diyComponent.value.bottomRounded) style += 'border-bottom-left-radius:' + diyComponent.value.bottomRounded * 2 + 'rpx;';
		if (diyComponent.value.bottomRounded) style += 'border-bottom-right-radius:' + diyComponent.value.bottomRounded * 2 + 'rpx;';
		return style;
	})

    onMounted(() => {
        refresh();
        // 装修模式下刷新
        if (diyFormStore.mode == 'decorate') {
            watch(
                () => diyComponent.value,
                (newValue, oldValue) => {
                    if (newValue && newValue.componentName == 'Input') {
                        refresh();
                    }
                }
            )
        }
    });

    const instance = getCurrentInstance();

    const refresh = ()=> {
    }
</script>

<style lang="scss" scoped>
</style>
