package actors.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import game.Main;
import screens.Dialogue;

import java.util.Locale;

public class DialogueUI {
    private Stage stage;
    private ScreenViewport viewport;
    private Main game;

    private final Skin skin = new Skin(Gdx.files.internal("skin/glassy-ui.json"));

    private Table root;

    private float heightPercent;

    private ScrollPane dialogueOut;
    private Table outerDialogueTable;
    private Table innerDialogueTable;

    private float dialogueWidthPercent;

    private ScrollPane optionsOut;
    private Table outerOptionsTable;
    private Table innerOptionsTable;

    private Dialogue dialogue;

    public DialogueUI(Main game) {
        this.game = game;
        heightPercent = .3f;
        dialogueWidthPercent = 0.7f;

        viewport = new ScreenViewport();
        viewport.setScreenPosition(0, 0);
        stage = new Stage(viewport, game.getBatch());
    }

    public void init() {
        root = new Table();
        stage.addActor(root);
        root.setBackground(new TextureRegionDrawable(new Texture(Gdx.files.internal("data/Test_Background.png"))));
        root.setSize(viewport.getScreenWidth(), heightPercent * viewport.getScreenHeight());
        root.top().left();

        Table leftRoot = new Table();
        leftRoot.top().left().pad(5).defaults().expandY().height(heightPercent * viewport.getScreenHeight()).width(dialogueWidthPercent * viewport.getScreenWidth());
        leftRoot.layout();

        outerDialogueTable = new Table();
        outerDialogueTable.top().left().defaults().space(4).expandY().height(heightPercent * viewport.getScreenHeight()).width(dialogueWidthPercent * viewport.getScreenWidth());
        outerDialogueTable.layout();

        innerDialogueTable = new Table();
        innerDialogueTable.top().left().defaults().expandY().height(heightPercent * viewport.getScreenHeight()).width(dialogueWidthPercent * viewport.getScreenWidth());
        innerDialogueTable.layout();

        dialogueOut = new ScrollPane(innerDialogueTable);
        dialogueOut.setFadeScrollBars(false);
        dialogueOut.setScrollingDisabled(true, false);
        outerDialogueTable.add(dialogueOut).expandY();

        //A little bit of extra padding in the scrollpane
        addToDialogueOut("");

        leftRoot.add(outerDialogueTable);
        root.add(leftRoot);

        Table rightRoot = new Table();
        rightRoot.top().left().pad(5).defaults().expandY().height(heightPercent * viewport.getScreenHeight()).width((1 - dialogueWidthPercent) * viewport.getScreenWidth());
        rightRoot.layout();

        outerOptionsTable = new Table();
        outerOptionsTable.top().left().defaults().space(4).expandY().height(heightPercent * viewport.getScreenHeight()).width((1 - dialogueWidthPercent) * viewport.getScreenWidth());
        outerOptionsTable.layout();

        innerOptionsTable = new Table();
        innerOptionsTable.top().left().defaults().expandY().height(heightPercent * viewport.getScreenHeight()).width((1 - dialogueWidthPercent) * viewport.getScreenWidth());
        innerOptionsTable.layout();

        optionsOut = new ScrollPane(innerOptionsTable);
        optionsOut.setFadeScrollBars(false);
        optionsOut.setScrollingDisabled(true, false);
        outerOptionsTable.add(optionsOut).expandY();

        rightRoot.add(outerOptionsTable);
        root.add(rightRoot);
    }

    public void act(float delta) {

        stage.act(delta);

        //Update location
        float menux = viewport.getCamera().position.x - viewport.getScreenWidth()/2f;
        float menuy = 0;

        root.setPosition(menux, menuy);

    }

    /**
     * Dispose of the stage
     */
    public void dispose() {
        stage.dispose();
    }

    public void clear() {
        innerOptionsTable.clear();
        dialogue = null;
        innerDialogueTable.clear();
        //A little bit of extra padding in the scrollpane
        addToDialogueOut("");
    }

