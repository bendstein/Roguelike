package actors.creatures;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import game.ApplicationMain;
import creature.Creature;

public class PlayerActor extends CreatureActor {

    public PlayerActor(Creature player) {
        super(player);
        this.creature.setActor(this);
    }

    @Override
    public void act(float delta) {
        if(System.currentTimeMillis() - creature.getLastMovedTime() >= 100) {
            switch (creature.getMoveDirection()) {
                case 0 : {
                    break;
                }
                case Input.Keys.NUMPAD_7: {
                    creature.moveBy(-1, 1);
                    break;
                }
                case Input.Keys.NUMPAD_8: {
                    creature.moveBy(0, 1);
                    break;
                }
                case Input.Keys.NUMPAD_9: {
                    creature.moveBy(1, 1);
                    break;
                }
                case Input.Keys.NUMPAD_4: {
                    creature.moveBy(-1, 0);
                    break;
                }
                case Input.Keys.NUMPAD_5: {
                    creature.moveBy(0, 0);
                    break;
                }
                case Input.Keys.NUMPAD_6: {
                    creature.moveBy(1, 0);
                    break;
                }
                case Input.Keys.NUMPAD_1: {
                    creature.moveBy(-1, -1);
                    break;
                }
                case Input.Keys.NUMPAD_2: {
                    creature.moveBy(0, -1);
                    break;
                }
                case Input.Keys.NUMPAD_3: {
                    creature.moveBy(1, -1);
                    break;
                }
                default:

            }
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(creature.getTexture(), ApplicationMain.getTILE_SIZE() * creature.getX(), ApplicationMain.getTILE_SIZE() * creature.getY());
    }

}
