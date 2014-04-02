requirejs.config({
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
    waitSeconds: 15
});