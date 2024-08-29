import request from '@/utils/request'

/**
 * 获取小票打印机列表
 * @param params
 * @returns
 */
export function getPrinterList(params: Record<string, any>) {
    return request.get(`sys/printer`, { params })
}

/**
 * 获取小票打印机详情
 * @param printer_id 小票打印机printer_id
 * @returns
 */
export function getPrinterInfo(printer_id: number) {
    return request.get(`sys/printer/${ printer_id }`);
}

/**
 * 添加小票打印机
 * @param params
 * @returns
 */
export function addPrinter(params: Record<string, any>) {
    return request.post('sys/printer', params, { showErrorMessage: true, showSuccessMessage: true })
}

/**
 * 编辑小票打印机
 * @param printer_id
 * @param params
 * @returns
 */
export function editPrinter(params: Record<string, any>) {
    return request.put(`sys/printer/${ params.printer_id }`, params, {
        showErrorMessage: true,
        showSuccessMessage: true
    })
}

/**
 * 删除小票打印机
 * @param printer_id
 * @returns
 */
export function deletePrinter(printer_id: number) {
    return request.delete(`sys/printer/${ printer_id }`, {
        showErrorMessage: true,
        showSuccessMessage: true
    })
}

/**
 * 获取小票打印模板列表
 * @param params
 * @returns
 */
export function getPrinterTemplateList(params: Record<string, any>) {
    return request.get(`sys/printer/template`, { params })
}

/**
 * 获取小票打印模板详情
 * @param template_id 小票打印模板template_id
 * @returns
 */
export function getPrinterTemplateInfo(template_id: number) {
    return request.get(`sys/printer/template/${ template_id }`);
}

/**
 * 添加小票打印模板
 * @param params
 * @returns
 */
export function addPrinterTemplate(params: Record<string, any>) {
    return request.post('sys/printer/template', params, {
        showErrorMessage: true,
        showSuccessMessage: true
    })
}

/**
 * 编辑小票打印模板
 * @param template_id
 * @param params
 * @returns
 */
export function editPrinterTemplate(params: Record<string, any>) {
    return request.put(`sys/printer/template/${ params.template_id }`, params, {
        showErrorMessage: true,
        showSuccessMessage: true
    })
}

/**
 * 删除小票打印模板
 * @param template_id
 * @returns
 */
export function deletePrinterTemplate(template_id: number) {
    return request.delete(`sys/printer/template/${ template_id }`, {
        showErrorMessage: true,
        showSuccessMessage: true
    })
}