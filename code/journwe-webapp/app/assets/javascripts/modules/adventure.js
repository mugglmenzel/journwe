require([
    "modules/common/adventure",
    "journwe"
], function (adventure, journwe) {
    journwe(function () {
        //INITIALIZE
        adventure.initScrollspy();
        adventure.initPrimeImage();
        adventure.initNavigation();


        adventure.initializeIndex();
        adventure.initializePeople();

        adventure.initializePlaces();
        adventure.initializeTime();
        adventure.initializeTodos();

        adventure.initializeTimeline();
        adventure.initializeEmails();
        adventure.initializeFiles();

        adventure.initializeOptions();
    });
});