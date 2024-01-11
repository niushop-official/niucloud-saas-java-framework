<!-- eslint-disable vue/multi-word-component-names -->
<template>
	<div class="main-container p-4 bg-[#eff0f4]">
		<!-- 实时概况 -->
		<div class="realtime-overview bg-white p-4">
			<el-row>
				<el-col :span="24">
					<div class="mb-[20px]">
						<span class="text-lg font-extrabold mr-[10px]">{{t('realtimeOverview')}}</span>
						<span class="text-sm text-[#a19f98]">{{t('updateTime')}}</span>
						<span class="text-sm text-[#a19f98]">{{ time }}</span>
					</div>
				</el-col>
			</el-row>
			<el-row>
				<el-col :span="6">
					<div class="ml-[10px]">
						<div class="text-sm text-[#a19f98] leading-8">
							<el-statistic :value="statToday.order_num">
								<template #title>
									<div style="display: inline-flex; align-items: center">
										<span class="mr-[5px]">{{t('todayOrderCount')}}</span>
										<el-tooltip class="box-item" effect="light" :content="t('todayOrderCount')"  placement="top">
											<el-icon>
												<QuestionFilled/>
											</el-icon>
										</el-tooltip>
									</div>
								</template>
							</el-statistic>
						</div>
						<div class="text-sm text-[#a19f98] leading-8">
							<span>{{t('yesterday')}}</span>
							<span>{{statYesterday.order_num}}</span>
						</div>
						<div class="text-sm text-[#a19f98] leading-8 mt-[15px]">
							<el-statistic :title="t('orderCount')" :value="statTotal.order_num"/>
						</div>
					</div>
				</el-col>
				<el-col :span="6">
					<div class="ml-[10px]">
						<div class="text-sm text-[#a19f98] leading-8">
							<el-statistic :value="statToday.sale_money">
								<template #title>
									<div style="display: inline-flex; align-items: center">
										<span class="mr-[5px]">{{t('todayOrderSale')}}</span>
										<el-tooltip class="box-item" effect="light" :content="t('todayOrderSale')"  placement="top">
											<el-icon>
												<QuestionFilled/>
											</el-icon>
										</el-tooltip>
									</div>
								</template>
							</el-statistic>
						</div>
						<div class="text-sm text-[#a19f98] leading-8">
							<span>{{t('yesterday')}}</span>
							<span>{{statYesterday.sale_money}}</span>
						</div>
						<div class="text-sm text-[#a19f98] leading-8 mt-[15px]">
							<el-statistic :title="t('salesTotal')" :value="statTotal.sale_money"/>
						</div>
					</div>
				</el-col>
				<el-col :span="6">
					<div class="ml-[10px]">
						<div class="text-sm text-[#a19f98] leading-8">
							<el-statistic :value="statToday.refund_money">
								<template #title>
									<div style="display: inline-flex; align-items: center">
										<span class="mr-[5px]">{{t('todayAddMemberCount')}}</span>
										<el-tooltip class="box-item" effect="light" :content="t('todayAddMemberCount')"  placement="top">
											<el-icon>
												<QuestionFilled/>
											</el-icon>
										</el-tooltip>
									</div>
								</template>
							</el-statistic>
						</div>
						<div class="text-sm text-[#a19f98] leading-8">
							<span>{{t('yesterday')}}</span>
							<span>{{statYesterday.refund_money}}</span>
						</div>
						<div class="text-sm text-[#a19f98] leading-8 mt-[15px]">
							<el-statistic :title="t('memberTotal')" :value="statTotal.refund_money"/>
						</div>
					</div>
				</el-col>
				<el-col :span="6">
					<div class="ml-[10px]">
						<div class="text-sm text-[#a19f98] leading-8">
							<el-statistic :value="statToday.access_sum">
								<template #title>
									<div style="display: inline-flex; align-items: center">
										<span class="mr-[5px]">{{t('todayBrowseCount')}}</span>
										<el-tooltip class="box-item" effect="light" :content="t('todayBrowseCount')"  placement="top">
											<el-icon>
												<QuestionFilled/>
											</el-icon>
										</el-tooltip>
									</div>
								</template>
							</el-statistic>
						</div>
						<div class="text-sm text-[#a19f98] leading-8">
							<span>{{t('yesterday')}}</span>
							<span>{{statYesterday.access_sum}}</span>
						</div>
						<div class="text-sm text-[#a19f98] leading-8 mt-[15px]">
							<el-statistic :title="t('browseTotal')" :value="statTotal.access_sum"/>
						</div>
					</div>
				</el-col>

			</el-row>
		</div>
		<!-- 代办事项 -->
		<div class="agent-matters bg-white p-4 mt-[15px]">
			<el-row>
				<el-col :span="24">
					<div class="mb-[20px] text-lg font-extrabold mr-[10px]">{{t('agentMatters')}}</div>
				</el-col>
			</el-row>
			<el-row>
				<el-col :span="4">
					<div class="ml-[10px]">
						<el-statistic :value="statOrder.wait_pay_order">
							<template #title>
								<div style="display: inline-flex; align-items: center">
									<span class="mr-[5px]">{{t('waitPayOrder')}}</span>
									<el-tooltip class="box-item" effect="light" :content="t('waitPayOrder')" placement="top">
										<el-icon>
											<QuestionFilled/>
										</el-icon>
									</el-tooltip>
								</div>
							</template>
						</el-statistic>
					</div>
				</el-col>
				<el-col :span="4">
					<el-statistic :value="statOrder.wait_delivery_order">
						<template #title>
							<div style="display: inline-flex; align-items: center">
								{{t('waitDeliveryOrder')}}
							</div>
						</template>
					</el-statistic>
				</el-col>
				<el-col :span="4">
					<el-statistic :value="statOrder.wait_take_order">
						<template #title>
							<div style="display: inline-flex; align-items: center">
								{{t('waitTakeOrder')}}
							</div>
						</template>
					</el-statistic>
				</el-col>
				<el-col :span="4">
					<el-statistic :value="statOrder.refund_order">
						<template #title>
							<div style="display: inline-flex; align-items: center">
								退款订单
							</div>
						</template>
					</el-statistic>
				</el-col>
				<el-col :span="4">
					<el-statistic :value="statGoods.sale_goods_num">
						<template #title>
							<div style="display: inline-flex; align-items: center">
								{{t('saleGoodsNum')}}
							</div>
						</template>
					</el-statistic>
				</el-col>
				<el-col :span="4">
					<el-statistic :value="statGoods.warehouse_goods_num">
						<template #title>
							<div style="display: inline-flex; align-items: center">
								{{t('warehouseGoodsNum')}}
							</div>
						</template>
					</el-statistic>
				</el-col>
			</el-row>
		</div>
		<!-- 订单趋势 -->
		<div class="rder-trend bg-white p-4 mt-[15px]">
			<el-row>
				<el-col :span="12">
					<div ref="visitStat" :style="{ width: '100%', height: '300px' }"></div>
				</el-col>
				<el-col :span="12">
					<div ref="hourStat" :style="{ width: '100%', height: '300px' }"></div>
				</el-col>
			</el-row>
		</div>
	</div>
