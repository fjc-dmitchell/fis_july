package gov.fjc.fis.view.report.divisionbalancefragment;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import gov.fjc.fis.entity.Appropriation;
import gov.fjc.fis.entity.dto.DivisionDto;
import gov.fjc.fis.service.report.DivisionBalancesReportService;
import io.jmix.core.LoadContext;
import io.jmix.flowui.component.details.JmixDetails;
import io.jmix.flowui.fragment.Fragment;
import io.jmix.flowui.fragment.FragmentDescriptor;
import io.jmix.flowui.model.CollectionLoader;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@FragmentDescriptor("division-balance-fragment.xml")
public class DivisionBalanceFragment extends Fragment<VerticalLayout> {

    @Autowired
    private DivisionBalancesReportService divisionBalancesReportService;
    @ViewComponent
    private CollectionLoader<DivisionDto> divisionBalancesDl;
    @ViewComponent
    private JmixDetails oneYearDetails;
    @ViewComponent
    private JmixDetails twoYearDetails;

    Appropriation appropriation;

    public void setAppropriation(Appropriation appropriation) {
        this.appropriation = appropriation;
        oneYearDetails.setSummaryText(appropriation.getBudgetFiscalYear().concat(" One Year Fund"));
        twoYearDetails.setSummaryText(appropriation.getBudgetFiscalYear().concat(" Two Year Fund"));
    }

    public void reloadAppropriation() {
        divisionBalancesDl.load();
    }

    @Subscribe(target = Target.HOST_CONTROLLER)
    protected void onHostReady(final View.ReadyEvent event) {
        divisionBalancesDl.load();
    }

    @Install(to = "divisionBalancesDl", target = Target.DATA_LOADER)
    protected List<DivisionDto> divisionBalancesDlLoadDelegate(final LoadContext loadContext) {
        return divisionBalancesReportService.getDivisionBalances(appropriation);
    }
}