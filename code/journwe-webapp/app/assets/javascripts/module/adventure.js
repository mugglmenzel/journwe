require([
    "common/utils",
    "module/common/adventure",
    "main"
], function (utils, adventure, main) {


    //INITIALIZE
    adventure.initScrollspy();
    adventure.initNavigation();
    adventure.initializeMap();


    adventure.initializeIndex();
    adventure.initializePeople();

    adventure.initializePlaces();
    adventure.initializeTime();
    adventure.initializeTimeline();

    adventure.initializeTodos();

    adventure.initializeEmails();
    adventure.initializeFiles();
    adventure.initializeOptions();
});