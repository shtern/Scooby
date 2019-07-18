
function updateTextInput(amount){
var num_of_scotters = document.getElementById('amount_of_scooters').value;
console.log(num_of_scotters)
load()

}


$(".switchbutton").click(function () {
    var button=$(".switchbutton");
    if (button.text()=="Switch to parking spots"){
    button.text("Switch to scooters location");
    $(".bar").css("display","flex");
    }else{
        button.text("Switch to parking spots");
        $(".bar").css("display","none");

    }
});





$(".switchbutton").click(function () {
    var button=$(".switchbutton");
    if (button.text()=="Switch to parking spots"){
    button.text("Switch to scooters location");
    $(".bar").css("display","flex");
    }else{
        button.text("Switch to parking spots");
        $(".bar").css("display","none");

    }
});