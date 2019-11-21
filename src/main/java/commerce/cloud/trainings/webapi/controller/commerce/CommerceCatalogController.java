package commerce.cloud.trainings.webapi.controller.commerce;

import commerce.cloud.trainings.webapi.occ.base.CommerceBaseRestUtil;
import commerce.cloud.trainings.webapi.occ.base.CommerceRestException;
import commerce.cloud.trainings.webapi.occ.catalog.CommerceCatalogRestUtil;
import commerce.cloud.trainings.webapi.occ.catalog.model.ExportCatalogModel;
import commerce.cloud.trainings.webapi.occ.catalog.model.ImportAssetsModel;
import commerce.cloud.trainings.webapi.occ.catalog.model.ValidateAssetsModel;
import org.apache.catalina.connector.Response;
import org.apache.catalina.connector.ResponseFacade;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.awt.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;


/**
 * @author Nick Mandrik
 */


@RestController
public class CommerceCatalogController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommerceBaseRestUtil.class);
    private CommerceCatalogRestUtil commerceCatalogRestUtil;

    @Autowired
    public CommerceCatalogController(CommerceCatalogRestUtil commerceCatalogRestUtil) {
        this.commerceCatalogRestUtil = commerceCatalogRestUtil;
    }

    @RequestMapping(value = "/commerce/api/exportAssets", method = RequestMethod.GET)
    public void exportAssets(HttpServletResponse response) {

        ExportCatalogModel contentModel = null;
        try {
            contentModel = commerceCatalogRestUtil.exportCatalogRequest();
        } catch(CommerceRestException e) {
            e.printStackTrace();
        }

        if(contentModel != null && contentModel.isValid()) {
            String mimeType = "application/octet-stream";
            response.setContentType(mimeType);

            response.setHeader("Content-Disposition",contentModel.getContentDisposition());
            response.setHeader("Set-Cookie","fileDownload=true; path=/");

            try {
                response.setContentLength(Math.toIntExact(contentModel.getContentLength()));
                FileCopyUtils.copy(contentModel.getContent(), response.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @RequestMapping(value = "/commerce/api/validateAssets", method = RequestMethod.POST)
    public ValidateAssetsModel validateAssets(@RequestParam MultipartFile file) {

        String fileName = file.getOriginalFilename();
        String bytesEncodedString = null;
        try {
            byte[] bytesEncoded = Base64.encodeBase64(file.getBytes());
            bytesEncodedString = new String(bytesEncoded);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ValidateAssetsModel validateAssetsModel = null;
        try {
            validateAssetsModel = commerceCatalogRestUtil.uploadAndValidateAssetsRequest(fileName, bytesEncodedString);
        } catch(CommerceRestException e) {
            e.printStackTrace();
        }

        return validateAssetsModel;
    }


    @RequestMapping(value = "/commerce/api/importAssets", method = RequestMethod.POST)
    public ImportAssetsModel confirmImportAssets(@RequestBody Map<String, String> tokenMap) {

        ImportAssetsModel importModel = null;
        if(tokenMap.containsKey("token")) {
            String token = tokenMap.get("token");
            try {
                importModel = commerceCatalogRestUtil.confirmImportAssets(token);
            } catch (CommerceRestException e) {
                e.printStackTrace();
            }
        }

        return importModel;
    }

    @RequestMapping(value = "/commerce/api/executeQuery", method = RequestMethod.POST)
    public void executeQuery(@RequestBody Map<String, String> requestMap, HttpServletResponse response) {

        HttpResponse executedQuery = null;
        if(requestMap.containsKey("query") && requestMap.containsKey("type")
                && requestMap.containsKey("addHeaders") && requestMap.containsKey("inputData")) {
            String query = requestMap.get("query");
            String typeQuery = requestMap.get("type");
            String addHeaders = requestMap.get("addHeaders");
            String inputData = requestMap.get("inputData");
            try {
                executedQuery = commerceCatalogRestUtil.getCommerceBaseRestUtil().executeQuery(
                        query, typeQuery, addHeaders, inputData);
            } catch (CommerceRestException e) {
                String ERROR_STRING = "{\"Internal error\": \"\"}";
                response.setStatus(500);
                response.setContentLength(ERROR_STRING.getBytes().length + e.getMessage().getBytes().length);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                try {
                    PrintWriter writer = new PrintWriter(response.getOutputStream());
                    writer.print(ERROR_STRING.substring(0, ERROR_STRING.length()-2));
                    writer.print(e.getMessage());
                    writer.print(ERROR_STRING.substring(ERROR_STRING.length()-2));
                    writer.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if(executedQuery != null) {
                HeaderIterator it = executedQuery.headerIterator();
                while (it.hasNext()) {
                    Header header = it.nextHeader();
                    response.setHeader(header.getName(), header.getValue());
                }
                response.setStatus(executedQuery.getStatusLine().getStatusCode());
                if(executedQuery.getEntity() != null) {
                    response.setContentLengthLong(executedQuery.getEntity().getContentLength());
                    response.setContentType(String.valueOf(executedQuery.getEntity().getContentType()));
                }
                if(response.getLocale() != null) {
                    response.setLocale(response.getLocale());
                }
                if(executedQuery.getEntity() != null) {
                    /*try {
                        FileCopyUtils.copy(executedQuery.getEntity().getContent(), response.getOutputStream());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }*/
                }
            }
        }
    }
}