require([
    "common/utils",
    "common/adventure"
], function (utils, adventure) {


    //INITIALIZE
    adventure.initBackground();
    //adventure.initScrollspy();
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

});