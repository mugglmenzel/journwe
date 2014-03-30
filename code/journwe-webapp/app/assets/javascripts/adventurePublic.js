require([
    'common/utils',
    'modules/adventure',
    'modules/main'
], function (utils, adventure) {


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