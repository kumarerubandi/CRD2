library HomeHealthService
using FHIR version '3.0.0'
include FHIRHelpers version '3.0.0' called FHIRHelpers

codesystem "ICD-10-CM": 'http://hl7.org/fhir/sid/icd-10-cm'
codesystem "LOINC": 'http://loinc.org'
codesystem "HCPCS": 'https://hcpcs.codes/'
codesystem "SNOMED-CT": 'http://snomed.info/sct'
codesystem "CPT": 'https://www.aapc.com/resources/medical-coding/cpt.aspx'
codesystem "NUCCPT":'https://www.nlm.nih.gov/research/umls/sourcereleasedocs/current/NUCCPT/sourcerepresentation.html'

//Reduced mobility
code "Z74.0": 'Z74.0' from "ICD-10-CM"
//Bed confinement status
code "Z74.01": 'Z74.01' from "ICD-10-CM"
//Other problems related to care provider dependency
code "Z74.8": 'Z74.8' from "ICD-10-CM"
//Dependence on other enabling machines and devices
code "Z99.8": 'Z99.8' from "ICD-10-CM"

//Under care of public health physician (finding)
code "305524001": '305524001' from "SNOMED-CT"
// Physician supervision of a hospice patient
code "99377": '99377' from "CPT"
//Prolonged Services With Direct Face-to-Face Patient Contact
code "99354": '99354' from "CPT"
code "99355" : '99355' from "CPT"
code "99356" : '99356' from "CPT"
code "99357" : '99357' from "CPT"

//Physician Assistants & Advanced Practice Nursing Providers; Clinical Nurse Specialist, Home Health
code "364SH0200X" : '364SH0200X' from "NUCCPT"

//Home health aide service (regime/therapy)
code "385781007" : '385781007' from "SNOMED-CT"
//Services performed by a qualified physical therapist in the home health or hospice setting, each 15 minutes 
code "G0151" : 'G0151' from "HCPCS"

//Medicare or comparable number 
code "45397-7" : '45397-7' from "LOINC"

//National provider ID
code "45952-9" : '45952-9' from "LOINC"

//CMS certification number
code "46456-0" : '46456-0' from "LOINC"

//Facility Address
code "65833-6" : '65833-6' from "LOINC"

//hcpcs codes for home health services
code "G0152" : 'G0152' from "HCPCS"
code "G0153" : 'G0153' from "HCPCS"
code "G0155" : 'G0155' from "HCPCS"
code "G0156" : 'G0156' from "HCPCS"
code "G0157" : 'G0157' from "HCPCS"
code "G0158" : 'G0158' from "HCPCS"
code "G0159" : 'G0159' from "HCPCS"
code "G0160" : 'G0160' from "HCPCS"
code "G0161" : 'G0161' from "HCPCS"
code "G0162" : 'G0162' from "HCPCS"
code "G0299" : 'G0299' from "HCPCS"
code "G0300" : 'G0300' from "HCPCS"
code "G0493" : 'G0493' from "HCPCS"
code "G0494" : 'G0494' from "HCPCS"
code "G0495" : 'G0495' from "HCPCS"
code "G0496" : 'G0496' from "HCPCS"

parameter "Measurement Period" Interval<DateTime>

context Patient

define "F2FEncounterCodes": { '99354', '99355', '99356', '99357' }

define "HomeHealthCareCodes": { '385781007', 'G0151', 'G0152', 'G0153', 'G0155', 'G0156', 'G0157', 'G0158', 'G0159', 'G0160', 'G0161', 'G0162', 'G0299', 'G0300', 'G0493', 'G0494', 'G0495', 'G0496' }

define HomeHealthCare:
	singleton from (
  		[Procedure] procedure
    		where procedure.code.coding[0].code.value in "HomeHealthCareCodes"
    		 )
    		
define Today: Today()

define FHIRQueries:
	{'Procedure?code=385781007',
	'Procedure?code=G0151',
	'Procedure?code=G0152',
	'Procedure?code=G0153',
	'Procedure?code=G0155',
	'Procedure?code=G0156',
	'Procedure?code=G0157',
	'Procedure?code=G0158',
	'Procedure?code=G0159',
	'Procedure?code=G0160',
	'Procedure?code=G0161',
	'Procedure?code=G0162',
	'Procedure?code=G0299',
	'Procedure?code=G0300',
	'Procedure?code=G0493',
	'Procedure?code=G0494',
	'Procedure?code=G0495',
	'Procedure?code=G0496',
	'Practitioner/'+"Practitioner".id.value,
	'Organization/'+"Agency".id.value,
	'EpisodeOfCare/'+"Episode".id.value
	}

// Beneficiary info
define BeneficiaryNameObject: 
	singleton from (Patient.name name where name.use.value = 'official')

define BeneficiaryName: 
	PatientFirstName +' '+PatientMiddleInitial + ' '+ PatientLastName
	//PatientFirstName + ' '+ PatientLastName

define PatientLastName: "BeneficiaryNameObject".family.value

define PatientMiddleInitial: 
	if (Length("BeneficiaryNameObject".given)>1) then GetMiddleInitials("BeneficiaryNameObject")
	else ''

	
define PatientFirstName: "BeneficiaryNameObject".given[0].value

define BeneficiaryGender: Patient.gender.value

define BeneficiaryDateOfBirth: Patient.birthDate.value

define BeneficiaryMedicareNumber:
	(singleton from (
  	Patient.identifier identifier
   	 where identifier.system.value = 'http://hl7.org/fhir/sid/us-medicare')).value.value


