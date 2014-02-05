require([
    'common/utils',
    'common/adventure'
], function (utils, adventure) {


    adventure.initBackground();
    adventure.loadAllAdventurers();


    utils.on({
        'click .btn-participate': function () {
            var el = $(this),
                html = el.html();

            el.css({width: el.width() + "px"})
                .html('<i class="fa fa-spin icon-journwe"></i>');

            return true;
        }


    });

});