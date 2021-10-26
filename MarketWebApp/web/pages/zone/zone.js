var userName;
var userType;
var currentTab = 0
var zoneOwner;
var zoneSelected;
var buyerSeller;
var messageText = "message="
var TWO_SECONDS = 2000;
// for updates!
var updateOrderVersion = 0;
var updateFeedbackVersion = 0;
var updateNewStoreVersion = 0;
// dynamic order objects
// -orders- are the orders received from server after a dynamic order ajax request sent.
var orders;
var userCart;
// sales received from server for current buy
var sales;
var salesChosen;
// objects to show details
// -userOrders- are the orders the customer have (for seller)
var userOrders;
var buyerOrders;
// stores and products from server
var stores;
var products;
//
var feedbacks;
var feedbackVersion = 0;
//// data received from ajax requests  ----   stores and products are saved when page loaded, and updated after order is made.
//// orders are updates and saved when dynamic order is made -> this is the orders the market calculate for the user.
//// userCart holds the products and data (date, stores...) of the order.
//// sales are the sales calculated after picking products and send it to the market.
//// salesChosen are the sales the user pick.
//// feedbacks - an array of feedback that the user can give after an order is made.
//// *** cart , location , sale(local) and feedbacks are json objects, located in jsonObj.js
/// feedback  =  the feedbacks user give the stores at the end of the order.

// each product :
// String NAME;
// String PRICING;
// double AVERAGE_PRICE;
// int SERIAL_NUMBER;
// double quantitySold;
// int NUMBER_OF_STORES_SELLING;

// each zone :
// String name;
// String owner;
// List<StoreDTO> stores;
// List<ProductDTO> products;

// each order:                          each store :                                each sale:
// int SERIAL_NUMBER;                          LocationDTO LOCATION;                    String NAME
// int SUB_ORDER_SERIAL_NUMBER;                int SERIAL_NUMBER;                       BuyDTO BUY--->   double QUANTITY;
// int NUMBER_OF_PRODUCTS;                     double PPK;                                               int ITEM_ID;
// double PRODUCTS_PRICE;                      double PRODUCTS_INCOME;
// String CUSTOMER_NAME;                       String NAME;                             GetDTO GET--->   String operator;
// LocationDTO CUSTOMER_LOCATION;              Map<Integer, SellDTO> ID_TO_SELL;                         List<OfferDTO> Offer --->  double QUANTITY
// SalesUsed SALES_USED;                       Set<SaleDTO> SALES;                                                                  int ID
// StoreDTO STORE;                             Set<OrderDTO> ORDERS;                                                                double EXTRA_COST
// int STORE_SERIAL_NUMBER;                    double DELIVERY_INCOME;
// Map<Integer, Double> ID_TO_QUANTITY;
// String DATE;
// int NUMBER_OF_PRODUCTS_TYPE;

function checkStoreLocations(xVal, yVal) {
    var i;

    for (i = 0; i < stores.length; i++) {
        if (stores[i].LOCATION.x === xVal && stores[i].LOCATION.y === yVal) {
            return false;
        }
    }
    return true;
}

function gatherFeedbackData() {
    var comment = $('#comment').val();
    var rating = $('input[name="rate"]').filter(':checked:first').val();
    var storeSerial = $('.inputGroupFeedbackStore').val();
    if(rating === undefined){
        $('#feedback-message').text("You must rate the store with stars to submit feedback! ")
        return undefined;
    } else if(storeSerial === 'Choose store'){
        $('#feedback-message').text("You must pick a store to submit feedback! ")
        return undefined;
    } else {
        $('#feedback-message').text('');
        var feedback = new Feedback(storeSerial, rating, comment, userCart.date);
        return feedback;
    }
}

function isFeedbackExist(storeSerial) {
    var isExist = false;
    $.each(feedbacks || [], function (index, feedback) {
        if(feedback.storeSerial == storeSerial){
            isExist = true;
            return false;
        }
    });
    return isExist;
}

function updateFeedbacksPage() {
    // reset data
    var select =  $('.inputGroupFeedbackStore')
    select.empty();
    select.append('<option selected>Choose store</option>');

    if(userCart.dynamicOrder){
        $.each(orders || [], function (index, order) {
            if(isFeedbackExist(order.STORE_SERIAL_NUMBER) === false){
                var store = getStore(order.STORE_SERIAL_NUMBER);
                select.append('<option value=' + order.STORE_SERIAL_NUMBER + '>' + store.NAME + '</option>');
            }
        });
    }

    if($('.inputGroupFeedbackStore').children('option').length === 1){
        $('#feedback-sent-message').text("Thank you for buying!")
        // wait 2 sec or pop message
        setTimeout(resetForm, 2000);
    }
}

function getRating(feedback) {
    if(feedback.rating === 'STAR'){
        return 1;
    } else if(feedback.rating === 'TWO_STARS'){
        return 2;
    } else if(feedback.rating === 'THREE_STARS'){
        return 3;
    } else if(feedback.rating === 'FOUR_STARS'){
        return 4;
    } else if(feedback.rating === 'FIVE_STARS'){
        return 5;
    }
}

function appendFeedbacks(index, feedback) {
    var rating = getRating(feedback);

    $('.feedback-panel').append('<div class="card">'
        + '<div class="container">'
        + '<h4><b>Customer : </b>' + feedback.customerName + '</h4>'
        + '<h4><b>Date : </b>' + feedback.date + '</h4>'
        + '<h4><b>Rating : </b>' + rating + '/5</h4>'
        + '<h4><b>Comment : </b>' + feedback.comment + '</h4>'
        + '</div></div>');
}

function ajaxGetFeedbacks(){
    $.ajax({
        method: "GET",
        url: "feedback",
        data: "feedbackVersion=" + feedbackVersion,
        success: function (feedbacksAndVersion) {
            feedbackVersion = feedbacksAndVersion.version;
            $.each(feedbacksAndVersion.feedbacks || [], appendFeedbacks);
        },
        error: function () {
            console.error("Failed to submit feedback");
        }
    });
}

function ajaxSendFeedback(){
    // get all feedback details.
    var feedback = gatherFeedbackData();

    // send feedback to server
    if(feedback !== undefined) {
        $.ajax({
            method: 'POST',
            url: "feedback",
            data: {
                feedback: JSON.stringify(feedback),
                zone: zoneSelected
            },
            success: function (message) {
                $('#feedback-sent-message').text(message);
            },
            error: function () {
                console.error("Failed to submit feedback");
            }
        });
        // alert the feedback was sent
        $('#feedback-message').text("")

        //clear comment
        $('#comment').val('');

        // add feedback to feedbacks array
        feedbacks.push(feedback);

        //update stores that can get feedback
        updateFeedbacksPage();
    }
}

