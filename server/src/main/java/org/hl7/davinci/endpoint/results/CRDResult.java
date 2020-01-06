/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hl7.davinci.endpoint.results;
import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.ClaimResponse;
import org.hl7.fhir.r4.model.DeviceRequest;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.json.JSONObject;

/**
 *
 * @author sreekanthpuram
 */
public class CRDResult {
    private String appContext;
    private JSONObject cdsResult;
    private ServiceRequest sr;
    private DeviceRequest de;
    private Patient pt;
    private Organization org;
    private ArrayList<String> errors;
    
    
    public CRDResult() {
        appContext = getSaltString();
        errors = new ArrayList();
        
    }
    
    public String getAppContext() {
        return appContext;
    }
    
    public void setResources(Bundle bundle) {
        //System.out.println("The resource is " + getString(bundle));
        List<Bundle.BundleEntryComponent> entries = bundle.getEntry();
        for (Iterator<Bundle.BundleEntryComponent> iterator = entries.iterator(); iterator.hasNext();) {
            Bundle.BundleEntryComponent next = iterator.next();
            //System.out.println("The next resource is " + getString(next.getResource()));
            if (next.getResource().getResourceType().toString().equals("ServiceRequest")) {
                sr = (ServiceRequest)next.getResource();
            }
            if (next.getResource().getResourceType().toString().equals("DeviceRequest")) {
                de = (DeviceRequest)next.getResource();
            }
            if (next.getResource().getResourceType().toString().equals("Patient")) {
                pt = (Patient)next.getResource();
            }
            if (next.getResource().getResourceType().toString().equals("Organization")) {
                org = (Organization)next.getResource();
            }
            
        }
    }
    
    public Organization getOrganization() {
        return org;
    }
    
    public void setCdsResult(JSONObject cdsResult) {
        this.cdsResult = cdsResult;
    }
    
    private String getDumpFile() {
        String fileName = "";
        String basePathOfClass = getClass().getProtectionDomain().getCodeSource().getLocation().getFile();
        String[] splitPath = basePathOfClass.split("build/classes/java/main/");
        if(splitPath.length == 1) {
            fileName = splitPath[0]+"src/main/jib/smartAppFhirArtifacts/"+appContext+".json";
            
        }
        return fileName;
    }
    
    public void dumpAppContext() {
        try {
        if (cdsResult != null) {
           String fileName = getDumpFile();
           if (!fileName.equals("")) {
               JSONObject newAppContext = new JSONObject();
               if (sr != null) {
                   newAppContext.put("request",getString(sr));
               }
               if (de != null) {
                   newAppContext.put("request",getString(de));
               }
               if (pt != null) {
                   String patientId = pt.getId();
                   patientId = patientId.substring(8);
                   newAppContext.put("patientId",patientId);
                   newAppContext.put("patient",getString(pt));
                   
               }
               if (org != null) {
                   String oid = org.getId();
                   oid = oid.substring(13);
                   newAppContext.put("payerName", "united_health_care");
               }
               newAppContext.put("prior_auth",cdsResult.get("prior_auth"));
	       newAppContext.put("template","urn:hl7:davinci:crd:"+cdsResult.get("template"));
               FileWriter file = new FileWriter(fileName);
               file.write(newAppContext.toString());
            file.flush();
            file.close();
           }
        } 
        } catch (Exception e ) {
            e.printStackTrace();
        }
    } 
    
    protected String getSaltString() {
      String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
      StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
      while (salt.length() < 12) { // length of the random string.
          int index = (int) (rnd.nextFloat() * SALTCHARS.length());
          salt.append(SALTCHARS.charAt(index));
      }
      String saltStr = salt.toString();
      return saltStr;

  }
    
    private JSONObject getString(Resource r) {
        try {
        IParser parser = FhirContext.forR4().newJsonParser();
        String parsed = parser.encodeResourceToString(r);
        System.out.println("The resource is " + parsed);
        return new JSONObject(parsed);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }
    
    public void addError(String error) {
        errors.add(error);
    }
    
    public ArrayList<String> getErrors() {
        return errors;
    }
    
    
    
    
    
    
    
    
    
}