// Practitioner info
define Practitioner: 
	singleton from (
  		[Practitioner] practitioner 
	//where practitioner.id.value = 'mettles-Practitioner-1')
    		where ('Practitioner/' + practitioner.id.value) = "HomeHealthCare".performer[0].actor.reference.value)
    
define PractitionerNameObject: 
	singleton from ("Practitioner".name)
	
define PractitionerName: 
	//PractitionerFirstName + ' '+ PractitionerMiddleInitial + ' '+ PractitionerLastName
	PractitionerFirstName + ' '+ PractitionerLastName

define PractitionerLastName: "PractitionerNameObject".family.value
define PractitionerMiddleInitial: GetMiddleInitials("PractitionerNameObject")
define PractitionerFirstName: "PractitionerNameObject".given[0].value

define PractitionerNPI: 
	(singleton from (
  		"Practitioner".identifier identifier
    		where identifier.system.value = 'http://hl7.org/fhir/sid/us-npi')).value.value
    
define PractitionerPTAN:
	(singleton from (
  		"Practitioner".identifier identifier
    		where identifier.type.coding[0].system.value = 'http://terminology.hl7.org/CodeSystem/v2-0203' and identifier.type.coding[0].code.value = 'PRN')).value.value

define PractitionerAddressObject: singleton from ("Practitioner".address address where address.use.value = 'home')  

define PractitionerAddress: 
	"PractitionerAddressObject".line[0].value +', ' +"PractitionerAddressObject".city.value +', ' +"PractitionerAddressObject".state.value+', ' +"PractitionerAddressObject".postalCode.value

// Agency info
define Agency: 
	singleton from (
  		[Organization] agency where agency.id.value= 'mettles-Organization-1')
    		//where ('Organization/' + agency.id.value) = "HomeHealthCare".performer[0].onBehalfOf.reference.value)
    
define AgencyName: "Agency".name.value

define AgencyNPI: (singleton from (
  "Agency".identifier identifier
    where identifier.system.value = 'http://hl7.org/fhir/sid/us-npi')).value.value
    
define AgencyPTAN:
	(singleton from (
  		"Agency".identifier identifier
    		where identifier.type.coding[0].system.value = 'http://terminology.hl7.org/CodeSystem/v2-0203' and identifier.type.coding[0].code.value = 'PRN')).value.value

define CMSCertificationNumber: 
	(singleton from (
  		"Agency".identifier identifier
    		where identifier.type.coding[0].system.value = 'http://terminology.hl7.org/CodeSystem/v2-0203' and identifier.type.coding[0].code.value = 'AC')).value.value

define AgencyAddressObject: singleton from ("Agency".address)  

define AgencyAddress: 
	"AgencyAddressObject".line[0].value +', ' +"AgencyAddressObject".city.value +', ' +"AgencyAddressObject".state.value+', ' +"AgencyAddressObject".postalCode.value

//Other info
define SubmissionDate: Today

define Episode: 
	singleton from ([EpisodeOfCare] e where e.patient.reference.value = 'Patient/'+Patient.id.value and e.type[0].coding[0].code.value = 'hacc')

define StartDateEpisode: "Episode".period.start.value
	
define EndDate: "Episode".period.end.value

define state: Patient.address[0].state.value

define StartDateService: "HomeHealthCare".performed.start.value

//Coverage Requirements
define InabilityToLeaveHome: 
	if (exists([Observation]A where A.code.coding[0].code.value = 'Z74.0' and A.value.value =  true)
		and exists([Observation]B where B.code.coding[0].code.value = 'Z74.01' and B.value.value =  true)
		and exists([Observation]C where C.code.coding[0].code.value = 'Z74.8' and C.value.value =  true)
		and exists([Observation]D where D.code.coding[0].code.value = 'Z99.8' and D.value.value =  true))
	then true 
	else false

define UnderCare:
	if (exists([Observation]E where E.code.coding[0].code.value = '99377' and E.value.value =  true)
		and exists([Observation]F where F.code.coding[0].code.value = '305524001' and F.value.value =  true))
	then true 
	else false

define EncounterObject: singleton from ([Encounter]G where G.type[0].coding[0].code.value in "F2FEncounterCodes")

define EncounterPerformed:
	if exists("EncounterObject")
	then true 
	else false

define Encounterdate: "EncounterObject".period.start.value

define PreClaimReview: 'Pre-Claim Review'

//Requestor info
define Requestor: 
	singleton from (
  		[Practitioner] practitioner 
	//where practitioner.id.value = 'mettles-Practitioner-1')
    		where ('Practitioner/' + practitioner.id.value) = "HomeHealthCare".performer[0].actor.reference.value)
    		
define RequestorNameObject: 
	singleton from ("Requestor".name)
	
define SubmitterName: 
	//PractitionerFirstName + ' '+ PractitionerMiddleInitial + ' '+ PractitionerLastName
	RequestorFirstName + ' '+ RequestorLastName

define RequestorLastName: "RequestorNameObject".family.value
define RequestorMiddleInitial: GetMiddleInitials("RequestorNameObject")
define RequestorFirstName: "RequestorNameObject".given[0].value

define SubmitterTelephone: 
	(singleton from (
  		"Requestor".telecom tele
    		where tele.system.value = 'phone' and tele.use.value='work')).value.value


define function GetMiddleInitials(name FHIR.HumanName):
  Substring(Combine((name.given given return Substring(given.value,0,1)),', '),3)


define function GetProcedure(ProcedureList List<Procedure>, ProcedureCode String):
  ProcedureList P where P.code.coding[0].code.value= ProcedureCode
