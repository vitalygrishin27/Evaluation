package app.controller;

import app.model.*;
import app.service.UserService;
import app.service.impl.ConfigurationServiceImpl;
import app.service.impl.POIServiceImpl;
import app.service.impl.PerformanceServiceImpl;
import com.ibm.icu.text.Transliterator;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.*;


@Controller
public class OnlineController {

    private static final Logger log = Logger.getLogger(OnlineController.class);

    @Autowired
    POIServiceImpl poiService;

    @Autowired
    PerformanceServiceImpl performanceService;

    @Autowired
    ConfigurationServiceImpl configurationService;

    @Autowired
    UserService userService;


    @RequestMapping(value = "/online", method = RequestMethod.GET)
    public String online(Model model, Principal principal) {
        PerformanceWrapperListByCategory performanceWrapperListByCategory = new PerformanceWrapperListByCategory(new ArrayList<PerformancesWrapperList>());
        List<Performance> sortedListPerformanceByTurnNumber = performanceService.findAllPerformances();
        List<Category> sortedListCategory = new LinkedList<>();
        for (Performance perfomance : sortedListPerformanceByTurnNumber
        ) {
            if (!sortedListCategory.contains(perfomance.getMember().getCategory())) {
                sortedListCategory.add(perfomance.getMember().getCategory());
            }
        }

        for (Category category : sortedListCategory
        ) {
            List<PerformancesWrapper> list = new ArrayList<>();
            for (Performance performance : performanceService.findPerformancesByCategory(category)
            ) {
                list.add(new PerformancesWrapper(performance.getPerformanceId(),
                        performance.getPerformanceName(),
                        performance.getMember().getLastName() + " " + performance.getMember().getName() + " " + performance.getMember().getSecondName(),
                        performance.getTurnNumber(), getSummaryMarks(performance)
                ));
            }
            PerformancesWrapperList performancesWrapperList = new PerformancesWrapperList(category.getCategoryName(), list);
            performanceWrapperListByCategory.addToPWLBC(performancesWrapperList);
        }

        model.addAttribute("performancesWrapperListByCategory", performanceWrapperListByCategory);
        model.addAttribute("isSortable", configurationService.getConfiguration().getIsSortable());
        return "online/online";
    }

    private Map<User, Integer> getSummaryMarks(Performance performance) {
        Map<app.model.User, Integer> result = new HashMap<>();
        for (app.model.User user : userService.findAllJuries()
        ) {
            result.put(user, 0);
        }

        for (Mark mark : performance.getMarks()
        ) {
            result.put(mark.getUser(), result.get(mark.getUser()) + mark.getValue());
        }

        return result;
    }

    @RequestMapping(value = "/online/send", method = RequestMethod.POST)
    public void onlineSend(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PerformanceServiceImpl.setCURRENT_ID_PERFORMANCE_IN_EVALUATION(Integer.valueOf(req.getParameter("performanceID")));
        System.out.println("New active performance id - " + PerformanceServiceImpl.getCURRENT_ID_PERFORMANCE_IN_EVALUATION());
    }

    @RequestMapping(value = "/online/saveTurnNumber", method = RequestMethod.POST)
    public String saveTurnNumber(String json) {
        List<String> splitList = Arrays.asList(json.split("\\s*,\\s*"));
        List<Integer> queue = new LinkedList<>();
        for (String element : splitList
        ) {
            try {
                queue.add(Integer.valueOf(element));
            } finally {
                continue;
            }
        }
        int turn = 1;
        for (int element : queue
        ) {
            Performance performance = performanceService.findPerformanceById(element);
            performance.setTurnNumber(turn++);
            performanceService.update(performance);
        }
        System.out.println(json);
        return "redirect:/online?lang=" + Locale.getDefault();
    }


    @RequestMapping(value = "/online/getNewMarksForActivePerformance", method = RequestMethod.POST)
    public void getNewMarksForActivePerformance(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.info("Вызов метода getNewMarksForActivePerformance");
        JSONObject jsonObjectResponse = new JSONObject();
        if(PerformanceServiceImpl.getCURRENT_ID_PERFORMANCE_IN_EVALUATION()!=-1) {
            Performance activePerformance = performanceService.findPerformanceById(PerformanceServiceImpl.getCURRENT_ID_PERFORMANCE_IN_EVALUATION());

            for (Map.Entry<User, Integer> entry : getSummaryMarks(activePerformance).entrySet()
            ) {
                try {
                    jsonObjectResponse.put(entry.getKey().getLogin() + activePerformance.getPerformanceId(), entry.getValue());
                } catch (JSONException e) {
                    System.out.println("ERROR with JSON");
                    e.printStackTrace();
                }

            }
            jsonObjectResponse.put("activePerformanceId", activePerformance.getPerformanceId());
            //  log.error("ошибка");
        }

        resp.getWriter().write(String.valueOf(jsonObjectResponse));
        resp.flushBuffer();
    }


    @RequestMapping(value = "/statement", method = RequestMethod.GET)
    public void createStatement(HttpServletResponse response) {

        poiService.createNewDocument(configurationService.getConfiguration().getContestName());


        try {
            ServletOutputStream out = response.getOutputStream();
            byte[] byteArray = Files.readAllBytes(Paths.get("FullStatement.xls"));
            response.setContentType("application/vnd.ms-excel");
            String filename = Transliterator.getInstance("Russian-Latin/BGN").transliterate(configurationService.getConfiguration().getContestName());
            response.setHeader("Content-Disposition", "attachment; filename=" + filename + ".xls");
            out.write(byteArray);
            out.flush();
            out.close();
            //response.flushBuffer();
        } catch (IOException e) {
            e.printStackTrace();
        }


        //    return "redirect:/?lang=" + Locale.getDefault();
    }

}
