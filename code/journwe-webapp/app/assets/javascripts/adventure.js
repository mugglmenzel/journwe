require([
    "modules/adventure",
    "modules/main"
], function (adventure) {
        console.log("initializing adventure...");

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