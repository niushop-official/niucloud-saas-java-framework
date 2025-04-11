package com.niu.core.common.utils.crypto;

import cn.hutool.crypto.digest.BCrypt;

public class PasswordEncipher {


    /**
     * 生成密码
     * @param text 明文密码
     * @return
     */
    public static String encode(String text) {
        return BCrypt.hashpw(text);
    }

    /**
     * 检测密码是否一样
     * @param text 明文
     * @param passwd 密文
     * @return
     */
    public static boolean matche(String text, String passwd) {
        return BCrypt.checkpw(text, passwd);
    }

}
