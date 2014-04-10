require([
    "common/utils",
    "modules/index",
//    "modules/tour",
    "modules/main"
], function (utils, index) {


    index.initializeCategories();
    // utils.loadGenericBgImage();
    $('.background-container').hide();

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
