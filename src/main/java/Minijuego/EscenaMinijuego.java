package Minijuego;

import Entidades.Futbolista;
import Entidades.Defensor;
import Entidades.Mediocampista;
import Entidades.Portero;
import Entidades.Delantero; 
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Es el controlador principal de la jugabilidad en tiempo real 2D. Controla la 
 * creación de la cancha, la lectura de controles (inputs), el cálculo de 
 * colisiones mediante Box2D y la sincronización a 60 FPS a través de la red.
 * @author GabrielTremaria
 */
public class EscenaMinijuego {

    private static Entity jugadorActivo;
    private static Entity balon;
    private static MagnetismoBalonComponent magnetismo;
    private static boolean controlesConfigurados = false; // Bandera para no duplicar controles
    private static javafx.scene.shape.Circle indicadorVisual; // Anillo indicador del futbolista
    
    // Variables para la sincronización correcta del minijuego
    private static boolean minijuegoTerminado = false;
    private static boolean fabricaRegistrada = false; 
    private static boolean colisionesConfiguradas = false;
    
    private static final int VELOCIDAD_JUGADOR = 150; // Variable estática y ya sin usar de la velocidad

    // Variables para cambiar de defensor
    private static java.util.List<Entity> defensoresCampo = new java.util.ArrayList<>();
    private static Entity porteroEntity;
    private static int indiceDefensor = 0;
    
    // Variables de red multijugador
    private static Logica.ComandoRed comandoLocal = new Logica.ComandoRed();
    private static Logica.ComandoRed comandoEnemigo = new Logica.ComandoRed();
    private static boolean hilosRedActivos = false;
    private static com.almasb.fxgl.time.TimerAction timerRed;
    private static Entity jugadorEnemigoActivo;
    private static javafx.scene.shape.Circle indicadorEnemigo;
    
    // Variables para evitar que el Cliente "spamee" cambios de jugador al dejar la tecla hundida
    private static boolean previoCambioDef = false;
    private static boolean previoCtrlPort = false;
    private static boolean previoClick = false;
    private static int indiceDefensorEnemigo = 0;
    
    // Para que el muro se muestre bien en ambas pantallas
    private static Entity muroVisualCliente;
    private static Entity muroFisicoHost;
    private static double anchoMuroHost;
    
    
    private static boolean isClienteOnline() {
        Logica.GestorJuego gestor = Logica.GestorJuego.getInstance();
        return !gestor.isEsHost() && gestor.getCliente() != null;
    }
    
