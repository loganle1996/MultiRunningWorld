/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MVCpattern;

import Bullet.Bullet;
import Entity.Entity;
import Entity.Id;
import Graphics.Sprite;
import Graphics.SpriteSheet;
import java.util.LinkedList;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import tile.Tile;
import tile.TileCache;
import tile.GameMap;

/**
 *
 * @author owne
 */
public class GameModel extends Application{
    static Scene mainScene;
    static GraphicsContext gc;
    public static int WIDTH =1600;
    public static int HEIGHT = 1600;
    static Image image1,image2,image3,playerImage;
    private Stage mainStage;
    public static SpriteSheet sheet;
    public static Sprite playerSprite [] = new Sprite[6];
    public static GameMap gameMap = new GameMap();
    public Image imageLeft,imageRight;
    public LinkedList<Entity> entity = new LinkedList<Entity>();
    public LinkedList<Entity> copiedEntity;
    public LinkedList<Tile> tile = new LinkedList<Tile>();;
//    public LinkedList<Entity> aiEntity = new LinkedList<Entity>();
    public LinkedList<Bullet> bullets = new LinkedList<Bullet>();
    public TileCache tileCache = new TileCache();
    public LinkedList<Bullet> copiedBullets;
//    public LinkedList<Camera> cameraList = new LinkedList<Camera>();
    public Entity player1;
    private PerspectiveCamera camera;
    
    public GameModel(){
    }
    //add objects to Linkedlist
    public void addEntity(Entity en){
        entity.add(en);
    }
    public void removeEntity(Entity en){
        entity.remove(en);
    }
//    public void addAiEntity(Entity en){
//        aiEntity.add(en);
//    }
//    public void removeAiEntity(Entity en ){
//        aiEntity.remove(en);
//    }
    public void addTile(Tile ti){
        tile.add(ti);
    }
    public void addBullets(Bullet bullet){
        bullets.add(bullet);
    }
    public void removeBullets(Bullet bullet){
        bullets.remove(bullet);
    }
    //Accessors and mutators

    public LinkedList<Bullet> getBullets() {
        return bullets;
    }

    public void setBullets(LinkedList<Bullet> bullets) {
        this.bullets = bullets;
    }
    
    public LinkedList<Entity> getEntity() {
        return entity;
    }

    public void setEntity(LinkedList<Entity> entity) {
        this.entity = entity;
    }

//    public LinkedList<Entity> getAiEntity() {
//        return aiEntity;
//    }

//    public void setAiEntity(LinkedList<Entity> aiEntity) {
//        this.aiEntity = aiEntity;
//    }

    public LinkedList<Tile> getTile() {
        return tile;
    }

    public void setTile(LinkedList<Tile> tile) {
        this.tile = tile;
    }

    public void removeTile(Tile ti) {
        tile.remove(ti);
    }
    public LinkedList<Tile> getTileList(){
        return tile;
    }
    public void TickModelGame(){
        copiedEntity = new LinkedList<Entity>(entity);
        for(Entity en: copiedEntity){
            en.tick();
        }
        for(Tile ti: tile){
            ti.tick();
        }
        copiedBullets = new LinkedList<Bullet>(bullets);
        for(Bullet bullet : copiedBullets){
            bullet.tick();
        }
    }
    
    public void renderTile(GraphicsContext g){
        for(Tile ti: tile){
            ti.render(g);
        }
    }
    public void renderEntities(GraphicsContext g){
        for(Entity en: entity){
            en.render(g);
        }
    }
    public void renderModelGame(GraphicsContext g){
    for(Entity en: entity){
            en.render(g);
        }
        for(Tile ti: tile){
            ti.render(g);
        }
    }
    public void renderBulletOfPlayer(Image imageleft,Image imageRight){
        copiedBullets = new LinkedList<Bullet>(bullets);
        for(Bullet bullet: copiedBullets){
            for(Entity en : entity){
                if(en.getId()== Id.player){
                    bullet.renderBullet(gc, en,imageleft,imageRight);
                }
            }
        }
    }
    @Override
    public void start(Stage primaryStage) throws Exception {
        
        //Create a camera
        this.camera = new PerspectiveCamera(true);

        primaryStage.setTitle("ArcaneArena");
        Group root = new Group();
        mainScene = new Scene(root);
        primaryStage.setScene(mainScene);
        mainScene.setCamera(camera);
        camera.setNearClip(0.1);
        camera.setFarClip(1500);
        camera.setTranslateX(800);
        camera.setTranslateY(800);
        camera.setTranslateZ(-1000);

        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        root.getChildren().add(canvas);
        root.getChildren().add(camera);
        //Associate gc to the canvas to draw.
        gc = canvas.getGraphicsContext2D();
        //Get all images from all resources.
        loadGraphics();
        player1 = gameMap.returnPlayer1();
// main scene listens for keyevent
        prepareKeyEvent(mainScene);
        final long startNanoTime = System.nanoTime();
        new AnimationTimer()
        {
            @Override
            public void handle(long currentNanoTime)
            {
                double t = (currentNanoTime - startNanoTime) / 1000000000.0; 

                double x = 232 + 128 * Math.cos(t);
                double y = 232 + 128 * Math.sin(t);
                tickAndRenderModel();
                camera.setTranslateX(player1.getX());
                camera.setTranslateY(player1.getY());
                
            }
        }.start();
        primaryStage.show();
    }
    private void loadGraphics()
    {
            imageLeft = new Image("fireball.jpeg");
            imageRight = new Image("fireball.jpeg");
            gameMap.mapData();
            tileCache.loadCache(this);
            sheet = new SpriteSheet("gameSheet5.png");
            for(int i = 0; i< playerSprite.length;i++){
                playerSprite[i] = new Sprite(sheet,i+1,1);
            }
            
            gameMap.addAllObjectsToGameModel(this,tileCache);
    }
    public static void main(String[] args) {
        launch(args);
    }
    public void tickAndRenderModel(){
         gc.clearRect(0, 0, WIDTH, HEIGHT);
         this.TickModelGame();
         this.renderModelGame(gc);
         this.renderBulletOfPlayer(imageLeft, imageRight);
    }
    public void prepareKeyEvent(Scene mainScene) {
        mainScene.setOnKeyPressed(new EventHandler<KeyEvent>()
        {
            @Override
            public void handle(KeyEvent event)
            {
                //currentlyActiveKeys.add(event.getCode().toString());
                for(Entity en: getEntity()){
                    if(en.getId() == Id.player){
                        if(event.getCode() == KeyCode.SPACE){
                            if(en.isJumping() == false){
                                en.setJumping(true);
                                en.setVelY(-15);
                            }
                        }
                        if(event.getCode() == KeyCode.R){
                            en.shootFireBall(gc);
                        }
                            if(event.getCode() == KeyCode.A){
                              en.setIsMovingLeft(true);
                            }
                            if(event.getCode() == KeyCode.D){
                                en.setIsMovingRight(true);
                            }
                    }

                }
            }
        });
        mainScene.setOnKeyReleased(new EventHandler<KeyEvent>()
        {
            @Override
            public void handle(KeyEvent event)
            {
                for(Entity en: getEntity()){   
                    if(en.getId() == Id.player){
                        if(event.getCode() == KeyCode.SPACE){
                        }
                        if(event.getCode() == KeyCode.A){
                            en.setIsMovingLeft(false);
                        }
                        if(event.getCode() == KeyCode.D){
                            en.setIsMovingRight(false);
                        }
                    }
                }
            }
        });      
    }
    public void display(){
        String[] args = null;
        main(args);
    }
}
