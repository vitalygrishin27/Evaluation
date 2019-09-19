package app.service.impl;

import app.model.*;
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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

@Service
public class POIServiceImpl implements POIService {
    private Workbook workbook;
    private Sheet activeSheet;
    private int summaryColsInPage = 14;
    private Row activeRow;
    private int activeRowIndex = 0;
    private int activeCellIndex = 0;
    private Cell activeCell;


    @Autowired
    CategoryServiceImpl categoryService;

    @Autowired
    UserServiceImpl userService;

    @Autowired
    MarkServiceImpl markService;

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
        LinkedList<User> juries = new LinkedList<>(userService.findAllJuries());

        for (Category category : categoryService.findAllCategories()
        ) {
            LinkedList<Criterion> criterions = new LinkedList<Criterion>(category.getCriterions());
            activeRowIndex = 0;
            activeCellIndex = 0;
            activeSheet = workbook.getSheet(category.getCategoryName());
            // Заполнение названия конкурса
            fillTitle(contestName);
            // Заполнение подзаголовка (Ведомость)
            fillSubTitle(messageSource.getMessage("statement.subTitle", null, Locale.getDefault()));
            // Заполение Категории
            fillCategoryName(category.getCategoryName());
            // Заполнить общее количество участников в категории
            fillSummaryCountMembersByCategory(category.getMembers().size());
            // Заполнить заголовки таблицы
            fillTableTitle(juries, category.getCriterions().size());
            // Заполнить данные участника
            fillMembers(new ArrayList<>(category.getMembers()), juries, criterions);
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

    private void fillCategoryName(String categoryName) {
        activeRow = activeSheet.createRow(activeRowIndex);
        activeCell = activeRow.createCell(1, CellType.STRING);
        activeCell.setCellValue(messageSource.getMessage("statement.label.category", null, Locale.getDefault()));
        activeCell.setCellStyle(createCellStyleForLabelCategory());
        activeCell = activeRow.createCell(2, CellType.STRING);
        activeCell.setCellValue(categoryName);
        activeCell.setCellStyle(createCellStyleForLabelCategory());
    }

    private void fillSummaryCountMembersByCategory(int count) {
        activeCell = activeRow.createCell(10, CellType.STRING);
        activeCell.setCellValue(messageSource.getMessage("statement.label.countOfMembers", null, Locale.getDefault()));
        activeCell = activeRow.createCell(11, CellType.STRING);
        activeCell.setCellValue(count);
        nextRow();
    }

    private void fillTableTitle(LinkedList<User> juries, int countCriterions) {
        activeRow = activeSheet.createRow(activeRowIndex);
        activeCell = activeRow.createCell(0, CellType.STRING);
        activeCell.setCellValue(messageSource.getMessage("statement.label.id", null, Locale.getDefault()));
        activeCell = activeRow.createCell(1, CellType.STRING);
        activeCell.setCellValue(messageSource.getMessage("statement.label.member", null, Locale.getDefault()));
        activeCellIndex = 2;
        for (User user : juries
        ) {
            activeCell = activeRow.createCell(activeCellIndex, CellType.STRING);
            activeSheet.addMergedRegion(new CellRangeAddress(activeRowIndex, activeRowIndex, activeCellIndex, activeCellIndex + countCriterions - 1));
            String juryFullName = user.getUserContact().getLastName() + " " + user.getUserContact().getFirstName() + user.getUserContact().getSecondName();
            activeCell.setCellValue(juryFullName);
            activeCellIndex += countCriterions;
        }
        activeCell = activeRow.createCell(activeCellIndex, CellType.STRING);
        activeSheet.addMergedRegion(new CellRangeAddress(activeRowIndex, activeRowIndex, activeCellIndex, activeCellIndex + countCriterions - 1));
        activeCell.setCellValue(messageSource.getMessage("statement.label.summaryMarks", null, Locale.getDefault()));
        activeCellIndex += countCriterions;
        activeCell = activeRow.createCell(activeCellIndex, CellType.STRING);
        activeCell.setCellValue(messageSource.getMessage("statement.label.position", null, Locale.getDefault()));
        nextRow();
    }

    private void fillMembers(List<Member> members, LinkedList<User> juries, LinkedList<Criterion> criterions) {
        for (Member member : members
        ) {
            activeRow = activeSheet.createRow(activeRowIndex);
            //ID
            activeCell = activeRow.createCell(activeCellIndex, CellType.STRING);
            activeSheet.addMergedRegion(new CellRangeAddress(activeRowIndex, activeRowIndex + member.getPerformances().size() + 1, activeCellIndex, activeCellIndex));
            activeCell.setCellValue(member.getId());
            activeCellIndex++;
            //NAME
            activeCell = activeRow.createCell(activeCellIndex, CellType.STRING);
            activeSheet.addMergedRegion(new CellRangeAddress(activeRowIndex, activeRowIndex + member.getPerformances().size() + 1, activeCellIndex, activeCellIndex));
            activeCell.setCellValue(member.getLastName() + " " + member.getName() + " " + member.getSecondName());
            activeCellIndex++;

            for (User jury : juries
            ) {
                int originalActiveCellIndex = activeCellIndex;
                int originalActiveRowIndex = activeRowIndex;
                for (Criterion criterion : criterions
                ) {
                    //Title of criterion
                    activeCell = activeRow.createCell(activeCellIndex, CellType.STRING);
                    activeCell.setCellValue(criterion.getName());
                    activeCellIndex++;
                    // TODO: 19.09.2019 сделать стиль для названий критерий маленький
                }
                activeCellIndex = originalActiveCellIndex;
                activeRowIndex++;

                for (Performance performance : member.getPerformances()
                ) {
                    ArrayList<Mark> currentMarks = new ArrayList<>();
                    currentMarks.addAll(markService.findMarkByUserAndCriterion(performance, jury));

                    activeRow = activeSheet.createRow(activeRowIndex);
                    for (Criterion criterion : criterions
                    ) {
                        activeCell = activeRow.createCell(activeCellIndex, CellType.STRING);
                        for (Mark mark : currentMarks
                        ) {
                            if (mark.getCriterion().equals(criterion)) {
                                activeCell.setCellValue(mark.getValue());
                            }

                        }
                        try {
                            activeCell.getStringCellValue();
                        } catch (Exception e) {
                            activeCell.setCellValue("0");
                        }

                        activeCellIndex++;
                    }
                    activeCellIndex -= criterions.size();
                    activeRowIndex++;
                }
                activeRowIndex = originalActiveRowIndex;
                activeCellIndex += criterions.size();

            }
            activeCellIndex = 0;
            activeRowIndex += (member.getPerformances().size() + 2);
        }

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

    private CellStyle createCellStyleForLabelCategory() {
        Font newFont = workbook.createFont();
        newFont.setBold(true);
        newFont.setColor(HSSFColor.HSSFColorPredefined.BLUE_GREY.getIndex());
        newFont.setFontHeightInPoints((short) 10);
        newFont.setItalic(false);

        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFont(newFont);
        cellStyle.setWrapText(false);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setAlignment(HorizontalAlignment.LEFT);
        return cellStyle;
    }


    private void nextRow() {
        activeRowIndex++;
        activeCellIndex = 0;
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