   /**
     * Prepara el entorno gráfico y físico del minijuego 2D.
     * Limpia la pantalla, registra la fábrica de entidades (Patrón Factory), 
     * carga el mapa TMX y genera dinámicamente a los futbolistas basándose en 
     * qué portería se debe atacar. Finalmente, configura los manejadores de colisión (Box2D).
     * @param atacanteEsLocal Determina quién tiene el turno ofensivo. Si es true, 
     * el equipo local (Host) ataca. Si es false, defiende.
     */
    public static void iniciarMinijuego(Boolean atacanteEsLocal) {
        minijuegoTerminado = false; 
        jugadorActivo = null; // Limpiamos al jugador del turno anterior
        
        // 1. Limpiar pantalla y el mapa
        FXGL.getGameScene().clearUINodes();
        FXGL.getGameWorld().getEntitiesCopy().forEach(Entity::removeFromWorld); 
        FXGL.getPhysicsWorld().setGravity(0, 0);
        indicadorVisual = new javafx.scene.shape.Circle(15, javafx.scene.paint.Color.TRANSPARENT);
        indicadorVisual.setStroke(javafx.scene.paint.Color.YELLOW);
        indicadorVisual.setStrokeWidth(3);
        indicadorVisual.setEffect(new javafx.scene.effect.DropShadow(10, javafx.scene.paint.Color.YELLOW));
        
        // Verifica si ya hay una fábrica de entidades para evitar que crashee
        if (!fabricaRegistrada) {
            FXGL.getGameWorld().addEntityFactory(new FabricaMinijuego());
            fabricaRegistrada = true;
        }

        FXGL.setLevelFromMap("cancha.tmx");

        // 2. Extracción de datos
        Entidades.Futbolista miAtacante1, miAtacante2, miPortero, def1, def2, def3;
        boolean atacaHaciaArriba;

        if (atacanteEsLocal != null) {
            // MODO PARTIDO REAL (El Host nos dice quién ataca) ---
            Logica.GestorJuego gestor = Logica.GestorJuego.getInstance();
            
            // Si el atacante es el local, tomamos nuestro equipo local. Si no, tomamos al rival.
            Entidades.Equipo eqAtacante = atacanteEsLocal ? gestor.getEquipoLocal() : gestor.getEquipoRival();
            Entidades.Equipo eqDefensor = atacanteEsLocal ? gestor.getEquipoRival() : gestor.getEquipoLocal();
            
            java.util.List<Entidades.Futbolista> atacantes = SelectorMinijuego.seleccionarAtacantes(eqAtacante.getTitulares());
            java.util.List<Entidades.Futbolista> defensores = SelectorMinijuego.seleccionarDefensores(eqDefensor.getTitulares());
            
            miAtacante1 = atacantes.size() > 0 ? atacantes.get(0) : new Entidades.Delantero("Ata 1", eqAtacante.getNombrePais(), 3,3,3,1,10);
            miAtacante2 = atacantes.size() > 1 ? atacantes.get(1) : new Entidades.Delantero("Ata 2", eqAtacante.getNombrePais(), 3,3,3,1,10);
            miPortero = defensores.size() > 0 ? defensores.get(0) : new Entidades.Portero("Port", eqDefensor.getNombrePais(), 1,1,1,1,3,10);
            def1 = defensores.size() > 1 ? defensores.get(1) : new Entidades.Defensor("Def 1", eqDefensor.getNombrePais(), 2,1,1,3,10);
            def2 = defensores.size() > 2 ? defensores.get(2) : new Entidades.Defensor("Def 2", eqDefensor.getNombrePais(), 2,1,1,3,10);
            def3 = defensores.size() > 3 ? defensores.get(3) : new Entidades.Defensor("Def 3", eqDefensor.getNombrePais(), 2,1,1,3,10);

            atacaHaciaArriba = atacanteEsLocal; // El jugador local siempre ataca hacia arriba
        } else {
            // MODO PRUEBA YA NO EN USO
            miAtacante1 = new Entidades.Delantero("Messi", "Argentina", 4, 5, 5, 1, 100);
            miAtacante2 = new Entidades.Delantero("Di Maria", "Argentina", 4, 4, 4, 1, 90); 
            def1 = new Entidades.Defensor("Ramos", "España", 3, 2, 3, 5, 80);
            def2 = new Entidades.Defensor("Puyol", "España", 2, 1, 2, 5, 85);
            def3 = new Entidades.Mediocampista("Kroos", "Alemania", 3, 4, 5, 3, 90);
            miPortero = new Entidades.Portero("Lloris", "Francia", 2, 1, 2, 2, 5, 75);
            atacaHaciaArriba = true;
        }

        defensoresCampo.clear();
        porteroEntity = null;
        indiceDefensor = 0;

        // 3. Spawnear a los futbolistas de forma dinámica (Basado en las porterías)
        double centroX = FXGL.getAppWidth() / 2.0;

        // Buscamos las arquerías en el mapa para saber el tamaño real de la cancha
        java.util.List<Entity> porterias = FXGL.getGameWorld().getEntitiesByType(TipoEntidad.PORTERIA);
        double porteriaTopY = 50; 
        double porteriaBottomY = FXGL.getAppHeight() - 50;
        
        if (porterias.size() >= 2) {
            double y1 = porterias.get(0).getY();
            double y2 = porterias.get(1).getY();
            porteriaTopY = Math.min(y1, y2);
            porteriaBottomY = Math.max(y1, y2);
        }

        // Posiciones relativas a la portería que está siendo atacada
        double atacanteY, defensaY, porteroY;

        if (atacaHaciaArriba) {
            // El objetivo es la portería de arriba
            porteroY = porteriaTopY + 40; 
            defensaY = porteriaTopY + 130; 
            atacanteY = porteriaTopY + 350; 
        } else {
            // El objetivo es la portería de abajo
            porteroY = porteriaBottomY - 40; 
            defensaY = porteriaBottomY - 130; 
            atacanteY = porteriaBottomY - 350; 
        }

        balon = FXGL.spawn("balon", centroX, atacanteY);

        // Atacantes (Lado a lado)
        SpawnData dataAtacante1 = new SpawnData(centroX - 60, atacanteY);
        dataAtacante1.put("datos", miAtacante1);
        dataAtacante1.put("esAtacante", true);
        dataAtacante1.put("atacaHaciaArriba", atacaHaciaArriba); 
        Entity entidadAtacante1 = FXGL.spawn("futbolista", dataAtacante1);

        SpawnData dataAtacante2 = new SpawnData(centroX + 60, atacanteY);
        dataAtacante2.put("datos", miAtacante2);
        dataAtacante2.put("esAtacante", true);
        dataAtacante2.put("atacaHaciaArriba", atacaHaciaArriba);
        FXGL.spawn("futbolista", dataAtacante2);

        // Defensores (En línea horizontal: Izquierda, Centro, Derecha)
        SpawnData d1 = new SpawnData(centroX - 150, defensaY);
        d1.put("datos", def1);
        d1.put("esAtacante", false);
        d1.put("atacaHaciaArriba", !atacaHaciaArriba); 
        defensoresCampo.add(FXGL.spawn("futbolista", d1));

        SpawnData d2 = new SpawnData(centroX, defensaY);
        d2.put("datos", def2);
        d2.put("esAtacante", false);
        d2.put("atacaHaciaArriba", !atacaHaciaArriba);
        defensoresCampo.add(FXGL.spawn("futbolista", d2));

        SpawnData d3 = new SpawnData(centroX + 150, defensaY);
        d3.put("datos", def3);
        d3.put("esAtacante", false);
        d3.put("atacaHaciaArriba", !atacaHaciaArriba);
        defensoresCampo.add(FXGL.spawn("futbolista", d3));

        SpawnData dPort = new SpawnData(centroX, porteroY);
        dPort.put("datos", miPortero);
        dPort.put("esAtacante", false);
        dPort.put("atacaHaciaArriba", !atacaHaciaArriba);
        porteroEntity = FXGL.spawn("futbolista", dPort);

 
        // 4. Iniciar el magnetismo y asignar control
        indicadorEnemigo = new javafx.scene.shape.Circle(15, javafx.scene.paint.Color.TRANSPARENT);
        indicadorEnemigo.setStroke(javafx.scene.paint.Color.RED);
        indicadorEnemigo.setStrokeWidth(3);
        indicadorEnemigo.setEffect(new javafx.scene.effect.DropShadow(10, javafx.scene.paint.Color.RED));

        magnetismo = new MagnetismoBalonComponent(balon);
        
        if (atacanteEsLocal == null || atacanteEsLocal == true) {
            setJugadorActivo(entidadAtacante1); // El Host (Amarillo) ataca
            setJugadorEnemigoActivo(defensoresCampo.get(1)); // El Cliente (Rojo) defiende
            
            // El cliente nunca toca el magnetismo localmente
            if (!isClienteOnline()) { 
                jugadorActivo.addComponent(magnetismo);
                magnetismo.setTieneElBalon(true);
            }
        } else {
            setJugadorActivo(defensoresCampo.get(1)); // El Host (Amarillo) defiende
            setJugadorEnemigoActivo(entidadAtacante1); // El Cliente (Rojo) ataca
            
            if (!isClienteOnline()) { 
                jugadorEnemigoActivo.addComponent(magnetismo); 
                magnetismo.setTieneElBalon(true);
            }
        }

        // Para que los controles no crasheen
        javafx.application.Platform.runLater(() -> {
            if (!controlesConfigurados) {
                configurarControles();
                controlesConfigurados = true;
            }
        });

        // 5. Manejo de colisiones
        if (!colisionesConfiguradas) {

           // A) Limite de cancha
            FXGL.getPhysicsWorld().addCollisionHandler(new com.almasb.fxgl.physics.CollisionHandler(TipoEntidad.BALON, TipoEntidad.LIMITE_CANCHA) {
                @Override
                protected void onCollisionBegin(Entity balonEntity, Entity limite) {
                    if (isClienteOnline()) return; 
                    
                    FXGL.getGameTimer().runOnceAfter(() -> {
                        terminarMinijuego("¡OPORTUNIDAD PERDIDA!");
                    }, javafx.util.Duration.millis(16));
                }
            });

            // B) Portería (La linea de gol)
            FXGL.getPhysicsWorld().addCollisionHandler(new com.almasb.fxgl.physics.CollisionHandler(TipoEntidad.BALON, TipoEntidad.PORTERIA) {
                @Override
                protected void onCollisionBegin(Entity balonEntity, Entity porteria) {
                    if (isClienteOnline()) return;
                    
                    FXGL.getGameTimer().runOnceAfter(() -> {
                        PhysicsComponent fisicasBalon = balonEntity.getComponent(PhysicsComponent.class);
                        fisicasBalon.setLinearVelocity(0, 0);
                        fisicasBalon.setAngularVelocity(0);
                        fisicasBalon.setBodyType(com.almasb.fxgl.physics.box2d.dynamics.BodyType.STATIC);
                        
                        terminarMinijuego("¡GOLAZO!");
                    }, javafx.util.Duration.millis(16));
                }
            });

            // C) Atacante
            FXGL.getPhysicsWorld().addCollisionHandler(new com.almasb.fxgl.physics.CollisionHandler(TipoEntidad.BALON, TipoEntidad.JUGADOR_ATACANTE) {
                @Override
                protected void onCollisionBegin(Entity balonEntity, Entity atacante) {
                    if (isClienteOnline()) return; 
                    
                    if (atacante.hasComponent(MagnetismoBalonComponent.class)) return;
                    
                    FXGL.getGameTimer().runOnceAfter(() -> {
                        // Verificamos de quién es el jugador que acaba de recibir el balón
                        boolean hostEsAtacante = (jugadorActivo != null && jugadorActivo.getType() == TipoEntidad.JUGADOR_ATACANTE);
                        
                        if (hostEsAtacante) {
                            jugadorActivo.removeComponent(MagnetismoBalonComponent.class);
                            setJugadorActivo(atacante); // El Host se lo queda
                            magnetismo = new MagnetismoBalonComponent(balonEntity);
                            jugadorActivo.addComponent(magnetismo);
                        } else {
                            if (jugadorEnemigoActivo != null) jugadorEnemigoActivo.removeComponent(MagnetismoBalonComponent.class);
                            setJugadorEnemigoActivo(atacante); // El Cliente se lo queda
                            magnetismo = new MagnetismoBalonComponent(balonEntity);
                            jugadorEnemigoActivo.addComponent(magnetismo);
                        }
                        magnetismo.setTieneElBalon(true);
                    }, javafx.util.Duration.millis(16));
                }
            });

            // D) Defensor
            FXGL.getPhysicsWorld().addCollisionHandler(new com.almasb.fxgl.physics.CollisionHandler(TipoEntidad.BALON, TipoEntidad.JUGADOR_DEFENSOR) {
                @Override
                protected void onCollisionBegin(Entity balonEntity, Entity defensor) {
                    if (isClienteOnline()) return;
                    
                    FXGL.getGameTimer().runOnceAfter(() -> {
                        balonEntity.getComponent(PhysicsComponent.class).setLinearVelocity(0, 0);
                        terminarMinijuego("¡OPORTUNIDAD PERDIDA!");
                    }, javafx.util.Duration.millis(16));
                }
            });

            // E) Colisión defensor-atacante (para la intercepción y robo)
            FXGL.getPhysicsWorld().addCollisionHandler(new com.almasb.fxgl.physics.CollisionHandler(TipoEntidad.JUGADOR_DEFENSOR, TipoEntidad.JUGADOR_ATACANTE) {
                @Override
                protected void onCollisionBegin(Entity defensor, Entity atacante) {
                    if (isClienteOnline()) return; 
                    // Si chocan, el Host revisa si el atacante tiene el balón
                    if (atacante.hasComponent(MagnetismoBalonComponent.class)) {
                        MagnetismoBalonComponent mag = atacante.getComponent(MagnetismoBalonComponent.class);
                        
                        // Si el atacante tiene el balón, robo y despeje.
                        if (mag.isTieneElBalon()) {
                            FXGL.getGameTimer().runOnceAfter(() -> {
                                terminarMinijuego("¡ROBO Y DESPEJE!");
                            }, javafx.util.Duration.millis(16));
                        }
                    }
                }
            });
            
            colisionesConfiguradas = true; 
        }

        // Arrancar motor de red multijugador
        iniciarRedMinijuego();
    }