function setFeedbackPage() {
    // clear select area
    var select =  $('.inputGroupFeedbackStore')
    select.empty();
    select.append('<option selected>Choose store</option>');

    // add stores
    if(userCart.dynamicOrder){
        $.each(orders || [], function (index, order) {
            var store = getStore(order.STORE_SERIAL_NUMBER);
            select.append('<option value=' + store.SERIAL_NUMBER + '>' + store.NAME + '</option>');
        });
    } else {
        var store = getStore(userCart.store);
        select.append('<option value=' + store.SERIAL_NUMBER + '>' + store.NAME + '</option>');
    }
}

function getWhatYouGet(sale, productSerial) {
    var offerToReturn = undefined;
    if(productSerial == 0){
        offerToReturn = [];
        $.each(sale.GET.Offer || [], function (index, offer) {
            offerToReturn.push(offer);
        });
    } else {
        $.each(sale.GET.Offer || [], function (index, offer) {
            if(offer.ID == productSerial){
                offerToReturn = offer;
            }
        });
    }

    return offerToReturn;
}

function getSale(store, saleName) {
    var saleToReturn = undefined;
    $.each(store.SALES || [], function (index, sale) {
        if(sale.NAME === saleName){
            saleToReturn = sale;
        }
    });

    return saleToReturn;
}

function getProduct(serialNumber) {
    var i;
    for (i = 0; i < products.length; i++) {
        if (products[i].SERIAL_NUMBER === parseInt(serialNumber)) {
            return products[i];
        }
    }
    return NaN;
}

function getStore(serialNumber){
    var i;
    for (i = 0; i < stores.length; i++) {
        if (stores[i].SERIAL_NUMBER === parseInt(serialNumber)) {
            return stores[i];
        }
    }
    return NaN;
}

function onSuccessUserDetails(customer) {
    // set name on header
    $("#customer-header").text(customer.NAME);

    // save data
    userName = customer.NAME;
    userType = customer.TYPE;
    zoneSelected = sessionStorage.getItem('zone');

    // set balance on header
    $("#balance").text(numberWithCommas(customer.WALLET.balance.toFixed(2)) + ' $');

    // set other data
    setAllData();
}

function appendUserDetails() {
    $.ajax({
        type: "GET",
        url: "../main/user",
        dataType: 'json',
        success: function (customer) {
            onSuccessUserDetails(customer);
        },
        error: function () {
            console.error("Failed to submit user logged in");
        }
    });
}

$(document).ready(function() {
    $("div.bhoechie-tab-menu>div.list-group>a").click(function (e) {
        e.preventDefault();
        $(this).siblings('a.active').removeClass("active");
        $(this).addClass("active");
        var index = $(this).index();
        $("div.bhoechie-tab>div.bhoechie-tab-content").removeClass("active");
        $("div.bhoechie-tab>div.bhoechie-tab-content").eq(index).addClass("active");
    });
});

function setTabs() {
    buyerSeller = sessionStorage.getItem('buyerSeller');
    if (buyerSeller === 'seller') {
        $('.buyer').attr('style', 'display: none');
    } else {
        $('.seller').attr('style', 'display: none');
    }
}

function addStoreProducts(storeSerial){
    var store = getStore(storeSerial);
    $('.store-products-area').empty();

    $.each(store.ID_TO_SELL || [], function addProd(index, sell){
        $('.store-products-area').append('<tr>'
            + '<th scope="row">' + sell.PRODUCT.SERIAL_NUMBER + '</th>'
            + '<td>' + sell.PRODUCT.NAME + '</td>'
            + '<td>' + sell.PRODUCT.PRICING.toString() + '</td>'
            + '<td>' + sell.PRICE + '</td>'
            + '<td>' + sell.QUANTITY_SOLD.toFixed(2) + '</td>'
            + '</tr>');
    });
}

// store ->  description in the top of page.
function addStore(index, store) {
    $('.store-area').append('<tr>'
        + '<th scope="row">' + store.SERIAL_NUMBER + '</th>'
        + '<td>' + store.NAME + '</td>'
        + '<td>' + store.OWNER + '</td>'
        + '<td>' + '[' + store.LOCATION.x + ',' + store.LOCATION.y + ']' + '</td>'
        + '<td>' + store.ORDERS.length + '</td>'
        + '<td>' + store.PRODUCTS_INCOME + '</td>'
        + '<td>' + store.PPK + '</td>'
        + '<td>' + store.deliveryIncome + '</td>'
        + '</tr>');

    $('.custom-select').append('<option value=' + store.SERIAL_NUMBER + '>' + store.NAME + '</option>');
    $('.inputGroupSelectStore').append('<option value=' + store.SERIAL_NUMBER + '>' + store.NAME + '</option>');
}

//// product ->  description in the top of page.
function addProduct(index, product) {
    $('.products-area').append('<tr>'
        + '<th scope="row">' + product.SERIAL_NUMBER + '</th>'
        + '<td>' + product.NAME + '</td>'
        + '<td>' + product.PRICING.toString() + '</td>'
        + '<td>' + product.NUMBER_OF_STORES_SELLING + '</td>'
        + '<td>' + product.AVERAGE_PRICE.toFixed(2) + '</td>'
        + '<td>' + product.quantitySold.toFixed(2) + '</td>'
        + '</tr>');

    $('.choose-products-to-store-area').append('<tr>'
        + '<th scope="row">' + product.SERIAL_NUMBER + '</th>'
        + '<td>' + product.NAME + '</td>'
        + '<td>' + product.PRICING.toString() + '</td>'
        + '<td>' + product.AVERAGE_PRICE.toFixed(2) + '</td>'
        + '<td><input type="number" min="0.01"  id="' + product.SERIAL_NUMBER + '" name="price-input"\n'
        + 'pattern="[1-9.1-9]"></td>'
        + '<td><input type="checkbox" class="products-checkbox" value="' + product.SERIAL_NUMBER + '"/>&nbsp;</td>');
}

function isLocationAvailable(locationX, locationY) {
    var isAvailable = true;
    $.each(stores || [], function (index, store) {
        if (store.LOCATION.x == locationX && store.LOCATION.y == locationY) {
            isAvailable = false;
        }
    });

    return isAvailable;
}

function getNewStoreProducts() {
    var idToPrice = new Map();
    var toReturn = true;
    
    $('.products-checkbox').each(function () {
        if(this.checked === true){
            var price = $('input[id="' + this.value + '"]');
            if(Number.isNaN(Number.parseFloat(price.val()))){
                $('#products-error').text("Some products you picked doesnt have price");
                setTimeout($('#products-error').text(), 2000);
                toReturn = false;
            } else {
                idToPrice.set(this.value, Number.parseFloat(price.val()));
            }
        }
    });

    if(idToPrice.size == 0){
        $('#products-error').text("You must pick products");
    } else if(toReturn === true && idToPrice.size != 0) {
        $('#products-error').text('');
        return idToPrice;
    }
}

