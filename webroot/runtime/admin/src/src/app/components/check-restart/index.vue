<template>

</template>

<script lang="ts" setup>
import { h, ref } from "vue";
import { checkIsRestart } from "@/app/api/sys"
import { ElNotification } from "element-plus";

const loading = ref(true)
let notificationEl : any = null
/**
 * 服务重启提示
 */
const showElNotification = () => {
    notificationEl = ElNotification.warning({
        title: "提示",
        dangerouslyUseHTMLString: true,
        message: h('div', { class: 'flex' }, [
            h('span', { class: 'text-secondary' }, ["服务正在重启中，请不要刷新页面或进行其他操作"])
        ]),
        duration: 0,
        showClose: false
    })
}

const emits = defineEmits(["restartSuccess"])

const check = () => {
    !notificationEl && showElNotification()
    setTimeout(() => {
        checkIsRestart().then(() => {
            if (notificationEl) {
                setTimeout(() => {
                    ElNotification.success({ title: "提示", message: "服务重启成功" })
                    notificationEl.close()
                    notificationEl = null
                    emits("restartSuccess")
                }, 5000)
            }
        }).catch(() => {
            check();
        })
    }, 5000)
}

defineExpose({
    check
})
</script>

<style lang="scss" scoped>
</style>