require([
    "common/utils",
    "common/adventure",
    "main"
], function (utils, adventure, main) {


    //INITIALIZE
    adventure.initBackground();
    adventure.initScrollspy();
    adventure.initNavigation();
    adventure.initializeMap();

    adventure.loadEmails();

    adventure.initializeIndex();
    adventure.initializeOptions();
    adventure.initializePlaces();
    adventure.initializePeople();
    adventure.initializeTime();
    adventure.initializeTodos();
    adventure.initializeFiles();
    adventure.initializeTimeline();

});