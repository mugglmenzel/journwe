package controllers;

import play.libs.Comet;
import play.mvc.Controller;
import play.mvc.Result;

public class ProgressController extends Controller {

    public static Result getProgress() {
		Comet comet = new Comet("parent.cometMessage") {
			public void onConnected() {
				String status = "0";
				int i = 0;
				while (i < 100
						&& !(status.equals("100") || status
								.equals("100.00"))) {
					status = ""+i*10;
					sendMessage(status);
					try {
						Thread.sleep(2000);
						i++;
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				close();
			}
		};
		return ok(comet);
    }

}
