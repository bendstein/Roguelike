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
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import creatureitem.Player;
import creatureitem.item.*;
import game.Main;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class InventoryScreen extends ScreenAdapter {

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
    private TreeMap<String, Item> inventoryMap;

    private Inventory inventory;

    /**
     * Variables related to the main stage
     */
    private Stage stage;
    private Table root;
    private Viewport viewport;
    private Camera camera;

    private final Skin skin = new Skin(Gdx.files.internal("skin/glassy-ui.json"));

    private Label verbLabel;

    private ScrollPane list;
    private Table listTable;
    private Table outerListTable;

    private String currentVerb;
    private String[] currentFilter;
    private String selection;
    private boolean shift;

    int turnsElapsed;

    public InventoryScreen(Main game) {
        this.game = game;
        this.inventoryMap = new TreeMap<>();
        this.inventory = null;

        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        viewport = new ScreenViewport(camera);
        stage = new Stage(viewport, game.getBatch());
        currentVerb = "";
        selection = "";
        shift = false;

        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {

                if(Input.Keys.toString(keycode).length() == 1) {
                    char in = Input.Keys.toString(keycode).charAt(0);
                    selection += shift? Character.toUpperCase(in) : Character.toLowerCase(in);

                    if(filtered().size() == 1) use(filtered().firstKey());
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
        currentVerb = "";
        currentFilter = new String[]{""};
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

        verbLabel = new Label(currentVerb.equals("")? "Inventory" : Character.toUpperCase(currentVerb.charAt(0)) + currentVerb.substring(1), skin);
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

        TreeMap<String, Item> filtered = filtered();

        for(Map.Entry<String, Item> entry : filtered.entrySet()) {
            Label l;
            if(entry.getValue() instanceof Equipable && getPlayer().isEquipped(entry.getValue()) && ((Equipable)entry.getValue()).isEquipped())
                l = new Label(String.format(Locale.getDefault(),"%s, %s (E)", entry.getKey(), entry.getValue().toString()), skin);
            else
                l = new Label(String.format(Locale.getDefault(),"%s, %s", entry.getKey(), entry.getValue().toString()), skin);

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

    public TreeMap<String, Item> filtered() {
        TreeMap<String, Item> filtered;

        if(!selection.equals("")) {
            filtered = new TreeMap<>();
            for(Map.Entry<String, Item> entry : inventoryMap.entrySet()) {
                if(entry.getKey().contains(selection)) filtered.put(entry.getKey(), entry.getValue());
            }
        }

        else
            filtered = inventoryMap;

        if(filtered.size() == 0)
            selection = "";

        return filtered;
    }

    /**
     * Perform the verb on the item
     * @param index the location in the inventory of the item to use
     */
    public void use(String index) {
        if(currentVerb.equals("drop")) {
            Item i = inventoryMap.get(index);
            game.getPlayer().drop(i);
            inventoryMap.remove(index);
            selection = "";
            game.getPlayer().increaseTurnsToProcess(1);
        }
        else if(currentVerb.equals("throw")) {
            game.getPlayer().prepThrow(inventoryMap.get(index));
            game.setScreen(game.getPlayScreen());
            selection = "";
            //game.getPlayer().increaseTurnsToProcess(1);
        }
        else if(currentVerb.equals("eat")) {
            Food i = new Food(inventoryMap.get(index));
            game.getPlayer().eat(i);
            selection = "";
            if(inventory.contains(i) == -1) inventoryMap.remove(index);
            else inventoryMap.replace(index, inventory.getItems()[inventory.contains(i)]);
        }
        else if(currentVerb.equals("equip")) {
            if(getPlayer().isEquipped(inventoryMap.get(index))) getPlayer().unequip(inventoryMap.get(index));
            else getPlayer().equip(inventoryMap.get(index));
            selection = "";
        }
        else if(currentVerb.equals("quaff")) {
            Potion p = (Potion) inventoryMap.get(index);
            game.getPlayer().drink(p);
            selection = "";
            if(inventory.contains(p) == -1) inventoryMap.remove(index);
            else inventoryMap.replace(index, inventory.getItems()[inventory.contains(p)]);
        }
        else if(currentVerb.equals("pickup")) {
            game.getPlayer().pickUp(inventoryMap.get(index));
            selection = "";
            inventoryMap.remove(index);
            game.getPlayer().increaseTurnsToProcess(1);
        }
    }

    /**
     * Filter the inventory to only show items that match the requirements
     */
    public void filterProperty(String ... keywords) {
        ArrayList<Item> filtered = new ArrayList<>();
        if(inventory.getCount() == 0) return;

        for(Item i : inventory.asList()) {
            if(i == null) continue;
            if(keywords.length == 0 || keywords[0].equals("") || i.hasProperty(keywords))
                filtered.add(i);
        }

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

            inventoryMap.put(s.toString(), filtered.get(i));
        }
    }

    /**
     * @param key The string associated with the item's index in the inventory
     * @return The item who's index is associated with the string given
     */
    public Item getItemAt(String key) {
        return inventoryMap.get(key);
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
        verbLabel.setText(currentVerb.equals("")? "Inventory" : Character.toUpperCase(currentVerb.charAt(0)) + currentVerb.substring(1));
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
        inventoryMap.clear();
        selection = "";
        turnsElapsed = 0;
        filterProperty(currentFilter);
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
        selection = "";
    }

    //<editor-fold desc="Getters and Setters">

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
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

    public TreeMap<String, Item> getInventoryMap() {
        return inventoryMap;
    }

    public void setInventoryMap(TreeMap<String, Item> inventoryMap) {
        this.inventoryMap = inventoryMap;
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

    public String getCurrentVerb() {
        return currentVerb;
    }

    public void setCurrentVerb(String currentVerb) {
        this.currentVerb = currentVerb;
    }

    public String[] getCurrentFilter() {
        return currentFilter;
    }

    public void setCurrentFilter(String ... currentFilter) {
        this.currentFilter = currentFilter;
    }

    //</editor-fold>
}
