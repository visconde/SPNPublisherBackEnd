/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spn.responses;

import java.io.Serializable;
import java.util.UUID;

/**
 *
 * @author pedrocosta
 */
public abstract  class AbstractMessage implements Serializable{

    private UUID id;
    private Long timestamp;

public AbstractMessage(){

    id = UUID.randomUUID();
   timestamp = System.currentTimeMillis();

}

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }


    public abstract Object getPayload();
}

