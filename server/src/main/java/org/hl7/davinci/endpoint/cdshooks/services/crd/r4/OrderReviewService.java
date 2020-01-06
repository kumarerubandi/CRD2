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

@Component("r4_OrderReviewService")
public class OrderReviewService extends CdsService<OrderReviewRequest> {

    public static final String ID = "order-review-crd";
    public static final String TITLE = "order-review Coverage Requirements Discovery";
    public static final Hook HOOK = Hook.ORDER_REVIEW;
    public static final String DESCRIPTION
            = "Get information regarding the coverage requirements for durable medical equipment";
    public static final List<PrefetchTemplateElement> PREFETCH_ELEMENTS = Arrays.asList(
            CrdPrefetchTemplateElements.DEVICE_REQUEST_BUNDLE,
            CrdPrefetchTemplateElements.SUPPLY_REQUEST_BUNDLE,
            CrdPrefetchTemplateElements.NUTRITION_ORDER_BUNDLE,
            CrdPrefetchTemplateElements.MEDICATION_REQUEST_BUNDLE,
            CrdPrefetchTemplateElements.SERVICE_REQUEST_BUNDLE);
    public static final FhirComponents FHIRCOMPONENTS = new FhirComponents();
    static final Logger logger = LoggerFactory.getLogger(OrderReviewService.class);

    public OrderReviewService() {
        super(ID, HOOK, TITLE, DESCRIPTION, PREFETCH_ELEMENTS, FHIRCOMPONENTS);
    }

    public List<CRDResult> getRequirements(@Valid @RequestBody OrderReviewRequest request) {
        List<CRDResult> results = new ArrayList<>();
        List<DeviceRequest> deviceRequestList = extractDeviceRequests(request);
        for (Iterator<DeviceRequest> iterator = deviceRequestList.iterator(); iterator.hasNext();) {
            DeviceRequest next = iterator.next();
            results.add(getRequirementForDevice(next));
        }
        if (results.isEmpty()) {
            CRDResult result = new CRDResult();
            result.addError("Could not get any Device requests");
        }

        return results;

    }

    private CRDResult getRequirementForDevice(DeviceRequest deviceRequest) {
        CRDResult result = new CRDResult();
        try {
            if (deviceRequest.getInsurance() == null) {
                result.addError("Insurance/Coverage information not provided");
                return result;
            } else {

                List<Coverage> coverages = deviceRequest.getInsurance().stream()
                        .map(reference -> (Coverage) reference.getResource()).collect(Collectors.toList());
                List<Organization> payors = Utilities.getPayors(coverages);
                for (Iterator<Organization> iterator = payors.iterator(); iterator.hasNext();) {
                    Organization next = iterator.next();
                    Patient patient = (Patient) deviceRequest.getSubject().getResource();
                    return checkPriorDeviceAuthRequirements(deviceRequest, next, patient);

                }
            }
        } catch (Exception e) {
            result.addError("Unexpected Error occured");
            logger.info("Error occured in getRequirementForDevice:" + e.getMessage());
            e.printStackTrace();
        }
        result.addError("Could not fetch any payors from the coverage information provided");

        return result;
    }

    private List<DeviceRequest> extractDeviceRequests(OrderReviewRequest orderReviewRequest) {
        Bundle deviceRequestBundle = orderReviewRequest.getPrefetch().getDeviceRequestBundle();
        List<DeviceRequest> deviceRequestList = Utilities
                .getResourcesOfTypeFromBundle(DeviceRequest.class, deviceRequestBundle);
        return deviceRequestList;
    }

    public List<CoverageRequirementRuleResult> createCqlExecutionContexts(OrderReviewRequest request, CoverageRequirementRuleFinder ruleFinder)
            throws RequestIncompleteException {

        List<CoverageRequirementRuleResult> list = new ArrayList();
        return list;
    }

    private CRDResult checkPriorDeviceAuthRequirements(DeviceRequest device, Organization payer, Patient patient) {
        Bundle bundle = new Bundle();
        CRDResult result = new CRDResult();
        if (payer == null) {
            result.addError("Payer information not complete");
        }
        if (device == null) {
            result.addError("Device request information not complete");
        }
        if (patient == null) {
            result.addError("patient information not complete");
        }
        if (result.getErrors().isEmpty()) {
            Identifier payerIdentifier = getPayerIdentifier(payer);
            payer.getIdentifier().add(payerIdentifier);

            bundle.addEntry().setResource(payer);
            bundle.addEntry().setResource(device);
            bundle.addEntry().setResource(patient);
            return getResult(bundle);
        }

        return result;
    }

}
