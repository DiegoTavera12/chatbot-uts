package com.uts.chatbotuts.infrastructure.adapter.in.web;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@AllArgsConstructor
@Getter
@Setter
public class MensajeChat implements Serializable {

    private String texto;
    private String alineacion;

}
