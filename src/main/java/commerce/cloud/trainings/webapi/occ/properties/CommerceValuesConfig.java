package commerce.cloud.trainings.webapi.occ.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


/**
 * @author Nick Mandrik
 */


@Component
public class CommerceValuesConfig {

    @Value("${commerce.cloud.store.domain}")
    private String storeDomain;

    @Value("${commerce.cloud.admin.domain}")
    private String adminDomain;

    @Value("${commerce.cloud.access.admin}")
    private String adminLink;

    @Value("${commerce.cloud.access.agent}")
    private String agentLink;

    @Value("${commerce.cloud.app.id}")
    private String applicationID;

    public String getAdminDomain() {
        return adminDomain;
    }

    public void setAdminDomain(String adminDomain) {
        this.adminDomain = adminDomain;
    }

    public String getStoreDomain() {
        return storeDomain;
    }

    public void setStoreDomain(String storeDomain) {
        this.storeDomain = storeDomain;
    }

    public String getAdminLink() {
        return adminLink;
    }

    public void setAdminLink(String adminLink) {
        this.adminLink = adminLink;
    }

    public String getAgentLink() {
        return agentLink;
    }

    public void setAgentLink(String agentLink) {
        this.agentLink = agentLink;
    }

    public String getApplicationID() {
        return applicationID;
    }

    public void setApplicationID(String applicationID) {
        this.applicationID = applicationID;
    }
}
