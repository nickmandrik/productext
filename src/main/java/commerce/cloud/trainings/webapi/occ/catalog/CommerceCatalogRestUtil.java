package commerce.cloud.trainings.webapi.occ.catalog;

import commerce.cloud.trainings.webapi.occ.ICommerceEndpointsUrls;
import commerce.cloud.trainings.webapi.occ.base.CommerceBaseRestUtil;
import commerce.cloud.trainings.webapi.occ.base.CommerceRestException;
import commerce.cloud.trainings.webapi.occ.catalog.model.ExportCatalogModel;
import commerce.cloud.trainings.webapi.occ.catalog.model.ImportAssetsModel;
import commerce.cloud.trainings.webapi.occ.catalog.model.ValidateAssetsModel;
import commerce.cloud.trainings.webapi.occ.properties.CommerceValuesConfig;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;


/**
 * @author Nick Mandrik
 */


@Component
public class CommerceCatalogRestUtil implements ICommerceEndpointsUrls, ICommerceCatalogParams {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommerceCatalogRestUtil.class);

    private CommerceBaseRestUtil commerceBaseRestUtil;
    private CommerceValuesConfig valuesConfiguration;

    @Autowired
    public CommerceCatalogRestUtil(CommerceBaseRestUtil commerceBaseRestUtil) {
        this.commerceBaseRestUtil = commerceBaseRestUtil;
        this.valuesConfiguration = commerceBaseRestUtil.getValuesConfiguration();
    }

    /**
     * Execute export catalog request
     * @return {@link ExportCatalogModel} represents data about output csv
     * @throws CommerceRestException if cannot validate token and execute request or response entity stream was closed
     */
    public ExportCatalogModel exportCatalogRequest() throws CommerceRestException {
        final String url = valuesConfiguration.getAdminDomain() + GET_EXPORT_ASSETS;
        HttpGet exportRequest = new HttpGet(url);

        try {
            URIBuilder uriBuilder = new URIBuilder(exportRequest.getURI());
            uriBuilder.addParameter(TYPE_EXPORT_PARAM_ARG, PRODUCT_EXPORT_PARAM_VALUE);
            uriBuilder.addParameter(TIME_ZONE_OFFSET_EXPORT_PARAM_ARG, MINSK_TIME_ZONE_OFFSET_EXPORT_PARAM_VALUE);
            uriBuilder.addParameter(LOCALE_EXPORT_PARAM_ARG, EN_LOCALE_EXPORT_PARAM_VALUE);

            exportRequest.setURI(uriBuilder.build());
        } catch (URISyntaxException e) {
            throw new CommerceRestException("Cannot add parameters to /" + GET_EXPORT_ASSETS + " request.", e);
        }

        CloseableHttpResponse exportResponse = commerceBaseRestUtil.validateTokenAndExecuteRequest(exportRequest);

        InputStream inputStream;
        try {
            inputStream = exportResponse.getEntity().getContent();
        } catch (IOException e) {
            throw new CommerceRestException("Cannot get InputStream content from response entity /" + GET_EXPORT_ASSETS, e);
        }
        return new ExportCatalogModel(inputStream, exportResponse.getEntity().getContentLength(),
                exportResponse.getFirstHeader("Content-Disposition").getValue());
    }

    /**
     * Execute upload, validate assets request
     * @return {@link ValidateAssetsModel} represents data about varified assets
     * @throws CommerceRestException if cannot validate token and execute request or response entity stream was closed
     */
    public ValidateAssetsModel uploadAndValidateAssetsRequest(String fileName, String fileInBase64)
            throws CommerceRestException {

        String token;
        try {
            token = uploadAssetsRequest(fileName, fileInBase64);
            LOGGER.info("Upload assets complete successful. Created new token to import: " + token);
        } catch (CommerceRestException e) {
            LOGGER.error(e.getMessage());
            throw new CommerceRestException("Upload assets failure. Uri - /" + POST_UPLOAD_ASSETS);
        }

        ValidateAssetsModel validateAssetsModel;
        try {
            validateAssetsModel = validateAssetsRequest(token);
            LOGGER.info("Validate assets complete successful for import with token " + token);
        } catch (CommerceRestException e) {
            LOGGER.error(e.getMessage());
            throw new CommerceRestException("Validate assets failure. Uri - /" + POST_VALIDATE_ASSETS);
        }

        return validateAssetsModel;
    }

    private String uploadAssetsRequest(String fileName, String fileInBase64) throws CommerceRestException {
        String url = valuesConfiguration.getAdminDomain() + POST_UPLOAD_ASSETS;
        String json = String.format("{\"%s\": \"%s\", \"%s\": \"%s\"}",
                FILENAME_UPLOAD_PARAM_ARG, fileName, FILE_UPLOAD_PARAM_ARG, fileInBase64);

        CloseableHttpResponse response = commerceBaseRestUtil.executePostJsonRequest(url, json);

        String token;
        try {
            String responseStr = EntityUtils.toString(response.getEntity());
            JSONObject jsonResponse = new JSONObject(responseStr);
            String uploadStatus = jsonResponse.getString(STATUS_UPLOAD_OUTPUT_PARAM_ARG);
            if(uploadStatus.equals(SUCCESS_UPLOAD_OUTPUT_PARAM_VALUE)) {
                token = jsonResponse.getString(TOKEN_IMPORT_PARAM_ARG);
            } else {
                throw new CommerceRestException("Upload assets /" + POST_UPLOAD_ASSETS +
                        " failure. Upload status: " + uploadStatus);
            }
        } catch (IOException | JSONException e) {
            throw new CommerceRestException("Cannot parse response from /" + POST_UPLOAD_ASSETS, e);
        }

        return token;
    }


    private ValidateAssetsModel validateAssetsRequest(String token) throws CommerceRestException {
        final String url = valuesConfiguration.getAdminDomain() + POST_VALIDATE_ASSETS;
        String json = String.format("{\"%s\": \"%s\"}", TOKEN_IMPORT_PARAM_ARG, token);

        CloseableHttpResponse validateResponse = commerceBaseRestUtil.executePostJsonRequest(url, json);

        ValidateAssetsModel validateModel = new ValidateAssetsModel();
        JSONObject jsonResponse;
        try {
            String responseStr = EntityUtils.toString(validateResponse.getEntity());
            jsonResponse = new JSONObject(responseStr);
            validateModel.setTotal(Integer.parseInt(
                    jsonResponse.getString(ValidateAssetsModel.TOTAL_ARG)
            ));
            validateModel.setModifiedCount(Integer.parseInt(
                    jsonResponse.getString(ValidateAssetsModel.MODIFIED_COUNT_ARG)
            ));
            validateModel.setNewCount(Integer.parseInt(
                    jsonResponse.getString(ValidateAssetsModel.NEW_COUNT_ARG)
            ));
            validateModel.setUnchangedCount(Integer.parseInt(
                    jsonResponse.getString(ValidateAssetsModel.UNCHANGED_COUNT_ARG)
            ));
            validateModel.setWarningCount(Integer.parseInt(
                    jsonResponse.getString(ValidateAssetsModel.WARNING_COUNT_ARG)
            ));
            validateModel.setErrorCount(Integer.parseInt(
                    jsonResponse.getString(ValidateAssetsModel.ERROR_COUNT_ARG)
            ));
        } catch (IOException | JSONException e) {
            throw new CommerceRestException("Cannot parse response from /" + POST_VALIDATE_ASSETS, e);
        }

        validateModel.setToken(token);

        return validateModel;
    }

    public ImportAssetsModel confirmImportAssets(String token) throws CommerceRestException {
        final String url = valuesConfiguration.getAdminDomain() + POST_IMPORT_ASSETS;
        String json = String.format("{\"%s\": \"%s\"}", TOKEN_IMPORT_PARAM_ARG, token);

        CloseableHttpResponse response;
        try {
            response = commerceBaseRestUtil.executePostJsonRequest(url, json);
        } catch(CommerceRestException e) {
            LOGGER.error("Execute request /" + POST_IMPORT_ASSETS + " error");
            throw new CommerceRestException(e);
        }

        ImportAssetsModel importModel = new ImportAssetsModel();
        try {
            String responseStr = EntityUtils.toString(response.getEntity());
            JSONObject jsonResponse = new JSONObject(responseStr);
            importModel.setTotal(Integer.parseInt(
                    jsonResponse.getString(ImportAssetsModel.TOTAL_ARG)
            ));
            importModel.setModifiedErrorCount(Integer.parseInt(
                    jsonResponse.getString(ImportAssetsModel.MODIFIED_ERROR_COUNT_ARG)
            ));
            importModel.setModifiedSuccessCount(Integer.parseInt(
                    jsonResponse.getString(ImportAssetsModel.MODIFIED_SUCCESS_COUNT_ARG)
            ));
            importModel.setUnchangedCount(Integer.parseInt(
                    jsonResponse.getString(ImportAssetsModel.UNCHANGED_COUNT_ARG)
            ));
            importModel.setNewErrorCount(Integer.parseInt(
                    jsonResponse.getString(ImportAssetsModel.NEW_ERROR_COUNT_ARG)
            ));
            importModel.setNewSuccessCount(Integer.parseInt(
                    jsonResponse.getString(ImportAssetsModel.NEW_SUCCESS_COUNT_ARG)
            ));
        } catch (IOException | JSONException e) {
            throw new CommerceRestException("Cannot parse response from /" + POST_IMPORT_ASSETS, e);
        }

        return importModel;
    }

    public CommerceBaseRestUtil getCommerceBaseRestUtil() {
        return commerceBaseRestUtil;
    }

    public void setCommerceBaseRestUtil(CommerceBaseRestUtil commerceBaseRestUtil) {
        this.commerceBaseRestUtil = commerceBaseRestUtil;
    }

    public CommerceValuesConfig getValuesConfiguration() {
        return valuesConfiguration;
    }

    public void setValuesConfiguration(CommerceValuesConfig valuesConfiguration) {
        this.valuesConfiguration = valuesConfiguration;
    }
}
