<template>
    <div>

    </div>
</template>

<script lang="ts" setup>
import { ref, reactive, computed } from 'vue'
import { t } from '@/lang'
import { FormInstance, ElMessage } from 'element-plus'
import { getCompanyList } from '@/addon/shop/api/delivery'
import { getOrderDeliveryType, orderDelivery } from '@/addon/shop/api/order'
import { getIsTradeManaged } from '@/app/api/weapp'
import { cloneDeep } from 'lodash-es'

</script>
<style lang="scss" scoped>

</style>
