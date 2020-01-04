package org.hl7.davinci.endpoint.cdshooks.services.crd;

import ca.uhn.fhir.context.FhirContext;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.validation.Valid;

import org.cdshooks.CdsRequest;
import org.cdshooks.CdsResponse;
import org.cdshooks.Hook;
import org.cdshooks.Link;
import org.cdshooks.Prefetch;
import org.hl7.davinci.FhirComponentsT;
import org.hl7.davinci.PrefetchTemplateElement;
import org.hl7.davinci.RequestIncompleteException;
import org.hl7.davinci.endpoint.YamlConfig;
import org.hl7.davinci.endpoint.components.CardBuilder;
import org.hl7.davinci.endpoint.components.CardBuilder.CqlResultsForCard;
import org.hl7.davinci.endpoint.components.PrefetchHydrator;
import org.hl7.davinci.endpoint.database.RequestService;
import org.hl7.davinci.endpoint.results.CRDResult;
import org.hl7.davinci.endpoint.rules.CoverageRequirementRuleCriteria;
import org.hl7.davinci.endpoint.rules.CoverageRequirementRuleFinder;
import org.hl7.davinci.endpoint.rules.CoverageRequirementRuleResult;
import org.hl7.davinci.r4.crdhook.orderreview.OrderReviewRequest;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Bundle;
import org.json.JSONObject;
import org.opencds.cqf.cql.execution.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

@Component
public abstract class CdsService<requestTypeT extends CdsRequest<?, ?>> {

    static final Logger logger = LoggerFactory.getLogger(CdsService.class);

    /**
     * The {id} portion of the URL to this service which is available at
     * {baseUrl}/cds-services/{id}. REQUIRED
     */
    public String id = null;

    /**
     * The hook this service should be invoked on. REQUIRED
     */
    public Hook hook = null;

    /**
     * The human-friendly name of this service. RECOMMENDED
     */
    public String title = null;

    /**
     * The description of this service. REQUIRED
     */
    public String description = null;

    /**
     * An object containing key/value pairs of FHIR queries that this service is
     * requesting that the EHR prefetch and provide on each service call. The
     * key is a string that describes the type of data being requested and the
     * value is a string representing the FHIR query. OPTIONAL
     */
    public Prefetch prefetch = null;
    Class<?> requestClass = null;

    @Autowired
    private YamlConfig myConfig;

    @Autowired
    RequestService requestService;

    @Autowired
    private CoverageRequirementRuleFinder ruleFinder;

    private List<PrefetchTemplateElement> prefetchElements = null;
    private FhirComponentsT fhirComponents;

    /**
     * Create a new cdsservice.
     *
     * @param id Will be used in the url, should be unique.
     * @param hook Which hook can call this.
     * @param title Human title.
     * @param description Human description.
     * @param prefetchElements List of prefetch elements, will be in prefetch
     * template.
     * @param fhirComponents Fhir components to use
     */
    public CdsService(String id, Hook hook, String title, String description,
            List<PrefetchTemplateElement> prefetchElements, FhirComponentsT fhirComponents) {
        if (id == null) {
            throw new NullPointerException("CDSService id cannot be null");
        }
        if (hook == null) {
            throw new NullPointerException("CDSService hook cannot be null");
        }
        if (description == null) {
            throw new NullPointerException("CDSService description cannot be null");
        }
        this.id = id;
        this.hook = hook;
        this.title = title;
        this.description = description;
        this.prefetchElements = prefetchElements;
        prefetch = new Prefetch();
        for (PrefetchTemplateElement prefetchElement : prefetchElements) {
            this.prefetch.put(prefetchElement.getKey(), prefetchElement.getQuery());
        }
        this.fhirComponents = fhirComponents;
    }

    public List<PrefetchTemplateElement> getPrefetchElements() {
        return prefetchElements;
    }

    public CdsResponse handleRequest(@Valid @RequestBody requestTypeT request, URL applicationBaseUrl) {
        CdsResponse response = new CdsResponse();
        System.out.println("begin execution");
        List<CRDResult> res = getRequirements(request);
        for (Iterator<CRDResult> iterator = res.iterator(); iterator.hasNext();) {
            CRDResult next = iterator.next();
            CardBuilder.CqlResultsForCard results = new CardBuilder.CqlResultsForCard();
            Link smartAppLink = smartLinkBuilder(next.getAppContext());
            response.addCard(CardBuilder.transform(results, smartAppLink));
            next.dumpAppContext();

        }

        return response;
    }

    public List<CRDResult> getRequirements(@Valid @RequestBody requestTypeT request) {
        List<CRDResult> results = new ArrayList<>();
        return results;
    }

