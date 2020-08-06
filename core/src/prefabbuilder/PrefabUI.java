package prefabbuilder;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import creatureitem.Creature;
import creatureitem.generation.CreatureItemFactory;
import game.Main;
import world.Tile;

public class PrefabUI {

    private Stage stage;
    private ScreenViewport viewport;
    private PrefabBuilderMain game;

    private final Skin skin = new Skin(Gdx.files.internal("skin/glassy-ui.json"));

    private Table root;

    private Table outerTable;
    private Table outerTileSelectTable, outerThingSelectTable, outerCreatureSelectTable;
    private ScrollPane tileSelect, thingSelect, creatureSelect;
    private Table innerTileSelectTable, innerThingSelectTable, innerCreatureSelectTable;

    private Object selected, selectedRight;

    public PrefabUI(PrefabBuilderMain game) {
        this.game = game;

        viewport = new ScreenViewport();
        viewport.setScreenPosition(0, Gdx.graphics.getHeight());
        stage = new Stage(viewport, game.getBatch());
        selected = selectedRight = null;
    }

    public void init() {
        root = new Table();
        stage.addActor(root);
        root.setBackground(new TextureRegionDrawable(new Texture(Gdx.files.internal("data/Test_Background.png"))));
        root.setSize(viewport.getScreenWidth(), (1f * viewport.getScreenHeight()) / 5f);
        root.setFillParent(true);
        root.top().left();

        outerTable = new Table();
        outerTable.top().left().defaults().padTop(5);
        outerTable.setSize(.8f * viewport.getScreenWidth(), .2f * viewport.getScreenHeight());
        outerTable.layout();

        outerTable.row().top().left().pad(5).padTop(10).expandX();

        outerTileSelectTable = new Table();
        outerTileSelectTable.setDebug(true);
        outerTileSelectTable.pad(5).defaults().space(4);
        outerTileSelectTable.layout();

        outerThingSelectTable = new Table();
        outerThingSelectTable.setDebug(true);
        outerThingSelectTable.pad(5).defaults().space(4);
        outerThingSelectTable.layout();

        outerCreatureSelectTable = new Table();
        outerCreatureSelectTable.setDebug(true);
        outerCreatureSelectTable.pad(5).defaults().space(4);
        outerCreatureSelectTable.layout();

        outerTable.add(outerTileSelectTable).expandY().padLeft(10);
        outerTable.add(outerThingSelectTable).expandY().padLeft(10);
        outerTable.add(outerCreatureSelectTable).expandY().padLeft(10);

        innerTileSelectTable = new Table();

        innerTileSelectTable.top().left();
        innerTileSelectTable.layout();

        tileSelect = new ScrollPane(innerTileSelectTable);
        tileSelect.setFadeScrollBars(false);
        tileSelect.setScrollingDisabled(true, false);
        innerThingSelectTable = new Table();
        thingSelect = new ScrollPane(innerThingSelectTable);
        thingSelect.setFadeScrollBars(false);
        thingSelect.setScrollingDisabled(true, false);
        innerCreatureSelectTable = new Table();
        creatureSelect = new ScrollPane(innerCreatureSelectTable);
        creatureSelect.setFadeScrollBars(false);
        creatureSelect.setScrollingDisabled(true, false);

        outerTileSelectTable.add(tileSelect);
        outerThingSelectTable.add(thingSelect);
        outerCreatureSelectTable.add(creatureSelect);

        for(Tile t : Tile.values()) {
            if(t.isNotPlacable()) continue;
            Label l = new Label(t.getName(), getSkin());
            l.setFontScale(0.8f);
            l.setWrap(true);
            l.pack();
            innerTileSelectTable.top().left().add(l).expandY().width(getViewport().getScreenWidth()/5f).height((1f * viewport.getScreenHeight()) / 30f);
            innerTileSelectTable.row();
            innerTileSelectTable.layout();
            tileSelect.scrollTo(0, 0, 0, 0);

            l.addListener(new ClickListener(Input.Buttons.RIGHT) {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if(!Tile.exists(l.getText().toString())) return;
                    Tile tl = Tile.getTile(l.getText().toString());
                    selectedRight = tl;
                }
            });
            l.addListener(new ClickListener(Input.Buttons.LEFT) {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if(!Tile.exists(l.getText().toString())) return;
                    Tile tl = Tile.getTile(l.getText().toString());
                    selected = tl;
                }
            });

        }

        for(Creature c : CreatureItemFactory.creatures.values()) {
            Label l = new Label(c.getName(), getSkin());
            l.setFontScale(0.8f);
            l.setWrap(true);
            l.pack();
            innerCreatureSelectTable.top().left().add(l).expandY().width(getViewport().getScreenWidth()/5f).height((1f * viewport.getScreenHeight()) / 30f);
            innerCreatureSelectTable.row();
            innerCreatureSelectTable.layout();
            creatureSelect.scrollTo(0, 0, 0, 0);

            l.addListener(new ClickListener(Input.Buttons.RIGHT) {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Creature c = CreatureItemFactory.newCreature(l.getText().toString());
                    if(c != null) selectedRight = c;
                }
            });
            l.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Creature c = CreatureItemFactory.newCreature(l.getText().toString());
                    if(c != null) selected = c;
                }

            });
        }

        root.addActor(outerTable);
    }

    /**
     * Dispose of the stage
     */
    public void dispose() {
        stage.dispose();
    }

    public void act(float delta) {

        stage.act(delta);

        //Update location
        float menux = viewport.getCamera().position.x - viewport.getScreenWidth()/2f;
        float menuy = viewport.getCamera().position.y + (2f * viewport.getScreenHeight()/5f) - (float)Math.pow(Main.getTileHeight(), 2)/10f;

        root.setPosition(menux, menuy);

        //Update content
        displayOutput();

    }

    /**
     * Write output to the log
     */
    public void displayOutput() {

    }

    //<editor-fold desc="Getters and Setters">

    public Object getSelectedRight() {
        return selectedRight;
    }

    public void setSelectedRight(Object selectedRight) {
        this.selectedRight = selectedRight;
    }

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

    public PrefabBuilderMain getGame() {
        return game;
    }

    public void setGame(PrefabBuilderMain game) {
        this.game = game;
    }

    public Skin getSkin() {
        return skin;
    }

    public Table getRoot() {
        return root;
    }

    public void setRoot(Table root) {
        this.root = root;
    }

    public Table getOuterTileSelectTable() {
        return outerTileSelectTable;
    }

    public void setOuterTileSelectTable(Table outerTileSelectTable) {
        this.outerTileSelectTable = outerTileSelectTable;
    }

    public ScrollPane getTileSelect() {
        return tileSelect;
    }

    public void setTileSelect(ScrollPane tileSelect) {
        this.tileSelect = tileSelect;
    }

    public ScrollPane getThingSelect() {
        return thingSelect;
    }

    public void setThingSelect(ScrollPane thingSelect) {
        this.thingSelect = thingSelect;
    }

    public ScrollPane getCreatureSelect() {
        return creatureSelect;
    }

    public void setCreatureSelect(ScrollPane creatureSelect) {
        this.creatureSelect = creatureSelect;
    }

    public Table getInnerTileSelectTable() {
        return innerTileSelectTable;
    }

    public void setInnerTileSelectTable(Table innerTileSelectTable) {
        this.innerTileSelectTable = innerTileSelectTable;
    }

    public Table getInnerThingSelectTable() {
        return innerThingSelectTable;
    }

    public void setInnerThingSelectTable(Table innerThingSelectTable) {
        this.innerThingSelectTable = innerThingSelectTable;
    }

    public Table getInnerCreatureSelectTable() {
        return innerCreatureSelectTable;
    }

    public void setInnerCreatureSelectTable(Table innerCreatureSelectTable) {
        this.innerCreatureSelectTable = innerCreatureSelectTable;
    }

    public Table getOuterTable() {
        return outerTable;
    }

    public void setOuterTable(Table outerTable) {
        this.outerTable = outerTable;
    }

    public Table getOuterThingSelectTable() {
        return outerThingSelectTable;
    }

    public void setOuterThingSelectTable(Table outerThingSelectTable) {
        this.outerThingSelectTable = outerThingSelectTable;
    }

    public Table getOuterCreatureSelectTable() {
        return outerCreatureSelectTable;
    }

    public void setOuterCreatureSelectTable(Table outerCreatureSelectTable) {
        this.outerCreatureSelectTable = outerCreatureSelectTable;
    }

    public Object getSelected() {
        return selected;
    }

    public void setSelected(Object selected) {
        this.selected = selected;
    }

    //</editor-fold>
}
