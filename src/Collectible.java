import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Collectible {
    private Texture texture;
    private Vector2 position;
    private Rectangle bounds;
    private final float SCALE = 2.0f; // Reduzir o tamanho do coletável pela metade

    public Collectible(float x, float y) {
        texture = new Texture(Gdx.files.internal("assets//Coletavel/rato_coletavel.png"));
        position = new Vector2(x, y);
        bounds = new Rectangle(position.x, position.y, texture.getWidth() * SCALE, texture.getHeight() * SCALE);
    }

    public void update(float deltaTime) {
        // Movimentação do objeto (opcional)
        position.x -= 100 * deltaTime; // Move o objeto para a esquerda
        bounds.setPosition(position);
    }

    public void draw(SpriteBatch batch) {
        batch.draw(texture, position.x, position.y, texture.getWidth() * SCALE, texture.getHeight() * SCALE); // Desenhar
                                                                                                              // com
                                                                                                              // tamanho
                                                                                                              // reduzido
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void dispose() {
        texture.dispose();
    }
}
