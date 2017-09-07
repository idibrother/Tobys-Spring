package com.idibros.study.template;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Created by dongba on 2017-09-07.
 */
public interface BufferedReaderCallback<T> {

    /**
     * 1. 문자열 연산을 위해 자바 제네릭을 활용한다.
     */
    T doSomethingWithReader(BufferedReader br, T result, String nextLine) throws IOException;

}
