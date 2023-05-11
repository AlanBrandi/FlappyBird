package com.mygdx.flappybird;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import java.util.Random;

// Custom game.
public class FlappyBird extends Game {
	//Variáveis do jogo.
	SpriteBatch batch;

	private SplashScreen splashScreen;
	Texture[] birdArray;
	Texture backgroundTexture;
	Texture pipeDownTexture;

	Texture pipeTopTexture;
	Texture gameOverPanelTexture;

	Texture startLogo;

	Texture splashLogo;
	Texture startText;

	Texture[] coin;
	Texture coinCurrent;

	ShapeRenderer shapeRenderer;
    //Collider.
	Circle birdCircleCollider;
	Circle coinCircleCollider;
	Rectangle rectanglePipeTopCollider;

	Rectangle downCollider;
	Rectangle topCollider;
	Rectangle rectanglePipeDownCollider;

	float deviceWidth;
	float deviceHeight;
	float variation = 0;
	float gravity = 2;
	float birdInitialVerticalPosition;
	float positionPipeHorizontal;

	float topDeathHorizontal;
	float downDeathHorizontal;

	float topDeathVertical;
	float downDeathVertical;

	float positionCoinHorizontal;
	float positionPipeVertical;

	float positionCoinVertical;
	float spaceBetweenPipes;
	Random random;
	int points = 0;
	int lastPoint = 20;
	int maxScore = 0;
	boolean pipePassed = false;
	public int gameState = 0;
	float positionBirdHorizontal;
	BitmapFont scoreText;
	BitmapFont restartText;
	BitmapFont bestScoreText;
    //Sounds.
	Sound flyingSound;
	Sound collisionSound;
	Sound scoreSound;

	Sound coinSound;

	double randomNum;
	int coinIndex;

	Preferences preferences;

	OrthographicCamera camera;
	Viewport viewport;
	final float VIRTUAL_WIDTH = 720;
	final float VIRTUAL_HEIGHT = 1280;

	boolean hard;
	public FlappyBird() {

	}

