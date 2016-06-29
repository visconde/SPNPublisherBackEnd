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
import com.spn.responses.TemperatureMessage;
import com.spn.utils.Configs;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;

import pt.ul.fc.di.navigators.trone.data.Event;
import pt.ul.fc.di.navigators.trone.mgt.MessageBrokerClient;

@WebServlet("/PublishTemperature")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2MB
        maxFileSize = 1024 * 1024 * 10, // 10MB
        maxRequestSize = 1024 * 1024 * 10)   // 10MB
public class PublishTemperature extends HttpServlet {

    private Configs conf;
    private MessageBrokerClient mbc;
    private boolean canSend;
    private int sequenceNumber;
    private AESKeyEngine ake;
    private CryptoEngine aes256_for_enc;
    
    SPNSender spnSender;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        try {
             
            conf = new Configs();
            spnSender = new SPNSender(conf);
        } catch (IOException e) {
            System.out.println("Problems reading configs");;
                 } catch (Exception ex) {
           System.out.println("Problems initing SPNSender");;
        }

        this.canSend = (spnSender.connectAndRegister());

        if (this.canSend) {
            System.out.println("We are ready to upload to SPN");
            this.sequenceNumber = 0;
        } else {
            System.out.println("Ups something went wrong. Going to disconnect form SPN.");
           spnSender.disconnectFromSPN();
        }

    }



    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

       handleRequest(request,response);
    }
    
private void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
     request.setCharacterEncoding("UTF-8");
        if (this.canSend) {
            this.sendToSPN(request);
        } else {
            System.out.println("Cant upload");
        }
}
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
       
       handleRequest(request,response);
    }





 

    private void sendToSPN(HttpServletRequest request) {
        try {

			// ---> buffer is written in the format: [ [NAME SIZE 4byte] [FILE NAME Nbytes] [FILE Nbytes] ]
//                       Integer payload = 1;
            TemperatureMessage msg = new TemperatureMessage();
            Integer temp = 1; //new Integer(request.getParameter("temperature"));
            msg.setTemperature(temp);
            
           spnSender.sendMessage(msg);
    }catch(Exception ex){
            System.out.println("Exception sending msg" + ex.toString());
    }
}
    



}
