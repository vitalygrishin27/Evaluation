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
            Map<Member, Integer> summaryMarkByCategory = new HashMap<>();
            for (Member member : category.getMembers()
            ) {
                summaryMarkByCategory.put(member, summaryMark.get(member));
            }
            List list = new ArrayList(summaryMarkByCategory.entrySet());
            Collections.sort(list, new Comparator<Map.Entry<Integer, Integer>>() {
                @Override
                public int compare(Map.Entry<Integer, Integer> a, Map.Entry<Integer, Integer> b) {
                    if(b.getValue()==0) return -1;
                    return a.getValue() + b.getValue();
                }
            });

            int place = 0;
            int lastSummaryMark = -1;
            for (int i = 0; i < list.size(); i++) {
                if (((Map.Entry<Member, Integer>) list.get(i)).getValue() != lastSummaryMark) {
                    lastSummaryMark = ((Map.Entry<Member, Integer>) list.get(i)).getValue();
                    place++;
                }
                result.put(((Map.Entry<Member, Integer>) list.get(i)).getKey(), place);
            }
        }

        return result;
    }

    @Override
    public void deleteAllMarks() {
        repository.deleteAll();
    }
}
