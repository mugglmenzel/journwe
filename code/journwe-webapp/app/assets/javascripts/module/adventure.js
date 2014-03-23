require([
    "common/utils",
    "module/common/adventure",
    "main"
], function (utils, adventure, main) {


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