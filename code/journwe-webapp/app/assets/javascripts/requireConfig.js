requirejs.config({
    paths: {
        'jquery': 'common/jquery',
        'bootstrap': 'common/bootstrap',
        'bootstrap-switch': 'common/bootstrap-switch',
        'bootstrap-editable': 'common/bootstrap-editable',
        'bootstrap-datepicker': 'common/bootstrap-datepicker',
        'typeahead': 'common/typeahead',
        'tmpl': 'common/tmpl'
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
    waitSeconds: 15
});