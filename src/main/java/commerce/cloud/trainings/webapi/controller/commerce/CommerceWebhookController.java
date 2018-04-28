package commerce.cloud.trainings.webapi.controller.commerce;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author Nick Mandrik
 */


@RestController
public class CommerceWebhookController {

    @RequestMapping(value = "/test", method = RequestMethod.POST)
    public ResponseEntity test() {
        return ResponseEntity.ok().build();
    }
}