    /**
     * Mapea los eventos de teclado (WASD, Q, Espacio) y ratón a acciones del juego.
     * Si la instancia es el Servidor (Host), aplica las fuerzas físicas de FXGL
     * directamente sobre los cuerpos de Box2D. Si es el Cliente, únicamente 
     * registra las intenciones en el objeto ComandoRed para ser enviadas por los Sockets.
     */
   private static void configurarControles() {
        Logica.GestorJuego gestor = Logica.GestorJuego.getInstance();
        // Definimos si esta computadora es exclusivamente un Cliente Online
        boolean esCliente = !gestor.isEsHost() && gestor.getCliente() != null;

        // MOVIMIENTO (con WASD)
        // Tecla W: Arriba
        FXGL.getInput().addAction(new com.almasb.fxgl.input.UserAction("Arriba") {
            @Override protected void onAction() { 
                if (minijuegoTerminado) return;
                
                if (esCliente) {
                    comandoLocal.arriba = true; // El Cliente solo anota su intención
                } else {
                    // El Host mueve las físicas
                    if (jugadorActivo == null || !jugadorActivo.hasComponent(AtributosFutbolistaComponent.class)) return;
                    double vel = jugadorActivo.getComponent(AtributosFutbolistaComponent.class).getVelocidadFXGL();
                    jugadorActivo.getComponent(PhysicsComponent.class).setVelocityY(-vel); 
                }
            }
            @Override protected void onActionEnd() { 
                if (minijuegoTerminado) return;
                
                if (esCliente) {
                    comandoLocal.arriba = false; // El Cliente apaga la señal
                } else {
                    if (jugadorActivo == null || !jugadorActivo.hasComponent(PhysicsComponent.class)) return;
                    jugadorActivo.getComponent(PhysicsComponent.class).setVelocityY(0); 
                }
            }
        }, javafx.scene.input.KeyCode.W);

        // Tecla S: Abajo
        FXGL.getInput().addAction(new com.almasb.fxgl.input.UserAction("Abajo") {
            @Override protected void onAction() { 
                if (minijuegoTerminado) return;
                
                if (esCliente) {
                    comandoLocal.abajo = true;
                } else {
                    if (jugadorActivo == null || !jugadorActivo.hasComponent(AtributosFutbolistaComponent.class)) return;
                    double vel = jugadorActivo.getComponent(AtributosFutbolistaComponent.class).getVelocidadFXGL();
                    jugadorActivo.getComponent(PhysicsComponent.class).setVelocityY(vel); 
                }
            }
            @Override protected void onActionEnd() { 
                if (minijuegoTerminado) return;
                
                if (esCliente) {
                    comandoLocal.abajo = false;
                } else {
                    if (jugadorActivo == null || !jugadorActivo.hasComponent(PhysicsComponent.class)) return;
                    jugadorActivo.getComponent(PhysicsComponent.class).setVelocityY(0); 
                }
            }
        }, javafx.scene.input.KeyCode.S);

        // Tecla A: Izquierda
        FXGL.getInput().addAction(new com.almasb.fxgl.input.UserAction("Izquierda") {
            @Override protected void onAction() { 
                if (minijuegoTerminado) return;
                
                if (esCliente) {
                    comandoLocal.izquierda = true;
                } else {
                    if (jugadorActivo == null || !jugadorActivo.hasComponent(AtributosFutbolistaComponent.class)) return;
                    double vel = jugadorActivo.getComponent(AtributosFutbolistaComponent.class).getVelocidadFXGL();
                    jugadorActivo.getComponent(PhysicsComponent.class).setVelocityX(-vel); 
                }
            }
            @Override protected void onActionEnd() { 
                if (minijuegoTerminado) return;
                
                if (esCliente) {
                    comandoLocal.izquierda = false;
                } else {
                    if (jugadorActivo == null || !jugadorActivo.hasComponent(PhysicsComponent.class)) return;
                    jugadorActivo.getComponent(PhysicsComponent.class).setVelocityX(0); 
                }
            }
        }, javafx.scene.input.KeyCode.A);

        // Tecla D: Derecha
        FXGL.getInput().addAction(new com.almasb.fxgl.input.UserAction("Derecha") {
            @Override protected void onAction() { 
                if (minijuegoTerminado) return;
                
                if (esCliente) {
                    comandoLocal.derecha = true;
                } else {
                    if (jugadorActivo == null || !jugadorActivo.hasComponent(AtributosFutbolistaComponent.class)) return;
                    double vel = jugadorActivo.getComponent(AtributosFutbolistaComponent.class).getVelocidadFXGL();
                    jugadorActivo.getComponent(PhysicsComponent.class).setVelocityX(vel); 
                }
            }
            @Override protected void onActionEnd() { 
                if (minijuegoTerminado) return;
                
                if (esCliente) {
                    comandoLocal.derecha = false;
                } else {
                    if (jugadorActivo == null || !jugadorActivo.hasComponent(PhysicsComponent.class)) return;
                    jugadorActivo.getComponent(PhysicsComponent.class).setVelocityX(0); 
                }
            }
        }, javafx.scene.input.KeyCode.D);

        
        // CAMBIO DE JUGADOR (Defensores)
        // Tecla Q: Alternar entre los 3 defensores de campo
        FXGL.getInput().addAction(new com.almasb.fxgl.input.UserAction("Alternar Defensor") {
            @Override
            protected void onActionBegin() {
                if (minijuegoTerminado) return;
                
                if (esCliente) {
                    comandoLocal.cambiarDefensor = true;
                } else {
                    if (defensoresCampo.isEmpty()) return;
                    indiceDefensor = (indiceDefensor + 1) % defensoresCampo.size();
                    setJugadorActivo(defensoresCampo.get(indiceDefensor));
                }
            }
            @Override
            protected void onActionEnd() {
                if (esCliente) comandoLocal.cambiarDefensor = false;
            }
        }, javafx.scene.input.KeyCode.Q);

        // Barra Espaciadora: Controlar al Portero
        FXGL.getInput().addAction(new com.almasb.fxgl.input.UserAction("Controlar Portero") {
            @Override
            protected void onActionBegin() {
                if (minijuegoTerminado) return;
                
                if (esCliente) {
                    comandoLocal.controlarPortero = true;
                } else {
                    if (porteroEntity == null) return;
                    setJugadorActivo(porteroEntity);
                }
            }
            @Override
            protected void onActionEnd() {
                if (esCliente) comandoLocal.controlarPortero = false;
            }
        }, javafx.scene.input.KeyCode.SPACE);

        // asar y disparar (con el mouse)
       FXGL.getGameScene().getRoot().setOnMousePressed(evento -> {
            if (minijuegoTerminado) return;

            if (esCliente) {
                if (evento.getButton() == javafx.scene.input.MouseButton.PRIMARY) {
                    comandoLocal.clickIzquierdo = true;
                    // Enviamos la posición real del mundo físico, no la de la ventana
                    comandoLocal.mouseX = FXGL.getInput().getMousePositionWorld().getX();
                    comandoLocal.mouseY = FXGL.getInput().getMousePositionWorld().getY();
                }
            } else {
                if (jugadorActivo == null) return;
                if (evento.getButton() == javafx.scene.input.MouseButton.PRIMARY) {
                    if (magnetismo != null && magnetismo.isTieneElBalon() && jugadorActivo.hasComponent(MagnetismoBalonComponent.class)) {
                        realizarDisparo(jugadorActivo, FXGL.getInput().getMousePositionWorld());
                    } else {
                        desplegarMuroDefensivo(jugadorActivo);
                    }
                }
            }
        });

        // Limpiamos la señal del click cuando el Cliente suelta el botón
        FXGL.getGameScene().getRoot().setOnMouseReleased(evento -> {
            if (esCliente && evento.getButton() == javafx.scene.input.MouseButton.PRIMARY) {
                comandoLocal.clickIzquierdo = false;
            }
        });
    }

