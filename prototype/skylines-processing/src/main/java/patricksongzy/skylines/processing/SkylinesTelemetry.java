package patricksongzy.skylines.processing;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import patricksongzy.skylines.processing.streaming.TelemetryStream;

public class SkylinesTelemetry {
    public static void main(String[] args) throws Exception {
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        context.getBean(TelemetryStream.class).execute();
    }
}
