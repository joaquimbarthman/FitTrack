package com.fittrack.model;

public class Objetivo {

    private int idObjetivo;
    private String tipoObjetivo;
    private String descricao;
    private String valorMeta;
    private String dataInicio;
    private String dataFim;
    private String status;
    private int idUsuario;

    public Objetivo() {
    }

    public Objetivo(int idObjetivo, String tipoObjetivo, String descricao, String valorMeta,
                    String dataInicio, String dataFim, String status, int idUsuario) {
        this.idObjetivo = idObjetivo;
        this.tipoObjetivo = tipoObjetivo;
        this.descricao = descricao;
        this.valorMeta = valorMeta;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.status = status;
        this.idUsuario = idUsuario;
    }

    public int getIdObjetivo() {
        return idObjetivo;
    }

    public void setIdObjetivo(int idObjetivo) {
        this.idObjetivo = idObjetivo;
    }

    public String getTipoObjetivo() {
        return tipoObjetivo;
    }

    public void setTipoObjetivo(String tipoObjetivo) {
        this.tipoObjetivo = tipoObjetivo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getValorMeta() {
        return valorMeta;
    }

    public void setValorMeta(String valorMeta) {
        this.valorMeta = valorMeta;
    }

    public String getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(String dataInicio) {
        this.dataInicio = dataInicio;
    }

    public String getDataFim() {
        return dataFim;
    }

    public void setDataFim(String dataFim) {
        this.dataFim = dataFim;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }
}