function isLocationValid(locationX, locationY) {
    var locationIsValid = true;
    if(locationX < 1 || locationX > 50 || locationY < 1 || locationY > 50){
        locationIsValid = false;
    }
    return locationIsValid;
}

function resetNewStoreForm() {
    $('#products-error').text('');
    $('#store-name-error').text('');
    $('#store-name-input').val('');
    $('#location-error').text('');
    $('#location-x-input').val('');
    $('#location-y-input').val('');
    $('.products-checkbox').each(function () {
        this.checked = false;
        $('input[id="' + this.value + '"]').val('');
    });
}

function sendNewStoreAjax(store) {
    $.ajax({
        method: 'GET',
        url: 'newStore',
        data: {
            zone: zoneSelected,
            store: JSON.stringify(store),
            idToPrice: JSON.stringify(Array.from(store.idToPrice.entries()))
        },
        success: function (message) {
            alert(message);
            resetNewStoreForm();
            setAllData();
        },
        error: function () {
            console.error("Failed to add new store");
        }
    });
}

function addNewStore() {
    // check store name
    var storeName = $('#store-name-input').val();
    if (!(/^[A-Za-z ]+$/.test(storeName))) {
        $('#store-name-error').text("Store name must contain only letters");
    } else {
        $('#store-name-error').text('');
        // check location valid and available
        var locationX = $('#location-x-input').val();
        var locationY = $('#location-y-input').val();
        if (!isInteger(locationX) || !isInteger(locationY)) {
            $('#location-error').text("Location must contain only integers");
        } else if (!isLocationAvailable(locationX, locationY)) {
            $('#location-error').text("Location not available");
        }else if(!isLocationValid(locationX, locationY)) {
            $('#location-error').text("Location must be in range 1-50");
        } else {
            $('#location-error').text('');
            var location = new Location();
            location.x = locationX;
            location.y = locationY;
            var PPK = $('#ppk-input').val();
            var idToPrice = getNewStoreProducts();
            if (idToPrice !== undefined) {
                var store = new Store(storeName, PPK, location, idToPrice, userName);
                sendNewStoreAjax(store);
            }
        }
    }
}

function addZoneName(name) {
    $('#zone-headline').text(", you are viewing " + name + " zone.");
}

function dataClear(){
    $('.store-area').empty();
    $('.products-area').empty();
    $('.choose-products-to-store-area').empty();
    $('.custom-select').empty();
    $('.inputGroupSelectStore').empty();
    $('.custom-select').append('<option selected>Choose store</option>');
    $('.inputGroupSelectStore').append('<option selected>Choose store</option>');
}

// zone -> description in the top of page.
function setData(zone) {
    // save data
    zoneOwner = zone.owner;
    products = zone.products;
    stores = zone.stores;

    // get user orders - after we got the zone
    if(buyerSeller === 'seller'){
        setSellerOrders();
    } else {
        setBuyerOrders();
    }

    // clear tables and messages from data
    dataClear();

    // append the new data
    $.each(zone.stores || [], addStore);
    $.each(zone.products || [], addProduct);
    addZoneName(zone.name);
    checkLocation();
    storeSelect();
}

function setAllData() {
    // set feedback
    ajaxGetFeedbacks();

    $.ajax({
        method: 'GET',
        url: "../../Main",
        dataType: 'json',
        data: "zone=" + zoneSelected,
        success: function (zone) {
            setData(zone);
        },
        error: function () {
            console.error("Failed to submit all data");
        }
    });
}

////////// set orders tab - depend on seller or buyer!
// each seller's store order.
function appendSellerOrders(index, order) {
    $('.orders-area').append('<tr>'
        + '<th scope="row">' + order.SERIAL_NUMBER + '.' + order.SUB_ORDER_SERIAL_NUMBER + '</th>'
        + '<td>' + order.DATE + '</td>'
        + '<td>' + order.CUSTOMER_NAME + '</td>'
        + '<td>' + '[' + order.CUSTOMER_LOCATION.x + ',' + order.CUSTOMER_LOCATION.y + ']' + '</td>'
        + '<td>' + order.NUMBER_OF_PRODUCTS_TYPE + '</td>'
        + '<td>' + order.PRODUCTS_PRICE.toFixed(2) + '</td>'
        + '<td>' + order.DELIVERY_PRICE.toFixed(2) + '</td>'
        + '</tr>');
    $('.inputGroupSelectOrder').append('<option id="' + order.SUB_ORDER_SERIAL_NUMBER + '" value=' + order.SERIAL_NUMBER + '>'
        + order.SERIAL_NUMBER + '.' + order.SUB_ORDER_SERIAL_NUMBER + '</option>');
}

// change table dynamic to seller's order details needed
function appendSellerTableHeader() {
    $('.orders-table').empty();
    $('.orders-table').append('<tr>'
        + '<th scope="col">#</th>'
        + '<th scope="col">Date</th>'
        + '<th scope="col">Customer name</th>'
        + '<th scope="col">Customer location</th>'
        + '<th scope="col">Number of products</th>'
        + '<th scope="col">Products price</th>'
        + '<th scope="col">Delivery price</th>'
        + '</tr>')
}

function setSellerOrders() {
    // table  header append
    appendSellerTableHeader();

    // clear current data
    $('.inputGroupSelectOrder').empty();
    $('.orders-area').empty();
    $('.inputGroupSelectOrder').append('<option selected>Choose order</option>');

    // find user orders
    userOrders = [];
    $.each(stores || [], function (index, store) {
        if(store.OWNER === userName){
            $.each(store.ORDERS || [], function (index1, order){
                userOrders.push(order);
            });
        }
    });

    // append orders
    $.each(userOrders || [], appendSellerOrders);
}

function calculateBuyerOrderDetails(orders) {
    var orderDetails = new BuyerOrderDetails();

    $.each(orders || [], function (index, order){
        orderDetails.productsPrice += order.PRODUCTS_PRICE;
        orderDetails.deliveryPrice += order.DELIVERY_PRICE;
        orderDetails.numOfProducts += order.NUMBER_OF_PRODUCTS;
    });

    return orderDetails;
}

