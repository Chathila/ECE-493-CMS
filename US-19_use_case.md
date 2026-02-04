# UC-19: Pay Conference Registration Fee

## Goal in Context
Allow an attendee to pay the conference registration fee online so that they can attend the conference.

## Scope
Conference Management System (CMS)

## Level
User Goal

## Primary Actor
Attendee

## Secondary Actors
- Online Payment Service  
- CMS Payment Processing Service  
- CMS Database

## Trigger
Attendee selects the option to pay the conference registration fee.

## Success End Condition
The registration fee payment is successfully processed, recorded in the CMS, and a confirmation is provided to the attendee.

## Failed End Condition
The payment is not completed, and the system displays an appropriate error message explaining the reason for failure.

## Preconditions
- The attendee is registered and logged in to the CMS.
- Conference registration prices are available.
- The attendee has selected a registration type.

## Main Success Scenario
1. Attendee navigates to the conference registration section.
2. Attendee selects a registration type.
3. System displays the payment interface with the total fee.
4. Attendee enters valid payment information.
5. Attendee submits the payment request.
6. System sends the payment information to the online payment service.
7. Online payment service confirms successful payment.
8. System records the payment confirmation in the CMS database.
9. System displays a payment confirmation message to the attendee.

## Extensions
- **4a**: Payment information is incomplete or invalid  
  - **4a1**: System displays an error message requesting corrected payment details.

- **7a**: Payment is declined by the payment service  
  - **7a1**: System displays a payment failure message and allows the attendee to retry.

- **6a**: Payment service is unavailable  
  - **6a1**: System displays an error message indicating the service is temporarily unavailable.

## Related Information
- **Priority**: Medium  
- **Frequency**: Medium  
- **Open Issues**: Supported payment methods and refund policies may be defined in future CMS versions.

## Fully Dressed Scenario Narratives

### Main Success Scenario Narrative
An attendee who is logged in to the CMS navigates to the conference registration section and selects a desired registration type. The system displays a secure payment interface showing the total registration fee. The attendee enters valid payment details and submits the payment request. The system forwards the payment information to the online payment service, which successfully processes the transaction and returns a confirmation. The CMS records the payment confirmation in its database and displays a confirmation message to the attendee, indicating that the registration fee has been paid and their conference registration is complete.

---

### Alternative Scenario Narrative (4a: Payment Information Is Incomplete or Invalid)
The attendee reaches the payment interface and attempts to submit the registration fee using incomplete or invalid payment information, such as missing fields or incorrect card details. The system detects the invalid input before sending the request to the payment service. The system displays an error message requesting the attendee to correct the payment details. The payment is not processed, and no payment record is created in the CMS.

---

### Alternative Scenario Narrative (7a: Payment Declined by Payment Service)
The attendee enters valid payment information and submits the payment request. The system sends the payment information to the online payment service, but the payment service declines the transaction (for example, due to insufficient funds or card restrictions). The system receives the decline response and displays a payment failure message to the attendee, explaining that the payment was not approved. The attendee is given the option to retry the payment with corrected or alternative payment information. No payment confirmation is stored in the CMS.

---

### Alternative Scenario Narrative (6a: Payment Service Unavailable)
The attendee submits valid payment information through the payment interface. When the system attempts to send the payment request to the online payment service, the service is unavailable or unreachable due to a temporary outage. The system displays an error message indicating that the payment service is temporarily unavailable and asks the attendee to try again later. The payment is not completed, and no payment confirmation is recorded in the CMS.

## Use case analysis
* Scenario validity and plausibility: All scenarios are valid instantiations of the use case flows. The main success scenario describes a plausible online payment workflow, and the extensions (invalid payment details, declined payment, service unavailable) branch from appropriate steps in the payment process.

* Flow coverage: The scenarios cover the main success flow and key failure cases related to input validation and payment processing. However, the use case does not include a scenario where payment succeeds at the payment provider but the CMS fails to record the confirmation (database error), which would affect the success end condition. Adding an extension for “payment confirmed but recording fails” would improve completeness.

* Undocumented or extra flows: No scenarios represent flows outside those documented in the use case. All scenarios align with the goal, preconditions, and payment/recording steps, and no undocumented functionality is introduced.