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
import com.spn.responses.AbstractMessage;
import com.spn.responses.Message;
import com.spn.responses.Status;
import com.spn.responses.TemperatureMessage;
import com.spn.utils.Configs;
import java.io.BufferedReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;

import pt.ul.fc.di.navigators.trone.data.Event;
import pt.ul.fc.di.navigators.trone.mgt.MessageBrokerClient;

@WebServlet("/SystemStatus")
public class SystemStatus extends HttpServlet {

    private Configs conf;
    SPNSender spnSender;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);

    }

    private Status getStatus(){
        
        Status status = new Status();
       

        try {
            conf = new Configs(Configs.CFT_CONFIGS);
            spnSender = new SPNSender(conf);
            status.setSystemStatus(0);
        } catch (IOException e) {
            System.out.println("Problems reading configs");;
            status.setSystemStatus(-1);

        } catch (Exception ex) {
            System.out.println("Problems initing SPNSender");;
            status.setSystemStatus(-2);
        }

        try {
            boolean canSend = (spnSender.connectAndRegister());
            if (!canSend) {
                status.setSpnStatus(-1);
            }
            else{
                 status.setSpnStatus(0);
            }
        } catch (Exception ex) {
            System.out.println("Problems initing SPNSender");;
            status.setSystemStatus(-2);
        }
return status;
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

      Status status = this.getStatus();
      
      Message<Status> msgStatus = new Message<>();
      msgStatus.setPayload(status);
      
      writeJsonResponse(response, msgStatus);

    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

     
     
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
