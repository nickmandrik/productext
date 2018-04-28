package commerce.cloud.trainings.webapi.occ.base;


/**
 * @author Nick Mandrik
 */


public interface ICommerceBaseParams {
    public static final String CONTENT_TYPE_HEADER_ARG = "Content-type";

    public static final String AUTHORIZATION_HEADER_ARG = "Authorization";
    public static final String BEARER_SPACE_HEADER_VALUE = "Bearer ";

    public static final String GRANT_TYPE_PARAM_ARG = "grant_type";
    public static final String CLIENT_CREDENTIALS_PARAM_VALUE = "client_credentials";

    public static final String ACCESS_TOKEN_OUTPUT_PARAM_ARG = "access_token";
    public static final String VERIFY_TOKEN_OUTPUT_PARAM_ARG = "success";

    public static final String OK_STATUS_CODE = "200";

    public static final String GET_PARAM = "GET";
    public static final String POST_PARAM = "POST";
    public static final String PUT_PARAM = "PUT";
    public static final String DELETE_PARAM = "DELETE";

}
