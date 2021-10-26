var chatVersion = 0;
var isButtonAdjust = false;
var userElement;
var userName;
var userType;
var messageText = "message="
var chatVersionText = "chatVersion=";
var chatTime = 1000;
var usersTime = 2000;
var zonesTime = 2000;
// for updates!
var updateOrderVersion = 0;
var updateFeedbackVersion = 0;
var updateNewStoreVersion = 0;

function addMyMessage(message) {
    var messageCopy = $('.messages-you').first().clone();
    messageCopy.removeClass('messages-you').addClass('messages-me');
    messageCopy.find('#chat-username').text('Me');
    messageCopy.find('p').text(message.message);
    messageCopy.find('#time').text(message.time);
    messageCopy.prependTo('.messages-list');
}

function addOtherMessage(message) {
    var messageCopy = $("#massage").clone(true);
    messageCopy.removeClass('messages-me').addClass('messages-you');
    messageCopy.find('#chat-username').text(message.username);
    messageCopy.find('p').text(message.message);
    messageCopy.find('#time').text(message.time)
    messageCopy.prependTo('.messages-list');
}

function addChatMassage (message){
    //add smileys if wanted
    // entry.massage = entry.massage.replace (":)", "<img class='smiley-image' src='../../common/images/smiley.png'/>");

    //copy and append new massage
    if(message.username === userName){
        addMyMessage(message);
    }
    else {
        addOtherMessage(message);
    }
}

function appendAndCreateChatMassage(index, message){
    addChatMassage(message);
}

function appendToChatArea(messages) {
    // add massages to chat
    $.each(messages || [], appendAndCreateChatMassage);

    // append scroller - option
    // var scroller = $("#chatarea");
    // var height = scroller[0].scrollHeight - $(scroller).height();
    // $(scroller).stop().animate({ scrollTop: height }, "slow");
}

//add a method to the button in order to make that form use AJAX
//and not actually submit the form
$(function() { // onload
    $("#send-message").submit(function() {
        $.ajax({
            data: messageText + $('#input-me').val(),
            url: 'sendChat',
            error: function() {
                console.error('Failed to submit chat');
            },
            success: function(r) {}
        });
        $('#input-me').val('');
        // by default - we'll always return false so it doesn't redirect the user.
        return false;
    });
});



function setChat(){
    // refresh chat
    $.ajax({
        type: "POST",
        url: 'chat',
        data: chatVersionText + chatVersion,
        dataType: 'json',
        success: function(result) {
            if (result.version !== chatVersion) {
                chatVersion = result.version;
                appendToChatArea(result.messages);
            }},
        error: function() {
            console.error("Failed to submit");
        }});
}

function setUserDetails(){
    // updates
    if(userType === "Seller"){
        getUpdates();
    } else {
        hideUpdates();
    }

    // send Ajax request again
    setTimeout(appendUserDetails, usersTime);
}

function numberWithCommas(x) {
    return x.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}


function appendUserDetails() {
    $.ajax({
        type: "GET",
        url: "user",
        dataType: 'json',
        success: function(customer) {
            $("#customer-header").text(customer.NAME);
            userName = customer.NAME;
            userType = customer.TYPE;
            adjustButton(customer.TYPE);
            $("#balance").text(numberWithCommas(customer.WALLET.balance.toFixed(2)) + ' $');
            $('.transaction-area').empty();
            $.each(customer.WALLET.transactions || [], appendTransaction);
            setUserDetails();
        },
        error: function () {
            console.error("Failed to submit user logged in");
            setUserDetails();
        }
    });
}

function addUsers(users) {
    //clear all current users
    $('.people-nearby').empty();

    // rebuild the list of users: scan all users and add them to the list of users
    $.each(users || [], function() {
        var userShow = userElement.clone();
        userShow.find('.username').text(this.NAME);
        userShow.find('#buyer-seller').text(this.TYPE);
        userShow.appendTo($('.people-nearby'));
    });
}

function setUsers() {
    $.ajax({
        type: "POST",
        url: "user",
        dataType: 'json',
        success: function(users) {
            addUsers(users);
        },
        error: function () {
            console.error("Failed to submit users in system");
        }
    });
}

function saveUserElement() {
    userElement = $('.nearby-user').clone();
    $('.people-nearby').empty();
}

function setFormToGetFile(){
    $("input[name='file']").change(function() {

        var file = this.files[0];
        var formData = new FormData();

        formData.append("file", file);
        formData.append("username", $('#customer-header').text());

        $.ajax({
            method:'POST',
            data: formData,
            url: 'file',
            processData: false, // Don't process the files
            contentType: false, // Set content type to false as jQuery will tell the server its a query string request
            error: function(e) {
                console.error("Failed to submit file");
            },
            success: function(message) {
                alert(message);
            }
        });
        return false;
    })
}

function adjustButton(type) {
    if(type === "Seller"){
        $('#add-money').attr('style', 'display: none');
        $('#uploadForm').show();
        if(isButtonAdjust === false){
            setFormToGetFile();
            isButtonAdjust = true;
        }
    } else {
        $('#uploadForm').attr('style', 'display: none')
        $('#add-money').show()
    }
}

// receive element Transaction =
// {
//     'action = LOAD, PAY, RECEIVE'
//     'money = 34.34'
//     'moneyBeforeAction = 43.43;
//     'date = 10.10.1201';
// }
function appendTransaction(index, value) {
    var current = (value.moneyBeforeAction + value.money);

    $('.transaction-area').append('<tr>'
        + '<th scope="row">' + ( index + 1) + '</th>'
        + '<td>' + value.action.toString() + '</td>'
        + '<td>' + value.date.toString() + '</td>'
        + '<td>' + value.money + '</td>'
        + '<td>' + value.moneyBeforeAction + '</td>'
        + '<td>' + current.toFixed(2) + '</td>'
        + '</tr>');
}

