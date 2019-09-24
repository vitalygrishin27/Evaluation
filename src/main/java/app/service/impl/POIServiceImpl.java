package app.service.impl;

import app.model.*;
import app.service.POIService;
import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

@Service
public class POIServiceImpl implements POIService {
    private Workbook workbook;
    private Sheet activeSheet;
    private int summaryColsInPage;
    private Row activeRow;
    private int activeRowIndex = 0;
    private int activeCellIndex = 0;
    private Cell activeCell;
    private Map<String, CellStyle> cellStyleMap = new HashMap<>();
    private int LABEL_TITLE_HEIGHT = 50;
    private int MARK_COLUMN_WIDTH = 900;
    private int ID_COLUMN_WIDTH = 1000;
    private int MEMBER_NAME_COLUMN_WIDTH = 5000;
    private Map<Member,Integer> summaryMarkByMember;
    private Map<Member,Integer> placeMember;


    @Autowired
    CategoryServiceImpl categoryService;

    @Autowired
    UserServiceImpl userService;

    @Autowired
    MarkServiceImpl markService;

    @Autowired
    MessageSource messageSource;

    @Autowired
    ConfigurationServiceImpl configurationService;


    public void createNewDocument(String contestName) {
        // Подготовка данных
        // Общие оценки всех участников
        summaryMarkByMember=markService.getSummaryMarkByAllPerformances();
        // Места участников
        placeMember=markService.getPlaces(summaryMarkByMember);

        // Создать новый документ
        workbook = new HSSFWorkbook();
        // Создание стилей
        createAllCellStyle();
        // Созданрие вкладок
        createSheetsByCategory();
        // Коррекция полей и ориентации документа
        editSizeAndBorder();
        // Заполнить старницы по категориям
        fillSheets(configurationService.getConfiguration().getContestName());
        // Сохранить файл
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
     /*       workbook.getSheetAt(i).setMargin(Sheet.LeftMargin, workbook.getSheetAt(0).getMargin(Sheet.LeftMargin));
            workbook.getSheetAt(i).setMargin(Sheet.RightMargin, workbook.getSheetAt(0).getMargin(Sheet.RightMargin));
            workbook.getSheetAt(i).setMargin(Sheet.TopMargin, workbook.getSheetAt(0).getMargin(Sheet.TopMargin));
            workbook.getSheetAt(i).setMargin(Sheet.BottomMargin, workbook.getSheetAt(0).getMargin(Sheet.BottomMargin));*/
        }
    }


    private void fillSheets(String contestName) {
        LinkedList<User> juries = new LinkedList<>(userService.findAllJuries());

        for (Category category : categoryService.findAllCategories()
        ) {
            //Count summaryColsInPage на каждой старнице будет разное количество ячеек, так как в категории может быть разное количество критериев
            fillSummaryColsInPage(category);

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

    private void fillSummaryColsInPage(Category category) {
        //2 - это 3 колонки(0,1,2), которые есть всегда ID, имя участника и место.
        //Добавляем количество критериев умноженное на (количество жюри +1) так есть общая оценка
        summaryColsInPage = 2 + category.getCriterions().size() * (userService.findAllJuries().size() + 1);
    }

    private void fillTitle(String contestName) {
        activeRow = activeSheet.createRow(activeRowIndex);
        activeCell = activeRow.createCell(activeCellIndex, CellType.STRING);
        activeCell.setCellValue(contestName);
        int countForIncreaseHeightRow = contestName.length() / 70;
        if (countForIncreaseHeightRow > 0) {
            activeRow.setHeightInPoints(25 * (countForIncreaseHeightRow+1));
        }else{
            activeRow.setHeightInPoints(30);
        }
        activeSheet.addMergedRegion(new CellRangeAddress(activeRowIndex, activeRowIndex, activeCellIndex, summaryColsInPage));
        activeCell.setCellStyle(cellStyleMap.get("title"));
        nextRow();
    }

    private void fillSubTitle(String subTitle) {
        activeRow = activeSheet.createRow(activeRowIndex);
        activeCell = activeRow.createCell(0, CellType.STRING);
        activeCell.setCellValue(subTitle);
        activeSheet.addMergedRegion(new CellRangeAddress(activeRowIndex, activeRowIndex, activeCellIndex, summaryColsInPage));
        activeRow.setHeightInPoints(20);
        activeCell.setCellStyle(cellStyleMap.get("subTitle"));
        nextRow();
    }

    private void fillCategoryName(String categoryName) {
        activeRow = activeSheet.createRow(activeRowIndex);
        activeCell = activeRow.createCell(1, CellType.STRING);
        activeCell.setCellValue(messageSource.getMessage("statement.label.category", null, Locale.getDefault()) + ": " + categoryName);
        activeCell.setCellStyle(cellStyleMap.get("category"));
    }

    private void fillSummaryCountMembersByCategory(int count) {
        activeCell = activeRow.createCell(10, CellType.STRING);
        activeCell.setCellValue(messageSource.getMessage("statement.label.countOfMembers", null, Locale.getDefault()) + ": " + count);
        activeCell.setCellStyle(cellStyleMap.get("category"));
        nextRow();
    }

    private void fillTableTitle(LinkedList<User> juries, int countCriterions) {
        activeRow = activeSheet.createRow(activeRowIndex);
        activeRow.setHeightInPoints(LABEL_TITLE_HEIGHT);
        activeCell = activeRow.createCell(0, CellType.STRING);
        activeCell.setCellValue(messageSource.getMessage("statement.label.id", null, Locale.getDefault()));
        activeCell.setCellStyle(cellStyleMap.get("columnName"));
        activeSheet.setColumnWidth(activeCellIndex, ID_COLUMN_WIDTH);
        activeCellIndex = 1;
        activeCell = activeRow.createCell(1, CellType.STRING);
        activeCell.setCellValue(messageSource.getMessage("statement.label.member", null, Locale.getDefault()));
        activeCell.setCellStyle(cellStyleMap.get("columnName"));
        activeSheet.setColumnWidth(activeCellIndex, MEMBER_NAME_COLUMN_WIDTH);
        activeCellIndex = 2;
        for (User user : juries
        ) {
            activeCell = activeRow.createCell(activeCellIndex, CellType.STRING);
            activeSheet.addMergedRegion(new CellRangeAddress(activeRowIndex, activeRowIndex, activeCellIndex, activeCellIndex + countCriterions - 1));
            String juryFullName = user.getUserContact().getLastName() + " " + user.getUserContact().getFirstName() + " " + user.getUserContact().getSecondName();
            activeCell.setCellValue(juryFullName);
            activeCell.setCellStyle(cellStyleMap.get("columnName"));
            activeCellIndex += countCriterions;
        }
        activeCell = activeRow.createCell(activeCellIndex, CellType.STRING);
        activeSheet.addMergedRegion(new CellRangeAddress(activeRowIndex, activeRowIndex, activeCellIndex, activeCellIndex + countCriterions - 1));
        activeCell.setCellValue(messageSource.getMessage("statement.label.summaryMarks", null, Locale.getDefault()));
        activeCell.setCellStyle(cellStyleMap.get("columnName"));
        activeCellIndex += countCriterions;
        activeCell = activeRow.createCell(activeCellIndex, CellType.STRING);
        activeCell.setCellValue(messageSource.getMessage("statement.label.position", null, Locale.getDefault()));
        activeCell.setCellStyle(cellStyleMap.get("columnName"));
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
            activeCell.setCellStyle(cellStyleMap.get("columnName"));
            activeCellIndex++;
            //NAME
            activeCell = activeRow.createCell(activeCellIndex, CellType.STRING);
            activeSheet.addMergedRegion(new CellRangeAddress(activeRowIndex, activeRowIndex + member.getPerformances().size() + 1, activeCellIndex, activeCellIndex));
            activeCell.setCellValue(member.getLastName() + " " + member.getName() + " " + member.getSecondName());
            activeCell.setCellStyle(cellStyleMap.get("columnName"));
            activeCellIndex++;


            for (int i = activeRowIndex + 1; i <= (activeRowIndex + (member.getPerformances().size() + 1)); i++) {
                activeSheet.createRow(i);
            }


            for (User jury : juries
            ) {
                int originalActiveCellIndex = activeCellIndex;
                int originalActiveRowIndex = activeRowIndex;
                for (Criterion criterion : criterions
                ) {
                    //Title of criterion
                    activeRow = activeSheet.getRow(activeRowIndex);
                    activeCell = activeRow.createCell(activeCellIndex, CellType.STRING);
                    activeCell.setCellValue(criterion.getName());
                    activeCell.setCellStyle(cellStyleMap.get("criterionName"));
                    activeSheet.setColumnWidth(activeCellIndex, MARK_COLUMN_WIDTH);
                    activeCellIndex++;
                }
                activeCellIndex = originalActiveCellIndex;
                activeRowIndex++;

                for (Performance performance : member.getPerformances()
                ) {
                    ArrayList<Mark> currentMarks = new ArrayList<>();
                    currentMarks.addAll(markService.findMarkByUserAndCriterion(performance, jury));
                    activeRow = activeSheet.getRow(activeRowIndex);
                    for (Criterion criterion : criterions
                    ) {
                        activeCell = activeRow.createCell(activeCellIndex, CellType.NUMERIC);
                        if (currentMarks.isEmpty()) {
                            activeCell.setCellValue(0);
                            activeCell.setCellStyle(cellStyleMap.get("NoneMark"));
                        } else {
                            for (Mark mark : currentMarks
                            ) {
                                if (mark.getCriterion().equals(criterion)) {
                                    activeCell.setCellValue(mark.getValue());
                                    activeCell.setCellStyle(cellStyleMap.get("mark"));
                                }

                            }
                        }

                        activeCellIndex++;
                    }
                    activeCellIndex -= criterions.size();
                    activeRowIndex++;
                }
                //Заполнение общих оценок по каждому жюри
                activeRow = activeSheet.getRow(activeRowIndex);
                activeCell = activeRow.createCell(activeCellIndex, CellType.STRING);
                activeSheet.addMergedRegion(new CellRangeAddress(activeRowIndex, activeRowIndex, activeCellIndex, activeCellIndex + criterions.size() - 1));
                activeCell.setCellValue(markService.getSummaryMarkByAllPerformancesByConcreteJury(member, jury));
                activeCell.setCellStyle(cellStyleMap.get("summaryMarkByConcreteJury"));
                activeRowIndex = originalActiveRowIndex;
                activeCellIndex += criterions.size();

            }
            //Общие оценки
            int originalActiveCellIndex = activeCellIndex;
            int originalActiveRowIndex = activeRowIndex;
            for (Criterion criterion : criterions
            ) {
                //Title of SUMMARY criterion
                activeRow = activeSheet.getRow(activeRowIndex);
                activeCell = activeRow.createCell(activeCellIndex, CellType.STRING);
                activeCell.setCellValue(criterion.getName());
                activeCell.setCellStyle(cellStyleMap.get("criterionName"));
                activeSheet.setColumnWidth(activeCellIndex, MARK_COLUMN_WIDTH);
                activeCellIndex++;
            }
            activeCellIndex = originalActiveCellIndex;
            activeRowIndex++;

            // Просчет суммарной оценки по каждому критерию
            originalActiveRowIndex = activeRowIndex;
            for (int j = 0; j < member.getPerformances().size(); j++) {
                activeRow = activeSheet.getRow(activeRowIndex + j);
                originalActiveCellIndex = activeCellIndex;

                for (int i = 0; i < criterions.size(); i++) {
                    int summaryByConcreteCriterion = 0;
                    activeCellIndex += i;
                    activeCellIndex -= criterions.size();
                    while (activeCellIndex > 1) {
                        activeCell = activeRow.getCell(activeCellIndex);
                        summaryByConcreteCriterion += activeCell.getNumericCellValue();
                        activeCellIndex -= criterions.size();
                    }
                    activeCellIndex = originalActiveCellIndex;
                    activeCell = activeRow.createCell(activeCellIndex + i, CellType.STRING);
                    activeCell.setCellValue(summaryByConcreteCriterion);
                }
            }
            //Заполнение общей оценки за все Performances
            activeRowIndex = activeRow.getRowNum() + 1;
            activeRow = activeSheet.getRow(activeRowIndex);
            activeCell = activeRow.createCell(activeCellIndex, CellType.STRING);
            activeSheet.addMergedRegion(new CellRangeAddress(activeRowIndex, activeRowIndex, activeCellIndex, activeCellIndex + criterions.size() - 1));
            activeCell.setCellValue(summaryMarkByMember.get(member));
            activeCell.setCellStyle(cellStyleMap.get("summaryMarkByConcreteJury"));
            activeRowIndex = originalActiveRowIndex;
            activeCellIndex += criterions.size();


            //Заполняем место, которое занял участник
            activeRowIndex = originalActiveRowIndex - 1;
            activeRow=activeSheet.getRow(activeRowIndex);
            activeCell = activeRow.createCell(activeCellIndex, CellType.STRING);
            activeSheet.addMergedRegion(new CellRangeAddress(activeRowIndex, activeRowIndex + member.getPerformances().size() + 1, activeCellIndex, activeCellIndex));
            activeCell.setCellValue(placeMember.get(member));
            activeCell.setCellStyle(cellStyleMap.get("summaryMark"));

            //Переход к следующему участнику
            activeCellIndex = 0;
            activeRowIndex += (member.getPerformances().size() + 2);
        }

    }

    private void createAllCellStyle() {
        cellStyleMap.put("title", createCellStyleForTitle());
        cellStyleMap.put("subTitle", createCellStyleForSubTitle());
        cellStyleMap.put("category", createCellStyleForLabelCategory());
        cellStyleMap.put("columnName", createCellStyleForColumnName());
        cellStyleMap.put("criterionName", createCellStyleForCriterionName());
        cellStyleMap.put("mark", createCellStyleForMark());
        cellStyleMap.put("NoneMark", createCellStyleForNoneMark());
        cellStyleMap.put("summaryMarkByConcreteJury", createCellStyleForSummaryMarkByConcreteJury());
        cellStyleMap.put("summaryMark", createCellStyleForSummaryMark());


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

    private CellStyle createCellStyleForColumnName() {
        Font newFont = workbook.createFont();
        newFont.setBold(true);
        newFont.setColor(HSSFColor.HSSFColorPredefined.BLUE_GREY.getIndex());
        newFont.setFontHeightInPoints((short) 8);
        newFont.setItalic(false);

        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFont(newFont);
        cellStyle.setWrapText(true);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        return cellStyle;
    }

    private CellStyle createCellStyleForCriterionName() {
        Font newFont = workbook.createFont();
        newFont.setBold(true);
        newFont.setColor(HSSFColor.HSSFColorPredefined.BLUE_GREY.getIndex());
        newFont.setFontHeightInPoints((short) 4);
        newFont.setItalic(false);

        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFont(newFont);
        cellStyle.setWrapText(true);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        return cellStyle;
    }

    private CellStyle createCellStyleForMark() {
        Font newFont = workbook.createFont();
        newFont.setBold(false);
        newFont.setColor(HSSFColor.HSSFColorPredefined.DARK_GREEN.getIndex());
        newFont.setFontHeightInPoints((short) 8);
        newFont.setItalic(false);

        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFont(newFont);
        cellStyle.setWrapText(false);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        return cellStyle;
    }

    private CellStyle createCellStyleForNoneMark() {
        Font newFont = workbook.createFont();
        newFont.setBold(false);
        newFont.setColor(HSSFColor.HSSFColorPredefined.DARK_RED.getIndex());
        newFont.setFontHeightInPoints((short) 8);
        newFont.setItalic(false);

        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFont(newFont);
        cellStyle.setWrapText(false);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        return cellStyle;
    }

    private CellStyle createCellStyleForSummaryMarkByConcreteJury() {
        Font newFont = workbook.createFont();
        newFont.setBold(true);
        newFont.setColor(HSSFColor.HSSFColorPredefined.DARK_BLUE.getIndex());
        newFont.setFontHeightInPoints((short) 9);
        newFont.setItalic(false);

        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFont(newFont);
        cellStyle.setWrapText(false);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        return cellStyle;
    }

    private CellStyle createCellStyleForSummaryMark() {
        Font newFont = workbook.createFont();
        newFont.setBold(true);
        newFont.setColor(HSSFColor.HSSFColorPredefined.GREEN.getIndex());
        newFont.setFontHeightInPoints((short) 12);
        newFont.setItalic(false);

        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFont(newFont);
        cellStyle.setWrapText(false);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
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
