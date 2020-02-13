var stripeElements = function(publicKey, setupIntent) {
  var stripe = Stripe(publicKey);
  var elements = stripe.elements();

  // Element styles
  var style = {
    base: {
      fontSize: "16px",
      color: "#32325d",
      fontFamily:
        "-apple-system, BlinkMacSystemFont, Segoe UI, Roboto, sans-serif",
      fontSmoothing: "antialiased",
      "::placeholder": {
        color: "rgba(0,0,0,0.4)"
      }
    }
  };

  var card = elements.create("card", { style: style });

  card.mount("#card-element");

  // Element focus ring
  card.on("focus", function() {
    var el = document.getElementById("card-element");
    el.classList.add("focused");
  });

  card.on("blur", function() {
    var el = document.getElementById("card-element");
    el.classList.remove("focused");
  });

  // Handle payment submission when user clicks the pay button.
  var button = document.getElementById("submit");
  button.addEventListener("click", function(event) {
    event.preventDefault();
    changeLoadingState(true);
    var email = document.getElementById("email").value;

    stripe
      .confirmCardSetup(setupIntent, {
        payment_method: {
          card: card,
          billing_details: { email: email }
        }
      })
      .then(function(result) {

        if (result.error) {
          changeLoadingState(false);
          var displayError = document.getElementById("card-errors");
          displayError.textContent = result.error.message;
        } else {
          // The PaymentMethod was successfully setup
          // Be sure to attach the PaymentMethod to a Customer as shown by
          // the server webhook in this sample
          console.log(result);
          console.log(result.setupIntent.id);
          console.log(result.setupIntent.client_secret);
          console.log(result.setupIntent.payment_method);
          var output = {
            id:result.setupIntent.id,
            client_secret:result.setupIntent.client_secret,
            payment_method:result.setupIntent.payment_method
          }

            return fetch("/add-customer", {
                        method: "POST",
                        headers: {
                          "Content-Type": "application/json"
                        },
                        body: JSON.stringify(output)
                      })
                      .then((response) => {
                          return response.json();
                        })
                        .then((myJson) => {
                          console.log(myJson.clientSecret);
                          //

                                  var stripe_api = Stripe('pk_test_161nsotSmf0zRm32dzXQBRs400DU8vowGY');


                                  stripe_api.retrievePaymentIntent(myJson.clientSecret)
                                      .then(function(result) {
                                          // Handle result.error or result.paymentIntent
                                          console.log(result.paymentIntent);
                                          console.log(result.paymentIntent.amount);
                                          console.log(result.paymentIntent.client_secret);

                                          stripe_api
                                          .handleCardPayment(result.paymentIntent.client_secret, {
                                              payment_method: result.paymentIntent.last_payment_error.payment_method.id,
                                          })
                                          .then(function(result) {
                                              // Handle result.error or result.paymentIntent
                                              console.log(result.paymentIntent);
                                              console.log(result.error);
                                                var output = {
                                                            id:result.paymentIntent.id,
                                                            client_secret:result.paymentIntent.client_secret,
                                                            payment_method:result.paymentIntent.payment_method
                                                          }
                                              return fetch("/capture_payment", {
                                                  method: "POST",
                                                  headers: {
                                                    "Content-Type": "application/json"
                                                  },
                                                  body: JSON.stringify(output)
                                                });


                                          });


                                      });

                          //

                        });

          location.reload();
        }
      });
  });
};

var getSetupIntent = function(publicKey) {
  return fetch("/create-setup-intent", {
    method: "post",
    headers: {
      "Content-Type": "application/json"
    }
  })
    .then(function(response) {
      return response.json();
    })
    .then(function(setupIntent) {
      stripeElements(publicKey, setupIntent);
    });
};

var getPublicKey = function() {
  return fetch("/public-key", {
    method: "get",
    headers: {
      "Content-Type": "application/json"
    }
  })
    .then(function(response) {
    var key = response.publicKey;
    console.log(key);
      return response.json();
    })
    .then(function(response) {
        var key = response.publicKey;
      //getSetupIntent(response.publicKey);
    });
};

// Show a spinner on payment submission
var changeLoadingState = function(isLoading) {
  if (isLoading) {
    document.querySelector("button").disabled = true;
    document.querySelector("#spinner").classList.remove("hidden");
    document.querySelector("#button-text").classList.add("hidden");
  } else {
    document.querySelector("button").disabled = false;
    document.querySelector("#spinner").classList.add("hidden");
    document.querySelector("#button-text").classList.remove("hidden");
  }
};

/* Shows a success / error message when the payment is complete */
var orderComplete = function(stripe, clientSecret) {
  stripe.retrieveSetupIntent(clientSecret).then(function(result) {
    var setupIntent = result.setupIntent;
    var setupIntentJson = JSON.stringify(setupIntent, null, 2);

    document.querySelector(".sr-payment-form").classList.add("hidden");
    document.querySelector(".sr-result").classList.remove("hidden");
    document.querySelector("pre").textContent = setupIntentJson;
    setTimeout(function() {
      document.querySelector(".sr-result").classList.add("expand");
    }, 200);

    changeLoadingState(false);
  });
};

//getPublicKey();


function getKey(){
var key;
    $.ajax({
        type: "GET",
        url: 'http://localhost:8082/public-key',
        dataType: "text",
        async : false,
        success: function( response) {
            key = response;
        }
    });
    console.log(key);
    return key;
}

function getSetupIntentObject(){
var key;
    $.ajax({
        type: "GET",
        url: 'http://localhost:8082/create-setup-intent',
        dataType: "text",
        async : false,
        success: function( response) {
            key = response;
        }
    });
    console.log(key);
    return key;
}

$(document).ready(function(){
var stripeKey = getKey();
var clientSecretKey = getSetupIntentObject();

stripeElements(stripeKey, clientSecretKey);
});