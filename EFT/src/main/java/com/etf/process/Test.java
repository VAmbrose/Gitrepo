/*
 * Decompiled with CFR 0_118.
 */
package com.etf.process;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.etf.dto.FileReaderLineDTO;


public class Test {
    private static final Logger LOGGER = Logger.getLogger(Test.class);

    public static List<FileReaderLineDTO> getFileReaderPositionList(int totalLines, int maxSize) {
        ArrayList<FileReaderLineDTO> totaLinesPositionList = new ArrayList<FileReaderLineDTO>();
        if (totalLines > 0 && maxSize > 0) {
            int modulo = totalLines / maxSize;
            int remains = totalLines % maxSize;
            int startPosition = 0;
            int endPosition = 0;
            for (int i2 = 0; i2 < modulo; ++i2) {
                FileReaderLineDTO dto = new FileReaderLineDTO();
                startPosition = i2 * maxSize + 1;
                endPosition = i2 * maxSize + maxSize;
                dto.setStartPosition(startPosition);
                dto.setEndPosition(endPosition);
                totaLinesPositionList.add(dto);
            }
            FileReaderLineDTO dto = new FileReaderLineDTO();
            dto.setStartPosition(endPosition + 1);
            dto.setEndPosition(endPosition + remains);
            totaLinesPositionList.add(dto);
        }
        return totaLinesPositionList;
    }

    public static void main(String[] args) {
    }
}