// date picker - block past days
function blockPastDates() {
    var dtToday = new Date();

    var month = dtToday.getMonth() + 1;
    var day = dtToday.getDate();
    var year = dtToday.getFullYear();
    if(month < 10)
        month = '0' + month.toString();
    if(day < 10)
        day = '0' + day.toString();

    var maxDate = year + '-' + month + '-' + day;
    $('#date').attr('min', maxDate);
}

function chargeCreditRequest() {
    $('#load-money-button').click(function(){
        var date = $('#date').val();
        var credit = $('#credit').val();

        if(credit !== '' && date !== ''){
            $.ajax({
                method: 'POST',
                data: { date: date, credit: credit },
                url: 'wallet',
                error: function(e) {
                    console.error("Failed to submit charge action");
                },
                success: function(message) {
                    $('.modal').modal('hide');
                    alert(message);
                }
            });
            return false;
        }
    });
}


// gets array of zones and version -

// ZoneDataAndVersion {
//      'version = 1'
//      'zones = { --->>>
                    // zone {
                    //     'name = zone name'
                    //     'owner = owner name'
                    //     'products = number of products'
                    //     'stores = number of stores'
                    //     'orders = number of orders'
                    //     'averageOrderPrice = 131231.312321'
// }
function appendZoneToTable(index, zone) {
    $('.custom-select').append('<option value=' + ( index + 1) + '>' + zone.name + '</option>');

    $('.zones-area').append('<tr>\n'
        +'<th scope="row">' + ( index + 1) + '</th>'
        +'<td>' + zone.name + '</td>'
        +'<td>' + zone.owner + '</td>'
        +'<td>' + zone.products + '</td>'
        +'<td>' + zone.stores + '</td>'
        +'<td>' + zone.orders + '</td>'
        +'<td>' + zone.averageOrderPrice.toFixed(2) + '</td>'
        +'</tr>');
}

function appendToZonesArea(zones) {
    if($('.custom-select option').length !== (zones.length + 1)){
        $('.custom-select').empty();
        $('.custom-select').append('<option selected>Choose zone..</option>')
        $('.zones-area').empty();
        $.each(zones || [], appendZoneToTable);
    }
}

function setZones() {
    // refresh chat
    $.ajax({
        type: 'POST',
        url: '../zone/zone',
        dataType: 'json',
        success: function(result) {
            appendToZonesArea(result.zones);
        },
        error: function() {
            console.error("Failed to submit zones");
        }});
}

function setZoneButton() {
    $('#zone-button').click(function () {
        var select = $("#inputGroupSelect option:selected").text();

        if (select !== 'Choose zone..') {
            sessionStorage.setItem('zone', select);

            if($('#uploadForm').is(":visible")){
                sessionStorage.setItem('buyerSeller', 'seller');
            } else {
                sessionStorage.setItem('buyerSeller', 'buyer');
            }
            window.location.assign('../zone/zone.html');
        }
    });
}


// updates functions
function addFeedbackUpdate(index, feedback) {
    var currentText = $('.update-text').text();
    var feedbackDetails = "NEW FEEDBACK !!! customer : " + feedback.customerName + ",\xa0\xa0rating : " + feedback.rating + "/5" +
        "\xa0\xa0,comment : " + feedback.comment + "\xa0\xa0||\xa0\xa0";
    $('.update-text').text(currentText + feedbackDetails);
}

function addStoreUpdate(index, store) {
    var currentText = $('.update-text').text();
    var storeDetails = "NEW STORE !!! store name : " + store.storeName + ",\xa0owner : " + store.ownerName + ",\xa0zone : "
        + store.zone + ",\xa0products : " + store.storeNumberOfProducts + "/" + store.zoneNumberOfProducts + "\xa0\xa0||\xa0\xa0";
    $('.update-text').text(currentText + storeDetails);
}

function addOrderUpdate(index, order) {
    var currentText = $('.update-text').text();
    var orderDetails = "NEW ORDER !!! Order no' : " + order.orderSerialNumber + "." + order.subOrderSerialNumber
        + ",\xa0Customer : " + order.customerName + ",\xa0Products : " + order.numberOfProductsTypes.toFixed(2)
        + ",\xa0Products price : " + order.productsPrice
        + ",\xa0Delivery price :" + order.deliveryPrice.toFixed(2) + "\xa0\xa0t||\xa0\xa0";
    $('.update-text').text(currentText + orderDetails);
}

function printAndGetUpdates(updatesAndVersions) {
    if(updatesAndVersions.feedbacks.length == 0 && updatesAndVersions.stores.length == 0 && updatesAndVersions.orders.length == 0){
        return;
    } else {
        $.each(updatesAndVersions.feedbacks || [], addFeedbackUpdate);
        $.each(updatesAndVersions.stores || [], addStoreUpdate);
        $.each(updatesAndVersions.orders || [], addOrderUpdate);
    }
}

function getUpdates() {
    $.ajax({
        method: 'GET',
        url: '../zone/update',
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

$(function() {
    // save user element and update users list
    saveUserElement();
    setUsers();
    setZones();
    setChat();

    // set zone button
    setZoneButton();

    // update user details
    appendUserDetails();

    // update zones
    setInterval(setZones, zonesTime);

    // update users dynamic
    setInterval(setUsers, usersTime);

    // block past dates
    blockPastDates();

    // charge credit ajax request
    chargeCreditRequest();

    // update chat
    setInterval(setChat, chatTime);

});

