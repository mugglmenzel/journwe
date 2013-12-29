require([
	"main", 
	"utils",
	"routes"
], function(main, utils, routes){

	console.log(routes)

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

			var btn = $(this);
			btn.find("i").attr("class", "icon-spin icon-journwe");

			btn.datepicker("hide");

			routes.controllers.AdventureController.updatePlaceVoteDeadline($(this).data("id")).ajax({
				data: {voteDeadline: e.date.getTime()},
				success: function(data) {
					var d = e.date,
						z = function(r){ return r < 10 ? "0"+r:r; },
						f = (z(d.getDate())+"-"+z(d.getMonth()+1)+"-"+(d.getYear()+1900));

					btn.html('<i class="icon-calendar"></i> '+f);
				}
			});
		}
	});


	$('.date').datepicker();



	// $('body').scrollspy({offset: 100}).on('activate', function(evt){
	// 	$('.row.active').removeClass('active');
	// 	$($(evt.target).find('a').attr('href')).parent('.row').addClass('active');
	// });


});