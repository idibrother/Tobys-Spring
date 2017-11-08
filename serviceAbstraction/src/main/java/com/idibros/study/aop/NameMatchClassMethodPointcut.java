package com.idibros.study.aop;

import org.springframework.aop.support.NameMatchMethodPointcut;

/**
 * 클래스 필터가 포함된 포인트컷을 정의한다.
 * Created by dongba on 2017-11-08.
 */
public class NameMatchClassMethodPointcut extends NameMatchMethodPointcut {

    public void setMappedClassName (String mappedClassName) {
        this.setClassFilter(new SimpleClassFilter(mappedClassName));
    }
}