    /**
     * Inicia la comunicación multijugador para el minijuego. Lanza un hilo 
     * encargado de escuchar los paquetes entrantes sin congelar la interfaz. 
     * Además, inicializa un temporizador para transmitir el estado del minijuego o 
     * los comandos del jugador.
     */
    private static void iniciarRedMinijuego() {
        Logica.GestorJuego gestor = Logica.GestorJuego.getInstance();
        boolean esOnline = (gestor.getServidor() != null || gestor.getCliente() != null);
        if (!esOnline) return;

        hilosRedActivos = true;

        // 1. El hilo listener para evitar que se congele
        new Thread(() -> {
            try {
                java.io.ObjectInputStream in = gestor.isEsHost() ? gestor.getServidor().getIn() : gestor.getCliente().getIn();
                
                while (hilosRedActivos && !minijuegoTerminado) {
                    Object paquete = in.readObject(); // El hilo se pausa aquí hasta recibir datos
                    
                    if (gestor.isEsHost() && paquete instanceof Logica.ComandoRed) {
                        comandoEnemigo = (Logica.ComandoRed) paquete;
                    } 
                    else if (!gestor.isEsHost() && paquete instanceof Logica.EstadoMinijuego) {
                        Logica.EstadoMinijuego estadoRecibido = (Logica.EstadoMinijuego) paquete;
                        
                        // Si el Host nos avisa que el juego terminó, matamos este bucle instantáneamente para que libere la red antes de que intente leer otro paquete y se congele.
                        if (estadoRecibido.minijuegoTerminado) {
                            hilosRedActivos = false;
                        }
                        
                        javafx.application.Platform.runLater(() -> actualizarPantallaCliente(estadoRecibido));
                    }
                }
            } catch (Exception e) {
                if (hilosRedActivos) System.err.println("Desconexión en el minijuego: " + e.getMessage());
            }
        }).start();

        // 2. El speaker
        timerRed = FXGL.getGameTimer().runAtInterval(() -> {
            try {
                java.io.ObjectOutputStream out = gestor.isEsHost() ? gestor.getServidor().getOut() : gestor.getCliente().getOut();
                
                if (gestor.isEsHost()) {
                    // El Host dicta lo que está pasando
                    procesarComandoEnemigo(); 
                    Logica.EstadoMinijuego estadoActual = empaquetarEstado();
                    out.writeObject(estadoActual);
                } else {
                    // El Cliente envía sus intenciones de teclado/ratón
                    out.writeObject(comandoLocal);
                }
                out.reset(); // Limpia la memoria caché del Socket
            } catch (Exception e) {
                System.err.println("Error enviando paquete a 60FPS: " + e.getMessage());
            }
        }, javafx.util.Duration.millis(16));
    }

