/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spn.responses;

public class TemperatureMessage extends AbstractMessage  {


    private String temperature;

    public TemperatureMessage(){
        super();

    }

    @Override
    public Object getPayload() {
        return temperature;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }
}

