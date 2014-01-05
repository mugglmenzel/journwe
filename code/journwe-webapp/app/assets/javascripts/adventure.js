require([
	"main",
	"utils",
	"routes"
], function(main, utils, routes){


	var now = new Date(),
		today = new Date(now.getFullYear(), now.getMonth(), now.getDate(),
				0, 0, 0, 0);


	var deadline = function(btn, date, route){

		// Set spinner/hide calendar
		btn.find("i").attr("class", "icon-spin icon-journwe");
		btn.datepicker("hide");

		// Do ajax call
		route(utils.id()).ajax({
			data: {voteDeadline: date.getTime()},
			success: function(data) {

				// Set response
				var d = date,
					z = function(r){ return r < 10 ? "0"+r:r; },
					f = (z(d.getDate())+"-"+z(d.getMonth()+1)+"-"+(d.getYear()+1900));
				btn.html('<i class="icon-calendar"></i> '+f);
			}
		});

	};

	utils.on({

		// Click on navigation to scroll to it
		'click .nav-adventure a': function(){

			var section = $(this).attr('href');

			$('html, body').animate({
				scrollTop : $(section).offset().top - 100
			}, 'slow');

			return false;
		},


		'changeDate .btn-set-reminder-place': function(e){
			deadline(
				$(this), 
				e.date,
				routes.controllers.AdventureController.updatePlaceVoteDeadline
			);
		},


		'changeDate .btn-set-reminder-time': function(e){
			deadline(
				$(this), 
				e.date,
				routes.controllers.AdventureController.updateTimeVoteDeadline
			);
		},

		'click .btn-favorit': function(){

			var el = $(this),
				tb = el.closest('table'),
				placeID = el.is(".btn-success") ? undefined : el.data('placeid');

			tb.find('td:first-child .btn-success').removeClass('btn-success');
			el.find('i').attr("class", "icon-spin icon-journwe");
			$('icon-favorite-places').removeClass("icon-star").addClass("icon-spin icon-journwe");

			routes.controllers.AdventurePlaceController.setFavoritePlace(utils.id()).ajax({
				data: {favoritePlaceId: placeID},
				success: function(data){
					$('icon-favorite-places').removeClass("icon-spin icon-journwe").addClass("icon-star");
					
					// Set stars
					$(el).find('i').attr("class", "icon-star");
					$('#places-favorite-place-name').html(data.address);
					$(el).addClass('btn-success');
				}
			});
		}
	});



	// Init all date fields
	$('.date').datepicker({
		startDate: today,
		weekStart: 1
	});



	$('body').scrollspy({offset: 100}).on('activate', function(evt){
		$('.row.active').removeClass('active');
		$($(evt.target).find('a').attr('href')).parent('.row').addClass('active');
	});


});