package com.fittrack.model;

public class Treino {

    private int idTreino;
    private String nomeTreino;
    private String diaSemana;
    private int idUsuario;

    public Treino() {
    }

    public Treino(int idTreino, String nomeTreino, String diaSemana, int idUsuario) {
        this.idTreino = idTreino;
        this.nomeTreino = nomeTreino;
        this.diaSemana = diaSemana;
        this.idUsuario = idUsuario;
    }

    public int getIdTreino() {
        return idTreino;
    }

    public void setIdTreino(int idTreino) {
        this.idTreino = idTreino;
    }

    public String getNomeTreino() {
        return nomeTreino;
    }

    public void setNomeTreino(String nomeTreino) {
        this.nomeTreino = nomeTreino;
    }

    public String getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(String diaSemana) {
        this.diaSemana = diaSemana;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }
}
