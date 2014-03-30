requirejs.config({
    paths: {
        'jquery': 'common/jquery',
        'bootstrap': 'common/bootstrap',
        'bootstrap-switch': 'common/bootstrap-switch',
        'bootstrap-editable': 'common/bootstrap-editable',
        'bootstrap-datepicker': 'common/bootstrap-datepicker',
        'typeahead': 'common/typeahead',
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
    optimize: "uglify2",
    uglify2: {
        output: {
            comments: false
        }
    },
    preserveLicenseComments: false

});