</template>

<script lang="ts" setup>
import { ref } from 'vue'
import { t } from '@/lang'
import {
    getShopCountList,
    getShopTodayCountList,
    getShopYesterdayCountList,
    getShopStat,
    getShopOrderStat,
    getShopGoodsStat
} from '@/addon/shop/api/shop'
import * as echarts from 'echarts'

const visitStat = ref<any>(null)
const hourStat = ref<any>(null)

interface statTotalTpe{
	order_num: number,
	sale_money: number,
    refund_money: number,
    access_sum: number
}
interface statTodayTpe{
	order_num: number,
	sale_money: number,
    refund_money: number,
    access_sum: number
}
interface statYesterdayTpe{
	order_num: number,
	sale_money: number,
    refund_money: number,
    access_sum: number
}
interface statOrderTpe{
	wait_pay_order: number,
	wait_delivery_order: number,
	wait_take_order: number,
	refund_order: number
}
interface statGoodsTpe{
	sale_goods_num: number,
	warehouse_goods_num: number
}
interface statCountTpe{
	order_num: number,
	time: string,
	sale_money: number
}
const statTotal = ref<statTotalTpe|any>([])
const statToday = ref<statTodayTpe|any>([])
const statYesterday = ref<statYesterdayTpe|any>([])
const statCount = ref<statCountTpe|any>([])
const statOrder = ref<statOrderTpe|any>([])
const statGoods = ref<statGoodsTpe|any>([])

const getStatInfoFn = async () => {
    statTotal.value = await (await getShopCountList()).data
    statToday.value = await (await getShopTodayCountList()).data
    statYesterday.value = await (await getShopYesterdayCountList()).data
    statOrder.value = await (await getShopOrderStat()).data
    statGoods.value = await (await getShopGoodsStat()).data
    statCount.value = await (await getShopStat()).data
    setTimeout(() => {
        drawChart('')
        drawChartTo('')
    }, 20)
}
getStatInfoFn()

const drawChart = (item:any) => {
    let value = statCount.value.order_num
    if (item) value = item
    if (!visitStat.value) return
    const visitStatChart = echarts.init(visitStat.value)
    const visitStatOption = ref({
        title: {
            text: '订单量趋势'
        },
        legend: {},
        xAxis: {
            data: []
        },
        yAxis: {},
        tooltip: {
            trigger: 'axis'
        },
        series: [
            {
                type: 'line',
                data: []
            }
        ]
    })
    visitStatOption.value.xAxis.data = statCount.value.time
    visitStatOption.value.series[0].data = value
    visitStatChart.setOption(visitStatOption.value)
}
const drawChartTo = (item:any) => {
    let valueTo = statCount.value.sale_money
    if (item) valueTo = item
    if (!hourStat.value) return
    const hourStatChart = echarts.init(hourStat.value)
    const hourStatOption = ref({
        title: {
            text: '销售额（元）'
        },
        legend: {},
        xAxis: {
            data: []
        },
        yAxis: {},
        tooltip: {
            trigger: 'axis'
        },
        series: [
            {
                type: 'line',
                data: []
            }
        ]
    })
    hourStatOption.value.xAxis.data = statCount.value.time
    hourStatOption.value.series[0].data = valueTo
    hourStatChart.setOption(hourStatOption.value)
}
const time = ref('')
const nowTime = () => {
    const date = new Date()
    const year = date.getFullYear()
    const month = date.getMonth() + 1
    const day = date.getDate()
    const hh = checkTime(date.getHours())
    const mm = checkTime(date.getMinutes())
    const ss = checkTime(date.getSeconds())
    function checkTime (i:any) {
        if (i < 10) {
            return '0' + i
        }
        return i
    }
    time.value = year + '-' + month + '-' + day + ' ' + hh + ':' + mm + ':' + ss
}
nowTime()
</script>

<style lang="scss" scoped>
</style>
