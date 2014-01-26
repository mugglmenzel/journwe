require([
    "common/utils",
    "routes",
    "messages",
    "common/gmaps",
    "common/index",
    "yingyangyong"

], function (utils, routes, messages, gmaps, index, yingyangyong) {


    var initialize = function () {

        index.loadMyAdventures(null, true);

        index.loadCategories(null, true);

        index.loadPublicAdventures(null, true);

    };


    utils.on({
        'click .btn-yingyangyong-start': function () {
            $('#yingyangyong-container').slideDown(function () {
                yingyangyong.resetBounds();
            });
            $('.btn-yingyangyong-start').prop('disabled', true);
        },
        'click .btn-yingyangyong-close': function () {
            $('#yingyangyong-container').slideUp(500);
            $('.btn-yingyangyong-start').prop('disabled', false);
        }
    });


    initialize();

});