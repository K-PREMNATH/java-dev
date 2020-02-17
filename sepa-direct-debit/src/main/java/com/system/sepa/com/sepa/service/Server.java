package com.system.sepa.com.sepa.service;

import java.util.HashMap;
import java.util.Map;
import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.port;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

public class Server {
    private static Gson gson = new Gson();

    static class CreatePaymentBody {
        @SerializedName("items")
        Object[] items;

        @SerializedName("currency")
        String currency;

        public Object[] getItems() {
            return items;
        }

        public String getCurrency() {
            return currency;
        }
    }

    static class CreatePaymentResponse {
        private String publicKey;
        private String clientSecret;

        public CreatePaymentResponse(String publicKey, String clientSecret) {
            this.publicKey = publicKey;
            this.clientSecret = clientSecret;
        }
    }


    public static void main(String[] args) {
        //port(4242);
/*

        Stripe.apiKey = "sk_test_4LzmKiYvBJKOdMNj1t7scZEc00A2wMcvUe";


        get("/configg", (request, response) -> {
            response.type("application/json");

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("publicKey", "pk_test_161nsotSmf0zRm32dzXQBRs400DU8vowGY");
            responseData.put("amount","1002");
            responseData.put("currency", "eur");
            return gson.toJson(responseData);
        });

        post("/create-payment-intentt", (request, response) -> {
            response.type("application/json");

            CreatePaymentBody postBody = gson.fromJson(request.body(), CreatePaymentBody.class);
            PaymentIntentCreateParams createParams = new PaymentIntentCreateParams.Builder()
                    .addPaymentMethodType("sepa_debit")
                    .setCurrency("eur")
                    .setAmount((long) 1002)
                    .build();
            // Create a PaymentIntent with the order amount and currency
            PaymentIntent intent = PaymentIntent.create(createParams);
            // Send publishable key and PaymentIntent details to client
            return gson.toJson(new CreatePaymentResponse("pk_test_161nsotSmf0zRm32dzXQBRs400DU8vowGY", intent.getClientSecret()));
        });
    }*/
    }
}