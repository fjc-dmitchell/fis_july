package gov.fjc.fis.app;

import gov.fjc.fis.view.report.educationprogramsreport.EducationProgramsReportView;
import io.jmix.flowui.DialogWindows;
import org.springframework.stereotype.Component;

@Component("fis_Report")
public class Report {
    private final DialogWindows dialogWindows;

    public Report(DialogWindows dialogWindows) {
        this.dialogWindows = dialogWindows;
    }

    public void openEducationProgramsReportView() {
        dialogWindows.view(null, EducationProgramsReportView.class).open();
    }
}