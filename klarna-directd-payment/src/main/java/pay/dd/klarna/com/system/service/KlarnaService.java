package pay.dd.klarna.com.system.service;

import com.google.gson.Gson;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Source;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pay.dd.klarna.com.system.model.Item;
import pay.dd.klarna.com.system.model.RequestObject;
import pay.dd.klarna.com.system.model.ResponseObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class KlarnaService {

    static String publicKey = "pk_test_zpvZCVNflHzto16a1dLAq4yd";
    static String privateKey = "sk_test_d1nvv6ItdRt7YEG2BzKhm3Tm";

    private static Gson gson = new Gson();

    @RequestMapping("/public-key")
    public String getConfigInfo() {
        return KlarnaService.publicKey;
    }

    @RequestMapping("/create-source")
    public ResponseObject createSource() {
        ResponseObject responseObject = new ResponseObject();

        Stripe.apiKey = "sk_test_d1nvv6ItdRt7YEG2BzKhm3Tm";

        Source sourceCreate;

        try {
            Map<String, Object> sourceParams = new HashMap<String, Object>();
            sourceParams.put("type", "klarna");
            sourceParams.put("amount", 1099);
            sourceParams.put("currency", "gbp");

            Map<String, Object> klarnaParams = new HashMap<String, Object>();
            klarnaParams.put("product","payment");
            klarnaParams.put("purchase_country","GB");

            sourceParams.put("klarna", klarnaParams);

            Map<String, Object> orderParams = new HashMap<String, Object>();

            Item item1 = new Item("sku","Grey cotton T-shirt",2,"gbp",500);
            Item item2 = new Item("tax","Taxes","gbp",500);
            Item item3 = new Item("shipping","Free Shipping","gbp",99);

            Item item[] = new Item[3];
            item[0] = item1;
            item[1] = item2;
            item[2] = item3;
            System.out.println(gson.toJson(item));


            orderParams.put("items",item);

            System.out.println(gson.toJson(orderParams));

            //Map<String, Object> orderParams = new HashMap<String, Object>();

           /* Map<String, Object> itemParams1 = new HashMap<String, Object>();
            itemParams1.put("type","sku");
            itemParams1.put("description","Grey cotton T-shirt");
            itemParams1.put("quantity",2);
            itemParams1.put("currency","gbp");
            itemParams1.put("amount",500);

            Map<String, Object> itemParams2 = new HashMap<String, Object>();
            itemParams2.put("type","tax");
            itemParams2.put("description","Taxes");
            itemParams2.put("currency","gbp");
            itemParams2.put("amount",500);

            Map<String, Object> itemParams3 = new HashMap<String, Object>();
            itemParams3.put("type","shipping");
            itemParams3.put("description","Free Shipping");
            itemParams3.put("currency","gbp");
            itemParams3.put("amount",99);

            orderParams.put("items",itemParams1);
            orderParams.put("items",itemParams2);
            orderParams.put("items",itemParams3);
*/
            sourceParams.put("source_order", orderParams);

            System.out.println(gson.toJson(orderParams));

            System.out.println(gson.toJson(sourceParams));

            /*
            *//*Owner Details*//*
            Map<String, Object> ownerParams = new HashMap<String, Object>();
            ownerParams.put("name", requestObject.getAcc_name());
            ownerParams.put("email", requestObject.getEmail());

            *//*Owner Address Details*//*
            Map<String, Object> ownerAddress = new HashMap<String, Object>();
            ownerAddress.put("line1", requestObject.getBill_street());
            ownerAddress.put("city", requestObject.getBill_city());
            ownerAddress.put("country", requestObject.getBill_country());
            ownerAddress.put("postal_code", requestObject.getBill_postalcode());
            ownerParams.put("address", ownerParams);

            sourceParams.put("owner", ownerParams);*/


            sourceCreate = Source.create(sourceParams);
            System.out.println(gson.toJson(sourceCreate));
        } catch (StripeException err) {

            System.out.println("Error :" + err.toString() + err.getCode());
            System.out.println(err.getStripeError().getPaymentIntent().getId());
            System.out.println(err.getStripeError().getPaymentIntent().getClientSecret());
            System.out.println(err.getStripeError().getPaymentMethod().getId());

            responseObject.setError(err.toString());

        } catch (Exception ex) {
            System.out.println("Error : " + ex.toString());
        }


        return responseObject;
    }

    @RequestMapping("/test")
    public ResponseObject createSourceTest() {
        ResponseObject responseObject = new ResponseObject();

        Stripe.apiKey = "sk_test_d1nvv6ItdRt7YEG2BzKhm3Tm";

        Source sourceCreate;


        try {
            Map<String, Object> sourceParams = new HashMap<String, Object>();
            sourceParams.put("type", "klarna");
            sourceParams.put("amount", 500);
            sourceParams.put("currency", "gbp");

            Map<String, Object> klarnaParams = new HashMap<String, Object>();
            klarnaParams.put("product","payment");
            klarnaParams.put("purchase_country","GB");
            sourceParams.put("klarna", klarnaParams);


            Map<String, Object> orderItem = new HashMap<String, Object>();
            orderItem.put("amount", 500);
            orderItem.put("type", "sku");
            orderItem.put("currency", "gbp");
            orderItem.put("description", "Test");
            orderItem.put("quantity", 1);
            ArrayList<Object> items = new ArrayList<Object>();
            items.add(orderItem);

            Map<String, Object> orderParams = new HashMap<String, Object>();
            orderParams.put("items", items);

            sourceParams.put("source_order", orderParams);

            /*System.out.println("\n\n"+gson.toJson(orderParams));

            System.out.println("\n\n"+gson.toJson(sourceParams));
*/
            sourceCreate = Source.create(sourceParams);
            System.out.println("\n\n"+gson.toJson(sourceCreate));
        } catch (StripeException err) {
            System.out.println("Error :" + err.toString() + err.getCode());
            responseObject.setError(err.toString());
        } catch (Exception ex) {
            System.out.println("Error : " + ex.toString());
        }


        return responseObject;
    }

}
