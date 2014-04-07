require([
    'common/utils',
    'modules/adventure',
    'modules/main'
], function (utils, adventure) {
    console.log("initializing adventure public page...");

    adventure.initBackground();
    adventure.initPrimeImage();
    adventure.loadPublicAdventurers();


    utils.on({
        'click .btn-participate': function () {
            var el = $(this),
                html = el.html();

            utils.setReplaceSpinning(el);

            return true;
        }


    });


});