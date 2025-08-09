package gov.fjc.fis.app;

import com.vaadin.flow.component.Unit;
import gov.fjc.fis.view.report.budgetrequestreport.BudgetRequestReportView;
import gov.fjc.fis.view.report.divisionobligationsreport.DivisionObligationsReportView;
import gov.fjc.fis.view.report.educationprogramsreport.EducationProgramsReportView;
import gov.fjc.fis.view.report.statusoffundsreport.StatusOfFundsReportView;
import io.jmix.flowui.DialogWindows;
import io.jmix.flowui.component.UiComponentUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("fis_ReportRouter")
public class ReportRouter {
    @Autowired
    private DialogWindows dialogWindows;

    public void openStatusOfFundsReport() {
        dialogWindows.view(UiComponentUtils.getCurrentView(), StatusOfFundsReportView.class).open().setWidth(20, Unit.EM);
    }

    public void openEducationProgramsReport() {
        dialogWindows.view(UiComponentUtils.getCurrentView(), EducationProgramsReportView.class).open();
    }

    public void openBudgetRequestReport() {
        dialogWindows.view(UiComponentUtils.getCurrentView(), BudgetRequestReportView.class).open().setWidth(20, Unit.EM);
    }

    public void openDivisionObligationsReport() {
        dialogWindows.view(UiComponentUtils.getCurrentView(), DivisionObligationsReportView.class).open();
    }
}