package com.simplesns.utils;

public class ClassUtils {

    /**
     * 객체를 지정된 클래스로 안전하게 캐스팅
     * 캐스팅이 가능한 경우에는 해당 객체를 반환하며, 그렇지 않은 경우 null 을 반환
     */
    public static <T> T getSafeCastInstance(Object o, Class<T> clazz) {
        return clazz != null && clazz.isInstance(o) ? clazz.cast(o) : null;
    }

}
