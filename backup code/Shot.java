import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Shot {
    private static final int FRAME_COLS = 5; // Número de colunas na spritesheet
    private static final int FRAME_ROWS = 1; // Número de linhas na spritesheet

    private Animation<TextureRegion> animation; // Animação do tiro
    private Texture spriteSheet; // Spritesheet do tiro
    private Vector2 position; // Posição do tiro na tela
    private float speed = 1000f; // Velocidade do tiro
    private Rectangle bounds; // Limites do tiro para verificação de colisão
    private float stateTime; // Tempo de estado para a animação

    // Construtor da classe Shot
    public Shot(float x, float y) {
        // Carregar a spritesheet do tiro
        spriteSheet = new Texture(Gdx.files.internal("assets/Beam/Charge_2.png"));

        // Dividir a spritesheet em frames individuais
        TextureRegion[][] tmp = TextureRegion.split(spriteSheet,
                spriteSheet.getWidth() / FRAME_COLS,
                spriteSheet.getHeight() / FRAME_ROWS);

        // Colocar os frames em um Array
        Array<TextureRegion> frames = new Array<>();
        for (int i = 0; i < FRAME_ROWS; i++) {
            for (int j = 0; j < FRAME_COLS; j++) {
                frames.add(tmp[i][j]);
            }
        }

        // Criar a animação do tiro
        animation = new Animation<>(0.1f, frames, Animation.PlayMode.LOOP);

        position = new Vector2(x, y); // Define a posição inicial do tiro
        bounds = new Rectangle(position.x, position.y, spriteSheet.getWidth() / FRAME_COLS,
                spriteSheet.getHeight() / FRAME_ROWS); // Inicializa os limites do tiro
        stateTime = 0f; // Inicializa o tempo de estado
    }

    // Método para atualizar a posição do tiro
    public void update(float deltaTime) {
        position.x += speed * deltaTime; // Move o tiro para a direita com base na velocidade e no deltaTime
        bounds.setPosition(position.x, position.y); // Atualiza a posição dos limites do tiro
        stateTime += deltaTime; // Atualiza o tempo de estado para a animação
    }

    // Método para desenhar o tiro na tela
    public void draw(SpriteBatch batch) {
        TextureRegion currentFrame = animation.getKeyFrame(stateTime, true); // Obter o frame atual da animação
        batch.draw(currentFrame, position.x, position.y); // Desenhar o frame atual na posição do tiro
    }

    // Método para obter os limites do tiro para verificação de colisão
    public Rectangle getBound() {
        return bounds;
    }

    // Método para obter a posição X do tiro
    public float getX() {
        return position.x;
    }

    // Método para liberar os recursos associados ao tiro
    public void dispose() {
        spriteSheet.dispose(); // Libera a memória ocupada pela spritesheet do tiro
    }
}