	//Inicia texturas e objetos da cena.
	@Override
	public void create () {

		startTextures();
		startObjects();
		//Random int para qual moeda dar spawn, seja dourada, ou prata.
		randomNum = Math.random();
		if (randomNum < 0.2) { // 20% de chance para o índice 0
			coinIndex = 0;}
		else {
			coinIndex = 1;
		}

	}
    //Verifica estado do jogo, pontos e crias as texturas + colliders.
	@Override
	public void render () {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
  		if(gameState == 4){
			verifyGameState();
			drawTextures();
		}
		verifyGameState();
		validatePoints();
		drawTextures();
		detectCollisions();
	}
	public void ChangeGameState(int state){
		gameState = state;
	}
    //Método para iniciar as texturas (sprites do player, fundo canos e tela de gameover).
	private void startTextures(){
		birdArray = new Texture[3];
		birdArray[0] = new Texture("passaro1.png");
		birdArray[1] = new Texture("passaro2.png");
		birdArray[2] = new Texture("passaro3.png");
		startLogo = new Texture("Cool-Text-434103276361438.png");
		startText = new Texture("cooltext434103395484379.png");
		backgroundTexture = new Texture("fundo.png");
		pipeDownTexture = new Texture("cano_baixo_maior.png");
		pipeTopTexture = new Texture("cano_topo_maior.png");
		gameOverPanelTexture = new Texture("game_over.png");
		coin = new Texture[2];
		coin[0] = new Texture("Coin.png");
		coin[1] = new Texture("Coin2.png");
		splashLogo = new Texture("Splash_Screen.png");
	}
    //Instancia os canos, nas devidas posições + espaço entre eles + player etc.
	// + sounds fundo etc.
	private void startObjects(){
		batch = new SpriteBatch();
		random = new Random();

		deviceWidth = VIRTUAL_WIDTH;
		deviceHeight = VIRTUAL_HEIGHT;
		birdInitialVerticalPosition = deviceHeight/2;
		positionCoinVertical = deviceHeight/2;
		positionCoinHorizontal = deviceWidth;
		positionPipeHorizontal = deviceWidth;
		topDeathVertical = deviceHeight;
		topDeathHorizontal = deviceWidth/2;
		spaceBetweenPipes = 350;
		downDeathVertical = deviceHeight;

		scoreText = new BitmapFont();
		scoreText.setColor(Color.WHITE);
		scoreText.getData().setScale(10);

		restartText = new BitmapFont();
		restartText.setColor(Color.GREEN);
		restartText.getData().setScale(2);

		bestScoreText = new BitmapFont();
		bestScoreText.setColor(Color.RED);
		bestScoreText.getData().setScale(2);

		shapeRenderer = new ShapeRenderer();
		birdCircleCollider = new Circle();
		coinCircleCollider = new Circle();
		rectanglePipeDownCollider = new Rectangle();
		rectanglePipeTopCollider = new Rectangle();
		topCollider = new Rectangle();
		downCollider = new Rectangle();

		flyingSound = Gdx.audio.newSound(Gdx.files.internal("som_asa.wav"));
		collisionSound = Gdx.audio.newSound(Gdx.files.internal("som_batida.wav"));
		scoreSound = Gdx.audio.newSound(Gdx.files.internal("som_pontos.wav"));
		coinSound = Gdx.audio.newSound(Gdx.files.internal("CoinSound.wav"));

		preferences = Gdx.app.getPreferences("flappyBird");
		maxScore = preferences.getInteger("maxScore",0);

		camera = new OrthographicCamera();
		camera.position.set(VIRTUAL_WIDTH/2, VIRTUAL_HEIGHT/2,0);
		viewport = new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
	}
    // Verifica o status do jogo.
	private void verifyGameState(){
        //Se ele aperta na tela gravidade fica -15 (o que faz ele pular).
		boolean touchScreen = Gdx.input.justTouched();
		if( gameState == 0){
			if( touchScreen){
				gravity = -15;
				gameState = 1;
				flyingSound.play();
			}
			//Gameplay de fato.
		}else if (gameState == 1){
			if(touchScreen){
				gravity = -15;
				flyingSound.play();
			}
			//Posições dos canos atualizando para ir em direção ao player.
			positionPipeHorizontal -= Gdx.graphics.getDeltaTime() * 200;
			positionCoinHorizontal -= Gdx.graphics.getDeltaTime() * 200;
			if( positionPipeHorizontal < -pipeTopTexture.getWidth()){
				positionPipeHorizontal = deviceWidth;
				positionPipeVertical = random.nextInt(400) - 200;
				pipePassed = false;
			}
			if( positionCoinHorizontal < -coin[0].getWidth()){
				positionCoinHorizontal = deviceWidth + random.nextInt(Math.round((deviceWidth * .5f)));
				positionCoinVertical = random.nextInt(Math.round(deviceHeight)) - 200;;
			}
			if( birdInitialVerticalPosition > 0 || touchScreen)
				birdInitialVerticalPosition = birdInitialVerticalPosition - gravity;
			gravity++;
			//DeathScreen + Score.
		}else if( gameState == 2){
			if (points> maxScore){
				maxScore = points;
				preferences.putInteger("maxScore", maxScore);
				preferences.flush();
			}

			positionBirdHorizontal -= Gdx.graphics.getDeltaTime()*500;;
          //Se ele apertar na tela reinicia o jogo mudando o state os points e a gravidade.
			if(touchScreen){
				gameState = 0;
				points = 0;
				gravity = 0;
				positionBirdHorizontal = 0;
				birdInitialVerticalPosition = deviceHeight/2;
				positionPipeHorizontal = deviceWidth;
				spaceBetweenPipes = 350;
			}
		}
		/*else if(gameState == 4){
				if(touchScreen){
					gameState = 1;
				}*/
	}
//Detecção de colliders.
	private void detectCollisions(){
		birdCircleCollider.set(
				50 + positionBirdHorizontal + birdArray[0].getWidth()/2,
				birdInitialVerticalPosition + birdArray[0].getHeight()/2,
				birdArray[0].getWidth()/2
		);
		coinCircleCollider.set(
				50 + positionCoinHorizontal + coin[0].getWidth()/2,
				positionCoinVertical + coin[0].getHeight()/2,
				coin[0].getWidth()/2
		);
		rectanglePipeDownCollider.set(
				positionPipeHorizontal,
				deviceHeight/2 - pipeDownTexture.getHeight() - spaceBetweenPipes / 2 + positionPipeVertical,
				pipeDownTexture.getWidth(), pipeDownTexture.getHeight()
		);
		rectanglePipeTopCollider.set(
				positionPipeHorizontal, deviceHeight / 2 + spaceBetweenPipes / 2 + positionPipeVertical,
				pipeTopTexture.getWidth(), pipeTopTexture.getHeight()
		);
		//Collider para as colisões do chão e do teto.
		topCollider.set(
				topDeathHorizontal - 300, topDeathVertical + 100, deviceWidth, 300);
		downCollider.set(
				topDeathHorizontal - 300, downDeathVertical - (downDeathVertical - 1), deviceWidth, 1);

		randomNum = Math.random();
		boolean collidedPipeTop = Intersector.overlaps(birdCircleCollider, rectanglePipeTopCollider);
		boolean collidedPipeDown = Intersector.overlaps(birdCircleCollider, rectanglePipeDownCollider);
		//Collider moeda.
		boolean collidedCoin = Intersector.overlaps(birdCircleCollider, coinCircleCollider);
		boolean collidedDeath = Intersector.overlaps(birdCircleCollider, topCollider);
		boolean collidedDeath2 = Intersector.overlaps(birdCircleCollider, downCollider);
        if(collidedCoin){
			if(coinCurrent == coin[0]){
				points = points + 10;
				coinSound.play();
				//Caso pegue a moeda gera uma nova moeda com 20% de chance para a dourada.
				if (randomNum < 0.2) { // 20% de chance para o índice 0
					coinIndex = 0;}
				else {
					coinIndex = 1;
				}
			}
			else{
				points = points + 5;
				coinSound.play();
				if (randomNum < 0.2) { // 20% de chance para o índice 0
					coinIndex = 0;}
				else {
					coinIndex = 1;
				}
			}
			positionCoinHorizontal = -deviceHeight;
		}
		if (collidedPipeTop || collidedPipeDown){
			if (gameState == 1){
				collisionSound.play();
				gameState = 2;
			}
		}
		//Se bateu no céu ou no chão, ele coloca como o gameState 2.
		else if(collidedDeath){
			if(gameState == 1){
				gameState = 2;
			}
		}
		else if(collidedDeath2){
			if(gameState == 1){
				gameState = 2;
			}
		}
	}
//Adiciona as texturas na tela do jogo.
	private void drawTextures(){

			batch.setProjectionMatrix(camera.combined);
			batch.begin();
			batch.draw(backgroundTexture,0,0,deviceWidth, deviceHeight);
			batch.draw(birdArray[(int) variation],
					50 + positionBirdHorizontal, birdInitialVerticalPosition);
			batch.draw(pipeDownTexture, positionPipeHorizontal,
					deviceHeight/2 - pipeDownTexture.getHeight() - spaceBetweenPipes/2 + positionPipeVertical);
			batch.draw(pipeTopTexture, positionPipeHorizontal,
					deviceHeight/2 + spaceBetweenPipes/2 + positionPipeVertical);
			scoreText.draw(batch, String.valueOf(points), deviceWidth/2.2f,
					deviceHeight - 110);

			batch.draw(coin[coinIndex],
					50 + positionCoinHorizontal, positionCoinVertical);
			coinCurrent = coin[coinIndex];

		if(gameState == 2){
			batch.draw(gameOverPanelTexture, deviceWidth/2 - gameOverPanelTexture.getWidth()/2,
					deviceHeight/2);
			restartText.draw(batch,
					"Toque para reiniciar!", deviceWidth/2 - 140,
					deviceHeight/2 - gameOverPanelTexture.getHeight()/2);
			bestScoreText.draw(batch,
					"Seu recorde é: "+ maxScore + " pontos",
					deviceWidth/2-140, deviceHeight/2 - gameOverPanelTexture.getHeight());
		}
		//Caso gameState 0, adiciona tituto e legenda para o que alterar.
		if(gameState == 0) {
			batch.draw(startLogo, deviceWidth/2 - startLogo.getWidth()/2,
					900);
			batch.draw(startText, deviceWidth/2 - startText.getWidth()/2,
					deviceHeight/8);
			batch.draw(splashLogo, deviceWidth/2 - deviceWidth/2,
					deviceHeight);
		}
		if(gameState == 4){
			batch.draw(coin[1], deviceWidth/2, deviceHeight/2);
		}

		batch.end();
	}
//Confere se o player passou os canos se sim toca um som e adiciona os mesmos.
	public void validatePoints(){
		if( positionPipeHorizontal < 50-birdArray[0].getWidth()){
			if (!pipePassed){
				points++;
				pipePassed = true;
				scoreSound.play();
				if(points > lastPoint && hard == false){
					if(spaceBetweenPipes <= 150){
					hard = true;
					}
					else{
						spaceBetweenPipes -= 50;
					}
					lastPoint = points + 20;
				}
			}
		}

		variation += Gdx.graphics.getDeltaTime() * 10;

		if (variation > 3)
			variation = 0;
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}

	@Override
	public void dispose () {
		super.dispose();
		splashScreen.dispose();
	}
}