package ru.litvinov.patientnotificator.config;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import ru.litvinov.patientnotificator.service.ReportService;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

@Configuration
@AllArgsConstructor
public class MBeanConfig {

    private final ReportService reportService;

    @PostConstruct
    public void registerMBeans() throws Exception {
        final ObjectName objectName = new ObjectName("patientnotificator:type=basic,name=report-service");
        final MBeanServer server = ManagementFactory.getPlatformMBeanServer();
        server.registerMBean(reportService, objectName);
    }

}
