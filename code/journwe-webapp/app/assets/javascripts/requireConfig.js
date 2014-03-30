requirejs.config({
    paths: {
        'jquery': '//cdnjs.cloudflare.com/ajax/libs/jquery/2.0.3/jquery.min',
        'bootstrap': 'common/bootstrap',
        'bootstrap-switch': '//cdnjs.cloudflare.com/ajax/libs/bootstrap-switch/2.0.0/js/bootstrap-switch.min',
        'bootstrap-editable': '//cdnjs.cloudflare.com/ajax/libs/x-editable/1.5.0/bootstrap3-editable/js/bootstrap-editable.min',
        'bootstrap-datepicker': '//cdnjs.cloudflare.com/ajax/libs/bootstrap-datepicker/1.1.3/js/bootstrap-datepicker.min',
        'typeahead': '//cdnjs.cloudflare.com/ajax/libs/typeahead.js/0.9.3/typeahead.min',
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