function fillBuyerOrderDetails(orderSerial) {
    var orders = buyerOrders[parseInt(orderSerial)];

    $('.order-product-area').empty();
    $.each(orders || [] , function (index, order){
        var store = getStore(order.STORE_SERIAL_NUMBER);
        Object.keys(order.ID_TO_QUANTITY).forEach(function (key) {
            var sell = store.ID_TO_SELL[key];
            $('.order-product-area').append('<tr>'
                + '<th scope="col">' + key + '</th>'
                + '<th scope="col">' + sell.PRODUCT.NAME + '</th>'
                + '<th scope="col">' + sell.PRODUCT.PRICING + '</th>'
                + '<th scope="col">' + order.ID_TO_QUANTITY[key].toFixed(2) + '</th>'
                + '<th scope="col">' + sell.PRICE.toFixed(2) + '</th>'
                + '<th scope="col">' + (sell.PRICE * order.ID_TO_QUANTITY[key]).toFixed(2) + '</th>'
                + '<th scope="col">No</th>'
                + '</tr>');
        });
        addSalesToOrdersDetails(store, order.SALES_USED);
    });
}

function setBuyerOrdersTable() {
    $('.orders-area').empty();
    $('.inputGroupSelectOrder').empty().append('<option selected>Choose order</option>');
    Object.keys(buyerOrders).forEach(function (key) {
        var ordersDetails = calculateBuyerOrderDetails(buyerOrders[key]);
        var numberOfStores = buyerOrders[key].length;
        var firstOrder = buyerOrders[key][0];
        $('.orders-area').append('<tr>'
            + '<th scope="col">' + key + '</th>'
            + '<th scope="col">' + firstOrder.DATE + '</th>'
            + '<th scope="col">[' + firstOrder.CUSTOMER_LOCATION.x + ',' + firstOrder.CUSTOMER_LOCATION.y + ']</th>'
            + '<th scope="col">' + numberOfStores + '</th>'
            + '<th scope="col">' + ordersDetails.numOfProducts + '</th>'
            + '<th scope="col">' + ordersDetails.productsPrice.toFixed(2) + '</th>'
            + '<th scope="col">' + ordersDetails.deliveryPrice.toFixed(2) + '</th>'
            + '<th scope="col">' + (ordersDetails.productsPrice + ordersDetails.deliveryPrice).toFixed(2) + '</th>'
            + '</tr>');
        $('.inputGroupSelectOrder').append('<option value=' + firstOrder.SERIAL_NUMBER + '>'
            + firstOrder.SERIAL_NUMBER + '</option>');
    });
}

function setBuyerOrders() {
    $.ajax({
        method: 'GET',
        url : 'order',
        dataType: 'json',
        data: "zone=" + zoneSelected,
        success: function(orders){
            buyerOrders = orders.idToOrder;
            setBuyerOrdersTable();
        },
        error: function () {
            console.error("Failed to submit user orders");
        }
    });
}

///////////// make order form functions
function showTab(n) {
    // This function will display the specified tab of the form...
    var x = document.getElementsByClassName("tab");
    x[n].style.display = "block";
    //... and fix the Previous/Next buttons:
    if (n === 0) {
        document.getElementById("prevBtn").style.display = "none";
    } else {
        document.getElementById("prevBtn").style.display = "inline";
    }
    if (n === (x.length - 1)) {
        document.getElementById("nextBtn").innerHTML = "Submit";
    } else {
        document.getElementById("nextBtn").innerHTML = "Next";
    }
    //... and run a function that will display the correct step indicator:
    fixStepIndicator(n)
}

function updateCart() {
    userCart.idToQuantity.forEach(((value, key) => {
        var product = getProduct(key);
        $('.cart-area').append('<tr>'
            + '<th scope="col">' + key + '</th>'
            + '<th scope="col">' + product.NAME + '</th>'
            + '<th scope="col">' + value.toFixed(2) + '</th>'
            + '</tr>');
    }));
}

function addProductToCart(serialNumber){
    // get quantity
    var quantity = parseFloat($("input[id="+serialNumber+"]").val());

    if(Number.isNaN(quantity) === false && quantity !== 0){
        // enable -next- button
        if($('#nextBtn').is(':disabled')){
            $('#nextBtn').prop('disabled', false);
        }

        // add item to cart
        userCart.addItem(serialNumber, quantity);

        // print message to user
        var product = getProduct(serialNumber);
        $('#item-add-label').text(quantity + " " + product.NAME + " added to cart!");

        // update cart table
        $('.cart-area').empty();
        updateCart();
    }
}

function checkFirstTabDetails() {
    if($('#error-location').text() !== 'Location unavailable'){
        var select = $("#inputGroupSelect option:selected").val();
        if($('#one-store').is(':checked')){
            if(select !== 'Choose store'){
                $('#error-store').text('');
                return true;
            } else {
                $('#error-store').text('No store selected');
            }
        } else if ($('#multiple-stores').is(':checked')){
            return true;
        }
    }
    return false;
}

function appendSellToOrder(index, sale) {
    var quantityLine = '<td><input class="col-lg-2 product-quantity" id="' + sale.PRODUCT.SERIAL_NUMBER + '" width="100%" value="1" min="1" step="1" type="number" placeholder="quantity"></td>';
    if(sale.PRODUCT.PRICING.toString() === 'Weight') {
        quantityLine = '<td><input class="col-lg-2 product-quantity" id="' + sale.PRODUCT.SERIAL_NUMBER + '" width="100%" value="0.1" min="0.1" step="0.1" type="number" placeholder="quantity"></td>'
    }
    $('.order-products-area').append('<tr>'
        + '<th scope="row">' + sale.PRODUCT.SERIAL_NUMBER + '</th>'
        + '<td>' + sale.PRODUCT.NAME + '</td>'
        + '<td>' + sale.PRICE + '$</td>'
        + quantityLine
        + '<td><button type="button" value="' + sale.PRODUCT.SERIAL_NUMBER + '" onclick="addProductToCart(this.value)">ADD PRODUCT</button></td>'
        + '</tr>');
}

function appendProductToOrder(index, product) {
    var quantityLine = '<td><input class="col-lg-2 product-quantity" id="' + product.SERIAL_NUMBER + '" width="120" value="1" min="1" step="1" type="number" placeholder="quantity"></td>';
    if(product.PRICING.toString() === 'Weight'){
        quantityLine = '<td><input class="col-lg-2 product-quantity" id="' + product.SERIAL_NUMBER + '" width="120" value="0.1" min="0.1" step="0.1" type="number" placeholder="quantity"></td>'
    }
    $('.order-products-area').append('<tr>'
        + '<th scope="row">' + product.SERIAL_NUMBER + '</th>'
        + '<td>' + product.NAME + '</td>'
        + quantityLine
        + '<td><button type="button" value="' + product.SERIAL_NUMBER + '" onclick="addProductToCart(this.value)">ADD PRODUCT</button></td>'
        + '</tr>');
}

