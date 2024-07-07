package gov.fjc.fis.service;

import gov.fjc.fis.entity.Appropriation;
import gov.fjc.fis.entity.Division;
import gov.fjc.fis.entity.Group;
import io.jmix.core.DataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component("fis_GroupService")
public class GroupService {
    @Autowired
    private DataManager dataManager;

    public List<Group> getGroupsForDivision(Division division) {
        return dataManager.load(Group.class)
                .query("SELECT e FROM fis_Group e"
                        + " WHERE e.division = :division"
                        + " ORDER BY e.groupCode")
                .parameter("division", division)
                .list();
    }

    public List<Group> getGroupSearchList(List<Appropriation> fiscalYears, String divCode) {
        fiscalYears = fiscalYears.stream().sorted(Comparator.comparing(Appropriation::getBudgetFiscalYear).reversed()).toList();
        List<Group> groupList = new ArrayList<>();
        Set<String> groupCodes = null;

        for (Appropriation year : fiscalYears) {
            List<Group> groupsInBfyList = dataManager.load(Group.class)
                    .query("SELECT e FROM fis_Group e"
                            + " WHERE e.division.appropriation = :year"
                            + " AND e.division.divisionCode = :divCode"
                            + " AND e.groupCode NOT IN :groupCodes")
                    .parameter("year", year)
                    .parameter("divCode", divCode)
                    .parameter("groupCodes", groupCodes)
                    .list();
            groupList.addAll(groupsInBfyList);
            groupCodes = groupList.stream().map(Group::getGroupCode).collect(Collectors.toSet());
        }

        return groupList.stream().sorted(Comparator.comparing(Group::getGroupCode)).toList();
    }
}