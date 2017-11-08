package com.idibros.study.aop;

import org.springframework.aop.ClassFilter;
import org.springframework.util.PatternMatchUtils;

/**
 * Created by dongba on 2017-11-08.
 */
public class SimpleClassFilter implements ClassFilter {

    private String mappedClassName;

    public SimpleClassFilter(String mappedClassName) {
        this.mappedClassName = mappedClassName;
    }

    @Override
    public boolean matches(Class<?> clazz) {
        return PatternMatchUtils.simpleMatch(mappedClassName, clazz.getSimpleName());
    }
}
