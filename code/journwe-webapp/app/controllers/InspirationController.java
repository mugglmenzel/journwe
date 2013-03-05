package controllers;

import static play.data.Form.form;

import java.io.File;

import models.Category;
import models.Inspiration;
import play.Logger;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import views.html.inspiration.get;
import views.html.inspiration.manage;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.typesafe.config.ConfigFactory;

import controllers.dao.CategoryDAO;
import controllers.dao.InspirationDAO;

public class InspirationController extends Controller {

	private static final String S3_BUCKET_INSPIRATION_IMAGES = "journwe-inspiration-images";

	private static Form<Inspiration> insForm = form(Inspiration.class);

	public static Result get(String catId, String id) {
		Inspiration ins = new InspirationDAO().get(catId, id);
		Category cat = new CategoryDAO().get(catId);
		return ok(get.render(ins, cat));
	}

	public static Result create() {
		return ok(manage.render(null, insForm,
				new CategoryDAO().allOptionsMap(50),
				new InspirationDAO().all(50)));
	}

	public static Result edit(String catId, String id) {
		Form<Inspiration> editInsForm = insForm.fill(new InspirationDAO().get(
				catId, id));
		return ok(manage.render(null, editInsForm,
				new CategoryDAO().allOptionsMap(50),
				new InspirationDAO().all(50)));
	}

	public static Result save() {

		Form<Inspiration> filledInsForm = insForm.bindFromRequest();
		MultipartFormData body = request().body().asMultipartFormData();
		FilePart image = body.getFile("image");

		if (filledInsForm.hasErrors()) {
			return badRequest(manage.render(
					"Please fill out the form correctly.", filledInsForm,
					new CategoryDAO().allOptionsMap(50),
					new InspirationDAO().all(50)));
		} else {
			Inspiration ins = filledInsForm.get();

			File file = image.getFile();

			try {
				if (!new InspirationDAO().save(ins))
					throw new Exception();

				ins = new InspirationDAO().get(ins.getInspirationCategoryId(),
						ins.getId());

				Logger.info("got image file " + image.getFilename());
				AmazonS3Client s3 = new AmazonS3Client(new BasicAWSCredentials(
						ConfigFactory.load().getString("aws.accessKey"),
						ConfigFactory.load().getString("aws.secretKey")));
				if (image.getFilename() != null
						&& !"".equals(image.getFilename()) && file.length() > 0) {
					s3.putObject(new PutObjectRequest(
							S3_BUCKET_INSPIRATION_IMAGES, ins.getId(), file)
							.withCannedAcl(CannedAccessControlList.PublicRead));
				}
				ins.setImage(s3.getResourceUrl(S3_BUCKET_INSPIRATION_IMAGES,
						ins.getId()));

				if (new InspirationDAO().save(ins)
						&& new InspirationDAO().delete(
								filledInsForm.field(
										"originalInspirationCategoryId")
										.value(), ins.getId()))
					return ok(manage.render("Saved Inspiration with image "
							+ ins.getImage() + ".", insForm,
							new CategoryDAO().allOptionsMap(50),
							new InspirationDAO().all(50)));
				else
					throw new Exception();
			} catch (Exception e) {
				return internalServerError(manage.render(
						"Something went wrong during saving :(", filledInsForm,
						new CategoryDAO().allOptionsMap(50),
						new InspirationDAO().all(50)));
			}
		}
	}

	public static Result delete(String catId, String id) {
		try {
			AmazonS3Client s3 = new AmazonS3Client(new BasicAWSCredentials(
					ConfigFactory.load().getString("aws.accessKey"),
					ConfigFactory.load().getString("aws.secretKey")));
			s3.deleteObject(S3_BUCKET_INSPIRATION_IMAGES, id);
			if (new InspirationDAO().delete(catId, id))
				return ok(manage.render("Inspiration with id " + id
						+ " deleted.", insForm,
						new CategoryDAO().allOptionsMap(50),
						new InspirationDAO().all(50)));
			else
				throw new Exception();
		} catch (Exception e) {
			return internalServerError(manage.render(
					"Something went wrong during deletion :(", insForm,
					new CategoryDAO().allOptionsMap(50),
					new InspirationDAO().all(50)));
		}
	}

}
