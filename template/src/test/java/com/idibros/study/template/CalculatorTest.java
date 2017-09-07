package com.idibros.study.template;

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

    @Test
    public void sumOfNumbers() throws IOException {
        Path path = Paths.get("src/test/resources", "numbers.txt");
        String filePath = path.toString();

        logger.info("################{}###################", filePath);

        Calculator calculator = new Calculator();
        int sum = calculator.calcSum(filePath);

        assertThat(sum, is(10));
    }

}