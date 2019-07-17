var scooby = {
    landing: function () {
        $("#buttonTutorial").on("click", this.instruct);
        $("#buttonstart").on("click", this.start);
    },
    instruct: function () {
        console.log("test");
    },
    start: function () {
        console.log('test');
        $(".landing").css("display", "none");
        $(".contain").css("display", "flex");
    }
}

scooby.landing();


