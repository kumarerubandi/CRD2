library OxygenTherapy
using FHIR version '3.0.0'
include FHIRHelpers version '3.0.0' called FHIRHelpers

codesystem "ICD-10-CM": 'http://hl7.org/fhir/sid/icd-10-cm'
codesystem "LOINC": 'http://loinc.org'
codesystem "SNOMED-CT": 'http://snomed.info/sct'

//COPD_Codes
code "J44": 'J44' from "ICD-10-CM"
code "J44.0": 'J44.0' from "ICD-10-CM"
code "J44.1": 'J44.1' from "ICD-10-CM"
code "J44.9": 'J44.9' from "ICD-10-CM"

//Bronchiectasis_Codes
code "J47": 'J47' from "ICD-10-CM"
code "J47.0": 'J47.0' from "ICD-10-CM"
code "J47.1": 'J47.1' from "ICD-10-CM"
code "J47.9": 'J47.9' from "ICD-10-CM"

//Diffuse_interstitial_lung_disease_Codes
code "J84": 'J84' from "ICD-10-CM"
code "J84.0": 'J84.0' from "ICD-10-CM"
code "J84.01": 'J84.01' from "ICD-10-CM"
code "J84.02": 'J84.02' from "ICD-10-CM"
code "J84.03": 'J84.03' from "ICD-10-CM"
code "J84.09": 'J84.09' from "ICD-10-CM"
code "J84.1": 'J84.1' from "ICD-10-CM"
code "J84.10": 'J84.10' from "ICD-10-CM"
code "J84.11": 'J84.11' from "ICD-10-CM"
code "J84.111": 'J84.111' from "ICD-10-CM"
code "J84.112": 'J84.112' from "ICD-10-CM"
code "J84.113": 'J84.113' from "ICD-10-CM"
code "J84.114": 'J84.114' from "ICD-10-CM"
code "J84.115": 'J84.115' from "ICD-10-CM"
code "J84.116": 'J84.116' from "ICD-10-CM"
code "J84.117": 'J84.117' from "ICD-10-CM"
code "J84.17": 'J84.17' from "ICD-10-CM"
code "J84.2": 'J84.2' from "ICD-10-CM"
code "J84.8": 'J84.8' from "ICD-10-CM"
code "J84.81": 'J84.81' from "ICD-10-CM"
code "J84.82": 'J84.82' from "ICD-10-CM"
code "J84.83": 'J84.83' from "ICD-10-CM"
code "J84.84": 'J84.84' from "ICD-10-CM"
code "J84.841": 'J84.841' from "ICD-10-CM"
code "J84.842": 'J84.842' from "ICD-10-CM"
code "J84.843": 'J84.843' from "ICD-10-CM"
code "J84.848": 'J84.848' from "ICD-10-CM"
code "J84.89": 'J84.89' from "ICD-10-CM"
code "J84.9": 'J84.9' from "ICD-10-CM"

//Cystic_fibrosis_Codes
code "E84": 'E84' from "ICD-10-CM"
code "E84.0": 'E84.0' from "ICD-10-CM"
code "E84.1": 'E84.1' from "ICD-10-CM"
code "E84.11": 'E84.11' from "ICD-10-CM"
code "E84.19": 'E84.19' from "ICD-10-CM"
code "E84.8": 'E84.8' from "ICD-10-CM"
code "E84.9": 'E84.9' from "ICD-10-CM"

//Pulmonary_hypertension_Codes
code "I27.0": 'I27.0' from "ICD-10-CM"
code "I27.2": 'I27.2' from "ICD-10-CM"

//Hypoxemia_Codes
code "R09.02": 'R09.02' from "ICD-10-CM"


//  Loinc codes for observations
//Arterial_oxygen_saturation_Codes
code "59408-5": '59408-5' from "LOINC"
//Arterial_partial_pressure_of_oxygen_Codes
code "2703-7": '2703-7' from "LOINC"
//Arterial_oxygen_saturation_during_exercise_Codes
code "89276-0": '89276-0' from "LOINC"
//Note: cant find loinc code for partial pressure during exercise

//Hematocrit_lab_test_Codes
code "20570-8": '20570-8' from "LOINC"
code "31100-1": '31100-1' from "LOINC"
code "32354-3": '32354-3' from "LOINC"
code "41654-5": '41654-5' from "LOINC"
code "41655-2": '41655-2' from "LOINC"
code "4544-3": '4544-3' from "LOINC"
code "4545-0": '4545-0' from "LOINC"
code "71829-6": '71829-6' from "LOINC"
code "71830-4": '71830-4' from "LOINC"
code "71832-0": '71832-0' from "LOINC"
code "71833-8": '71833-8' from "LOINC"



