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
    private int summaryColsInPage = 14;
    private Row activeRow;
    private int activeRowIndex =0;
    private int activeCellIndex =0;
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
            // Заполнение подзаголовка (Ведомость)
            fillSubTitle(messageSource.getMessage("statement.subTitle", null, Locale.getDefault()));
            // Заполение Категории
            fillCategoryName(category.getCategoryName());


        }

    }

    private void fillTitle(String contestName) {
        activeRow = activeSheet.createRow(activeRowIndex);
        activeCell = activeRow.createCell(activeCellIndex, CellType.STRING);
        activeCell.setCellValue(contestName);
        int countForIncreaseHeightRow = contestName.length() / 70;
        if (countForIncreaseHeightRow > 0) {
            activeRow.setHeightInPoints(30 * countForIncreaseHeightRow);
        }
        activeSheet.addMergedRegion(new CellRangeAddress(activeRowIndex, activeRowIndex, activeCellIndex, summaryColsInPage));
        activeCell.setCellStyle(createCellStyleForTitle());
        nextRow();
    }

    private void fillSubTitle(String subTitle) {
        activeRow = activeSheet.createRow(activeRowIndex);
        activeCell = activeRow.createCell(0, CellType.STRING);
        activeCell.setCellValue(subTitle);
        activeSheet.addMergedRegion(new CellRangeAddress(activeRowIndex, activeRowIndex, activeCellIndex, summaryColsInPage));
        activeCell.setCellStyle(createCellStyleForSubTitle());
        nextRow();
    }

    private void fillCategoryName(String categoryName){
        activeRow = activeSheet.createRow(activeRowIndex);
        activeCell = activeRow.createCell(1, CellType.STRING);
        activeCell.setCellValue(messageSource.getMessage("statement.label.category", null, Locale.getDefault()));
        // TODO: 18.09.2019 Стиль для названия категории и лейбл 
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

    private void nextRow(){
        activeRowIndex++;
        activeCellIndex=0;
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

}
