package com.spn.demo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.xml.bind.DatatypeConverter;

import com.cryptutils.engines.aes256.AES256Engine;
import com.cryptutils.engines.aes256.AESKeyEngine;
import com.cryptutils.interfaces.CryptoEngine;
import com.cryptutils.utils.CompressorUtils;
import com.google.gson.Gson;
import com.spn.connector.SPNSender;
import com.spn.connector.TemperaturePublishThread;
import com.spn.responses.AbstractMessage;
import com.spn.responses.TemperatureMessage;
import com.spn.utils.Configs;
import java.io.BufferedReader;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;

import pt.ul.fc.di.navigators.trone.data.Event;
import pt.ul.fc.di.navigators.trone.mgt.MessageBrokerClient;

@WebServlet("/Temperature")
public class Temperature extends HttpServlet {

    
   public static Integer currentTemperature;
    public static ConcurrentLinkedQueue<Integer> temperatureValues;
    public Thread threadBft;
    public Thread threadCft;
   // SPNSender spnSender;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        
       this.temperatureValues = new ConcurrentLinkedQueue<Integer>();
       
        try {
           currentTemperature = 25;
//            conf = new Configs();
//            spnSender = new SPNSender(conf);
//            
            Configs bftConfigs = new Configs(Configs.BFT_CONFIGS);
        threadBft = new Thread(new TemperaturePublishThread(bftConfigs));
       
       
             Configs cftConfigs = new Configs(Configs.CFT_CONFIGS);
        threadCft = new Thread(new TemperaturePublishThread(cftConfigs));
       
        threadCft.start();
        threadBft.start();

//        } catch (IOException e) {
//            System.out.println("Problems reading configs");;
     } catch (Exception ex) {
            System.out.println("Problems initing Thread");;
        }

//        this.canSend = (spnSender.connectAndRegister());

//        if (this.canSend) {
//            System.out.println("We are ready to publish to SPN");
//            this.sequenceNumber = 0;
//        } else {
//            System.out.println("Something went wrong. Disconnect from SPN...");
//            spnSender.disconnectFromSPN();
//        }

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        TemperatureMessage tempResponse = new TemperatureMessage();
        tempResponse.setTemperature(currentTemperature);
        writeJsonResponse(response, tempResponse);

    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String requestBody = getRequestBody(request);
        TemperatureMessage msg = new Gson().fromJson(requestBody, TemperatureMessage.class);
        currentTemperature = msg.getTemperature();
        temperatureValues.add(currentTemperature);
//     if (this.canSend) {
//            spnSender.sendMessage(msg);
//        } else {
//            System.out.println("Cant publish data to SPN :(");
//        }

        writeJsonResponse(response, msg);
    }

    private String getRequestBody(HttpServletRequest request) throws IOException {
        BufferedReader r = request.getReader();
        String line = null;
        StringBuilder sb = new StringBuilder();
        while ((line = r.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    private void writeJsonResponse(HttpServletResponse response, AbstractMessage payload) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(new Gson().toJson(payload));
    }

}