//Immobilization_Codes
code "102491009": '102491009' from "SNOMED-CT"
code "129857008": '129857008' from "SNOMED-CT"
code "129859006": '129859006' from "SNOMED-CT"
code "160685001": '160685001' from "SNOMED-CT"
code "371632003": '371632003' from "SNOMED-CT"
code "8510008": '8510008' from "SNOMED-CT"

// Relevant Diagnosis Code Lists
define "COPD_Codes": { 'J44', 'J44.0', 'J44.1', 'J44.9' }
define "Bronchiectasis_Codes": { 'J47', 'J47.0', 'J47.1', 'J47.9' }
define "Diffuse_interstitial_lung_disease_Codes": { 'J84', 'J84.0', 'J84.01', 'J84.02', 'J84.03',
  'J84.09', 'J84.1', 'J84.10', 'J84.11', 'J84.111', 'J84.112', 'J84.113', 'J84.114', 'J84.115',
  'J84.116', 'J84.117', 'J84.17', 'J84.2', 'J84.8', 'J84.81', 'J84.82', 'J84.83', 'J84.84',
  'J84.841', 'J84.842', 'J84.843', 'J84.848', 'J84.89', 'J84.9' }
define "Cystic_fibrosis_Codes": { 'E84', 'E84.0', 'E84.1', 'E84.11', 'E84.19', 'E84.8', 'E84.9' }
define "Pulmonary_hypertension_Codes": { 'I27.0', 'I27.2' }
define "Hypoxemia_Codes": { 'R09.02' }

define "Arterial_oxygen_saturation_Codes": { '59408-5' }
define "Arterial_partial_pressure_of_oxygen_Codes": { '2703-7' }
define "Arterial_oxygen_saturation_during_exercise_Codes": { '89276-0' }

define "Hematocrit_lab_test_Codes": { '20570-8', '31100-1', '32354-3', '41654-5', '41655-2', '4544-3', '4545-0', '71829-6', '71830-4', '71832-0', '71833-8' }

// Other codes
define "Immobilization_Codes": { '102491009', '129857008', '129859006', '160685001', '371632003', '8510008' }

context Patient

define "DeviceRequestResource":
	[DeviceRequest]

define function GetMiddleInitials(name FHIR.HumanName):
  Substring(Combine((name.given given return Substring(given.value,0,1)),', '),3)


define Today: Today()

// Beneficiary info
define BeneficiaryNameObject: 
	singleton from (Patient.name)

define BeneficiaryName: 
	PatientFirstName +' '+PatientMiddleInitial + ' '+ PatientLastName
	//PatientFirstName + ' '+ PatientLastName

define PatientLastName: "BeneficiaryNameObject".family.value

define PatientMiddleInitial: 
	if (Length("BeneficiaryNameObject".given)>1) then GetMiddleInitials("BeneficiaryNameObject")
	else ''

	
define PatientFirstName:
	"BeneficiaryNameObject".given[0].value

define BeneficiaryGender: Patient.gender.value

define BeneficiaryDateOfBirth: Patient.birthDate.value

define BeneficiaryIdentificationNumber:
	(singleton from (
  	Patient.identifier identifier
   	 where identifier.system.value = 'http://hl7.org/fhir/sid/us-medicare')).value.value



define PatientCoverageResource: singleton from (
  [Coverage] coverage
    // pull coverage resource id from the device request insurance extension
    where ('Coverage/' + coverage.id) = (("DeviceRequestResource".extension ext where ext.url = 'http://build.fhir.org/ig/HL7/davinci-crd/STU3/ext-insurance.html')[0].value.reference.value))
define PatientMedicareId: "PatientCoverageResource".subscriberId.value

// coverage requirement info
define RelevantDiagnoses: {
  if exists([Condition] P
		where P.code.coding[0].code in "COPD_Codes") then 'COPD' else 'null',
  if exists([Condition]  P
		where P.code.coding[0].code in "Bronchiectasis_Codes") then 'Bronchiectasis' else 'null',
  if exists([Condition] P
		where P.code.coding[0].code in  "Diffuse_interstitial_lung_disease_Codes") then 'Diffuse Interstitial Lung Disease' else 'null',
  if exists([Condition] P
		where P.code.coding[0].code in "Cystic_fibrosis_Codes") then 'Cystic Fibrosis' else 'null',
  if exists([Condition] P
		where P.code.coding[0].code in  "Pulmonary_hypertension_Codes") then 'Pulmonary Hypertension' else 'null',
  if exists([Condition] P
		where P.code.coding[0].code in  "Hypoxemia_Codes") then 'Hypoxemia' else 'null'
}


define ArterialOxygenSaturation: 
	if exists([Observation] P where P.code.coding[0].code in  "Arterial_oxygen_saturation_Codes") then FHIRHelpers.ToQuantity(Last([Observation] P where P.code.coding[0].code in  "Arterial_oxygen_saturation_Codes").value).value else null
	

