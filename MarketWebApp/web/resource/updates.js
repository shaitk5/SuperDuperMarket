function addFeedbackUpdate(index, feedback) {
    var currentText = $('.update-text').text();
    var feedbackDetails = "NEW FEEDBACK !!! customer : " + feedback.customer + ",  rating : " + feedback.rating + "/5" +
        ",  comment : " + feedback.comment + "        ";
    $('.update-text').text(currentText + feedbackDetails);
}

function addStoreUpdate(index, store) {
    var currentText = $('.update-text').text();
    var storeDetails = "NEW STORE !!! store name : " + store.storeName + ",  owner : " + store.ownerName + ",  zone : "
        + store.zone + ",  products : " + store.storeNumberOfProducts + "/" + store.zoneNumberOfProducts + "        ";
    $('.update-text').text(currentText + storeDetails);
}

function addOrderUpdate(index, order) {
    var currentText = $('.update-text').text();
    var orderDetails = "NEW ORDER !!! Order no' : " + order.orderSerialNumber + "." + order.subOrderSerialNumber +
        + ",  Customer : " + order.customerName + ",  Products : " + order.numberOfProductsTypes + ",  Products price : " + order.productsPrice
        + ",  Delivery price :" + order.deliveryPrice;
    $('.update-text').text(currentText + orderDetails);
}

function printAndGetUpdates(updatesAndVersions) {
    if(updatesAndVersions.feedbacks.length == 0 && updatesAndVersions.stores.length == 0 && updatesAndVersions.orders.length == 0){
        return;
    } else {
        setAllData();
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