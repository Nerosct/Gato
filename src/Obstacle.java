import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Obstacle {
    private static final int WALK_FRAME_COLS = 6;
    private static final int WALK_FRAME_ROWS = 1;
    private static final int DEATH_FRAME_COLS = 4;
    private static final int DEATH_FRAME_ROWS = 1;

    private Animation<TextureRegion> walkAnimation;
    private Animation<TextureRegion> deathAnimation;
    private Texture walkSpriteSheet;
    private Texture deathSpriteSheet;
    private Vector2 position;
    private Rectangle bounds;
    private final float SPEED = 500f;
    private float stateTime;
    private boolean isDead;

    public Obstacle(float x, float y) {
        walkSpriteSheet = new Texture(Gdx.files.internal("assets/Inimigo/Walk.png"));
        TextureRegion[][] walkTmp = TextureRegion.split(walkSpriteSheet, walkSpriteSheet.getWidth() / WALK_FRAME_COLS,
                walkSpriteSheet.getHeight() / WALK_FRAME_ROWS);

        Array<TextureRegion> walkFrames = new Array<>();
        for (int i = 0; i < WALK_FRAME_ROWS; i++) {
            for (int j = 0; j < WALK_FRAME_COLS; j++) {
                TextureRegion frame = walkTmp[i][j];
                frame.flip(true, false);
                walkFrames.add(frame);
            }
        }
        walkAnimation = new Animation<>(0.1f, walkFrames, Animation.PlayMode.LOOP);

        deathSpriteSheet = new Texture(Gdx.files.internal("assets/Inimigo/Death.png"));
        TextureRegion[][] deathTmp = TextureRegion.split(deathSpriteSheet,
                deathSpriteSheet.getWidth() / DEATH_FRAME_COLS,
                deathSpriteSheet.getHeight() / DEATH_FRAME_ROWS);

        Array<TextureRegion> deathFrames = new Array<>();
        for (int i = 0; i < DEATH_FRAME_ROWS; i++) {
            for (int j = 0; j < DEATH_FRAME_COLS; j++) {
                TextureRegion frame = deathTmp[i][j];
                deathFrames.add(frame);
            }
        }
        deathAnimation = new Animation<>(0.1f, deathFrames, Animation.PlayMode.NORMAL);

        float scale = 3.0f; // Fator de escala para aumentar o tamanho do inimigo
        position = new Vector2(x, y);
        bounds = new Rectangle(position.x, position.y, (walkSpriteSheet.getWidth() / WALK_FRAME_COLS) * scale,
                (walkSpriteSheet.getHeight() / WALK_FRAME_ROWS) * scale);
        stateTime = 0f;
        isDead = false;
    }

    public void update(float deltaTime) {
        if (!isDead) {
            position.x -= SPEED * deltaTime;
        }
        bounds.setPosition(position.x, position.y);
        stateTime += deltaTime;
    }

    public void draw(SpriteBatch batch) {
        TextureRegion currentFrame;
        float scale = 3.0f; // Fator de escala para aumentar o tamanho do inimigo

        if (isDead) {
            currentFrame = deathAnimation.getKeyFrame(stateTime);
            if (deathAnimation.isAnimationFinished(stateTime)) {
                return;
            }
        } else {
            currentFrame = walkAnimation.getKeyFrame(stateTime, true);
        }
        float width = currentFrame.getRegionWidth() * scale;
        float height = currentFrame.getRegionHeight() * scale;
        batch.draw(currentFrame, position.x, position.y, width, height);
    }

    public void die() {
        isDead = true;
        stateTime = 0f;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public Vector2 getPosition() {
        return position;
    }

    public float getWidth() {
        return walkSpriteSheet.getWidth() / WALK_FRAME_COLS;
    }

    public void dispose() {
        walkSpriteSheet.dispose();
        deathSpriteSheet.dispose();
    }
}
