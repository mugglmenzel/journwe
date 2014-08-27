/**
 * Defines a list on utils which can
 * be used over the whole application
 *
 */

define(['routes', 'config'], function (routes) {

    var spinnerTempStore = [];

    // Formats an int to a string with a leading zero
    var two = function (r) {
            return (r < 10 ? "0" : "") + r;
        },
        isMobile = new RegExp("Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini", "i").test(navigator.userAgent);

    console.log("utils loaded.");

    return {
        adventurerCSSLabel: {
            'APPLICANT': 'default',
            'INVITEE': 'default',
            'NOTGOING': 'danger',
            'UNDECIDED': 'warning',
            'GOING': 'info',
            'BOOKED': 'success'
        },
        /**
         * Returns the last string of the path
         * @return String
         */
        id: function () {
            return location.pathname.split("/").slice(-1) + "";
        },

        /**
         * Returns TRUE if the client is a mobile browser
         * @return Boolean
         */
        isMobile: function () {
            return isMobile;
        },

        /**
         * Helper function to specify multiple event listeners
         * @param Object
         */
        on: function (obj) {

            for (var i in obj) {

                if (typeof i != "string" || typeof obj[i] != "function") {
                    continue;
                }

                var evt = i.split(" ").slice(0, 1)[0],
                    cls = i.split(" ").slice(1).join(" ");

                $(document).on(evt, cls, obj[i]);
            }
        },

        scrollTo: function (target, speed) {
            $('html, body').animate({
                scrollTop : $(target).offset().top
            }, speed?speed:'slow');
        },

        /**
         * Formats a date and return either the date or the time (if the date is today)
         * @param Date|Int
         */
        formatTime: function (time) {
            time = time instanceof Date ? time : new Date(parseInt(time, 10));

            var now = new Date(),
                today = now.getDate() == time.getDate()
                    && now.getMonth() == time.getMonth()
                    && now.getFullYear() == time.getFullYear();

            if (today) {
                return time.getHours()
                    + ":"
                    + two(time.getMinutes());
            } else {
                return this.formatDateShort(time);
            }
        },

        /**
         * Formats a date in the format dd.MM.YYYY
         * @param Date|Int
         */
        formatDate: function (time) {
            time = time instanceof Date ? time : new Date(parseInt(time, 10));

            // Todo Use better formating
            return two(time.getDate())
                + "."
                + two(time.getMonth() + 1)
                + "."
                + time.getFullYear();
        },

        /**
         * Formats a date in the format dd.MM.YYYY
         * @param Date|Int
         */
        formatDateLong: function (time) {
            time = time instanceof Date ? time : new Date(parseInt(time, 10));

            // Todo Use better formating
            return two(time.getDate())
                + "."
                + two(time.getMonth() + 1)
                + "."
                + time.getFullYear()
                + " "
                + two(time.getHours())
                + ":"
                + two(time.getMinutes())
                + ":"
                + two(time.getSeconds());
        },

        /**
         * Formats a date in the format dd.MM.YY
         * @param Date|Int
         */
        formatDateShort: function (time) {
            time = time instanceof Date ? time : new Date(parseInt(time, 10));

            // Todo Use better formating
            return two(time.getDate())
                + "."
                + two(time.getMonth() + 1)
                + "."
                + ("" + time.getYear()).substr(1, 2);
        },

        formatDateSDF: function (time) {
            time = time instanceof Date ? time : new Date(parseInt(time, 10));
            return time.getFullYear()
                + "-"
                + two(time.getMonth() + 1)
                + "-"
                + two(time.getDate());
        },

        /**
         * Replaces all the link from the text into anchors
         * @param String text
         * @return String
         */
        replaceURLWithHTMLLinks: function (text) {
            var exp = /(\b(https?|ftp|file):\/\/[-A-Z0-9+&@#\/%?=~_|!:,.;]*[-A-Z0-9+&@#\/%=~_|])/ig;
            return text.replace(exp, "<a href='$1'>$1</a>");
        },

        colorOfUser: function (userStr) {
            return ('#' + ('000000' + (parseInt(parseInt(userStr, 36).toExponential().slice(2, -5), 10) & 0xFFFFFF).toString(16).toUpperCase()).slice(-6));
        },

        setSpinning: function (el) {
            $(el).addClass('fa fa-spin');
        },
        resetSpinning: function (el) {
            $(el).removeClass('fa-spin');
        },
        setStash: function (el) {
            $(el).addClass('stash');
        },
        resetStash: function (el) {
            $(el).removeClass('stash');
        },
        setReplaceSpinning: function (el) {
            var id = new Date().getUTCMilliseconds() + '-' + Math.round(Math.random() * 1000);
            spinnerTempStore[id] = el.html();
            el.data('spinner-id', id);
            el.css({width: el.outerWidth() + 'px', height: el.outerHeight() + 'px'})
                .html('<i class="fa fa-spinner fa-spin"></i>');
        },
        resetReplaceSpinning: function (el) {
            var id = el.data('spinner-id');
            el.css({width: '', height: ''})
                .html(spinnerTempStore[id]);
            delete spinnerTempStore[id];
            el.data('spinner-id', '');
        },

        loadGenericBgImage: function () {
            var bgr = $('#background');

            // Set background image
            var images = ['cinque_terre.jpg', 'tokyo_tower.jpg', 'camper_beach.jpg', 'lake_sailing.jpg']; //  'tokyo_drums.jpg', 'arirang.jpg','kilimanjaro.jpg',
            if (!bgr.css('background-image') || bgr.css('background-image') == "none") {
                bgr.css('background-image', 'url("http://www.journwe.com/thumbnail?w=1600&u=http%3A%2F%2Fwww.journwe.com%2Fassets%2Fimg%2Fbg%2F' + images[Math.floor(Math.random() * images.length)] + '")');
            }
        }


};
});