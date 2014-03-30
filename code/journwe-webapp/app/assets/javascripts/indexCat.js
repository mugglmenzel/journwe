require([
    "common/utils",
    "modules/index",
    "categoryData",
    "modules/main"
], function (utils, index, cat) {


    index.initializeCategories(null, cat.id);

    index.initializeInspirations(null, cat.id);

    index.initializePublicAdventures();

    if (cat.image != null && cat.image != "") {
        $('#background').css('background-image', 'url("http://www.journwe.com/thumbnail?w=1600&u=' + cat.image + '")').addClass('blur');
    } else utils.loadGenericBgImage();

});