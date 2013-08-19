package models.helpers;

import com.journwe.productadvertising.webservice.client.AWSECommerceService;
import com.typesafe.config.ConfigFactory;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import javax.xml.namespace.QName;
import javax.xml.soap.*;
import javax.xml.ws.Service;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.HandlerResolver;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.PortInfo;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: mugglmenzel
 * Date: 19.08.13
 * Time: 09:57
 * To change this template use File | Settings | File Templates.
 */
public class AWSProductAdvertisingAPIHelper {

    public static AWSECommerceService getService() {
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

                                awsAccessKeyIdElem.addTextNode(ConfigFactory.load().getString("aws.productadvertising.accessKey"));

                                Calendar cal = new GregorianCalendar();
                                timestampElem.addTextNode(DatatypeConverter.printDateTime(cal));

                                try {
                                    Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
                                    SecretKeySpec secret_key = new SecretKeySpec(ConfigFactory
                                            .load().getString("aws.productadvertising.secretKey").getBytes(), "HmacSHA256");
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
        return service;
    }

}
