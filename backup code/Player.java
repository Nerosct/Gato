import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Player {
    private static final int WALK_FRAME_COLS = 8;
    private static final int WALK_FRAME_ROWS = 1;
    private static final int RUN_FRAME_COLS = 8;
    private static final int RUN_FRAME_ROWS = 1;
    private static final int SHOOT_FRAME_COLS = 4;
    private static final int SHOOT_FRAME_ROWS = 1;
    private static final int JUMP_FRAME_COLS = 8;
    private static final int JUMP_FRAME_ROWS = 1;
    private static final float SCALE = 3.0f;

    private Animation<TextureRegion> walkAnimation;
    private Animation<TextureRegion> shootAnimation;
    private Animation<TextureRegion> jumpAnimation;
    private Animation<TextureRegion> runningAnimation;
    private Animation<TextureRegion> deathAnimation;
    private Texture walkSpriteSheet;
    private Texture shootSpriteSheet;
    private Texture jumpSpriteSheet;
    private Texture deathSpriteSheet;
    private Texture runSpriteSheet;

    private Vector2 position;
    private Vector2 velocity;
    private Rectangle bounds;
    private final float GRAVITY = -0.3f;
    private final float JUMP_VELOCITY = 14f;
    private float invincibilityTimer = 0f;
    private final float INVINCIBILITY_DURATION = 2.0f;

    private float blinkTimer = 0f;
    private final float BLINK_INTERVAL = 0.1f;
    private boolean isVisible = true;

    private float animationTime = 0f;
    private boolean isShooting = false;
    private boolean isJumping = false;
    private boolean isDead = false;
    private boolean isRunning = false;

    public Player() {
        walkSpriteSheet = new Texture(Gdx.files.internal("assets/player/walk/Gato.PNG"));
        TextureRegion[][] walkTmp = TextureRegion.split(walkSpriteSheet,
                walkSpriteSheet.getWidth() / WALK_FRAME_COLS,
                walkSpriteSheet.getHeight() / WALK_FRAME_ROWS);
        Array<TextureRegion> walkFrames = new Array<>();
        for (int i = 0; i < WALK_FRAME_ROWS; i++) {
            for (int j = 0; j < WALK_FRAME_COLS; j++) {
                walkFrames.add(walkTmp[i][j]);
            }
        }
        walkAnimation = new Animation<>(0.1f, walkFrames, Animation.PlayMode.LOOP);

        runSpriteSheet = new Texture(Gdx.files.internal("assets/player/correr/Correr.png"));
        TextureRegion[][] runTmp = TextureRegion.split(runSpriteSheet,
                runSpriteSheet.getWidth() / RUN_FRAME_COLS,
                runSpriteSheet.getHeight() / RUN_FRAME_ROWS);
        Array<TextureRegion> runFrames = new Array<>();
        for (int i = 0; i < RUN_FRAME_ROWS; i++) {
            for (int j = 0; j < RUN_FRAME_COLS; j++) {
                runFrames.add(runTmp[i][j]);
            }
        }
        runningAnimation = new Animation<>(0.1f, runFrames, Animation.PlayMode.LOOP);

        shootSpriteSheet = new Texture(Gdx.files.internal("assets/player/attack/Attack.png"));
        TextureRegion[][] shootTmp = TextureRegion.split(shootSpriteSheet,
                shootSpriteSheet.getWidth() / SHOOT_FRAME_COLS,
                shootSpriteSheet.getHeight() / SHOOT_FRAME_ROWS);
        Array<TextureRegion> shootFrames = new Array<>();
        for (int i = 0; i < SHOOT_FRAME_ROWS; i++) {
            for (int j = 0; j < SHOOT_FRAME_COLS; j++) {
                shootFrames.add(shootTmp[i][j]);
            }
        }
        shootAnimation = new Animation<>(0.1f, shootFrames, Animation.PlayMode.LOOP);

        jumpSpriteSheet = new Texture(Gdx.files.internal("assets/player/pulo/Pulo.png"));
        TextureRegion[][] jumpTmp = TextureRegion.split(jumpSpriteSheet,
                jumpSpriteSheet.getWidth() / JUMP_FRAME_COLS,
                jumpSpriteSheet.getHeight() / JUMP_FRAME_ROWS);
        Array<TextureRegion> jumpFrames = new Array<>();
        for (int i = 0; i < JUMP_FRAME_ROWS; i++) {
            for (int j = 0; j < JUMP_FRAME_COLS; j++) {
                jumpFrames.add(jumpTmp[i][j]);
            }
        }
        jumpAnimation = new Animation<>(0.2f, jumpFrames, Animation.PlayMode.LOOP);

        deathSpriteSheet = new Texture(Gdx.files.internal("assets/player/morte/Morte.png"));
        TextureRegion[][] deathTmp = TextureRegion.split(deathSpriteSheet,
                deathSpriteSheet.getWidth() / 4,
                deathSpriteSheet.getHeight() / 1);
        Array<TextureRegion> deathFrames = new Array<>();
        for (int i = 0; i < 1; i++) {
            for (int j = 0; j < 4; j++) {
                deathFrames.add(deathTmp[i][j]);
            }
        }
        deathAnimation = new Animation<>(0.2f, deathFrames, Animation.PlayMode.NORMAL);

        position = new Vector2(50, 100);
        velocity = new Vector2(0, 0);
        bounds = new Rectangle(
                position.x,
                position.y,
                (walkSpriteSheet.getWidth() / WALK_FRAME_COLS) * SCALE,
                (walkSpriteSheet.getHeight() / WALK_FRAME_ROWS) * SCALE);
    }

    public void update(float deltaTime) {
        if (isDead) {
            animationTime += deltaTime;
            if (deathAnimation.isAnimationFinished(animationTime)) {
                reset();
                isDead = false;
                animationTime = 0;
            }
        }
        if (invincibilityTimer > 0) {
            invincibilityTimer -= deltaTime;
            blinkTimer -= deltaTime;
            if (blinkTimer <= 0) {
                isVisible = !isVisible;
                blinkTimer = BLINK_INTERVAL;
            }
        } else {
            isVisible = true;
        }

        velocity.y += GRAVITY;
        position.add(velocity);

        if (position.y < 100) {
            position.y = 100;
            velocity.y = 0;
        }
        bounds.setPosition(position);

        animationTime += deltaTime;
        if (isShooting) {
            if (shootAnimation.isAnimationFinished(animationTime)) {
                isShooting = false;
                animationTime = 0;
            }
        } else if (isJumping) {
            animationTime += deltaTime;
            if (position.y == 100) {
                isJumping = false;
                animationTime = 0;
            }
        }
    }

    public void setSpeed(float speed) {
        this.velocity.x = speed;
    }

    public void drawRunning(SpriteBatch batch) {
        if (isVisible) {
            TextureRegion currentFrame = runningAnimation.getKeyFrame(animationTime, true);
            batch.draw(currentFrame, position.x, position.y, currentFrame.getRegionWidth() * SCALE,
                    currentFrame.getRegionHeight() * SCALE);
        }
    }

    // aqui que vem o pulo do gato kkkk :P
    public void jump() {
        if (position.y == 100) {
            velocity.y = JUMP_VELOCITY;
            isJumping = true;
            animationTime = 0;
        }
    }

    public void die() {
        isDead = true;
        invincibilityTimer = 0f;
        isVisible = true;
    }

    public void startInvincibility() {
        invincibilityTimer = INVINCIBILITY_DURATION;
        blinkTimer = BLINK_INTERVAL;
        isVisible = true;
    }

    public void shoot() {
        isShooting = true;
        animationTime = 0;
    }

    public boolean isInvincible() {
        return invincibilityTimer > 0;
    }

    public void draw(SpriteBatch batch) {
        if (isDead) {
            TextureRegion currentFrame = deathAnimation.getKeyFrame(animationTime, false);
            batch.draw(currentFrame, position.x, position.y, currentFrame.getRegionWidth() * SCALE,
                    currentFrame.getRegionHeight() * SCALE);
        } else if (isVisible) {
            TextureRegion currentFrame;
            if (isJumping) {
                currentFrame = jumpAnimation.getKeyFrame(animationTime, true);
            } else if (isShooting) {
                currentFrame = shootAnimation.getKeyFrame(animationTime, true);
            } else if (isRunning) {
                currentFrame = runningAnimation.getKeyFrame(animationTime, true);
            } else {
                currentFrame = walkAnimation.getKeyFrame(animationTime, true);
            }
            batch.draw(currentFrame, position.x, position.y, currentFrame.getRegionWidth() * SCALE,
                    currentFrame.getRegionHeight() * SCALE);
        }
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void reset() {
        position.set(50, 100);
        velocity.set(0, 0);
        invincibilityTimer = 0f;
        isVisible = true;
        isShooting = false;
        isJumping = false;
        isDead = false;
        animationTime = 0f;
    }

    public void dispose() {
        walkSpriteSheet.dispose();
        shootSpriteSheet.dispose();
        jumpSpriteSheet.dispose();
        runSpriteSheet.dispose();
        deathSpriteSheet.dispose();
    }

    public void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }
}