    /**
     * Escanea el mundo de físicas de Box2D y toma las coordenadas X, Y del balón, 
     * de todos los futbolistas y del muro defensivo activo y empaqueta esos datos
     * en un DTO.
     * @return El objeto EstadoMinijuego poblado y listo para ser serializado por la red.
     */
    private static Logica.EstadoMinijuego empaquetarEstado() {
        java.util.List<Entity> todosLosJugadores = FXGL.getGameWorld().getEntitiesByType(TipoEntidad.JUGADOR_ATACANTE, TipoEntidad.JUGADOR_DEFENSOR);
        Logica.EstadoMinijuego estado = new Logica.EstadoMinijuego(todosLosJugadores.size());
        
        if (balon != null) {
            boolean balonEnPosesion = false;
            
            // Buscamos si algún jugador tiene secuestrado el balón con el magnetismo
            for (Entity jugador : todosLosJugadores) {
                if (jugador.hasComponent(MagnetismoBalonComponent.class)) {
                    MagnetismoBalonComponent mag = jugador.getComponent(MagnetismoBalonComponent.class);
                    if (mag.isTieneElBalon()) {
                        // Empaquetamos la posición del dibujo falso en lugar de la coordenada -1000, -1000
                        Point2D posReal = mag.getPosicionVisualBalon();
                        estado.balonX = posReal.getX();
                        estado.balonY = posReal.getY();
                        balonEnPosesion = true;
                        break;
                    }
                }
            }
            
            // 2. Si nadie lo tiene (está volando por un pase o disparo), leemos la física normal
            if (!balonEnPosesion) {
                estado.balonX = balon.getX();
                estado.balonY = balon.getY();
            }
        }
        // Chequeo del muro defensivo
        estado.muroActivo = false;
        
        // Si el Host tiene un muro físico registrado y sigue vivo en el mundo, entonces:
        if (muroFisicoHost != null && muroFisicoHost.isActive()) {
            estado.muroActivo = true;
            estado.muroX = muroFisicoHost.getX();
            estado.muroY = muroFisicoHost.getY();
            estado.muroAncho = anchoMuroHost; 
        }
        
        for (int i = 0; i < todosLosJugadores.size(); i++) {
            estado.jugadoresX[i] = todosLosJugadores.get(i).getX();
            estado.jugadoresY[i] = todosLosJugadores.get(i).getY();
        }
        
        estado.minijuegoTerminado = minijuegoTerminado;
        return estado;
    }

