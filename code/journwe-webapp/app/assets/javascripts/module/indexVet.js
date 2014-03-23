require([
    "common/utils",
    "routes",
    "messages",
    "common/gmaps",
    "common/index",
    "main",
    "yingyangyong"
], function (utils, routes, messages, gmaps, index, main, yingyangyong) {

    utils.loadGenericBgImage();
    index.initializeMyAdventures();
    index.initializeCategories();
    //index.initializePublicAdventures();


    utils.on({
        'click .btn-yingyangyong-start': function () {
            $('#yingyangyong-container').slideDown(function () {
                yingyangyong.resetBounds();
            });
            $('.btn-yingyangyong-start').prop('disabled', true);
            $(this).parents(".jrn-container").removeClass("jrn-no-top-round");
        },
        'click .btn-yingyangyong-close': function () {
            $('#yingyangyong-container')
                .slideUp(500)
                .next().next().addClass("jrn-no-top-round");
            $('.btn-yingyangyong-start')
                .prop('disabled', false);
        }
    });

});