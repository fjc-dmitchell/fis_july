package gov.fjc.fis.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component("fis_ProcessDocuments")
public class ProcessDocuments implements Job {
    private static final Logger log = LoggerFactory.getLogger(ProcessDocuments.class);

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info("Processing Documents");
    }
}