    public void addToDialogueOut(String s) {
        System.out.println(s);
        Label l = new Label(s, skin);
        Label whiteSpace = new Label("", skin);
        l.setFontScale(0.7f);
        l.setWrap(true);
        l.pack();
        innerDialogueTable.top().left().add(l).expandY().width(dialogueWidthPercent * viewport.getScreenWidth())
                .height(Math.max((1f * viewport.getScreenHeight()) / 30f, l.getPrefHeight())).padTop(1.5f).padBottom(1.5f);
        innerDialogueTable.row();
        innerDialogueTable.layout();
        innerDialogueTable.top().left().add(whiteSpace).expandY().width(dialogueWidthPercent * viewport.getScreenWidth())
                .height(Math.max((1f * viewport.getScreenHeight()) / 30f, l.getPrefHeight())).padTop(1.5f);
        innerDialogueTable.row();
        innerDialogueTable.layout();
        dialogueOut.scrollTo(0, 0, 0, 0);
    }

    public void addToOptions(String s) {
        System.out.println(s);
        Label l = new Label(s, skin);
        l.setFontScale(0.7f);
        l.setWrap(true);
        l.pack();
        innerOptionsTable.top().left().add(l).expandY().width((1 - dialogueWidthPercent) * viewport.getScreenWidth())
                .height(Math.max((1f * viewport.getScreenHeight()) / 30f, l.getPrefHeight())).padTop(1.5f).padBottom(1.5f);
        innerOptionsTable.row();
        innerOptionsTable.layout();
        optionsOut.scrollTo(0, 0, 0, 0);
    }

    //<editor-fold desc="Getters and Setters">
    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public ScreenViewport getViewport() {
        return viewport;
    }

    public void setViewport(ScreenViewport viewport) {
        this.viewport = viewport;
    }

    public Main getGame() {
        return game;
    }

    public void setGame(Main game) {
        this.game = game;
    }

    public Table getRoot() {
        return root;
    }

    public void setRoot(Table root) {
        this.root = root;
    }

    public ScrollPane getDialogueOut() {
        return dialogueOut;
    }

    public void setDialogueOut(ScrollPane dialogueOut) {
        this.dialogueOut = dialogueOut;
    }

    public Table getOuterDialogueTable() {
        return outerDialogueTable;
    }

    public void setOuterDialogueTable(Table outerDialogueTable) {
        this.outerDialogueTable = outerDialogueTable;
    }

    public Table getInnerDialogueTable() {
        return innerDialogueTable;
    }

    public void setInnerDialogueTable(Table innerDialogueTable) {
        this.innerDialogueTable = innerDialogueTable;
    }

    public ScrollPane getOptionsOut() {
        return optionsOut;
    }

    public void setOptionsOut(ScrollPane optionsOut) {
        this.optionsOut = optionsOut;
    }

    public Table getOuterOptionsTable() {
        return outerOptionsTable;
    }

    public void setOuterOptionsTable(Table outerOptionsTable) {
        this.outerOptionsTable = outerOptionsTable;
    }

    public Table getInnerOptionsTable() {
        return innerOptionsTable;
    }

    public void setInnerOptionsTable(Table innerOptionsTable) {
        this.innerOptionsTable = innerOptionsTable;
    }

    public float getHeightPercent() {
        return heightPercent;
    }

    public void setHeightPercent(float heightPercent) {
        this.heightPercent = heightPercent;
    }

    public void resizeBy(float delta) {
        if(heightPercent + delta < .125 || heightPercent + delta > .8) return;
        heightPercent += delta;
    }

    public void resizeTo(float newHeight) {
        resizeBy(newHeight - heightPercent);
    }

    public Skin getSkin() {
        return skin;
    }

    public Dialogue getDialogue() {
        return dialogue;
    }

    public void setDialogue(Dialogue dialogue) {
        if(this.dialogue != null) this.dialogue.setSelection("");
        this.dialogue = dialogue;

        innerOptionsTable.clear();

        addToDialogueOut(dialogue.getText());
        for(String s : dialogue.getOptions().keySet())
            addToOptions(String.format(Locale.getDefault(), "%s. %s", s, dialogue.getResponseAt(s)));

    }

    public float getDialogueWidthPercent() {
        return dialogueWidthPercent;
    }

    public void setDialogueWidthPercent(float dialogueWidthPercent) {
        this.dialogueWidthPercent = dialogueWidthPercent;
    }

    //</editor-fold>
}
