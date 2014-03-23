require([
    "common/utils",
    "common/index",
    "main",
    "yingyangyong"
], function (utils, index, main, yingyangyong) {

    utils.loadGenericBgImage();
    index.initializeCategories();
    index.initializePublicAdventures();

});