function updateProductsAndCart() {
    userCart = new Cart();
    var storeSerial= $('#inputGroupSelect').val();

    // add products for user choice
    $('.order-products-area').empty();
    if ($('#one-store').is(':checked')) {
        // add price column
        $('<th scope="col">' + 'Price' + '</th>').insertAfter($('#insert-price'));
        var store = getStore(storeSerial);
        userCart.dynamicOrder = false;
        userCart.store = storeSerial;
        // add all stores product
        $.each(store.ID_TO_SELL || [], appendSellToOrder)
    } else {
        // add all products in zone
        $.each(products || [], appendProductToOrder)
    }

    $('#nextBtn').prop('disabled', true);
    userCart.date = $('#date').val();
    userCart.customer = userName;
    userCart.zone = zoneSelected;
    userCart.location.y = $('#location-input-y').val();
    userCart.location.x = $('#location-input-x').val();
}

function resetForm() {
    document.getElementById("regForm").submit();
    return false;
}

function setNextTab(x, n) {
    // Hide the current tab:
    x[currentTab].style.display = "none";
    // Increase or decrease the current tab by 1:
    currentTab = currentTab + n;
}

function addOrder(key, order) {
    var store = getStore(order.STORE_SERIAL_NUMBER);

    $('.store-area-order').append('<tr>'
        + '<th scope="row">' + order.STORE_SERIAL_NUMBER + '</th>'
        + '<td>' + store.NAME + '</td>'
        + '<td>' + '[' + store.LOCATION.x + ',' + store.LOCATION.y + ']' + '</td>'
        + '<td>' + order.DISTANCE.toFixed(2) + '</td>'
        + '<td>' + store.PPK + '</td>'
        + '<td>' + order.DELIVERY_PRICE.toFixed(2) + '</td>'
        + '<td>' + order.NUMBER_OF_PRODUCTS_TYPE + '</td>'
        + '<td>' + order.PRODUCTS_PRICE + '</td>'
        + '</tr>');
}

function getOperator(operator) {
    if(operator === 'ALL-OR-NOTHING'){
        return 'all or nothing'
    } else {
        return 'one of';
    }
}

function addSale(key, sale) {
    var product = getProduct(sale.BUY.ITEM_ID);
    var operator = getOperator(sale.GET.operator);

    $('#sale-container').append('<div class="card shadow p-3 mb-2 bg-white rounded card-' + key + '">'
        + '<div class="card-body text-center">'
        + '<h5>' + sale.NAME + '</h5>'
        + '<h6>Because you bought ' + sale.BUY.QUANTITY + ' ' + product.NAME +'</h6>'
        + '<h6>you can get ' + operator + '</h6>'
        + '<fieldsetid id="sale-' + key + '" class="' + sale.NAME + '"></div></div>');

    if(sale.GET.operator !== 'ALL-OR-NOTHING'){
        $.each(sale.GET.Offer || [], function addRadioButton(index, offer){
            var product = getProduct(offer.ID);
            $('#sale-' + key + '').append('<input type="radio" name="sale-' + key + '" class="' + offer.ID + '">'
               + '<label>' + offer.QUANTITY + " " + product.NAME + " for extra " + offer.EXTRA_COST + '</label><br>');
        });
        $('#sale-' + key + '').after('<br>'
            + '<button id="' + key + '" onclick="event.preventDefault(); addSaleToCart(this.id, true,)">Select</button>');
    } else {
        $.each(sale.GET.Offer || [], function addRadioButton(index, offer){
            var product = getProduct(offer.ID);
            $('#sale-' + key + '').append('<label class="container sales">'
                +  offer.QUANTITY + " " + product.NAME + " for extra " + offer.EXTRA_COST + '</label>');
        });
        $('#sale-' + key + '').after('<button id="' + key + '" onclick="event.preventDefault(); addSaleToCart(this.id, false)">Select</button>');
    }
}

function finishAddSale(parent, id) {
    $('.sale-message').text(parent[0].className + ' sale was added!');
    $('.card-' + id + '').remove();
    if($('#sale-container').is(':empty')){
        $('#sale-container').after('<h4 class="text-lg-center glyphicon glyphicon-cutlery" style="color:#55518a">No sales available</h4>\n')
    } else {
        setTimeout(() => {$('.sale-message').text('')}, 3000);
    }
}

function addSaleToCart(id, isRadio){
    var parent = $('#sale-' + id + ''); //find checked check box;
    if(isRadio){
        $("fieldsetid :radio").each(function() {
            if(this.checked === true) {
                salesChosen.push(new Sale(parent[0].className, this.className));
                finishAddSale(parent, id);
            }});
    } else {
        salesChosen.push(new Sale(parent[0].className, 0));
        finishAddSale(parent, id);
    }
}

// SalesAndOrders{
//     Map<SaleDTO, Integer> sales;
//     List<OrderDTO> orders;
function orderAjaxRequest() {
    $.ajax({
        method: 'POST',
        url: "order",
        dataType: 'json',
        data: {
            idToQuantity: JSON.stringify(Array.from(userCart.idToQuantity.entries())),
            cart: JSON.stringify(userCart)
        },
        success: function (salesAndOrders) {
            if(userCart.dynamicOrder === true){
                $('.store-area-order').empty();
                orders = salesAndOrders.orders;
                $.each(salesAndOrders.orders || [], addOrder);
            }
            salesChosen = [];
            sales = salesAndOrders.sales;
            $('#sale-container').empty();
            $.each(salesAndOrders.sales || [], addSale);
            if($('#sale-container').is(':empty')){
                $('#sale-container').after('<h4 class="text-lg-center glyphicon glyphicon-cutlery" style="color:#55518a">No sales available</h4>\n')
            }
        },
        error: function () {
            console.error("Failed to submit all data");
        }
    });
}

function addSaleToSummary(idToQuantity, idToTotalPrice){
    idToQuantity.forEach(((value, key) => {
        var product = getProduct(key);
        $('.summary-products').append('<tr>'
            + '<th scope="col">' + key + '</th>'
            + '<th scope="col">' + product.NAME + '</th>'
            + '<th scope="col">' + product.PRICING + '</th>'
            + '<th scope="col">' + idToQuantity.get(key).toFixed(2) + '</th>'
            + '<th scope="col">' + (idToTotalPrice.get(key) / idToQuantity.get(key)).toFixed(2) + '</th>'
            + '<th scope="col">' + (idToTotalPrice.get(key)).toFixed(2) + '</th>'
            + '<th scope="col">Yes</th>'
            + '</tr>');
    }));
}

