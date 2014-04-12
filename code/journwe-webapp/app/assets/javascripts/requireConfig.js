requirejs.config({
    shim: {
        'common/bootstrap': ['common/jquery'],
        'common/bootstrap-switch': ['common/bootstrap'],
        'common/bootstrap-editable': ['common/bootstrap'],
        'common/bootstrap-datetimepicker': ['common/bootstrap'],
        'common/typeahead': ['common/jquery'],
        'common/tmpl': {
            exports: 'tmpl'
        },
        'common/moment-with-langs': {
            exports: 'moment'
        }
    },
    waitSeconds: 0
});