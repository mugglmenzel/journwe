package com.journwe.bond;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;

@SuppressWarnings("restriction")
public class HttpRequestParser {


    static Map<String, Object> parsePostParameters(HttpExchange exchange)
        throws IOException {

        if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            @SuppressWarnings("unchecked")
            Map<String, Object> parameters = new HashMap<String,Object>();
            //Map<String, Object> parameters =
            //    (Map<String, Object>)exchange.getAttribute("parameters");
            InputStreamReader isr =
                new InputStreamReader(exchange.getRequestBody(),"utf-8");
            BufferedReader br = new BufferedReader(isr);
            String query = br.readLine();
            parseQuery(query, parameters);
            return parameters;
        }
        return new HashMap<String,Object>();
    }

     @SuppressWarnings("unchecked")
     static void parseQuery(String query, Map<String, Object> parameters)
         throws UnsupportedEncodingException {

         if (query != null) {
             String pairs[] = query.split("[&]");

             for (String pair : pairs) {
                 String param[] = pair.split("[=]");

                 String key = "NO_KEY";
                 String value = "NO_VALUE";
                 if (param.length > 0) {
                     key = URLDecoder.decode(param[0],
                         System.getProperty("file.encoding"));
                 }

                 if (param.length > 1) {
                     value = URLDecoder.decode(param[1],
                         System.getProperty("file.encoding"));
                 }
                 
                 parameters.put(key, value);

//                 if (parameters.containsKey(key)) {
//                     Object obj = parameters.get(key);
//                     if(obj instanceof List<?>) {
//                         List<String> values = (List<String>)obj;
//                         values.add(value);
//                     } else if(obj instanceof String) {
//                         List<String> values = new ArrayList<String>();
//                         values.add((String)obj);
//                         values.add(value);
//                         parameters.put(key, values);
//                     }
//                 } else {
//                     parameters.put(key, value);
//                 }
             }
         }
    }

}
