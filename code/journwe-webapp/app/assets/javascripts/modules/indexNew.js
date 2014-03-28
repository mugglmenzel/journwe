require([
    "common/utils",
    "journwe",
    "modules/common/index",
    "modules/yingyangyong"
], function (utils, journwe) {
    journwe(function () {
        utils.loadGenericBgImage();
    });
});