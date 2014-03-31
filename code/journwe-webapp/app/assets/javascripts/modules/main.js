define([
    "common/utils",
    "modules/header",
    "journwe"
], function (utils, header, journwe) {
    journwe(function () {
        utils.on({
            'click .btn-scroll-top': function () {
                $('html, body').animate({ scrollTop: 0 }, 600);
                return false;
            }
        });

        header.initialize();
    });
});