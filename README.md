# Aplicación de Ejercicios Android

Este proyecto contiene dos ejercicios de Android Studio accesibles a través de un menú principal con botones de navegación.

## Ejercicio 1: Valoración de Paisajes

### Descripción
Aplicación interactiva que muestra una galería de paisajes deslizables con funcionalidad de valoración.
La aplicación incluye:
- Animaciones de texto al iniciar
- Galería de imágenes deslizable horizontalmente
- Sistema de valoración con RatingBar (10 estrellas)
- Efectos de sonido al cambiar imágenes
- Persistencia de valoraciones usando SharedPreferences

### Características técnicas

- **Componentes principales**:
  - `ViewFlipper` para la galería de imágenes
  - `RatingBar` para el sistema de valoración
  - `MediaPlayer` para efectos de sonido
  - `SharedPreferences` para almacenamiento persistente

- **Interacción**:
  - Deslizar horizontalmente para cambiar imágenes
  - Valoración persistente por cada imagen

### Uso
1. Al iniciar, se muestra una animación de texto
2. Tras la animación, aparece la galería de paisajes
3. Deslizar izquierda/derecha para navegar entre imágenes
4. Usar el RatingBar inferior para valorar cada imagen
5. Las valoraciones se guardan automáticamente


# Ejercicio 2: Descarga y Visualización de Imágenes

## Descripción
Aplicación Android que descarga y muestra imágenes desde URLs remotas, con funcionalidad de manejo de errores y visualización interactiva.

## Características Principales

### Descarga Asíncrona
- Descarga lista de imágenes desde URL remota
- Proceso en segundo plano usando Corrutinas
- Barra de progreso en tiempo real

### Carga Inteligente de Imágenes
- Validación de URLs antes de descargar
- Uso de Picasso para caché y optimización
- Placeholders para imágenes en carga/error
  
###  Registro de Errores
- Detección y registro de fallos
- Archivo de errores en la carpeta Downloads
- Formato detallado con timestamp

### Controles Interactivos
- Navegación manual con botones
- Transiciones animadas 

