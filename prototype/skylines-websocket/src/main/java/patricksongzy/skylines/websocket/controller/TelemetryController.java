package patricksongzy.skylines.websocket.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TelemetryController {
    private static final Logger LOG = LogManager.getLogger(TelemetryController.class);

    @GetMapping(path = "/live")
    public String live() {
        LOG.info("liveness check succeeded");
        return "live";
    }
}
