package com.idibros.study.template;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Created by dongba on 2017-09-07.
 */
public interface BufferedReaderCallback {

    int doSomethingWithReader(BufferedReader br) throws IOException;

}
