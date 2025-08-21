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

    public List<Group> getGroups(Division division) {
        return dataManager.load(Group.class)
                .query("SELECT e FROM fis_Group e"
                        + " WHERE e.division = :division"
                        + " ORDER BY e.groupCode")
                .parameter("division", division)
                .list();
    }

    public Boolean groupsExist(Division division) {
        return dataManager.loadValue("SELECT CASE WHEN COUNT(e) > 0 THEN TRUE ELSE FALSE END"
                        + " FROM fis_Group e WHERE e.division = :division", Boolean.class)
                .parameter("division", division)
                .one();
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

    public Group getGroupByActivity(Division division, String activityNumber) {
        if (activityNumber.length() >= 2) {
            activityNumber = activityNumber.substring(0, 2);
        }
        return dataManager.load(Group.class)
                .query("SELECT g FROM fis_Group g"
                        + " WHERE g.groupCode = :activityNumber"
                        + " AND g.division = :division")
                .parameter("activityNumber", activityNumber)
                .parameter("division", division)
                .optional().orElse(null);
    }

    public Group getGroupByCode(List<Appropriation> appropriations, String groupCode) {
        return dataManager.load(Group.class)
                .query("SELECT g FROM fis_Group g"
                        + " WHERE g.groupCode = :groupCode"
                        + " AND g.division.appropriation.budgetFiscalYear = (SELECT MAX(e.budgetFiscalYear)"
                        + " FROM fis_Appropriation e WHERE e IN :appropriations)")
                .parameter("groupCode", groupCode)
                .parameter("appropriations", appropriations)
                .optional().orElse(null);
    }
}