// calculate sales (products price and quantity) and add them to summary table
function addSalesToSummary(storeSerial) {
    var idToQuantity = new Map();
    var idToTotalPrice = new Map();
    var store = getStore(storeSerial);

    $.each(salesChosen || [] ,function (index, sale){
        var storeSale = getSale(store, sale.saleName);

        if(storeSale) {
            var offers = getWhatYouGet(storeSale, sale.productSerial);
            if(sale.productSerial == 0){
                $.each(offers || [] , function (index, offer){
                    var quantity1 = idToQuantity.get(offer.ID);
                    var extraCost1 = idToTotalPrice.get(offer.ID);

                    if (quantity1) {
                        idToQuantity.set(offer.ID, quantity1 + offer.QUANTITY);
                        idToTotalPrice.set(offer.ID, extraCost1 + offer.EXTRA_COST);
                    } else {
                        idToQuantity.set(offer.ID, offer.QUANTITY);
                        idToTotalPrice.set(offer.ID, offer.EXTRA_COST);
                    }
                });
            } else {
                var quantity = idToQuantity.get(sale.productSerial);
                var extraCost = idToTotalPrice.get(sale.productSerial);

                if (quantity) {
                    idToQuantity.set(sale.productSerial, quantity + offers.QUANTITY);
                    idToTotalPrice.set(sale.productSerial, extraCost + offers.EXTRA_COST);
                } else {
                    idToQuantity.set(sale.productSerial, offers.QUANTITY);
                    idToTotalPrice.set(sale.productSerial, offers.EXTRA_COST);
                }
            }
        }
    });
    addSaleToSummary(idToQuantity, idToTotalPrice);
}

// after user pick a store to show in summary window, all the store product that was orders will shown!
function addSummaryProducts(storeSerial) {
    $('.summary-products').empty();

    if (userCart.dynamicOrder === false) {
        var store = getStore(storeSerial);
        userCart.idToQuantity.forEach(((value, key) => {
            var sell = store.ID_TO_SELL[key];
            $('.summary-products').append('<tr>'
                + '<th scope="col">' + key + '</th>'
                + '<th scope="col">' + sell.PRODUCT.NAME + '</th>'
                + '<th scope="col">' + sell.PRODUCT.PRICING + '</th>'
                + '<th scope="col">' + value.toFixed(2) + '</th>'
                + '<th scope="col">' + sell.PRICE.toFixed(2) + '</th>'
                + '<th scope="col">' + (sell.PRICE * value).toFixed(2) + '</th>'
                + '<th scope="col">No</th>'
                + '</tr>');
        }));
        addSalesToSummary(storeSerial);
    } else {
        $.each(orders || [], function (index, order) {
            if (order.STORE_SERIAL_NUMBER == storeSerial) {
                var store = getStore(order.STORE_SERIAL_NUMBER);
                Object.keys(order.ID_TO_QUANTITY).forEach(function (key) {
                    var sell = store.ID_TO_SELL[key];
                    $('.summary-products').append('<tr>'
                        + '<th scope="col">' + key + '</th>'
                        + '<th scope="col">' + sell.PRODUCT.NAME + '</th>'
                        + '<th scope="col">' + sell.PRODUCT.PRICING + '</th>'
                        + '<th scope="col">' + order.ID_TO_QUANTITY[key].toFixed(2) + '</th>'
                        + '<th scope="col">' + sell.PRICE.toFixed(2) + '</th>'
                        + '<th scope="col">' + (sell.PRICE * order.ID_TO_QUANTITY[key]).toFixed(2) + '</th>'
                        + '<th scope="col">No</th>'
                        + '</tr>');
                });
                addSalesToSummary(storeSerial);
            }
        });
    }
}

function fillDynamicStoresSummary(index, order) {
    var store = getStore(order.STORE_SERIAL_NUMBER);

    $('.summary-stores').append('<tr>'
        + '<th scope="row">' + store.SERIAL_NUMBER + '</th>'
        + '<td>' + store.NAME + '</td>'
        + '<td>' + store.PPK + '</td>'
        + '<td>' + order.DISTANCE.toFixed(2) + '</td>'
        + '<td>' + order.DELIVERY_PRICE.toFixed(2) + '</td>'
        + '</tr>');
    $('.inputGroupSelectSummary').append('<option value=' + store.SERIAL_NUMBER + '>' + store.NAME + '</option>');

    var currentProductsPrice = parseFloat($('#products-price-summary').text());
    var currentDeliveryPrice = parseFloat($('#delivery-price-summary').text());
    var currentTotalPrice = parseFloat($('#total-price-summary').text());
    var saleMoney = getProductsPriceFromSalesChosen(store);
    $('#products-price-summary').text((currentProductsPrice + order.PRODUCTS_PRICE  + saleMoney).toFixed(2));
    $('#delivery-price-summary').text((currentDeliveryPrice + order.DELIVERY_PRICE).toFixed(2));
    $('#total-price-summary').text((currentTotalPrice + order.PRODUCTS_PRICE + order.DELIVERY_PRICE  + saleMoney).toFixed(2));
}

function getProductsPriceFromCart(store) {
    var productsPrice = 0;
    userCart.idToQuantity.forEach(((value, key) => {
        var sell = store.ID_TO_SELL[key];
        productsPrice += (sell.PRICE * value);
    }));
    return productsPrice.toFixed(2);
}

function getProductsPriceFromSalesChosen(store) {
    var productsPrice = 0;
    $.each(salesChosen || [] ,function (index, sale) {
        var storeSale = getSale(store, sale.saleName);
        if (storeSale) {
            var offers = getWhatYouGet(storeSale, sale.productSerial);
            if (sale.productSerial == 0) {
                $.each(offers || [], function (index, offer) {
                    productsPrice += offer.EXTRA_COST;
                });
            } else {
                productsPrice += offers.EXTRA_COST;
            }
        }
    });
    return productsPrice;
}

function resetOrderSummary() {
    $('#products-price-summary').text(0);
    $('#delivery-price-summary').text(0);
    $('#total-price-summary').text(0);
    $('.summary-stores').empty();
    $('.inputGroupSelectSummary').empty();
    $('.inputGroupSelectSummary').append('<option selected>Choose store</option>');
}

function setOrderSummary() {
    resetOrderSummary();

    if(userCart.dynamicOrder === false){
        var store = getStore(userCart.store);
        var distance = Location.calculateDistance(store.LOCATION.x, store.LOCATION.y, userCart.location.x, userCart.location.y);
        var deliveryPrice = (distance * store.PPK).toFixed(2);
        var productsPrice = parseFloat(getProductsPriceFromCart(store)) + parseFloat(getProductsPriceFromSalesChosen(store));

        //add store details
        $('.summary-stores').append('<tr>'
            + '<th scope="row">' + store.SERIAL_NUMBER + '</th>'
            + '<td>' + store.NAME + '</td>'
            + '<td>' + store.PPK + '</td>'
            + '<td>' + parseFloat(distance).toFixed(2) + '</td>'
            + '<td>' + deliveryPrice + '</td>'
            + '</tr>');

        $('#products-price-summary').text(parseFloat(productsPrice).toFixed(2));
        $('#delivery-price-summary').text(parseFloat(deliveryPrice).toFixed(2));
        $('#total-price-summary').text((parseFloat(productsPrice) + parseFloat(deliveryPrice)).toFixed(2));
        $('.inputGroupSelectSummary').append('<option value=' + store.SERIAL_NUMBER + '>' + store.NAME + '</option>');
    } else {
        $.each(orders || [], fillDynamicStoresSummary);
    }
}

