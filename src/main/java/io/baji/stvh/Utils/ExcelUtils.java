package io.baji.stvh.Utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;

public class ExcelUtils {
    /**
     * excel值处理
     *
     * @param hssfCell
     * @return
     */
    public static Object getXSSFValue(XSSFCell hssfCell) {
        if (hssfCell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
            return hssfCell.getNumericCellValue();    //数字
        } else if (hssfCell.getCellType() == XSSFCell.CELL_TYPE_BOOLEAN) {
            return hssfCell.getBooleanCellValue();    //boolean
        } else if (hssfCell.getCellType() == XSSFCell.CELL_TYPE_ERROR) {
            return hssfCell.getErrorCellValue();      //故障
        } else if (hssfCell.getCellType() == XSSFCell.CELL_TYPE_FORMULA) {
            return hssfCell.getCellFormula();         //公式
        } else if (hssfCell.getCellType() == XSSFCell.CELL_TYPE_BLANK) {
            return "";                                //空值
        } else {
            return hssfCell.getStringCellValue();     //字符串
        }

    }

    /**
     * excel值处理
     *
     * @param hssfCell
     * @return
     */
    public static Object getValue(Cell hssfCell) {
        if (hssfCell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
            return hssfCell.getNumericCellValue();   //数字
        } else if (hssfCell.getCellType() == XSSFCell.CELL_TYPE_BOOLEAN) {
            return hssfCell.getBooleanCellValue();   //boolean
        } else if (hssfCell.getCellType() == XSSFCell.CELL_TYPE_ERROR) {
            return hssfCell.getErrorCellValue();     //故障
        } else if (hssfCell.getCellType() == XSSFCell.CELL_TYPE_FORMULA) {
            return hssfCell.getCellFormula();        //公式
        } else if (hssfCell.getCellType() == XSSFCell.CELL_TYPE_BLANK) {
            return "";                                 //空值
        } else {
            return hssfCell.getStringCellValue();    //字符串
        }
    }

    public static Integer getUserSex(Cell hssfCell) {
        if ("男".equals(hssfCell.getStringCellValue())) {
            return 1;
        } else if ("女".equals(hssfCell.getStringCellValue())) {
            return 2;
        }
        return 0;

    }


}
