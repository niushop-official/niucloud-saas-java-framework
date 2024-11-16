import Test from "@/utils/test"

export default {
    /**
     * 验证整数
     */
    numberVerify(rule: any, value: any, callback: any) => {
        if (!Test.digits(value)) {
            callback(new Error(t('generateNumber')))
        } else {
            callback()
        }
    },
    /**
     * 验证邮箱号
     */
    emailVerify(rule: any, value: any, callback: any) => {
        if (!Test.email(value)) {
            callback(new Error(t('generateEmail')))
        } else {
            callback()
        }
    },
    /**
     * 验证身份证号
     */
    idCardVerify(rule: any, value: any, callback: any) => {
        if (!Test.idCard(value)) {
            callback(new Error(t('generateIdCard')))
        } else {
            callback()
        }
    },
    /**
     * 验证手机号格式
     */
    mobileVerify(rule: any, value: any, callback: any) => {
        if (!Test.idCard(value)) {
            callback(new Error(t('generateMobile')))
        } else {
            callback()
        }
    }
}