require([
    "common/utils",
    "modules/common/index",
    "journwe",
    "modules/yingyangyong"
], function (utils, index, journwe, yingyangyong) {

    utils.loadGenericBgImage();
    index.initializeCategories();
    index.initializePublicAdventures();

});