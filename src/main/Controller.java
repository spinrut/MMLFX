package main;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import mml.MMLPlayer;

public class Controller {
    @FXML
    private TextArea track1;
    @FXML
    private TextArea track2;
    @FXML
    private TextArea track3;
    @FXML
    private TextArea track4;
    @FXML
    private TextArea track5;
    @FXML
    private TextArea track6;
    @FXML
    private TextArea track7;
    @FXML
    private TextArea track8;

    private MMLPlayer mmlPlayer;

    public void handleSubmitButtonAction() {
        if(mmlPlayer == null) throw new IllegalStateException("mmlPlayer is not initialized");
        mmlPlayer.play(new String[] { track1.getText(), track2.getText(), track3.getText(), track4.getText(), track5.getText(), track6.getText(), track7.getText(), track8.getText() });
    }

    public void setMMLPlayer(MMLPlayer mmlPlayer) {
        this.mmlPlayer = mmlPlayer;
    }
}
