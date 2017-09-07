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
//        /**
//         * 1. 중복 코드를 검토한다.
//         */
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

//        /**
//         * 3. 변경되는 부분을 callback으로 분리한다.
//         */
        BufferedReaderCallback callback = new BufferedReaderCallback() {
            @Override
            public int doSomethingWithReader(BufferedReader br, int result, String nextLine) throws IOException {
                return result + Integer.valueOf(nextLine);
            }
        };

//        /**
//         * 2. 중복 코드는 템플릿으로 생성하고,
//         */
        int result = fileReadTemplate(filePath, callback, 0);

        return result;
    }

    public int calcMult(String filePath) throws IOException {
        /**
         * 1. 또 중복 코드가 있는지 검토하고,
         */
        BufferedReaderCallback callback = new BufferedReaderCallback() {
            /**
             * result의 초기값과 연산 부분을 제외하고 중복이다.
             */
            @Override
            public int doSomethingWithReader(BufferedReader br, int result, String nextLine) throws IOException {
                return result * Integer.valueOf(nextLine);
            }
        };

        /**
         * 2. 템플릿에 전달하여 처리한다.
         */
        return fileReadTemplate(filePath, callback, 1);
    }

    public int fileReadTemplate(String filePath, BufferedReaderCallback callback, int result) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = null;
            while ((line = br.readLine()) != null) {
                result = callback.doSomethingWithReader(br, result, line);
            }
            return result;
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw e;
        }
    }
}
