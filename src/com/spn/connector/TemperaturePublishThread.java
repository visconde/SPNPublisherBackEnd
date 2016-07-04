/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spn.connector;

import com.google.gson.Gson;
import com.spn.demo.Temperature;
import static com.spn.demo.Temperature.currentTemperature;
import com.spn.responses.TemperatureMessage;
import com.spn.utils.Configs;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author pedrocosta
 */
public class TemperaturePublishThread implements Runnable {
    
    private Configs configs;
    
 
    public TemperaturePublishThread(Configs configs){
        this.configs = configs;
        this.configs.dumProperties();
    }

    @Override
    public void run() {
        System.out.println("Initing thread...");

        try {
         //   Configs conf = new Configs();
            SPNSender spnSender = new SPNSender(configs);
            Boolean canSend = (spnSender.connectAndRegister());

            while (canSend) {
               
                int currentTemperature = Temperature.currentTemperature;
                TemperatureMessage msg = new TemperatureMessage();
                msg.setTemperature(currentTemperature);
                
                spnSender.sendMessage(msg);

                Thread.sleep(1000);
            }
        } catch (Exception ex) {
            Logger.getLogger(TemperaturePublishThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
