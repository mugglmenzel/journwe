requirejs.config({
    paths: {
        'jquery': 'empty:',
        'bootstrap': 'common/bootstrap',
        'bootstrap-switch': 'empty:',
        'bootstrap-editable': 'empty:',
        'bootstrap-datepicker': 'empty:',
        'typeahead': 'empty:',
        'tmpl': 'common/tmpl',
        'routes': 'empty:',
        'messages': 'empty:',
        'categoryData': 'empty:',
        'adventureData': 'empty:',
        'adventurerData': 'empty:',
        'inspirationData': 'empty:',
        'userData': 'empty:'
    },
    shim: {
        'bootstrap': ['jquery'],
        'bootstrap-switch': ['bootstrap'],
        'bootstrap-editable': ['bootstrap'],
        'bootstrap-datepicker': ['bootstrap'],
        'typeahead': ['jquery'],
        'tmpl': {
            exports: 'tmpl'
        }
    },
    waitSeconds: 15,
    keepBuildDir: true,
    optimize: "uglify2"

});