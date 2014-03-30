require([
    "common/utils",
    "modules/index",
    "modules/yingyangyong",
    "modules/main"
], function (utils, index, yingyangyong) {

        utils.loadGenericBgImage();

        yingyangyong.initialize();

});