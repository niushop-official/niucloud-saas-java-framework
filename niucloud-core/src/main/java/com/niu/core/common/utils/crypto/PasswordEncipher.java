package com.niu.core.common.utils.crypto;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncipher {

    private static BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 生成密码
     * @param text 明文密码
     * @return
     */
    public static String encode(String text) {
        return passwordEncoder.encode(text);
    }

    /**
     * 检测密码是否一样
     * @param text 明文
     * @param passwd 密文
     * @return
     */
    public static boolean matche(String text, String passwd) {
        return passwordEncoder.matches(text, passwd);
    }

}