    /**
     * Recibe el estado empaquetada con los datos de lo que pasa en el minijuego
     * desde el Servidor y sobreescribe el Estado que tiene el cliente. Apaga temporalmente
     * las fisicas del cliente para que sea el host quien dicte la realidad con
     * las coordenadas que transmita.
     * @param estado El DTO recibido por la red con las posiciones precisas de 
     * esta fracción de segundo.
     */
    private static void actualizarPantallaCliente(Logica.EstadoMinijuego estado) {
        // 1. Apaga el magnestismo dela pelota local
        // Si el cliente cree que tiene el balón, lo suelta para que sea el host quien dicte las coordenadas reales
        if (magnetismo != null && magnetismo.isTieneElBalon()) {
            magnetismo.setTieneElBalon(false);
        }

        if (balon != null && balon.hasComponent(PhysicsComponent.class)) {
            // Para que el cuerpo de Box2D a moverse, no solo al dibujo
            balon.getComponent(PhysicsComponent.class).overwritePosition(new Point2D(estado.balonX, estado.balonY));
        }
        
        java.util.List<Entity> todosLosJugadores = FXGL.getGameWorld().getEntitiesByType(TipoEntidad.JUGADOR_ATACANTE, TipoEntidad.JUGADOR_DEFENSOR);
        for (int i = 0; i < Math.min(todosLosJugadores.size(), estado.jugadoresX.length); i++) {
            Entity j = todosLosJugadores.get(i);
            
            if (j.hasComponent(PhysicsComponent.class)) {
                j.getComponent(PhysicsComponent.class).overwritePosition(new Point2D(estado.jugadoresX[i], estado.jugadoresY[i]));
            }
        }

        // LA SINCRONIZACIÓN PARA QUE EL CLIENTE LO VEA CORRECTAMENTE
        if (estado.muroActivo) {
            // Si el host dice que hay un muro, lo creamos si no existe
            if (muroVisualCliente == null || !muroVisualCliente.isActive()) {
                muroVisualCliente = FXGL.entityBuilder()
                        .at(estado.muroX, estado.muroY)
                        .view(new javafx.scene.shape.Rectangle(estado.muroAncho, 15, javafx.scene.paint.Color.YELLOW))
                        .buildAndAttach();
            } else {
                // Si ya existe, nos aseguramos de que esté en la posición exacta
                muroVisualCliente.setPosition(estado.muroX, estado.muroY);
            }
        } else {
            // Si el host dice que no hay muro, lo borramos de la pantalla (porque ya expiró su tiempo de vida)
            if (muroVisualCliente != null && muroVisualCliente.isActive()) {
                muroVisualCliente.removeFromWorld();
            }
        }
        // Si el Host dice que la jugada terminó, el Cliente obedece
        if (estado.minijuegoTerminado && !minijuegoTerminado) {
            terminarMinijuego(estado.mensajeFinal != null ? estado.mensajeFinal : "¡OPORTUNIDAD PERDIDA!");
        }
    }

