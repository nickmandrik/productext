package commerce.cloud.trainings.webapi.controller.internal;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


/**
 * @author Nick Mandrik
 */


@Controller
public class PageController implements ISourcePathConstants {

    @RequestMapping("/")
    public String getHomePage() {
        return HOME_PAGE_PATH;
    }
}
