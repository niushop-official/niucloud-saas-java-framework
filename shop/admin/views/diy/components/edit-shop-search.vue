<template>
	<!-- 内容 -->
	<div class="content-wrap" v-show="diyStore.editTab == 'content'">

	</div>

	<!-- 样式 -->
	<div class="style-wrap" v-show="diyStore.editTab == 'style'">
		<!-- 组件样式 -->
		<slot name="style"></slot>
	</div>

</template>

<script lang="ts" setup>
import useDiyStore from '@/stores/modules/diy'

const diyStore = useDiyStore()
diyStore.editComponent.ignore = [] // 忽略公共属性

// const selectStyle = ref(diyStore.editComponent.style)

// const changeStyle = () => {
//     switch (selectStyle.value) {
//         case 'style-1':
//             diyStore.editComponent.subTitle.control = false
//             diyStore.editComponent.more.control = false
//             diyStore.editComponent.styleName = '风格1'
//             break
//         case 'style-2':
//             diyStore.editComponent.subTitle.control = true
//             diyStore.editComponent.more.control = true
// 		    diyStore.editComponent.styleName = '风格2'
//             break
//     }
//     diyStore.editComponent.style = selectStyle.value
// }

defineExpose({})
</script>

<style lang="scss" scoped></style>