    /*private Link smartLinkBuilder(String patientId, String fhirBase, URL applicationBaseUrl, String questionnaireUri,
                                String reqResourceId, CoverageRequirementRuleCriteria criteria) {
    URI configLaunchUri = myConfig.getLaunchUrl();
    String launchUrl;
    if (myConfig.getLaunchUrl().isAbsolute()) {
      launchUrl = myConfig.getLaunchUrl().toString();
    } else {
      try {
        launchUrl = new URL(applicationBaseUrl.getProtocol(), applicationBaseUrl.getHost(),
            applicationBaseUrl.getPort(), applicationBaseUrl.getFile() + configLaunchUri.toString(),
            null).toString();
      } catch (MalformedURLException e) {
        String msg = "Error creating smart launch URL";
        logger.error(msg);
        throw new RuntimeException(msg);
      }
    }

    if (fhirBase != null && fhirBase.endsWith("/")) {
      fhirBase = fhirBase.substring(0, fhirBase.length() - 1);
    }
    if (patientId != null && patientId.startsWith("Patient/")) {
      patientId = patientId.substring(8,patientId.length());
    }

    // PARAMS:
    // template is the uri of the questionnaire
    // request is the ID of the device request or medrec (not the full URI like the IG says, since it should be taken from fhirBase
    HashMap<String,String> appContextMap = new HashMap<>();
    appContextMap.put("template", questionnaireUri);
    appContextMap.put("request", reqResourceId);
    String filepath = "../../getfile/" + criteria.getQueryString();
    String appContext = "template=" + questionnaireUri + "&request=" + reqResourceId + "&filepath=";
    if (myConfig.getIncludeFilepathInAppContext()) {
      appContext = appContext + filepath;
    } else {
      appContext = appContext + "_";
    }
    logger.info("smarLinkBuilder: appContext: " + appContext);

    if (myConfig.isAppendParamsToSmartLaunchUrl()) {
      launchUrl = launchUrl + "?iss=" + fhirBase + "&patientId=" + patientId + "&template=" + questionnaireUri + "&request=" + reqResourceId;
    }else {
      // TODO: The iss should be set by the EHR?
      launchUrl = launchUrl;
    }

    Link link = new Link();
    link.setType("smart");
    link.setLabel("SMART App");
    link.setUrl(launchUrl);

    link.setAppContext(appContext);

    return link;
  }*/
    private Link smartLinkBuilder(String appContext) {
        URI configLaunchUri = myConfig.getLaunchUrl();
        String launchUrl = "";
        if (myConfig.getLaunchUrl().isAbsolute()) {
            launchUrl = myConfig.getLaunchUrl().toString();
        }
        /*
    else {
      try {
        launchUrl = new URL(applicationBaseUrl.getProtocol(), applicationBaseUrl.getHost(),
            applicationBaseUrl.getPort(), applicationBaseUrl.getFile() + configLaunchUri.toString(),
            null).toString();
      } catch (MalformedURLException e) {
        String msg = "Error creating smart launch URL";
        logger.error(msg);
        throw new RuntimeException(msg);
      }
    }

    if (fhirBase != null && fhirBase.endsWith("/")) {
      fhirBase = fhirBase.substring(0, fhirBase.length() - 1);
    }
    if (patientId != null && patientId.startsWith("Patient/")) {
      patientId = patientId.substring(8,patientId.length());
    }

    // PARAMS:
    // template is the uri of the questionnaire
    // request is the ID of the device request or medrec (not the full URI like the IG says, since it should be taken from fhirBase
    HashMap<String,String> appContextMap = new HashMap<>();
    appContextMap.put("template", questionnaireUri);
    appContextMap.put("request", reqResourceId);
    //String filepath = "../../getfile/" + criteria.getQueryString();
    String filepath = "";
    String appContext = "template=" + questionnaireUri + "&request=" + reqResourceId + "&filepath=";
    if (myConfig.getIncludeFilepathInAppContext()) {
      appContext = appContext + filepath;
    } else {
      appContext = appContext + "_";
    }
    
      launchUrl = launchUrl;
    
         */

        Link link = new Link();
        link.setType("smart");
        link.setLabel("SMART App");
        link.setUrl(launchUrl);

        link.setAppContext(appContext);

        return link;
    }

    // Implement this in child class
    public abstract List<CoverageRequirementRuleResult> createCqlExecutionContexts(requestTypeT request, CoverageRequirementRuleFinder ruleFinder)
            throws RequestIncompleteException;

    protected CRDResult getResult(Bundle b) {
        CRDResult result = new CRDResult();
        try {
            String cqlJsonStr = FhirContext.forR4().newJsonParser().encodeResourceToString(b);
            System.out.println("The bundle is " + cqlJsonStr);
            System.out.println("Trying to connect to " + myConfig.getCdsServer() + "/getCqlData");
            URL cqlDataUrl = new URL(myConfig.getCdsServer() + "/getCqlData");
            byte[] cqlReqDataBytes = cqlJsonStr.getBytes("UTF-8");
            HttpURLConnection cqlDataconn = (HttpURLConnection) cqlDataUrl.openConnection();
            cqlDataconn.setRequestMethod("POST");
            cqlDataconn.setRequestProperty("Content-Type", "application/json");
            cqlDataconn.setRequestProperty("Accept", "application/json");

            cqlDataconn.setDoOutput(true);
            cqlDataconn.getOutputStream().write(cqlReqDataBytes);
            BufferedReader cqlResReader = new BufferedReader(new InputStreamReader(cqlDataconn.getInputStream(),
                    "UTF-8"));
            String line = null;
            StringBuilder cqlResStrBuilder = new StringBuilder();
            while ((line = cqlResReader.readLine()) != null) {
                cqlResStrBuilder.append(line);
            }

            JSONObject cqlResObj = new JSONObject(cqlResStrBuilder.toString());

            result.setResources(b);
            result.setCdsResult(cqlResObj);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

}
