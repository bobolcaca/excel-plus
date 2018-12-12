/**
 *  Copyright (c) 2018, biezhi (biezhi.me@gmail.com)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package io.github.biezhi.excel.plus.utils;

import io.github.biezhi.excel.plus.enums.ExcelType;
import lombok.experimental.UtilityClass;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.InputStream;

/**
 * @author biezhi
 * @date 2018-12-11
 */
@UtilityClass
public class ExcelUtils {

    public static ExcelType getType(String fileName) {
        String ext = getFileExtension(fileName);
        if (ext.toUpperCase().equals(ExcelType.XLSX.name())) {
            return ExcelType.XLSX;
        }
        if (ext.toUpperCase().equals(ExcelType.XLS.name())) {
            return ExcelType.XLS;
        }
        throw new RuntimeException("");
    }

    public static String getFileExtension(String fileName) {
        int lastIndexOf = fileName.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // empty extension
        }
        return fileName.substring(lastIndexOf + 1);
    }

    public static boolean isXLSX(File file) {
        String ext = getFileExtension(file.getName());
        return ext.toUpperCase().equals("XLSX");
    }

    public static boolean isXLSX(InputStream inputStream) {
        try {
            new XSSFWorkbook(inputStream);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

}