// after finishing order and going over the summary, send all order details to add to zone!
function ajaxOrderSend() {
    $.ajax({
        method: 'POST',
        url: "orderFinish",
        dataType: 'json',
        data: {
            zone: zoneSelected,
            idToQuantity: JSON.stringify(Array.from(userCart.idToQuantity.entries())),
            cart: JSON.stringify(userCart),
            salesChosen: JSON.stringify(salesChosen),
            orders: JSON.stringify(orders)
        },
        success: function (message) {
            $('#ajax-message').text(message);
        },
        error: function () {
            console.error("Failed to submit order");
        }
    });
}

function getOrderToShow(orderSerial, subOrderSerial) {
    var orderReturn;
    $.each(userOrders || [], function (index, order){
        if(order.SERIAL_NUMBER == orderSerial && order.SUB_ORDER_SERIAL_NUMBER == subOrderSerial){
            orderReturn = order;
            return false;
        }
    });
    return orderReturn;
}

function addOneOfSalesData(store, SALES_USED, idToQuantity, idToTotalPrice){
    Object.keys(SALES_USED.saleNameToItemPick).forEach(function (key) {
        var sale = getSale(store, key);
        $.each(SALES_USED.saleNameToItemPick[key] || [] , function (index, productSerial) {
            if(sale) {
                var offer = getWhatYouGet(sale, productSerial);
                var quantity = idToQuantity.get(productSerial);
                var extraCost = idToTotalPrice.get(productSerial);

                if (quantity) {
                    idToQuantity.set(productSerial, quantity + offer.QUANTITY);
                    idToTotalPrice.set(productSerial, extraCost + offer.EXTRA_COST);
                } else {
                    idToQuantity.set(productSerial, offer.QUANTITY);
                    idToTotalPrice.set(productSerial, offer.EXTRA_COST);
                }
            }
        });
    });
}

function addAllOrNothingSale(store, SALES_USED, idToQuantity, idToTotalPrice) {
    Object.keys(SALES_USED.allOrNothingSaleCounter).forEach(function (key) {
        var sale = getSale(store, key);
        $.each(sale.GET.Offer || [], function (index, offer) {
            var times = SALES_USED.allOrNothingSaleCounter[key];
            var quantity = idToQuantity.get(offer.ID);
            var extraCost = idToTotalPrice.get(offer.ID);

            if (quantity) {
                idToQuantity.set(offer.ID, quantity + (offer.QUANTITY * times));
                idToTotalPrice.set(offer.ID, extraCost + (offer.EXTRA_COST * times));
            } else {
                idToQuantity.set(offer.ID, (offer.QUANTITY * times));
                idToTotalPrice.set(offer.ID, (offer.EXTRA_COST * times));
            }
        });
    });
}

function addSalesToOrdersDetails(store, SALES_USED) {
    var idToQuantity = new Map();
    var idToTotalPrice = new Map();

    // add one-of sales
    addOneOfSalesData(store, SALES_USED, idToQuantity, idToTotalPrice);

    //add all or nothing sales
    addAllOrNothingSale(store, SALES_USED, idToQuantity, idToTotalPrice);

    idToQuantity.forEach(((value, key) => {
        var product = getProduct(key);
        $('.order-product-area').append('<tr>'
            + '<th scope="col">' + key + '</th>'
            + '<th scope="col">' + product.NAME + '</th>'
            + '<th scope="col">' + product.PRICING + '</th>'
            + '<th scope="col">' + idToQuantity.get(key).toFixed(2) + '</th>'
            + '<th scope="col">' + (idToTotalPrice.get(key) / idToQuantity.get(key)).toFixed(2) + '</th>'
            + '<th scope="col">' + (idToTotalPrice.get(key)).toFixed(2) + '</th>'
            + '<th scope="col">Yes</th>'
            + '</tr>');
    }));
}
function fillSellerOrderDetails(orderSerial){
    var subOrderSerial = $('.inputGroupSelectOrder').children(":selected").attr("id");
    $('.order-product-area').empty();
    var order = getOrderToShow(orderSerial, subOrderSerial);
    var store = getStore(order.STORE_SERIAL_NUMBER);
    Object.keys(order.ID_TO_QUANTITY).forEach(function (key) {
        var sell = store.ID_TO_SELL[key];
        $('.order-product-area').append('<tr>'
            + '<th scope="col">' + key + '</th>'
            + '<th scope="col">' + sell.PRODUCT.NAME + '</th>'
            + '<th scope="col">' + sell.PRODUCT.PRICING + '</th>'
            + '<th scope="col">' + order.ID_TO_QUANTITY[key].toFixed(2) + '</th>'
            + '<th scope="col">' + sell.PRICE.toFixed(2) + '</th>'
            + '<th scope="col">' + (sell.PRICE * order.ID_TO_QUANTITY[key]).toFixed(2) + '</th>'
            + '<th scope="col">No</th>'
            + '</tr>');
    });
    addSalesToOrdersDetails(store, order.SALES_USED);
}

//fill order details
function fillOrdersDetails(orderSerial){
    if(buyerSeller === "buyer"){
        fillBuyerOrderDetails(orderSerial);
    } else {
        fillSellerOrderDetails(orderSerial);
    }
}

// this function gather the order info and sends it to the server to be executed!
// also opens a modal to give feedback for the seller for the shopping experience.
function sendOrder() {
    //send order
    ajaxOrderSend();

    // allocate* feedback array
    feedbacks = [];

    // open feedback modal
    setFeedbackPage();
}

// This function will figure out which tab to display
function nextPrev(n) {
    var x = document.getElementsByClassName("tab");

    //if cancel button was pressed
    if(n === -1){
        resetForm();
    }
    // Exit the function if any field in the current tab is invalid:
    if (n == 1 && !validateForm()) return false;

    // valid first tab data
    if (currentTab === 0) {
        if (checkFirstTabDetails()) {
            updateProductsAndCart()
            setNextTab(x, n);
        }
    }
    // after user picked products
    else if(currentTab === 1) {
        orderAjaxRequest();
        if($('#one-store').is(':checked')){  //skip dynamic order details
            setNextTab(x, (n+1));
            fixStepIndicator(n);
        } else {
            setNextTab(x, n);
        }
    }
    // after user picked sales.
    else if (currentTab === 3){
        setOrderSummary();
        setNextTab(x, n);
    } else if(currentTab === 4){
        sendOrder();
        setNextTab(x, n);
    } else {
        // set next tab
        setNextTab(x, n);
    }

    // if user have reached the end of the form...
    if (currentTab >= x.length) {
        // reset form
        resetForm();
    }
    // Otherwise, display the correct tab:
    showTab(currentTab);
}

