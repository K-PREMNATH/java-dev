package com.stripe.api.service;

import com.google.gson.Gson;
import com.stripe.Stripe;
import com.stripe.api.model.RequestObject;
import com.stripe.api.model.ResponseObject;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.SetupIntent;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
public class StripeService {

    static String publicKey = "pk_test_161nsotSmf0zRm32dzXQBRs400DU8vowGY";
    static String privateKey = "sk_test_4LzmKiYvBJKOdMNj1t7scZEc00A2wMcvUe";

    private static Gson gson = new Gson();

    @RequestMapping("/public-key")
    public String index() {
        return StripeService.publicKey;
        //return gson.toJson(StripeService.publicKey);
    }

    @RequestMapping("/create-setup-intent")
    public String createSetupIntent() {
        Stripe.apiKey = "sk_test_4LzmKiYvBJKOdMNj1t7scZEc00A2wMcvUe";

        Map<String, Object> params = new HashMap<>();
        SetupIntent setupIntent = null;
        try {
            setupIntent = SetupIntent.create(params);
        } catch (StripeException stripeException) {
            System.out.println("Error code is : " + stripeException.getCode() + "	Error Msg : " + stripeException.toString());
        }

        System.out.println(setupIntent);
        System.out.println("Status : " + setupIntent.getStatus());

        String client_secret = setupIntent.getClientSecret();

        return client_secret;
    }

    @RequestMapping("/add-customer")
    public ResponseObject addCustomer(@RequestBody RequestObject requestObject) {
        ResponseObject responseObject = new ResponseObject();

        Stripe.apiKey = "sk_test_4LzmKiYvBJKOdMNj1t7scZEc00A2wMcvUe";
        String client_secret = requestObject.getClient_secret();

        try {

            /* Add the customer */
            Map<String, Object> customerParams = new HashMap<String, Object>();
            customerParams.put("payment_method", requestObject.getPayment_method());
            customerParams.put("name", "Duxion");
            customerParams.put("phone", "0773104294");
            customerParams.put("email", "murugesu.duxion@smartzi.com");
            Customer customer = Customer.create(customerParams);


            /*Create PaymentIntent*/
            Map<String, Object> paramsCreateIntent = new HashMap<String, Object>();

            paramsCreateIntent.put("amount", 257500);
            paramsCreateIntent.put("currency", "usd");

            ArrayList paymentMethodTypes = new ArrayList();
            paymentMethodTypes.add("card");

            paramsCreateIntent.put("payment_method_types", paymentMethodTypes);
            paramsCreateIntent.put("capture_method", "manual");
            paramsCreateIntent.put("confirm", "true");
            paramsCreateIntent.put("payment_method", requestObject.getPayment_method());
            //paramsCreateIntent.put("setup_future_usage", "off_session");
            paramsCreateIntent.put("customer",customer.getId());
            paramsCreateIntent.put("off_session","true");


            PaymentIntent payIntentCreate = null;
            PaymentIntent payIntentUpdate = null;
            PaymentIntent payIntentConfirm = null;
            PaymentIntent payIntentCapture = null;

            try {
                payIntentCreate = PaymentIntent.create(paramsCreateIntent);

                System.out.println(payIntentCreate);
                System.out.println("Create Status : " + payIntentCreate.getStatus());


                switch(payIntentCreate.getStatus()){
                    case "requires_confirmation":
                    case "requires_payment_method":

                        /*Update PaymentIntent*/
                        payIntentUpdate = PaymentIntent.retrieve(payIntentCreate.getId());
                        Map<String, Object> paramsUpdateIntent = new HashMap<String, Object>();
                        paramsUpdateIntent.put("payment_method", requestObject.getPayment_method());
                        //paramsUpdateIntent.put("setup_future_usage", "off_session");
                        paramsUpdateIntent.put("customer",customer.getId());
                        payIntentUpdate.update(paramsUpdateIntent);

                        System.out.println(payIntentUpdate);
                        System.out.println("Update Status : " + payIntentUpdate.getStatus());

                        /*Confirm PaymentIntent*/
                        payIntentConfirm = PaymentIntent.retrieve(payIntentUpdate.getId());
                        Map<String, Object> paramsConfirmIntent = new HashMap<String, Object>();
                        paramsConfirmIntent.put("payment_method", requestObject.getPayment_method());
                        //paramsConfirmIntent.put("setup_future_usage", "off_session");
                        payIntentConfirm.confirm(paramsConfirmIntent);

                        System.out.println(payIntentConfirm);
                        System.out.println("Confirm Status : " + payIntentConfirm.getStatus());

                        if(payIntentConfirm.getStatus() != "requires_capture" || payIntentConfirm.getStatus() == "requires_confirmation" || payIntentConfirm.getStatus() != "succeeded"){
                            PaymentIntent pi = PaymentIntent.retrieve(payIntentConfirm.getId());
                            System.out.println(pi);
                            System.out.println("After Confirm Status : " + pi.getStatus());


                            responseObject.setRequiresAction(true);
                            responseObject.setClientSecret(payIntentConfirm.getClientSecret());
                            responseObject.setPaymentIntentId(payIntentConfirm.getId());
                            responseObject.setCaptureAmount(222500);
                        }else{
                            /*Capture PaymentIntent*/
                            payIntentCapture = PaymentIntent.retrieve(payIntentConfirm.getId());
                            Map<String, Object> paramsCaptureIntent = new HashMap<String, Object>();
                            paramsCaptureIntent.put("amount_to_capture", 222500);
                            payIntentCapture.capture(paramsCaptureIntent);

                            System.out.println(payIntentCapture);
                            System.out.println("Confirm Status : " + payIntentCapture.getStatus());
                        }
                        break;
                    case "succeeded":
                        System.out.println("Succeed");

                        break;
                    case "requires_capture":
                        /*Capture PaymentIntent*/
                        payIntentCapture = PaymentIntent.retrieve(payIntentCreate.getId());
                        Map<String, Object> paramsCaptureIntent = new HashMap<String, Object>();
                        paramsCaptureIntent.put("amount_to_capture", 222500);
                        payIntentCapture.capture(paramsCaptureIntent);

                        System.out.println(payIntentCapture);
                        System.out.println("Confirm Status : " + payIntentCapture.getStatus());
                        break;

                }

            } catch (StripeException err) {
                responseObject.setError(err.getMessage());
                System.out.println("Error :" + err.toString() + err.getCode());
                System.out.println(err.getStripeError().getPaymentIntent().getId());
                System.out.println(err.getStripeError().getPaymentIntent().getClientSecret());
                System.out.println(err.getStripeError().getPaymentMethod().getId());

                System.out.println(err);

                if(err.getCode().equals("authentication_required")){
                    responseObject.setRequiresAction(true);
                    responseObject.setClientSecret(err.getStripeError().getPaymentIntent().getClientSecret());
                    responseObject.setPaymentIntentId(err.getStripeError().getPaymentIntent().getId());
                    responseObject.setCaptureAmount(222500);

                }

            }catch (Exception ex){
                System.out.println("Error : "+ ex.toString());
            }

        } catch (StripeException err) {
            responseObject.setError(err.getMessage());
            System.out.println("Error :" + err.toString() + err.getCode());

        }


        return responseObject;

    }


