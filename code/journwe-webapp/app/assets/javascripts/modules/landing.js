require([
    "common/utils",
    "journwe"
], function (utils, journwe) {

    journwe(function () {
        utils.loadGenericBgImage();

        utils.on({
            "click .btn-start-tour": function (e) {
                e.preventDefault();

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

});
