package com.idibros.study.template;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by dongba on 2017-09-07.
 */
public class CalculatorTest {

    private Logger logger = LoggerFactory.getLogger(CalculatorTest.class);

    private String filePath;

    private Calculator calculator;

    @Before
    public void init() {
        /**
         * 테스트 코드에서도 반복해서 사용하는 객체는 Before 어노테이션 메소드에서 생성해서 초기화한다.
         */
        Path path = Paths.get("src/test/resources", "numbers.txt");
        filePath = path.toString();
        calculator = new Calculator();
    }

    @Test
    public void sumOfNumbers() throws IOException {
        int sum = calculator.calcSum(filePath);

        assertThat(sum, is(10));
    }

    @Test
    public void multOfNumbers() throws IOException {
        int mult = calculator.calcMult(filePath);

        assertThat(mult, is(24));
    }

}