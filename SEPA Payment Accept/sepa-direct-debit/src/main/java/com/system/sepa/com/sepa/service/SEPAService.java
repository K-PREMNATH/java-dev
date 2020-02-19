package com.system.sepa.com.sepa.service;

import com.google.gson.Gson;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.SetupIntent;
import com.system.sepa.model.RequestObject;
import com.system.sepa.model.ResponseObject;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestController
public class SEPAService {

    static String publicKey = "pk_test_zpvZCVNflHzto16a1dLAq4yd";
    static String privateKey = "sk_test_d1nvv6ItdRt7YEG2BzKhm3Tm";

    private static Gson gson = new Gson();

    @RequestMapping("/config")
    public String getDDConfigInfo() {
        return SEPAService.publicKey;
    }

    @RequestMapping("/create-setup-intent")
    public String addBankDetail() {
        Stripe.apiKey = "sk_test_d1nvv6ItdRt7YEG2BzKhm3Tm";

        SetupIntent setupIntent = null;

        Map<String, Object> params = new HashMap<String, Object>();
        ArrayList paymentMethodTypes = new ArrayList();
        paymentMethodTypes.add("sepa_debit");
        params.put("payment_method_types", paymentMethodTypes);

        try {
            setupIntent = SetupIntent.create(params);
            System.out.println(setupIntent);
            System.out.println("Create Status : " + setupIntent.getStatus());

        } catch (StripeException err) {
            System.out.println("Error :" + err.toString() + err.getCode());
            System.out.println(err);
        }catch (Exception ex){
            System.out.println("Error : "+ ex.toString());
        }
        String client_secret = setupIntent.getClientSecret();

        return client_secret;
    }

    @RequestMapping("/create-payment-intent")
    public ResponseObject createDDPaymentIntent () {//(@RequestBody RequestObject requestObject)
        Stripe.apiKey = "sk_test_d1nvv6ItdRt7YEG2BzKhm3Tm";
        ResponseObject responseObject = new ResponseObject();

        PaymentIntent createPaymentIntent = new PaymentIntent();

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("amount", 1099);
        params.put("currency", "eur");
        params.put("setup_future_usage", "off_session");
        params.put("payment_method_types", Arrays.asList("sepa_debit"));
        try {
            createPaymentIntent = PaymentIntent.create(params);
            System.out.println(createPaymentIntent);
            System.out.println("Create Status : " + createPaymentIntent.getStatus());

        } catch (StripeException err) {
            System.out.println("Error :" + err.toString() + err.getCode());
            System.out.println(err);
        }catch (Exception ex){
            System.out.println("Error : "+ ex.toString());
        }

        responseObject.setClientSecret(createPaymentIntent.getClientSecret());
        responseObject.setPaymentIntentId(createPaymentIntent.getId());
        responseObject.setRequiresAction(true);

        return responseObject;
    }

    @RequestMapping("/add-customer")
    public ResponseObject addCustomer(@RequestBody RequestObject requestObject) {
        ResponseObject responseObject = new ResponseObject();

        Stripe.apiKey = "sk_test_d1nvv6ItdRt7YEG2BzKhm3Tm";
        String client_secret = requestObject.getClient_secret();

        try {
            Map<String, Object> customerParams = new HashMap<String, Object>();
            customerParams.put("name", "SepaCustomerTest");
            customerParams.put("phone", "0773104294");
            customerParams.put("email", "sepatest.customer@smartzi.com");
            customerParams.put("payment_method", requestObject.getPayment_method());
            Customer customer = Customer.create(customerParams);

        } catch (StripeException err) {

            System.out.println("Error :" + err.toString() + err.getCode());
            System.out.println(err.getStripeError().getPaymentIntent().getId());
            System.out.println(err.getStripeError().getPaymentIntent().getClientSecret());
            System.out.println(err.getStripeError().getPaymentMethod().getId());

        }catch (Exception ex){
            System.out.println("Error : "+ ex.toString());
        }

        return responseObject;
    }

    @RequestMapping("/getPayment")
    public String getPaymentFromDriver(@RequestBody RequestObject requestObject) {
        ResponseObject responseObject = new ResponseObject();

        Stripe.apiKey = "sk_test_d1nvv6ItdRt7YEG2BzKhm3Tm";
        PaymentIntent payIntentCreate = null;
        PaymentIntent payIntentUpdate = null;
        PaymentIntent payIntentConfirm = null;
        PaymentIntent payIntentCapture = null;
        try {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("amount", 2800);
        params.put("currency", "eur");
        params.put("setup_future_usage", "off_session");
        params.put("payment_method_types", Arrays.asList("sepa_debit"));
        params.put("customer", requestObject.getId());
        params.put("payment_method", requestObject.getPayment_method());
        payIntentCreate = PaymentIntent.create(params);

        System.out.println(payIntentCreate);
        System.out.println("Create Status : " + payIntentCreate.getStatus());

            switch(payIntentCreate.getStatus()){
                case "requires_confirmation":

                    /*Confirm PaymentIntent*/
                    payIntentConfirm = PaymentIntent.retrieve(payIntentCreate.getId());
                    Map<String, Object> paramsConfirmIntent = new HashMap<String, Object>();
                    paramsConfirmIntent.put("payment_method", requestObject.getPayment_method());
                    paramsConfirmIntent.put("setup_future_usage", "off_session");

                    Map<String, String> customerAccept = new HashMap<>();
                    customerAccept.put("type", "offline");

                    Map<String, Object> mandate_data = new HashMap<>();
                    mandate_data.put("customer_acceptance",customerAccept);
                    paramsConfirmIntent.put("mandate_data", mandate_data);


                    payIntentConfirm.confirm(paramsConfirmIntent);

                    System.out.println(payIntentConfirm);
                    System.out.println("Confirm Status : " + payIntentConfirm.getStatus());
                    break;

                case "succeeded":
                    System.out.println("Succeed");
                    break;
            }

        } catch (StripeException err) {
            System.out.println("Error :" + err.toString() + err.getCode());
            System.out.println(err.getStripeError().getPaymentIntent().getId());
            System.out.println(err.getStripeError().getPaymentIntent().getClientSecret());
            System.out.println(err.getStripeError().getPaymentMethod().getId());
        }catch (Exception ex){
            System.out.println("Error : "+ ex.toString());
        }

        return gson.toJson(responseObject);
    }

}
