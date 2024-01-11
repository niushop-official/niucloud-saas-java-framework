import request from '@/utils/request'

/**
 * 营销中心
 * @param params
 * @returns
 */
export function getMarketingIndex(params: Record<string, any>) {
    return request.get(`shop/marketing`, {params})
}

/**
 * 获取商品分类列表
 * @param params
 * @returns
 */
export function getGoodsCategoryList(params: Record<string, any>) {
    return request.get(`shop/goods/coupon/init`, params)
}

/**
 * 添加优惠券
 * @param params
 * @returns
 */
export function addCoupon(params: Record<string, any>) {
    return request.post(`shop/goods/coupon`, params, {showErrorMessage: true, showSuccessMessage: true})
}

/**
 * 获取优惠券列表
 * @param params
 * @returns
 */
export function getCouponList(params: Record<string, any>) {
    return request.get(`shop/goods/coupon`, {params})
}

/**
 * 获取优惠券领取记录
 * @param params
 * @returns
 */
export function getCouponRecords(params: Record<string, any>) {
    return request.get(`shop/goods/coupon/records`, {params});
}

/**
 * 获取优惠券详情
 * @param id
 * @returns
 */
export function getCouponInfo(id: number) {
    return request.get(`shop/goods/coupon/detail/${id}`);
}

/**
 * 优惠券状态变更
 * @param params
 * @returns
 */
export function editCouponStatus(params: Record<string, any>) {
    return request.put(`shop/goods/coupon/setstatus/${params.status}`, params, {showSuccessMessage: true})
}

/**
 * 编辑优惠券
 * @param params
 * @returns
 */
export function editCoupon(params: Record<string, any>) {
    return request.put(`shop/goods/coupon/edit/${params.id}`, params, {
        showErrorMessage: true,
        showSuccessMessage: true
    })
}

/**
 * 删除优惠券
 * @param id
 * @returns
 */
export function deleteCoupon(id: number) {
    return request.delete(`shop/goods/coupon/${id}`, {showSuccessMessage: true})
}

