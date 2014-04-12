package controller;

import com.feth.play.module.mail.Mailer.Mail.Body;
import play.Logger;
import play.i18n.Lang;
import play.mvc.Http;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class JournweMailer {

    private static final String EMAIL_TEMPLATE_FALLBACK_LANGUAGE = "en";

    /**
    *    templatePath is the path after views.html.* or views.txt.*
     */
    public static Body getMailBody(final String templatePath, final Http.Context ctx, final Object[] params) {

        final Lang lang = Lang.preferred(ctx.request().acceptLanguages());
        final String langCode = lang.code();

        final String html = getEmailTemplate(
                "views.html."+templatePath, langCode, params);
        final String text = getEmailTemplate(
                "views.txt."+templatePath, langCode, params);

        return new Body(text, html);
    }

    public static String getEmailTemplate(final String template,
                                      final String langCode, final Object[] params) {
        Class<?> cls = null;
        String ret = null;
        try {
            cls = Class.forName(template + "_" + langCode);
        } catch (ClassNotFoundException e) {
            Logger.warn("Template: '"
                    + template
                    + "_"
                    + langCode
                    + "' was not found! Trying to use language fallback template instead.");
        }
        if (cls == null) {
            try {
                cls = Class.forName(template + "_"
                        + EMAIL_TEMPLATE_FALLBACK_LANGUAGE);
            } catch (ClassNotFoundException e) {
                Logger.error("Fallback template: '" + template + "_"
                        + EMAIL_TEMPLATE_FALLBACK_LANGUAGE
                        + "' was not found either!");
            }
        }
        if (cls != null) {
            Method htmlRender = null;
            try {
                htmlRender = cls.getMethod("render", String.class);
                ret = htmlRender.invoke(null, params)
                        .toString();

            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

}
