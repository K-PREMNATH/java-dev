package com.system.sepa.com.sepa.service;

import com.google.gson.Gson;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
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
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("publicKey", SEPAService.publicKey);
        responseData.put("amount", "1002");
        responseData.put("currency", "eur");

        return gson.toJson(responseData);
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
            System.out.println("Confirm Status : " + setupIntent.getStatus());

        } catch (StripeException err) {
            System.out.println("Error :" + err.toString() + err.getCode());
            System.out.println(err);
        }catch (Exception ex){
            System.out.println("Error : "+ ex.toString());
        }

        return gson.toJson(setupIntent);
    }

    @RequestMapping("/create-payment-intent")
    public String createDDPaymentIntent () {//(@RequestBody RequestObject requestObject)
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
            System.out.println("Confirm Status : " + createPaymentIntent.getStatus());

        } catch (StripeException err) {
            System.out.println("Error :" + err.toString() + err.getCode());
            System.out.println(err);
        }catch (Exception ex){
            System.out.println("Error : "+ ex.toString());
        }


        return "";
    }

}
