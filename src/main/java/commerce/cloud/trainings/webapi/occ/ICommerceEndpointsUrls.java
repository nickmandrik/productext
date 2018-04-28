package commerce.cloud.trainings.webapi.occ;


/**
 * @author Nick Mandrik
 */


public interface ICommerceEndpointsUrls {
    String POST_CCADMIN_LOGIN = "ccadminui/v1/login";
    String POST_CCADMIN_LOGOUT = "ccadmin/v1/logout";
    String POST_CCADMIN_REFRESH = "ccadmin/v1/refresh";
    String POST_CCADMIN_VERIFY = "ccadmin/v1/verify";

    String GET_EXPORT_ASSETS = "ccadmin/v1/asset/export";

    String POST_UPLOAD_ASSETS = "ccadmin/v1/asset/upload";
    String POST_VALIDATE_ASSETS = "ccadmin/v1/asset/validate";
    String POST_IMPORT_ASSETS = "ccadmin/v1/asset/import";
}
