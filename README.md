# 🚀 Through Spcae

# Introducción
 
Through Space es un videojuego arcade desarrollado con Java y libGDX como parte del proyecto basado en los tutoriales A Simple Game y Extending the Simple Game.

En el juego, el jugador controla una nave espacial que se mueve por el espacio mientras intenta rescatar astronautas perdidos. Durante la partida aparecen distintos objetos peligrosos que deben evitarse para no perder vidas.

El objetivo del juego es rescatar cuatro astronautas antes de perder todas las vidas, mientras se recogen monedas para aumentar la puntuación.

# Desarrollo
Lógica del juego-
El jugador controla una nave espacial que puede moverse libremente por la pantalla.
Controles:
Teclas de dirección → mover la nave
Pantalla táctil (Android) → mover la nave hacia el punto indicado

Las colisiones se detectan mediante rectángulos (AABB collision detection).
El movimiento utiliza Delta Time, lo que permite que el juego funcione de forma consistente independientemente del hardware.

Condiciones de finalización:
- Victoria: rescatar 4 astronautas
- Derrota: perder todas las vidas.
  
# Estructura del juego

El proyecto utiliza la arquitectura Game + Screen de libGDX.

Las clases principales son:

# MenuScreen
Pantalla inicial del juego donde se muestra el menú principal y el botón para comenzar la partida.

# GameScreen
Contiene la lógica principal del juego:
Movimiento del jugador
Aparición aleatoria de objetos
Sistema de colisiones
Sistema de puntuación
Sistema de vidas

# EndScreen
Muestra la pantalla final del juego dependiendo del resultado:
Pantalla de victoria
Pantalla de derrota
Además permite volver al menú principal.

# Conclusiones

Este proyecto permitió comprender mejor la estructura básica de un videojuego utilizando libGDX, incluyendo:
- El ciclo principal del juego (Input → Update → Render)
- El uso de Delta Time para movimientos independientes del hardware
- La detección de colisiones mediante rectángulos
- La organización del código usando pantallas (Screens)
  
Además, el juego amplía el ejemplo clásico Drop añadiendo nuevas mecánicas como:
- Sistema de vidas
- Diferentes tipos de obstáculos
- Sistema de puntuación
- Condiciones de victoria y derrota

# Plataformas

Este proyecto está organizado en módulos siguiendo la estructura estándar de libGDX:
- core → contiene la lógica principal del juego compartida por todas las plataformas.
- lwjgl3 → plataforma principal para ejecutar el juego en Desktop utilizando LWJGL3.
