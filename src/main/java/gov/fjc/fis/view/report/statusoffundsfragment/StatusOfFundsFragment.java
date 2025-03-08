package gov.fjc.fis.view.report.statusoffundsfragment;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import gov.fjc.fis.entity.Appropriation;
import gov.fjc.fis.entity.dto.CategoryDto;
import gov.fjc.fis.entity.dto.DivisionDto;
import gov.fjc.fis.service.report.StatusOfFundsReportService;
import io.jmix.core.LoadContext;
import io.jmix.flowui.component.details.JmixDetails;
import io.jmix.flowui.fragment.Fragment;
import io.jmix.flowui.fragment.FragmentDescriptor;
import io.jmix.flowui.model.CollectionLoader;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@FragmentDescriptor("status-of-funds-fragment.xml")
public class StatusOfFundsFragment extends Fragment<VerticalLayout> {

    @Autowired
    private StatusOfFundsReportService statusOfFundsReportService;
    @ViewComponent
    private CollectionLoader<CategoryDto> categorySofDl;
    @ViewComponent
    private CollectionLoader<CategoryDto> categorySof2Dl;
    @ViewComponent
    private JmixDetails oneYearDetails;
    @ViewComponent
    private JmixDetails twoYearDetails;

    Appropriation appropriation;
    List<CategoryDto> categoryBalances;

    public void setAppropriation(Appropriation appropriation) {
        this.appropriation = appropriation;
        oneYearDetails.setSummaryText(appropriation.getBudgetFiscalYear().concat(" One Year Fund"));
        twoYearDetails.setSummaryText(appropriation.getBudgetFiscalYear().concat(" Two Year Fund"));

        categoryBalances = statusOfFundsReportService.getStatusOfFundsCategoryData(appropriation, 0, false);
        categorySofDl.load();
        categorySof2Dl.load();
    }

    @Subscribe(target = Target.HOST_CONTROLLER)
    protected void onHostReady(final View.ReadyEvent event) {
        categorySofDl.load();
    }

    @Install(to = "categorySofDl", target = Target.DATA_LOADER)
    protected List<CategoryDto> categorySofDlLoadDelegate(final LoadContext<CategoryDto> loadContext) {
        return categoryBalances;
    }

    @Install(to = "categorySof2Dl", target = Target.DATA_LOADER)
    protected List<CategoryDto> categorySof2DlLoadDelegate(final LoadContext<CategoryDto> loadContext) {
        var twoYearCategories = categoryBalances.stream().filter(CategoryDto::isTwoYearCategory).toList();
//        twoYearDetails.setVisible(!twoYearDivisions.isEmpty());
        return twoYearCategories;
    }


}