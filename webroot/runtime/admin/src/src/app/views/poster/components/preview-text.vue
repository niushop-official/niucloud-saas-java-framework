<template>
	<div class="overflow-hidden" :style="componentStyle">{{ data.value }}</div>
</template>

<script lang="ts" setup>
import { ref,computed } from 'vue'

const prop = defineProps({
    value: {
        type: Object,
        default: {}
    }
})

const data = computed(()=> {
    return prop.value;
})

const componentStyle = computed(()=> {
    var style = '';
    style += `font-size: ${prop.value.fontSize}px;color: ${prop.value.fontColor};`;
    let box: any = document.getElementById(prop.value.id)
    if (box) {
        style += `width:${box.offsetWidth}px;height:${box.offsetHeight}px;`;
    }
    return style;
})

defineExpose({})

</script>

<style lang="scss" scoped></style>
