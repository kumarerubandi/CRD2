package org.hl7.davinci.r4;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;

/**
 * Build some expensive objects here so we can reuse them.
 */
public class FhirComponents {
  private static FhirComponents single_instance = null;
  private FhirContext fhirContext;
  private IParser jsonParser;

  private FhirComponents() {
    this.fhirContext = FhirContext.forR4();
    this.jsonParser = fhirContext.newJsonParser();
  }

  public FhirContext getFhirContext() {
    return fhirContext;
  }

  public IParser getJsonParser() {
    return jsonParser;
  }

  /**
   * Gets the current FhirComponents instance.
   * @return the instance
   */
  public static FhirComponents getInstance() {
    if (single_instance == null) {
      single_instance = new FhirComponents();
    }
    return single_instance;
  }
}