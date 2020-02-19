var stripe = Stripe('pk_test_zpvZCVNflHzto16a1dLAq4yd');
var elements = stripe.elements();

var style = {
  base: {
    color: '#32325d',
    fontSize: '16px',
    '::placeholder': {
      color: '#aab7c4'
    },
    ':-webkit-autofill': {
      color: '#32325d',
    },
  },
  invalid: {
    color: '#fa755a',
    iconColor: '#fa755a',
    ':-webkit-autofill': {
      color: '#fa755a',
    },
  },
};

var options = {
  style: style,
  supportedCountries: ['SEPA'],
  // Elements can use a placeholder as an example IBAN that reflects
  // the IBAN format of your customer's country. If you know your
  // customer's country, we recommend that you pass it to the Element as the
  // placeholderCountry.
  placeholderCountry: 'DE',
};

// Create an instance of the IBAN Element
var iban = elements.create('iban', options);

// Add an instance of the IBAN Element into the `iban-element` <div>
iban.mount('#iban-element');

var form = document.getElementById('payment-form');
var accountholderName = document.getElementById('accountholder-name');
var email = document.getElementById('email');
var submitButton = document.getElementById('submit-button');



form.addEventListener('submit', function(event) {
  event.preventDefault();
  var data = getSetupIntentObject();
  var jsonToText = JSON.parse(data);
  var clientSecret = jsonToText.clientSecret;
  var paymentIntentId = data.paymentIntentId;

  var conPay = stripe.confirmSepaDebitPayment(
                clientSecret,
                {
                  payment_method: {
                    sepa_debit: iban,
                    billing_details: {
                      name: accountholderName.value,
                      email: email.value,
                    },
                  },
                }
              )
              .then(function(result) {
                    console.log(result);
                    var output = {
                                id:result.paymentIntent.id,
                                client_secret:result.paymentIntent.client_secret,
                                payment_method:result.paymentIntent.payment_method
                            }
                    return fetch("/add-customer", {
                        method: "POST",
                        headers: {
                            "Content-Type": "application/json"
                        },
                        body: JSON.stringify(output)
                        });

              })
              ;

  console.log(conPay);

});

function getSetupIntentObject(){
var key;
    $.ajax({
        type: "GET",
        url: 'http://localhost:8080/create-payment-intent',
        dataType: "text",
        async : false,
        success: function(response) {
            key = response;
        }
    });
    console.log(key);
    return key;
}

function sendPaymentDetail(){
var key;
    $.ajax({
        type: "GET",
        url: 'http://localhost:8080/create-payment-intent',
        dataType: "text",
        async : false,
        success: function(response) {
            key = response;
        }
    });
    console.log(key);
    return key;
}