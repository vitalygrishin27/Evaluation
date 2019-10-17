package app.service.impl;

import app.model.*;
import app.repository.MarkRepository;
import app.service.MarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MarkServiceImpl implements MarkService {

    @Autowired
    private MarkRepository repository;

    @Autowired
    private MemberServiceImpl memberService;

    @Autowired
    private CategoryServiceImpl categoryService;

    @Override
    public void save(Mark mark) {
        repository.saveAndFlush(mark);
    }

    @Override
    public List<Mark> findMarkByJuryAndPerformance(Performance performance, User user) {
        return repository.findMarkByUserAndPerformance(performance, user);
    }

    @Override
    public List<Mark> findAllMarkByCriterion(Criterion criterion) {
        return repository.findAllMarkByCriterion(criterion);
    }

    @Override
    public int getSummaryMarkByAllPerformancesByConcreteJury(Member member, User jury) {

        int result = 0;
        for (Performance performance : member.getPerformances()
        ) {
            for (Mark mark : performance.getMarks()
            ) {
                if (mark.getUser().equals(jury)) {
                    result += mark.getValue();
                }
            }
        }
        return result;
    }

    @Override
    public Map<Member, Integer> getSummaryMarkByAllPerformances() {
        Map<Member, Integer> resultMap = new HashMap<>();
        for (Member member : memberService.findAllMembers()
        ) {
            int result = 0;
            for (Performance performance : member.getPerformances()
            ) {
                for (Mark mark : performance.getMarks()
                ) {
                    result += mark.getValue();
                }
            }
            resultMap.put(member, result);
        }
        return resultMap;
    }

    @Override
    public Map<Member, Integer> getPlaces(Map<Member, Integer> summaryMark) {
        Map<Member, Integer> result = new HashMap<>();

        for (Category category : categoryService.findAllCategories()
        ) {
            Set<Integer> marksValues = new TreeSet<>();
            Map<Member, Integer> summaryMarkByCategory = new HashMap<>();
            for (Member member : category.getMembers()
            ) {
                int currentSummaryMark = summaryMark.get(member);
                summaryMarkByCategory.put(member, currentSummaryMark);
                marksValues.add(currentSummaryMark);
            }
            List<Integer> sortedList = new ArrayList<>(marksValues);

            for (Map.Entry<Member, Integer> entry : summaryMarkByCategory.entrySet()
            ) {
                if (sortedList.indexOf(entry.getValue()) == -1) {
                    result.put(entry.getKey(), 0);
                } else {
                    result.put(entry.getKey(), sortedList.size() - sortedList.indexOf(entry.getValue()));
                }
            }
        }

        return result;
    }

    @Override
    public void deleteAllMarks() {
        repository.deleteAll();
    }

    @Override
    public List<Mark> findMarksByPerformance(Performance performance) {
       return repository.findMarksByPerformance(performance);
    }

    @Override
    public void deleteMark(Mark mark) {
        repository.delete(mark);
    }
}
