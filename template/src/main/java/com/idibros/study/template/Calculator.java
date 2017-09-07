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
        /**
         * 1. 중복 코드를 검토한다.
         */
//        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
//            int sum = 0;
//            String line = null;
//            while ((line = br.readLine()) != null) {
//                sum += Integer.valueOf(line);
//            }
//            return sum;
//        } catch (IOException e) {
//            logger.error(e.getMessage());
//            throw e;
//        }

        /**
         * 3. 변경되는 부분을 callback으로 분리한다.
         */
        BufferedReaderCallback callback = new BufferedReaderCallback() {
            @Override
            public int doSomethingWithReader(BufferedReader br) throws IOException {
                int sum = 0;
                String line = null;
                while ((line = br.readLine()) != null) {
                    sum += Integer.valueOf(line);
                }
                return sum;
            }
        };

        /**
         * 2. 중복 코드는 템플릿으로 생성하고,
         */
        int result = fileReadTemplate(filePath, callback);

        return result;
    }

    public int fileReadTemplate(String filePath, BufferedReaderCallback callback) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            int result = callback.doSomethingWithReader(br);
            return result;
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw e;
        }
    }

}
