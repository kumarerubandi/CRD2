package org.hl7.davinci.endpoint.cdshooks.services.crd.r4;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import com.google.gson.JsonObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.cdshooks.Hook;
import org.hl7.davinci.PrefetchTemplateElement;
import org.hl7.davinci.RequestIncompleteException;
import org.hl7.davinci.endpoint.cdshooks.services.crd.CdsService;
import org.hl7.davinci.endpoint.cql.r4.CqlExecutionContextBuilder;
import org.hl7.davinci.endpoint.rules.CoverageRequirementRuleCriteria;
import org.hl7.davinci.endpoint.rules.CoverageRequirementRuleFinder;
import org.hl7.davinci.endpoint.rules.CoverageRequirementRuleQuery;
import org.hl7.davinci.endpoint.rules.CoverageRequirementRuleResult;
import org.hl7.davinci.endpoint.cql.bundle.CqlBundle;
import org.hl7.davinci.endpoint.database.CoverageRequirementRule;
import org.hl7.davinci.endpoint.results.CRDResult;
import org.hl7.davinci.r4.FhirComponents;
import org.hl7.davinci.r4.Utilities;
import org.hl7.davinci.r4.crdhook.CrdPrefetchTemplateElements;
import org.hl7.davinci.r4.crdhook.orderreview.OrderReviewRequest;
import org.hl7.davinci.r4.crdhook.orderselect.OrderSelectRequest;
import org.hl7.fhir.r4.model.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.opencds.cqf.cql.execution.Context;

import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Coverage;
import org.hl7.fhir.r4.model.DeviceRequest;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.springframework.web.bind.annotation.RequestBody;

@Component("r4_OrderSelectService")
public class OrderSelectService extends CdsService<OrderSelectRequest> {

    public static final String ID = "order-select-crd";
    public static final String TITLE = "order-review Coverage Requirements Discovery";
    public static final Hook HOOK = Hook.ORDER_SELECT;
    public static final String DESCRIPTION
            = "Get information regarding the coverage requirements for durable medical equipment";
    public static final List<PrefetchTemplateElement> PREFETCH_ELEMENTS = Arrays.asList(
            CrdPrefetchTemplateElements.DEVICE_REQUEST_BUNDLE,
            CrdPrefetchTemplateElements.SUPPLY_REQUEST_BUNDLE,
            CrdPrefetchTemplateElements.NUTRITION_ORDER_BUNDLE,
            CrdPrefetchTemplateElements.MEDICATION_REQUEST_BUNDLE,
            CrdPrefetchTemplateElements.SERVICE_REQUEST_BUNDLE);
    public static final FhirComponents FHIRCOMPONENTS = new FhirComponents();
    static final Logger logger = LoggerFactory.getLogger(OrderSelectService.class);

    public OrderSelectService() {
        super(ID, HOOK, TITLE, DESCRIPTION, PREFETCH_ELEMENTS, FHIRCOMPONENTS);
    }

    public List<CRDResult> getRequirements(@Valid @RequestBody OrderSelectRequest request) {
        List<CRDResult> results = new ArrayList<>();

        List<ServiceRequest> serviceRequestList = extractServiceRequests(request);
        System.out.println("Getting requirements for ");
        for (Iterator<ServiceRequest> iterator = serviceRequestList.iterator(); iterator.hasNext();) {
            ServiceRequest next = iterator.next();
            results.add(getRequirementForService(next));

        }
        if (results.isEmpty()) {
            CRDResult result = new CRDResult();
            result.addError("Could not get any Service requests");
        }

        return results;

    }

    private CRDResult getRequirementForService(ServiceRequest serviceRequest) {
        CRDResult result = new CRDResult();
        try {
            if (serviceRequest.getInsurance() == null) {
                result.addError("Insurance/Coverage information not provided");
                return result;
            } else {
                List<Coverage> coverages = serviceRequest.getInsurance().stream()
                        .map(reference -> (Coverage) reference.getResource()).collect(Collectors.toList());
                List<Organization> payors = Utilities.getPayors(coverages);
                for (Iterator<Organization> iterator = payors.iterator(); iterator.hasNext();) {
                    Organization next = iterator.next();
                    Patient patient = (Patient) serviceRequest.getSubject().getResource();
                    return checkPriorServiceAuthRequirements(serviceRequest, next, patient);

                }
            }

        } catch (Exception e) {
            result.addError("Unexpected Error occured");
            logger.info("Error occured in getRequirementForService:" + e.getMessage());
            e.printStackTrace();
        }

        result.addError("Could not fetch any payors from the coverage information provided");

        return result;
    }

    private List<ServiceRequest> extractServiceRequests(OrderSelectRequest orderReviewRequest) {
        Bundle serviceRequestBundle = orderReviewRequest.getPrefetch().getServiceRequestBundle();
        List<ServiceRequest> serviceRequestList = Utilities
                .getResourcesOfTypeFromBundle(ServiceRequest.class, serviceRequestBundle);
        return serviceRequestList;
    }

    public List<CoverageRequirementRuleResult> createCqlExecutionContexts(OrderSelectRequest request, CoverageRequirementRuleFinder ruleFinder)
            throws RequestIncompleteException {

        List<CoverageRequirementRuleResult> list = new ArrayList();
        return list;
    }

    private CRDResult checkPriorServiceAuthRequirements(ServiceRequest ser, Organization payer, Patient patient) {
        CRDResult result = new CRDResult();

        if (payer == null) {
            result.addError("Payer information not complete");
        }
        if (ser == null) {
            result.addError("Service request information not complete");
        }
        if (patient == null) {
            result.addError("patient information not complete");
        }

        if (result.getErrors().isEmpty()) {
            Identifier payerIdentifier = getPayerIdentifier(payer);
            payer.getIdentifier().add(payerIdentifier);

            Bundle bundle = new Bundle();

            bundle.addEntry().setResource(payer);
            bundle.addEntry().setResource(ser);
            bundle.addEntry().setResource(patient);
            return getResult(bundle);
        }
        return result;
    }

}


