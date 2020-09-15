package sample.Models;

import java.io.Serializable;
import java.util.Timer;

public class MessageSocket implements Serializable {

    private String tipo;
    private Object conteudo;

    public Object getConteudo() {
        return conteudo;
    }

    public void setConteudo(Object conteudo) {
        this.conteudo = conteudo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
