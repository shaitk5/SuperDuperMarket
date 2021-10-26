var refreshRate = 1000;
var user_name_available = "Username available"

function ajaxUsernameContent() {
    $.ajax({
        type: "POST",
        url: "Login",
        data: "username=" + $("#username").val(),
        success: function(result) {
            if(result === user_name_available){
                $("#errorMassage").css({"color":"green"});
            } else {
                $("#errorMassage").css({"color":"red"});
            }
            $("#errorMassage").text(result);},
        error: function() {
            console.error("Failed to submit");
        }});
}

$(function() {
    //refresh username check
    setInterval(ajaxUsernameContent, refreshRate);
});