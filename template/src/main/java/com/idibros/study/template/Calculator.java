package com.idibros.study.template;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by dongba on 2017-09-07.
 */
public class Calculator {

    private Logger logger = LoggerFactory.getLogger(Calculator.class);

    public int calcSum(String filePath) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            int sum = 0;
            String line = null;
            while ((line = br.readLine()) != null) {
                sum += Integer.valueOf(line);
            }
            return sum;
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw e;
        }
    }

}
