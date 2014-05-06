require([
    'common/utils',
    'modules/adventure',
    'modules/main'
], function (utils, adventure) {
    console.log("initializing adventure invited page...");

    //adventure.initBackground();
    adventure.initPrimeImage();
    adventure.loadPublicAdventurers();
    adventure.updateFavoritePlace();
    adventure.updateFavoriteTime();
    adventure.initializePlaces();
    adventure.initializeTime();


    utils.on({
        'click .btn-participate': function () {
            var el = $(this),
                html = el.html();

            utils.setReplaceSpinning(el);

            return true;
        }


    });


});