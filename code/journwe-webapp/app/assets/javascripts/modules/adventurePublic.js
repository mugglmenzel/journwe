require([
    'common/utils',
    'modules/common/adventure',
    'journwe'
], function (utils, adventure, journwe) {

    journwe(function () {
        adventure.initBackground();
        adventure.initPrimeImage();
        adventure.loadAllAdventurers();


        utils.on({
            'click .btn-participate': function () {
                var el = $(this),
                    html = el.html();

                utils.setReplaceSpinning(el);

                return true;
            }


        });
    });

});