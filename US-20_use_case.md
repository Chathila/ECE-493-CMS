# UC-20: Receive Registration Payment Confirmation Ticket

## Goal in Context
Allow an attendee to receive a payment confirmation ticket after successful registration payment so that they can prove their conference registration.

## Scope
Conference Management System (CMS)

## Level
User Goal

## Primary Actor
Attendee

## Secondary Actors
- CMS Payment Processing Service  
- CMS Notification Service  
- CMS Database  
- Email Service

## Trigger
Successful completion of the conference registration payment.

## Success End Condition
The attendee receives a registration payment confirmation ticket, and the ticket is stored in the CMS for future reference.

## Failed End Condition
The payment confirmation ticket is not delivered, and the system records the failure and informs the attendee appropriately.

## Preconditions
- The attendee is registered and logged in to the CMS.
- The attendee has successfully completed the registration payment.
- Valid contact information is available for the attendee.

## Main Success Scenario
1. Attendee completes the registration payment process.
2. System receives payment confirmation from the payment processing service.
3. System generates a registration confirmation ticket.
4. System stores the ticket in the CMS database.
5. System sends the confirmation ticket to the attendee.
6. Attendee receives the registration confirmation ticket.

## Extensions
- **5a**: Ticket delivery fails (email or system notification error)  
  - **5a1**: System logs the delivery failure.
  - **5a2**: System displays a message informing the attendee of the issue.

- **6a**: Attendee attempts to access the ticket before delivery  
  - **6a1**: System allows the attendee to view or download the ticket directly from the CMS.

## Related Information
- **Priority**: Medium  
- **Frequency**: Medium  
- **Open Issues**: Ticket format (PDF, QR code) and delivery channels may be enhanced in future CMS versions.

## Use case analysis
* Scenario validity and plausibility: The scenarios are plausible for a ticket-generation and delivery workflow, but the main success scenario is only partially instantiated from the attendee’s perspective because most steps are system-driven after payment completion. The attendee’s participation is largely passive (receiving/accessing the ticket), so the flow reads partly as a post-payment system process rather than a fully interactive user-goal use case.

* Flow coverage: The scenarios cover successful ticket generation/delivery and ticket delivery failure, as well as accessing the ticket via the CMS before delivery. However, the use case does not include a scenario where ticket generation/storage fails (e.g., database error at Step 4), which would prevent both delivery and future access. Adding an extension for “ticket generation or storage failure” would improve completeness.

* Undocumented or extra flows: No scenarios represent flows outside those documented in the use case. Similar to UC-19, this use case overlaps with the payment flow (where confirmation is part of the success end condition); it may be more appropriate to treat ticket generation/delivery as a sub-flow of UC-19 unless the assignment requires modeling it as a separate use case.