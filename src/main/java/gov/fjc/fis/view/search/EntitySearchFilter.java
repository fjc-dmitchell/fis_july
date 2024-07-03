package gov.fjc.fis.view.search;

import io.jmix.core.querycondition.Condition;

import java.util.List;

public interface EntitySearchFilter {
    List<Condition> getCustomConditions();
    void clearSearchFilters();
}
