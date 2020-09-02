package screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import creatureitem.Player;
import creatureitem.item.Inventory;
import creatureitem.item.Item;
import creatureitem.item.ItemSlot;
import creatureitem.item.behavior.equipable.Slot;
import game.Main;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class EquipScreen extends ScreenAdapter {

    /**
     * Reference to the main application
     */
    private Main game;

    /**
     * Letters referring to the index in the inventory
     */
    private final String LETTERS = "abcdefghijklmnopqrstuvwxyz";

    /**
     * Mapping from the letter index to the item
     */
    private TreeMap<String, ItemSlot> equipMap;

    private ArrayList<ItemSlot> slots;

    /**
     * Variables related to the main stage
     */
    private Stage stage;
    private Table root;
    private Viewport viewport;
    private Camera camera;
    private Label verbLabel;

    private final Skin skin = new Skin(Gdx.files.internal("skin/glassy-ui.json"));

    private ScrollPane list;
    private Table listTable;
    private Table outerListTable;

    private String selection;
    private boolean shift;

    int turnsElapsed;

    public EquipScreen(Main game) {
        this.game = game;
        this.equipMap = new TreeMap<>();
        this.slots = null;

        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        viewport = new ScreenViewport(camera);
        stage = new Stage(viewport, game.getBatch());
        selection = "";
        shift = false;

        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {

                if(Input.Keys.toString(keycode).length() == 1) {
                    char in = Input.Keys.toString(keycode).charAt(0);
                    selection += shift? Character.toUpperCase(in) : Character.toLowerCase(in);

                    if(filtered().size() == 1) use(filtered().firstKey());
                    return true;
                }

                switch (keycode) {
                    case Input.Keys.ESCAPE: {
                        game.setScreen(game.getPlayScreen());
                        return true;
                    }
                    case Input.Keys.SHIFT_LEFT:
                    case Input.Keys.SHIFT_RIGHT: {
                        shift = true;
                        return true;
                    }
                }

                return false;
            }

            @Override
            public boolean keyUp(InputEvent event, int keycode) {
                switch (keycode) {
                    case Input.Keys.SHIFT_LEFT:
                    case Input.Keys.SHIFT_RIGHT: {
                        shift = false;
                        return true;
                    }
                }

                return false;
            }

            @Override
            public boolean scrolled(InputEvent event, float x, float y, int amount) {
                if(amount == 1) {
                    list.setScrollY(list.getScrollY() + 20);
                    return true;
                }
                else if(amount == -1) {
                    list.setScrollY(list.getScrollY() - 20);
                    return true;
                }

                return false;
            }

        });
        turnsElapsed = 0;

        init();

    }

    public void init() {

        root = new Table();
        root.setFillParent(true);
        root.setBackground(new TextureRegionDrawable(new Texture(Gdx.files.internal("data/Test_Background.png"))));
        root.top().left();
        root.setSize(viewport.getScreenWidth(), viewport.getScreenHeight());
        stage.addActor(root);

        verbLabel = new Label("Equip", skin);
        verbLabel.setWrap(true);
        verbLabel.pack();
        root.add(verbLabel).width(9f * getViewport().getScreenWidth()/10f);
        root.row();

        outerListTable = new Table();
        outerListTable.pad(5).defaults();
        outerListTable.setSize(viewport.getScreenWidth(), viewport.getScreenHeight());
        outerListTable.layout();

        root.add(outerListTable);

        listTable = new Table();
        listTable.pad(5).defaults();
        listTable.setSize(viewport.getScreenWidth(), viewport.getScreenHeight());
        listTable.layout();

        list = new ScrollPane(listTable);
        list.setSize(viewport.getScreenWidth(), viewport.getScreenHeight());
        list.setFadeScrollBars(false);
        list.setScrollingDisabled(true, false);
        outerListTable.add(list);

    }

    public void out() {
        listTable.clearChildren();

        TreeMap<String, ItemSlot> entries = filtered();

        for(Map.Entry<String, ItemSlot> entry : entries.entrySet()) {
            Label l;
            if(entry.getValue().isEquipped())
                l = new Label(String.format(Locale.getDefault(),"%s. (%s), %s", entry.getKey(), entry.getValue().getName(), entry.getValue().getI().toString()), skin);
            else if(entry.getValue().isObstructed())
                l = new Label(String.format(Locale.getDefault(),"%s. (%s), [%s]", entry.getKey(), entry.getValue().getName(), entry.getValue().getI().toString()), skin);
            else
                l = new Label(String.format(Locale.getDefault(),"%s. (%s)", entry.getKey(), entry.getValue().getName()), skin);

            l.addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    String s = l.toString();
                    String[] key = s.split("\\s|:|,");
                    use(key[2]);
                    return true;
                }
            });
            l.setWrap(true);
            l.pack();
            listTable.add(l).width(9f * getViewport().getScreenWidth()/10f);
            listTable.row();
            listTable.layout();
        }
    }

    public void list() {
        ArrayList<ItemSlot> filtered = slots;
        if(slots == null || slots.isEmpty()) return;

        for(int i = 0; i < filtered.size(); i++) {
            int index = i % LETTERS.length();
            int times = Math.floorDiv(i, LETTERS.length()) + 1;
            StringBuilder s = new StringBuilder();

            do {
                if(times % 2 == 0) {
                    s.append(Character.toUpperCase(LETTERS.charAt(index)));
                    times -= 2;
                }
                else {
                    s.append(LETTERS.charAt(index));
                    times -= 1;
                }

            } while (times > 0);

            equipMap.put(s.toString(), filtered.get(i));
        }
    }

    public TreeMap<String, ItemSlot> filtered() {
        TreeMap<String, ItemSlot> filtered;

        if(!selection.equals("")) {
            filtered = new TreeMap<>();
            for(Map.Entry<String, ItemSlot> entry : equipMap.entrySet()) {
                if(entry.getKey().contains(selection)) filtered.put(entry.getKey(), entry.getValue());
            }
        }

        else
            filtered = equipMap;

        if(filtered.size() == 0)
            selection = "";

        return filtered;
    }

    /**
     * Perform the verb on the item
     * @param index the location in the inventory of the item to use
     */
    public void use(String index) {

        ItemSlot is = equipMap.get(index);

        if(is.isEquipped()) {
            is.getI().unequip();
            selection = "";
        } else if(is.isEmpty()) {
            game.getInventoryScreen().setInventory(game.getPlayer().getInventory());
            game.getInventoryScreen().setCurrentVerb("equip");
            game.getInventoryScreen().setCurrentFilter(String.format(Locale.getDefault(), "%d", is.getSlot().getSlot()));
            game.getInventoryScreen().setToEquip(is);
            game.getInventoryScreen().setReturnScreen(this);
            selection = "";
            game.setScreen(game.getInventoryScreen());
        }
    }

    /**
     * @param key The string associated with the item's index in the inventory
     * @return The item who's index is associated with the string given
     */
    public ItemSlot getItemSlot(String key) {
        return equipMap.get(key);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        Gdx.gl.glClearColor(0, 0, 0, 255);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        stage.act(Gdx.graphics.getDeltaTime());
        outerListTable.bottom().left();
        outerListTable.setPosition(0f, 0f);
        verbLabel.setText("Equip");
        out();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        viewport.update(width, height);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        listTable.clearChildren();
        equipMap.clear();
        selection = "";
        turnsElapsed = 0;
        list();
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
        selection = "";
    }

    //<editor-fold desc="Getters and Setters">


    public void setEquipMap(TreeMap<String, ItemSlot> equipMap) {
        this.equipMap = equipMap;
    }

    public TreeMap<String, ItemSlot> getEquipMap() {
        return equipMap;
    }

    public ArrayList<ItemSlot> getSlots() {
        return slots;
    }

    public void setSlots(ArrayList<ItemSlot> slots) {
        this.slots = slots;
    }

    public Label getVerbLabel() {
        return verbLabel;
    }

    public void setVerbLabel(Label verbLabel) {
        this.verbLabel = verbLabel;
    }

    public String getSelection() {
        return selection;
    }

    public void setSelection(String selection) {
        this.selection = selection;
    }

    public boolean isShift() {
        return shift;
    }

    public void setShift(boolean shift) {
        this.shift = shift;
    }

    public int getTurnsElapsed() {
        return turnsElapsed;
    }

    public void setTurnsElapsed(int turnsElapsed) {
        this.turnsElapsed = turnsElapsed;
    }

    public Player getPlayer() {
        return game.getPlayer();
    }

    public Main getGame() {
        return game;
    }

    public void setGame(Main game) {
        this.game = game;
    }

    public String getLETTERS() {
        return LETTERS;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public Table getRoot() {
        return root;
    }

    public void setRoot(Table root) {
        this.root = root;
    }

    public Viewport getViewport() {
        return viewport;
    }

    public void setViewport(Viewport viewport) {
        this.viewport = viewport;
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public Skin getSkin() {
        return skin;
    }

    public ScrollPane getList() {
        return list;
    }

    public void setList(ScrollPane list) {
        this.list = list;
    }

    public Table getListTable() {
        return listTable;
    }

    public void setListTable(Table listTable) {
        this.listTable = listTable;
    }

    public Table getOuterListTable() {
        return outerListTable;
    }

    public void setOuterListTable(Table outerListTable) {
        this.outerListTable = outerListTable;
    }

    //</editor-fold>
}