define ArterialPartialPressureOfOxygen: 
	if exists([Observation] P where P.code.coding[0].code in  "Arterial_partial_pressure_of_oxygen_Codes") then FHIRHelpers.ToQuantity(Last([Observation] P where P.code.coding[0].code in  "Arterial_partial_pressure_of_oxygen_Codes").value).value else null
	

define ArterialOxygenSaturationExercise:
	 if exists([Observation] P where P.code.coding[0].code in  "Arterial_oxygen_saturation_during_exercise_Codes") then FHIRHelpers.ToQuantity(Last([Observation] P where P.code.coding[0].code in  "Arterial_oxygen_saturation_during_exercise_Codes").value).value else null
	

define PatientMobile: not exists([Condition] P
		where P.code.coding[0].code in "Immobilization_Codes")

define HematocritThatIsGreaterThanThreshold: 
	if exists([Observation] P where P.code.coding[0].code in  "Hematocrit_lab_test_Codes") then FHIRHelpers.ToQuantity(Last([Observation] P where P.code.coding[0].code in  "Hematocrit_lab_test_Codes").value).value else null
	

define PatientHasHematocritThatIsGreaterThanThreshold: exists("HematocritThatIsGreaterThanThreshold")

// device request info

//define DeviceRequestHcpcsCoding: singleton from (
  //((cast "DeviceRequestResource".code as CodeableConcept).coding) coding
    //where coding.system.value = 'https://hcpcs.codes/')

//define DeviceRequestDescription: 'HCPCS ' + "DeviceRequestHcpcsCoding".code.value + ' - ' + "DeviceRequestHcpcsCoding".display.value

//define DeviceRequestedIsPortable: "DeviceRequestHcpcsCoding".code.value in { 'E0433', 'E0434', 'E0444', 'EO431', 'K0738', 'E0443', 'E1392' }

//define DeviceRequestedIsStationary: "DeviceRequestHcpcsCoding".code.value in { 'E0439', 'E0442', 'E0424', 'E0441', 'E1390', 'E1391' }

// ordering provider info
define Practitioner: 
  [Practitioner] practitioner
    where ('Practitioner/' + practitioner.id) = "DeviceRequestResource"[0].performer.reference.value

define PractitionerNameObject: 
	singleton from ("Practitioner"[0].name)
	
define PractitionerName: 
	//PractitionerFirstName + ' '+ PractitionerMiddleInitial + ' '+ PractitionerLastName
	PractitionerFirstName + ' '+ PractitionerLastName
define PractitionerLastName: "PractitionerNameObject".family.value
define PractitionerMiddleInitial: GetMiddleInitials("PractitionerNameObject")
define PractitionerFirstName: "PractitionerNameObject".given[0].value
define PractitionerNPI: 
	(singleton from (
  		"Practitioner".identifier identifier
    		where identifier.system.value = 'http://hl7.org.fhir/sid/us-npi')).value.value
    

define PractitionerAddressObject: singleton from ("Practitioner".address address where address.use.value = 'home')  
define PractitionerAddress: 
	"PractitionerAddressObject".line[0].value +', ' +"PractitionerAddressObject".city.value +', ' +"PractitionerAddressObject".state.value+', ' +"PractitionerAddressObject".postalCode.value


////////////////////////////// Taken from CDS Connect Commons for FHIR, could replace with stu3 version of helper library
define function ActiveOrRecurring(Cond Condition):
  Cond C where C.clinicalStatus.value in {'active', 'relapse'}

define function ObservationLookBack(ObsList List<Observation>, LookBack System.Quantity):
  ObsList O
    let LookBackInterval: Interval[Now() - LookBack, Now()]
    where (cast O.effective as dateTime).value in LookBackInterval
      or NullSafeToInterval(cast O.effective as Period) overlaps LookBackInterval
      or O.issued in LookBackInterval

define function NullSafeToInterval(Pd FHIR.Period):
  if Pd is not null then Interval[Pd."start".value, Pd."end".value] else null

define function Verified(ObsList List<Observation>):
  ObsList O where O.status.value in {'final', 'amended'}

define function WithUnit(ObsList List<Observation>, Unit String):
  ObsList O where (cast O.value as Quantity).unit.value = Unit or (cast O.value as Quantity).code.value = Unit

define function HighestObservation(ObsList List<Observation>):
  Max(ObsList O return NullSafeToQuantity(cast O.value as Quantity))

define function Confirmed(CondList List<Condition>):
  CondList C where C.verificationStatus.value = 'confirmed'

define function NullSafeToQuantity(Qty FHIR.Quantity):
  if Qty is not null then
    System.Quantity {
      value: Qty.value.value,
      unit: Coalesce(Qty.unit.value, Qty.code.value)
    }
  else null

define function LowestObservation(ObsList List<Observation>):
  Min(ObsList O return NullSafeToQuantity(cast O.value as Quantity))
