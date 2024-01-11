import request from '@/utils/request'

/**
 * 申请退款
 */
export function applyRefund(params: Record<string, any>) {
	return request.post(`shop/refund/apply`, params, { showSuccessMessage: true })
}

/**
 * 修改退款申请
 */
export function editRefund(params: Record<string, any>) {
	return request.put(`shop/refund/${params.order_refund_no}`, params, { showSuccessMessage: true })
}

/**
 * 申请退款
 */
export function refundDelivery(params: Record<string, any>) {
	return request.post(`shop/refund/delivery/${params.order_refund_no}`, params, { showSuccessMessage: true })
}

/**
 * 修改退款申请
 */
export function editRefundDelivery(params: Record<string, any>) {
	return request.put(`shop/refund/delivery/${params.order_refund_no}`, params, { showSuccessMessage: true })
}

/**
 * 获取退款原因
 */
export function getRefundReason() {
    return request.get('shop/refund/reason')
}

/**
 * 获取退款列表
 */
export function getRefundList() {
    return request.get('shop/order/refund')
}

/**
 * 获取退款详情
 */
export function getRefundDetail(orderRefundNo: string) {
    return request.get(`shop/order/refund/${orderRefundNo}`)
}

/**
 * 取消维权
 */
export function closeRefund(orderRefundNo: string) {
    return request.put(`shop/refund/close/${orderRefundNo}`,{}, { showSuccessMessage: true })
}
