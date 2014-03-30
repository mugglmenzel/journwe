require([
    "common/utils",
    "modules/index",
    "modules/yingyangyong",
    "modules/main"
], function (utils, index, yingyangyong) {

    utils.loadGenericBgImage();
    index.initializeCategories();
    index.initializePublicAdventures();
    yingyangyong.initialize();

});