function updateDeliveryPrice() {
    var xVal = $('#location-input-x').val();
    var yVal = $('#location-input-y').val();
    var storeSerial;
    var i;

    if (isInteger(xVal) && isInteger(yVal) && $('#error-location').text() !== 'Location unavailable') {
        if ($('#inputGroupSelect').val() !== 'Choose store' && $('#one-store').is(':checked')) {
            storeSerial = $('#inputGroupSelect').val();
            if(storeSerial !== 'Choose store'){
                var store = getStore(storeSerial);
                printDeliveryPrice(store, parseInt(xVal), parseInt(yVal));
            }
        } else {
            $('#delivery-price').text('');
        }
    } else {
        $('#delivery-price').text('');
    }
}

// bloc store pick if multiple store was selected!
function blockStorePick(){
    if ($('#one-store').is(':checked')) {
        updateDeliveryPrice();
        $("#inputGroupSelect").prop('disabled', false);
    } else {
        $('#delivery-price').text('');
        $('#error-store').text('');
        $("#inputGroupSelect").prop('disabled', true);
    }
}

// This function deals with validation of the form fields
function validateForm() {
    var x, y, i, valid = true;
    x = document.getElementsByClassName("tab");
    y = x[currentTab].getElementsByTagName("input");
    // A loop that checks every input field in the current tab:
    for (i = 0; i < y.length; i++) {
        // If a field is empty...
        if (y[i].value == "") {
            // add an "invalid" class to the field:
            y[i].className += " invalid";
            // and set the current valid status to false
            valid = false;
        }
    }
    // If the valid status is true, mark the step as finished and valid:
    if (valid) {
        document.getElementsByClassName("step")[currentTab].className += " finish";
    }
    return valid; // return the valid status
}

// This function removes the "active" class of all steps...
function fixStepIndicator(n) {
    var i, x = document.getElementsByClassName("step");
    for (i = 0; i < x.length; i++) {
        x[i].className = x[i].className.replace(" active", "");
    }
    //... and adds the "active" class on the current step:
    x[n].className += " active";
}

// after setUpLocation function - if the location is unavailable - prints message.
function printIsLocationAvailable(isAvailable){
    if(isAvailable === false){
        $('#error-location').text('Location unavailable');
    } else {
        $('#error-location').text('');
    }
}

// check if the location given is available
function setUpLocation() {
    var xVal = $('#location-input-x').val();
    var yVal = $('#location-input-y').val();
    var isAvailable = true;

    if(isInteger(xVal) && isInteger(yVal)){
        isAvailable = checkStoreLocations(parseInt(xVal), parseInt(yVal));
    }

    printIsLocationAvailable(isAvailable);
}

// set on change function for location fields to update!
function checkLocation() {
    $("#location-input-x").on("keyup keydown change",function(event){
        setUpLocation();
    });

    $("#location-input-y").on("keyup keydown change",function(event){
        setUpLocation();
    });
}

// on make order first tab - print the delivery price
function printDeliveryPrice(store, xLocation, yLocation) {
    $('#delivery-price').text('delivery price : ' +
        (Location.calculateDistance(xLocation, yLocation, store.LOCATION.x, store.LOCATION.y) * store.PPK).toFixed(2)+ '$');
    $('#error-store').text('');
}

// on make order, was choosing store or setting up location - update the delivery price.
function storeSelect() {
    $('.check-price').change(function (){
        updateDeliveryPrice();
    });
}

function addFeedbackUpdate(index, feedback) {
    var currentText = $('.update-text').text();
    var feedbackDetails = "NEW FEEDBACK !!! customer : " + feedback.customerName + ",\trating : " + feedback.rating + "/5" +
        "\t,comment : " + feedback.comment + "\t\t||\t\t";
    $('.update-text').text(currentText + feedbackDetails);
}

function addStoreUpdate(index, store) {
    var currentText = $('.update-text').text();
    var storeDetails = "NEW STORE !!! store name : " + store.storeName + ",\towner : " + store.ownerName + ",\tzone : "
        + store.zone + ",\tproducts : " + store.storeNumberOfProducts + "/" + store.zoneNumberOfProducts + "\t\t||\t\t";
    $('.update-text').text(currentText + storeDetails);
}

function addOrderUpdate(index, order) {
    var currentText = $('.update-text').text();
    var orderDetails = "NEW ORDER !!! Order no' : " + order.orderSerialNumber + "." + order.subOrderSerialNumber
        + ",\tCustomer : " + order.customerName + ",\tProducts : " + order.numberOfProductsTypes.toFixed(2) + ",\tProducts price : " + order.productsPrice
        + ",\tDelivery price :" + order.deliveryPrice.toFixed(2) + "\t\t||\t\t";
    $('.update-text').text(currentText + orderDetails);
}

function printAndGetUpdates(updatesAndVersions) {
    if(updatesAndVersions.feedbacks.length == 0 && updatesAndVersions.stores.length == 0 && updatesAndVersions.orders.length == 0){
        return;
    } else {
        appendUserDetails();
        $.each(updatesAndVersions.feedbacks || [], addFeedbackUpdate);
        $.each(updatesAndVersions.stores || [], addStoreUpdate);
        $.each(updatesAndVersions.orders || [], addOrderUpdate);
    }
}

function getUpdates() {
    $.ajax({
        method: 'GET',
        url: 'update',
        dataType: 'JSON',
        data: {
            updateOrderVersion: updateOrderVersion,
            updateFeedbackVersion : updateFeedbackVersion,
            updateNewStoreVersion: updateNewStoreVersion
        },
        success: function (updatesAndVersions){
            updateOrderVersion = updatesAndVersions.orderVersion;
            updateFeedbackVersion = updatesAndVersions.feedbackVersion;
            updateNewStoreVersion = updatesAndVersions.newStoreVersion;
            printAndGetUpdates(updatesAndVersions);
        },
        error: function (){
            console.log("Failed to get updates");
        }
    });
}


function hideUpdates() {
    $('.updates').hide();
}

// on load data arrange
$(function() {
    // set tabs
    setTabs();

    // update user details
    appendUserDetails();

    // date block
    blockPastDates("date");

    // set make order tabs
    showTab(currentTab); // Display the current tab

    // in case the user is seller - get updates!
    if(buyerSeller == "seller"){
        setInterval(getUpdates, TWO_SECONDS);
    } else {
        hideUpdates();
    }
});

