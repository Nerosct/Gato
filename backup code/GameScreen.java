import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import java.util.Iterator;
import com.badlogic.gdx.audio.Music;

public class GameScreen extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture background;
    private Texture background2;
    private Texture lifeTexture;
    private Player player;
    private Array<Obstacle> obstacles;
    private Array<Collectible> collectibles;
    private long lastObstacleTime;
    private long lastCollectibleTime;
    private int score;
    private int powers;
    private int vidas;
    private int collectiblesCollected;
    private Array<Shot> shots;
    private BitmapFont font;
    private boolean gameWon;
    private boolean gameOver;
    private Sound powerShotSound;
    private Music backgroundMusic;
    private Sound jumpSound;
    private Music defeat;
    private float background1X;
    private float background2X;

    private boolean inMenu = true; // Controla se o jogo está no menu

    @Override
    public void create() {
        batch = new SpriteBatch();
        background = new Texture(Gdx.files.internal("assets/Background/Cozinha.png"));
        background2 = new Texture(Gdx.files.internal("assets/Background/Sala.png"));
        lifeTexture = new Texture(Gdx.files.internal("assets/player/vida.png"));
        player = new Player();
        obstacles = new Array<>();
        collectibles = new Array<>();
        spawnObstacle();

        score = 0;
        powers = 0;
        shots = new Array<>();
        font = new BitmapFont();
        vidas = 3;
        lastCollectibleTime = TimeUtils.nanoTime();
        gameWon = false;
        gameOver = false;
        collectiblesCollected = 0;

        powerShotSound = Gdx.audio.newSound(Gdx.files.internal("assets/sounds/Meow.mp3"));
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("assets/sounds/Background Music.mp3"));
        jumpSound = Gdx.audio.newSound(Gdx.files.internal("assets/sounds/Pulo_Gato.mp3"));
        defeat = Gdx.audio.newMusic(Gdx.files.internal("assets/sounds/defeat.mp3"));

        backgroundMusic.setLooping(true);
        backgroundMusic.play();

        background1X = 0;
        background2X = background.getWidth();
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (inMenu) {
            drawMenu(); // Desenha o menu
            handleMenuInput(); // Verifica a entrada do jogador no menu
        } else {
            update(Gdx.graphics.getDeltaTime()); // Atualiza o jogo
            drawGame(); // Desenha o jogo
        }
    }

    private void drawMenu() {
        batch.begin();
        // Desenha o fundo do menu
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Desenha a mensagem do menu
        font.getData().setScale(2);
        String message1 = "Gato";
        String message = "Pressione ENTER para começar";
        float messageWidth = font.getRegion().getRegionWidth() * 2; // Ajusta o tamanho da mensagem
        font.draw(batch, message1, Gdx.graphics.getWidth() / 2f - messageWidth / 2f,
                Gdx.graphics.getHeight() / 2f);
        font.draw(batch, message, Gdx.graphics.getWidth() / 2f - messageWidth / 2f,
                Gdx.graphics.getHeight() / 2f);

        batch.end();
    }

    private void handleMenuInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            inMenu = false; // Sai do menu e começa o jogo
        }
    }

    private void drawGame() {
        batch.begin();
        batch.draw(background, background1X, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.draw(background2, background2X, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        player.draw(batch);

        for (Obstacle obstacle : obstacles) {
            obstacle.draw(batch);
        }
        for (Shot shot : shots) {
            shot.draw(batch);
        }
        for (Collectible collectible : collectibles) {
            collectible.draw(batch);
        }

        // Desenhar as vidas restantes
        for (int i = 0; i < vidas; i++) {
            batch.draw(lifeTexture, 10 + i * 30, Gdx.graphics.getHeight() - 50);
        }

        if (!gameWon && !gameOver) {
            font.draw(batch, "Score: " + score, 10, Gdx.graphics.getHeight() - 60);
            font.draw(batch, "Powers: " + powers, 10, Gdx.graphics.getHeight() - 80);
        } else if (gameWon) {
            String message = "Você ganhou! Aperte R para Jogar Novamente";

            font.getData().setScale(2);

            font.draw(batch, message, Gdx.graphics.getWidth() / 2f - font.getRegion().getRegionWidth() / 2f,
                    Gdx.graphics.getHeight() / 2f);
        } else if (gameOver) {
            backgroundMusic.stop();
            defeat.play();
            font.getData().setScale(2);
            player.die();
            String message = "Você Perdeu, Aperte R para Recomeçar";
            float messageWidth = font.getRegion().getRegionWidth() * 2; // Ajusta o tamanho da mensagem
            font.draw(batch, message, Gdx.graphics.getWidth() / 2f - messageWidth / 2f,
                    Gdx.graphics.getHeight() / 2f);
        }

        batch.end();
    }

    public void update(float deltaTime) {
        if (gameOver || gameWon) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
                defeat.stop();
                resetGame();
                backgroundMusic.setLooping(true);
                backgroundMusic.play();
            }
            return;
        }

        updateBackground(deltaTime);

        if (score >= 5) {
            player.setRunning(true);
        } else {
            player.setRunning(false);
        }

        if (score >= 20) {
            gameWon = true;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            player.jump();
            jumpSound.play();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) && powers > 0) {
            player.shoot();
            userPower();
        }
        player.update(deltaTime);

        if (TimeUtils.nanoTime() - lastObstacleTime > 2000000000) {
            spawnObstacle();
        }

        if (TimeUtils.nanoTime() - lastCollectibleTime > 8000000000L) {
            spawnCollectible();
        }

        boolean collided = false;

        for (Obstacle obstacle : obstacles) {
            obstacle.update(deltaTime);

            if (obstacle.getPosition().x < -obstacle.getWidth()) {
                if (score % 2 == 0) {
                    powers++;
                }
            }
        }

        Iterator<Obstacle> obstacleIterator = obstacles.iterator();
        while (obstacleIterator.hasNext()) {
            Obstacle obstacle = obstacleIterator.next();
            if (obstacle.getPosition().x < -obstacle.getWidth()) {
                obstacleIterator.remove();
            }
        }

        for (Obstacle obstacle : obstacles) {
            if (!collided && obstacle.getBounds().overlaps(player.getBounds())) {
                if (!player.isInvincible()) {
                    vidas--;
                    player.startInvincibility();
                    Gdx.app.log("Game", "Jogador acertou um obstáculo! Vidas restantes: " + vidas);
                } else {
                    Gdx.app.log("Game", "Jogador está invencível e não perdeu vida.");
                }

                collided = true;

                if (vidas <= 0) {
                    Gdx.app.log("Game", "Game Over! Jogador perdeu todas as vidas.");
                    gameOver = true;
                }
            }
        }

        Iterator<Collectible> collectibleIterator = collectibles.iterator();
        while (collectibleIterator.hasNext()) {
            Collectible collectible = collectibleIterator.next();
            collectible.update(deltaTime);

            if (collectible.getBounds().overlaps(player.getBounds())) {
                score += 1;
                collectibleIterator.remove();
                collectiblesCollected++;

                if (collectiblesCollected >= 2) {
                    powers++;
                    collectiblesCollected = 0;
                }
            }

            if (collectible.getBounds().x + collectible.getBounds().width < 0) {
                collectibleIterator.remove();
            }
        }

        for (Shot shot : shots) {
            shot.update(deltaTime);
        }

        Iterator<Shot> iterator = shots.iterator();
        while (iterator.hasNext()) {
            Shot shot = iterator.next();
            for (Obstacle obstacle : obstacles) {
                if (shot.getBound().overlaps(obstacle.getBounds())) {
                    obstacle.die();
                    iterator.remove();
                    obstacles.removeValue(obstacle, true);
                    break;
                }
            }
        }

        Iterator<Shot> shotIterator = shots.iterator();
        while (shotIterator.hasNext()) {
            Shot shot = shotIterator.next();
            if (shot.getX() > Gdx.graphics.getWidth()) {
                shotIterator.remove();
            }
        }
    }

    private void updateBackground(float deltaTime) {
        float scrollSpeed = 100f;
        background1X -= scrollSpeed * deltaTime;
        background2X -= scrollSpeed * deltaTime;

        if (background1X + background.getWidth() <= 0) {
            background1X = background2X + background.getWidth();
        }
        if (background2X + background.getWidth() <= 0) {
            background2X = background1X + background.getWidth();
        }
    }

    private void spawnObstacle() {
        obstacles.add(new Obstacle(Gdx.graphics.getWidth(), 100));
        lastObstacleTime = TimeUtils.nanoTime();
    }

    private void spawnCollectible() {
        float y = 100;
        float x = Gdx.graphics.getWidth();

        boolean overlap;
        do {
            overlap = false;
            for (Obstacle obstacle : obstacles) {
                if (Math.abs(obstacle.getPosition().x - x) < obstacle.getWidth()) {
                    overlap = true;
                    x += obstacle.getWidth();
                    break;
                }
            }
        } while (overlap);

        collectibles.add(new Collectible(x, y));
        lastCollectibleTime = TimeUtils.nanoTime();
    }

    private void resetGame() {
        obstacles.clear();
        collectibles.clear();
        player.reset();
        score = 0;
        powers = 0;
        vidas = 3;
        collectiblesCollected = 0;
        gameWon = false;
        gameOver = false;
        spawnObstacle();
    }

    @Override
    public void dispose() {
        batch.dispose();
        background.dispose();
        background2.dispose();
        lifeTexture.dispose();
        player.dispose();
        font.dispose();
        backgroundMusic.dispose();
        powerShotSound.dispose();
        jumpSound.dispose();
        for (Obstacle obstacle : obstacles) {
            obstacle.dispose();
        }
        for (Shot shot : shots) {
            shot.dispose();
        }
        for (Collectible collectible : collectibles) {
            collectible.dispose();
        }
    }

    private void userPower() {
        if (powers > 0) {
            Shot shot = new Shot(
                    player.getPosition().x + player.getBounds().width,
                    player.getPosition().y + player.getBounds().height / 2);
            shots.add(shot);
            powers--;
            powerShotSound.play();
            Gdx.app.log("Game", "Poder utilizado!");
        }
    }
}