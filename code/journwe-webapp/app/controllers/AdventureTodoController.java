package controllers;

import com.feth.play.module.pa.PlayAuthenticate;
import com.journwe.productadvertising.webservice.client.*;
import com.journwe.productadvertising.webservice.client.enums.EMarketplaceDomain;
import com.typesafe.config.ConfigFactory;
import models.Inspiration;
import models.adventure.Adventure;
import models.adventure.Adventurer;
import models.adventure.checklist.EStatus;
import models.auth.SecuredBetaUser;
import models.dao.*;
import models.user.User;
import org.apache.commons.codec.binary.Base64;
import play.Logger;
import play.data.DynamicForm;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import views.html.adventure.getTodos;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import javax.xml.namespace.QName;
import javax.xml.soap.*;
import javax.xml.ws.Holder;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.HandlerResolver;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.PortInfo;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.util.*;

import static play.data.Form.form;

/**
 * Created with IntelliJ IDEA.
 * User: mugglmenzel
 * Date: 27.06.13
 * Time: 12:16
 * To change this template use File | Settings | File Templates.
 */
public class AdventureTodoController extends Controller {

    @Security.Authenticated(SecuredBetaUser.class)
    public static Result getTodos(String advId) {
        User usr = new UserDAO().findByAuthUserIdentity(PlayAuthenticate.getUser(Http.Context.current()));
        Adventure adv = new AdventureDAO().get(advId);
        Adventurer advr = new AdventurerDAO().get(advId, usr.getId());
        Inspiration ins = Inspiration.fromId(adv.getInspirationId());
        ins = ins != null ? new InspirationDAO().get(ins.getCategoryId(), ins.getInspirationId()) : ins;


        for (models.adventure.checklist.Todo todo : new TodoDAO().all(usr.getId(), advId)) {
            ItemSearchRequest shared = new ItemSearchRequest();
            shared.setKeywords(todo.getTitle());
            shared.setSearchIndex("All");
            List<ItemSearchRequest> itemSearches = new ArrayList<ItemSearchRequest>();
            itemSearches.add(shared);


            Holder<OperationRequest> opOut = new Holder<OperationRequest>();

            Holder<List<Items>> itemsOut = new Holder<List<Items>>();
            AWSECommerceService service = new AWSECommerceService();
            service.setHandlerResolver(new HandlerResolver() {
                @Override
                public List<Handler> getHandlerChain(PortInfo portInfo) {
                    List<Handler> result = new ArrayList<Handler>();
                    result.add(new SOAPHandler<SOAPMessageContext>() {
                        @Override
                        public Set<QName> getHeaders() {
                            return new HashSet<QName>();
                        }

                        @Override
                        public boolean handleMessage(SOAPMessageContext soapMessageContext) {
                            try {
                                final Boolean outbound = (Boolean) soapMessageContext.get("javax.xml.ws.handler.message.outbound");
                                if (outbound != null && outbound) {
                                    SOAPMessage msg = soapMessageContext.getMessage();
                                    SOAPEnvelope envelope = msg
                                            .getSOAPPart().getEnvelope();
                                    SOAPFactory factory = SOAPFactory.newInstance();
                                    String prefix = "aws";
                                    String uri = "http://security.amazonaws.com/doc/2007-01-01/";
                                    SOAPElement awsAccessKeyIdElem =
                                            factory.createElement("AWSAccessKeyId", prefix, uri);
                                    SOAPElement timestampElem =
                                            factory.createElement("Timestamp", prefix, uri);
                                    SOAPElement signatureElem =
                                            factory.createElement("Signature", prefix, uri);

                                    awsAccessKeyIdElem.addTextNode(ConfigFactory.load().getString("aws.accessKey"));

                                    Calendar cal = new GregorianCalendar();
                                    timestampElem.addTextNode(DatatypeConverter.printDateTime(cal));

                                    try {
                                        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
                                        SecretKeySpec secret_key = new SecretKeySpec(ConfigFactory
                                                .load().getString("aws.secretKey").getBytes(), "HmacSHA256");
                                        sha256_HMAC.init(secret_key);
                                        String data = "ItemSearch" + DatatypeConverter.printDateTime(cal);

                                        signatureElem.addTextNode(Base64.encodeBase64String(sha256_HMAC.doFinal(data.getBytes())));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }


                                    SOAPHeader header = envelope.addHeader();
                                    header.addChildElement(awsAccessKeyIdElem);
                                    header.addChildElement(timestampElem);
                                    header.addChildElement(signatureElem);

                                    msg.saveChanges();
                                }
                            } catch (final Exception e) {
                                throw new RuntimeException(e);
                            }
                            return true;
                        }

                        @Override
                        public boolean handleFault(SOAPMessageContext soapMessageContext) {
                            return false;
                        }

                        @Override
                        public void close(MessageContext messageContext) {

                        }
                    });
                    return result;
                }
            });
            service.getAWSECommerceServicePort().itemSearch(EMarketplaceDomain.US.getMarketPlaceDomain(), ConfigFactory.load().getString("aws.accessKey"), "jourwheradvem-21", "Single", "False", shared, itemSearches, opOut, itemsOut);
            if (opOut.value.getErrors() != null)
                for (Errors.Error e : opOut.value.getErrors().getError())
                    Logger.debug("opOut error " + e.getMessage());
            for (Items i : itemsOut.value)
                for (Item j : i.getItem())
                    Logger.debug(j.getDetailPageURL());
        }

        return ok(getTodos.render(adv, ins, advr, AdventureTimeController.timeForm, AdventureFileController.fileForm));
    }

    @Security.Authenticated(SecuredBetaUser.class)
    public static Result addTodo(String id) {

        DynamicForm requestData = form().bindFromRequest();

        User usr = new UserDAO().findByAuthUserIdentity(PlayAuthenticate.getUser(Http.Context.current()));

        models.adventure.checklist.Todo todo = new models.adventure.checklist.Todo();
        todo.setAdventureId(id);
        todo.setUserId(usr.getId());
        todo.setTitle(requestData.get("title"));

        new TodoDAO().save(todo);

        return ok(Json.toJson(todo));
    }

    @Security.Authenticated(SecuredBetaUser.class)
    public static Result setTodo(String id, String tid) {

        DynamicForm requestData = form().bindFromRequest();

        models.adventure.checklist.Todo todo = new TodoDAO().get(tid, id);

        String status = requestData.get("status").toUpperCase();
        todo.setStatus(EStatus.valueOf(status));

        new TodoDAO().save(todo);

        return ok(Json.toJson(todo)); //TODO: Error handling
    }

    @Security.Authenticated(SecuredBetaUser.class)
    public static Result deleteTodo(String id, String tid) {

        new TodoDAO().delete(tid, id);

        return ok(); //TODO: Error handling
    }

}