    /**
     * El método que maneja la conexión entre las físicas y las interacciones del
     * jugador para efectuar un disparo si tiene el balón.
     * @param tirador Es el jugador que intenta efectuar un tiro
     * @param objetivo Las coordenadas a donde apunta el ratón del jugador para
     * disparara en esa dirección
     */
    private static void realizarDisparo(Entity tirador, Point2D objetivo) {
        double fuerza = tirador.getComponent(AtributosFutbolistaComponent.class).getFuerzaTiroFXGL();
        magnetismo.setTieneElBalon(false);
        Point2D direccion = objetivo.subtract(tirador.getCenter()).normalize();
        balon.getComponent(PhysicsComponent.class).setLinearVelocity(direccion.multiply(fuerza));
    }
    /**
     * El método que usa el Portero para poner un muro defensivo para su atajada.
     * @param caster Es el portero
     */
   private static void desplegarMuroDefensivo(Entity caster) {
        if (!caster.hasComponent(AtributosFutbolistaComponent.class)) return;
        Entidades.Futbolista datos = caster.getComponent(AtributosFutbolistaComponent.class).getDatos();
        
        if (datos instanceof Entidades.Portero) {
            Entidades.Portero portero = (Entidades.Portero) datos;
            double anchoMuro = portero.getNivelAtajada() * 30.0; 
            PhysicsComponent fisicasMuro = new PhysicsComponent();
            fisicasMuro.setBodyType(com.almasb.fxgl.physics.box2d.dynamics.BodyType.STATIC);
            
            // Cálculo de dirección para que el portero ponga el muro del lado correcto
            double mitadCancha = FXGL.getAppHeight() / 2.0;
            double offsetY = (caster.getY() < mitadCancha) ? 35 : -30; // +35 si está arriba, -30 si está abajo
            
            Entity muro = FXGL.entityBuilder().type(TipoEntidad.LIMITE_CANCHA) 
                    .at(caster.getX() - (anchoMuro / 2) + 15, caster.getY() + offsetY)
                    .bbox(new HitBox(BoundingShape.box(anchoMuro, 15)))
                    .view(new javafx.scene.shape.Rectangle(anchoMuro, 15, javafx.scene.paint.Color.YELLOW))
                    .with(fisicasMuro).buildAndAttach();
                    
            // Guardamos la referencia
            muroFisicoHost = muro;
            anchoMuroHost = anchoMuro;
                    
            FXGL.getGameTimer().runOnceAfter(() -> {
                if (muro.isActive()) muro.removeFromWorld();
            }, javafx.util.Duration.seconds(1));
        }
    }

   /**
    * Es el método para que el Host lea las interacciones del Cliente. Traduce 
    * los comandos de red recibidos del Cliente en fuerza de movimiento y
    * clics en pantalla
    */
    private static void procesarComandoEnemigo() {
        if (jugadorEnemigoActivo == null || !jugadorEnemigoActivo.hasComponent(PhysicsComponent.class)) return;

        // 1. Movimiento WASD del Cliente
        double vel = jugadorEnemigoActivo.getComponent(AtributosFutbolistaComponent.class).getVelocidadFXGL();
        PhysicsComponent fisicas = jugadorEnemigoActivo.getComponent(PhysicsComponent.class);
        double velX = 0; double velY = 0;
        if (comandoEnemigo.arriba) velY -= vel;
        if (comandoEnemigo.abajo) velY += vel;
        if (comandoEnemigo.izquierda) velX -= vel;
        if (comandoEnemigo.derecha) velX += vel;
        fisicas.setLinearVelocity(velX, velY);

        // 2. Cambio de defensor
        if (comandoEnemigo.cambiarDefensor && !previoCambioDef) {
            if (!defensoresCampo.isEmpty()) {
                indiceDefensorEnemigo = (indiceDefensorEnemigo + 1) % defensoresCampo.size();
                setJugadorEnemigoActivo(defensoresCampo.get(indiceDefensorEnemigo));
            }
        }
        previoCambioDef = comandoEnemigo.cambiarDefensor;

        // 3. Cambiar al portero
        if (comandoEnemigo.controlarPortero && !previoCtrlPort) {
            if (porteroEntity != null) { 
                setJugadorEnemigoActivo(porteroEntity);
            }
        }
        previoCtrlPort = comandoEnemigo.controlarPortero;

        // 4. Interacciones (Patear y el muro defensivo)
        if (comandoEnemigo.clickIzquierdo && !previoClick) {
            if (jugadorEnemigoActivo.hasComponent(MagnetismoBalonComponent.class)) {
                // Dispara hacia la coordenada exacta donde el cliente hizo clic
                realizarDisparo(jugadorEnemigoActivo, new Point2D(comandoEnemigo.mouseX, comandoEnemigo.mouseY));
            } else {
                desplegarMuroDefensivo(jugadorEnemigoActivo);
            }
        }
        previoClick = comandoEnemigo.clickIzquierdo;
    }
    
    // Método para que la IA sepa a quién no debe controlar
    public static Entity getJugadorActivo() {
        return jugadorActivo;
    }
    
    // SISTEMA DE CONTROL VISUAL
    private static void setJugadorActivo(Entity nuevoJugador) {
        // Si este jugador ya es el activo, se ignora la transferencia para no tener un montón de anillos
        if (jugadorActivo == nuevoJugador) {
            return; 
        }

        if (jugadorActivo != null) {
            jugadorActivo.getViewComponent().removeChild(indicadorVisual);
            jugadorActivo.getComponent(com.almasb.fxgl.physics.PhysicsComponent.class).setLinearVelocity(0, 0);
        }

        jugadorActivo = nuevoJugador;

        indicadorVisual.setTranslateX(-1); 
        indicadorVisual.setTranslateY(-1); 
        jugadorActivo.getViewComponent().addChild(indicadorVisual);
        
        // La cámara sigue al jugador que estamos controlando
        FXGL.getGameScene().getViewport().bindToEntity(jugadorActivo, FXGL.getAppWidth() / 2.0, FXGL.getAppHeight() / 2.0);
    }
    
