requirejs.config({
    paths: {
        'routes': 'empty:',
        'messages': 'empty:',
        'categoryData': 'empty:',
        'adventureData': 'empty:',
        'adventurerData': 'empty:',
        'inspirationData': 'empty:',
        'userData': 'empty:'
    },
    shim: {
        'common/bootstrap': ['common/jquery'],
        'common/bootstrap-switch': ['common/bootstrap'],
        'common/bootstrap-editable': ['common/bootstrap'],
        'common/bootstrap-datepicker': ['common/bootstrap'],
        'common/typeahead': ['common/jquery'],
        'common/tmpl': {
            exports: 'tmpl'
        }
    },
    waitSeconds: 0,
    keepBuildDir: true,
    optimize: "uglify2",
    uglify2: {
        output: {
            comments: false
        }
    },
    preserveLicenseComments: false

});