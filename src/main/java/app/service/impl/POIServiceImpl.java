package app.service.impl;

import app.model.Category;
import app.service.POIService;
import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

@Service
public class POIServiceImpl implements POIService {
    private Workbook workbook;
    private Sheet activeSheet;
    private Row activeRow;
    private Cell activeCell;


    @Autowired
    CategoryServiceImpl categoryService;

    @Autowired
    ReloadableResourceBundleMessageSource messageSource;


    public void createNewDocument(String contestName) {
        // Создать новый документ
        workbook = new HSSFWorkbook();
        // Созданрие вкладок
        createSheetsByCategory();
        // Коррекция полей и ориентации документа
        editSizeAndBorder();


        fillSheets(contestName);
        saveFile();

    }

    private void saveFile() {
        try {
            File file = new File("FullStatement.xls");
            if (file.exists()) {
                file.delete();
            }

            FileOutputStream outFile = new FileOutputStream("FullStatement.xls");
            workbook.write(outFile);
            outFile.close();
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createSheetsByCategory() {
        for (Category category : categoryService.findAllCategories()
        ) {
            workbook.createSheet(category.getCategoryName());
        }
    }

    private void editSizeAndBorder() {
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            workbook.getSheetAt(i).getPrintSetup().setLandscape(true);
            workbook.getSheetAt(i).getPrintSetup().setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
            workbook.getSheetAt(i).setMargin(Sheet.LeftMargin, workbook.getSheetAt(0).getMargin(Sheet.LeftMargin));
            workbook.getSheetAt(i).setMargin(Sheet.RightMargin, workbook.getSheetAt(0).getMargin(Sheet.RightMargin));
            workbook.getSheetAt(i).setMargin(Sheet.TopMargin, workbook.getSheetAt(0).getMargin(Sheet.TopMargin));
            workbook.getSheetAt(i).setMargin(Sheet.BottomMargin, workbook.getSheetAt(0).getMargin(Sheet.BottomMargin));
        }
    }




    private void fillSheets(String contestName) {
        for (Category category : categoryService.findAllCategories()
        ) {
            activeSheet = workbook.getSheet(category.getCategoryName());
            // Заполнение названия конкурса
            fillTitle(contestName);
            fillSubTitle(messageSource.getMessage("statement.subTitle", null, Locale.getDefault()));


            int currentRowCount = 0;
            int currentCellCount = 0;


            //  activeSheet.setColumnWidth(currentCellCount,1000);
            //sheet.addMergedRegion(new CellRangeAddress(start-col,end-col,start-cell,end-cell));

            //  activeRow.
            //     activeCell.getRow().setHeight((short)0);

        }

    }

    private void fillTitle(String contestName) {
        activeRow = activeSheet.createRow(0);
        activeCell = activeRow.createCell(0, CellType.STRING);
        activeCell.setCellValue(contestName);
        int countForIncreaseHeightRow = contestName.length() / 70;
        if (countForIncreaseHeightRow > 0) {
            activeRow.setHeightInPoints(30 * countForIncreaseHeightRow);
        }
        activeSheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 14));
        activeCell.setCellStyle(createCellStyleForTitle());
    }

    private void fillSubTitle(String subTitle) {
        activeRow = activeSheet.createRow(1);
        activeCell = activeRow.createCell(0, CellType.STRING);
        activeCell.setCellValue(subTitle);
        activeSheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 14));
        activeCell.setCellStyle(createCellStyleForSubTitle());
    }


    private CellStyle createCellStyleForTitle() {
        Font newFont = workbook.createFont();
        newFont.setBold(true);
        newFont.setColor(HSSFColor.HSSFColorPredefined.BLUE.getIndex());
        newFont.setFontHeightInPoints((short) 17);
        newFont.setItalic(false);
        // CellUtil.setFont(activeCell,newFont);

        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFont(newFont);
        cellStyle.setWrapText(true);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        return cellStyle;
    }

    private CellStyle createCellStyleForSubTitle() {
        Font newFont = workbook.createFont();
        newFont.setBold(true);
        newFont.setColor(HSSFColor.HSSFColorPredefined.BLUE_GREY.getIndex());
        newFont.setFontHeightInPoints((short) 14);
        newFont.setItalic(true);

        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFont(newFont);
        cellStyle.setWrapText(true);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        return cellStyle;
    }


}