    // SISTEMA DE CONTROL VISUAL DEL ENEMIGO
    public static Entity getJugadorEnemigoActivo() {
        return jugadorEnemigoActivo;
    }

    private static void setJugadorEnemigoActivo(Entity nuevoEnemigo) {
        // Verifica: Si este jugador ya es el activo enemigo, ignoramos
        if (jugadorEnemigoActivo == nuevoEnemigo) {
            return; 
        }

        // Le quitamos el anillo rojo al jugador anterior y lo frenamos
        if (jugadorEnemigoActivo != null) {
            jugadorEnemigoActivo.getViewComponent().removeChild(indicadorEnemigo);
            if (jugadorEnemigoActivo.hasComponent(com.almasb.fxgl.physics.PhysicsComponent.class)) {
                jugadorEnemigoActivo.getComponent(com.almasb.fxgl.physics.PhysicsComponent.class).setLinearVelocity(0, 0);
            }
        }

        // Asignamos el nuevo y le ponemos el anillo rojo
        jugadorEnemigoActivo = nuevoEnemigo;

        indicadorEnemigo.setTranslateX(-1); 
        indicadorEnemigo.setTranslateY(-1); 
        jugadorEnemigoActivo.getViewComponent().addChild(indicadorEnemigo);
    }
    
    
     /**
     * Hace una limpieza tras finalizar una jugada de peligro.Desvincula la cámara, 
     * congela las físicas, apaga la transmisión por Sockets y muestra el mensaje 
     * de resolución en pantalla. Tras un retraso programado, destruye el mundo 
     * 2D y devuelve el control al MotorSimulacion matemático.
     * @param mensaje El texto que se renderizará en el centro de la pantalla (ej. "¡GOLAZO!").
     */    private static void terminarMinijuego(String mensaje) {
        // Verifica: Si el minijuego ya terminó en este fotograma, ignoramos
        if (minijuegoTerminado) {
            return; 
        }
        minijuegoTerminado = true; // Cerramos la compuerta inmediatamente

        // El Host envía el último mensaje antes de apagar (o el Cliente se quedará atrapado en el minijuego)
        Logica.GestorJuego gestor = Logica.GestorJuego.getInstance();
        if (gestor.isEsHost() && gestor.getServidor() != null && hilosRedActivos) {
            try {
                Logica.EstadoMinijuego estadoFinal = empaquetarEstado();
                estadoFinal.mensajeFinal = mensaje;
                gestor.getServidor().getOut().writeObject(estadoFinal);
                gestor.getServidor().getOut().reset();
            } catch (Exception e) {
                System.err.println("Error enviando paquete final: " + e.getMessage());
            }
        }

        // Detenemos la transmisión y rompemos el hilo receptor
        hilosRedActivos = false;
        if (timerRed != null) {
            timerRed.expire();
        }

        // 1. Liberamos la cámara
        FXGL.getGameScene().getViewport().unbind();
        
        // 2. Detener jugadores
        FXGL.getGameWorld().getEntitiesByType(TipoEntidad.JUGADOR_ATACANTE, TipoEntidad.JUGADOR_DEFENSOR)
            .forEach(jugador -> {
                if (jugador.hasComponent(PhysicsComponent.class)) {
                    jugador.getComponent(PhysicsComponent.class).setLinearVelocity(0, 0);
                }
            });

        // 3. Crear el texto 
        javafx.scene.text.Text textoFin = new javafx.scene.text.Text(mensaje);
        textoFin.setFont(javafx.scene.text.Font.font("Impact", 50));
        textoFin.setFill(javafx.scene.paint.Color.WHITE);
        textoFin.setStroke(javafx.scene.paint.Color.BLACK);
        textoFin.setStrokeWidth(2);
        
        textoFin.setTranslateX(FXGL.getAppWidth() / 2.0 - 100); // Centrado aproximado
        textoFin.setTranslateY(FXGL.getAppHeight() / 2.0);
        textoFin.setEffect(new javafx.scene.effect.DropShadow(5, javafx.scene.paint.Color.BLACK));

        FXGL.addUINode(textoFin);

        // 4. Retorno al motor de simulación
        FXGL.getGameTimer().runOnceAfter(() -> {
            boolean huboGol = mensaje.equals("¡GOLAZO!");
            Logica.MotorSimulacion motor = Logica.GestorJuego.getInstance().getMotorActivo();
            
            // Solo el Host o modo offline le notifica al motor (El Cliente solo es un receptor visual)
            if (motor != null && (Logica.GestorJuego.getInstance().isEsHost() || Logica.GestorJuego.getInstance().getCliente() == null)) {
                if (huboGol) {
                    motor.registrarGol(motor.getEquipoAtacante());
                }
                motor.finalizarMinijuego(2, huboGol);
            }

            FXGL.getGameScene().clearUINodes();
            FXGL.getGameWorld().getEntitiesCopy().forEach(Entity::removeFromWorld);
            
            MenuJuego.PantallaSimulacion.reanudarDesdeMinijuego(); 

        }, javafx.util.Duration.seconds(2.5));
    }
}

