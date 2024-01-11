import request from '@/utils/request'

/**
 * 优惠券列表
 */
export function getShopCouponList(params : Record<string, any>) {
	return request.get(`shop/coupon`, params)
}
/**
 * 优惠券详情
 */
export function getShopCouponInfo(id:number) {
	return request.get(`shop/coupon/${id}`)
}
/**
 * 领取优惠券
 */
export function getCoupon(params : Record<string, any>) {
	return request.post(`shop/coupon`, params, {showSuccessMessage: true})
}
/**
 * 获取我的优惠券
 */
export function getMyCouponList(params : Record<string, any>) {
	return request.get(`shop/member/coupon`, params)
}
