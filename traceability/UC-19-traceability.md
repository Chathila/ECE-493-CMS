# UC-19 Traceability Matrix

- AT-01 Successful Online Payment
  - `src/test/java/com/ece493/cms/acceptance/UC19AcceptanceIT.java#AT01_successfulOnlinePayment`
  - `src/test/java/com/ece493/cms/integration/RegistrationPaymentEndpointIT.java#requiresLoginAndSupportsSuccessAndDecline`
- AT-02 Invalid or Incomplete Payment Information
  - `src/test/java/com/ece493/cms/acceptance/UC19AcceptanceIT.java#AT02_invalidOrIncompletePaymentInformation`
  - `src/test/java/com/ece493/cms/unit/PaymentProcessingServiceTest.java#handlesValidationProviderAndPersistenceOutcomes`
- AT-03 Payment Declined by Payment Service
  - `src/test/java/com/ece493/cms/acceptance/UC19AcceptanceIT.java#AT03_paymentDeclinedByPaymentService`
  - `src/test/java/com/ece493/cms/integration/RegistrationPaymentEndpointIT.java#requiresLoginAndSupportsSuccessAndDecline`
- AT-04 Payment Service Unavailable
  - `src/test/java/com/ece493/cms/acceptance/UC19AcceptanceIT.java#AT04_paymentServiceUnavailable`
  - `src/test/java/com/ece493/cms/unit/PaymentProcessingServiceTest.java#handlesValidationProviderAndPersistenceOutcomes`
