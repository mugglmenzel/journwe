require([
    "common/utils",
//    "modules/tour",
    "modules/main"
], function (utils) {


    utils.loadGenericBgImage();

    //tour.initialize();

    utils.on({
        "click .btn-start": function (e) {
            e.preventDefault();

            $('.landing-section-start').slideDown();


            var hash = this.hash;


            if (hash)
                $('html, body').animate({
                    scrollTop: $(this.hash).offset().top
                }, 300, function () {
                    window.location.hash = hash;
                });

        }
    });


});
