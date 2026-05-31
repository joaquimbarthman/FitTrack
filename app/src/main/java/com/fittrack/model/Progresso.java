package com.fittrack.model;

public class Progresso {

    private int idProgresso;
    private int idUsuario;
    private String dataRegistro;
    private double peso;
    private double gordura;
    private double cintura;
    private double quadril;
    private double peito;
    private String notas;

    public Progresso() {
    }

    public Progresso(int idProgresso, int idUsuario, String dataRegistro, double peso,
                     double gordura, double cintura, double quadril, double peito, String notas) {
        this.idProgresso = idProgresso;
        this.idUsuario = idUsuario;
        this.dataRegistro = dataRegistro;
        this.peso = peso;
        this.gordura = gordura;
        this.cintura = cintura;
        this.quadril = quadril;
        this.peito = peito;
        this.notas = notas;
    }

    public int getIdProgresso() {
        return idProgresso;
    }

    public void setIdProgresso(int idProgresso) {
        this.idProgresso = idProgresso;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(String dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    public double getGordura() {
        return gordura;
    }

    public void setGordura(double gordura) {
        this.gordura = gordura;
    }

    public double getCintura() {
        return cintura;
    }

    public void setCintura(double cintura) {
        this.cintura = cintura;
    }

    public double getQuadril() {
        return quadril;
    }

    public void setQuadril(double quadril) {
        this.quadril = quadril;
    }

    public double getPeito() {
        return peito;
    }

    public void setPeito(double peito) {
        this.peito = peito;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }
}