    @RequestMapping("/capture_payment")
    public ResponseObject capture_payment(@RequestBody RequestObject requestObject) {
        ResponseObject responseObject = new ResponseObject();

        Stripe.apiKey = "sk_test_4LzmKiYvBJKOdMNj1t7scZEc00A2wMcvUe";
        String client_secret = requestObject.getClient_secret();

        PaymentIntent payIntentCapture = null;

        try {

            /*Capture PaymentIntent*/
            payIntentCapture = PaymentIntent.retrieve(requestObject.getId());
            Map<String, Object> paramsCaptureIntent = new HashMap<String, Object>();
            paramsCaptureIntent.put("amount_to_capture", 222500);
            payIntentCapture.capture(paramsCaptureIntent);

            System.out.println(payIntentCapture);
            System.out.println("Capture Status : " + payIntentCapture.getStatus());
        }catch (StripeException err) {

            if(err.getCode().equals("authentication_required")){
                responseObject.setRequiresAction(true);
                responseObject.setClientSecret(err.getStripeError().getPaymentIntent().getClientSecret());
                responseObject.setPaymentIntentId(err.getStripeError().getPaymentIntent().getId());
                responseObject.setCaptureAmount(222500);
            }

            responseObject.setError(err.getMessage());
            System.out.println("Error :" + err.toString() + err.getCode());
        }catch (Exception ex){
            System.out.println("Error : "+ ex.toString());
        }


        return responseObject;

    }



    static ResponseObject generateResponse(PaymentIntent intent) {
        ResponseObject responseObject = null;
        switch (intent.getStatus()) {
            case "requires_action":
            case "requires_source_action":
                // Card requires authentication
                responseObject.setClientSecret(intent.getClientSecret());
                responseObject.setPaymentIntentId(intent.getId());
                responseObject.setRequiresAction(true);
                break;
            case "requires_payment_method":
            case "requires_source":
                // Card was not properly authenticated, suggest a new payment method
                responseObject.setError("Your card was denied, please provide a new payment method");
                break;
            case "succeeded":
                System.out.println("Payment received!");
                // Payment is complete, authentication not required
                // To cancel the payment after capture you will need to issue a Refund
                // (https://stripe.com/docs/api/refunds)
                responseObject.setClientSecret(intent.getClientSecret());
                break;
            default:
                responseObject.setError("Unrecognized status");
        }
        return responseObject;
    }
}
