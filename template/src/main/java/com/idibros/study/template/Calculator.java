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
        BufferedReaderCallback callback = new BufferedReaderCallback<Integer>() {
            @Override
            public Integer doSomethingWithReader(BufferedReader br, Integer result, String nextLine) throws IOException {
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
        BufferedReaderCallback callback = new BufferedReaderCallback<Integer>() {
            @Override
            public Integer doSomethingWithReader(BufferedReader br, Integer result, String nextLine) throws IOException {
                return result * Integer.valueOf(nextLine);
            }
        };

        return fileReadTemplate(filePath, callback, 1);
    }

    public String concOfNumbers(String filePath) throws IOException {
        BufferedReaderCallback callback = new BufferedReaderCallback<String>() {
            @Override
            public String doSomethingWithReader(BufferedReader br, String result, String nextLine) throws IOException {
                return result + nextLine;
            }
        };

        return fileReadTemplate(filePath, callback, "");
    }

    /**
     * 2. 콜백의 연산 결과를 리턴하는 템플릿에도 제네릭을 적용한다.
     */
    public <T> T fileReadTemplate(String filePath, BufferedReaderCallback<T> callback, T result) throws IOException {
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
