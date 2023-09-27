# APPSemana6
 Sistema de Cálculo de Despacho de Envío con Geolocalización


¡Por supuesto! Aquí tienes un `README.md` basado en la información que proporcionaste:

---

# APPSemana6 - Sistema de Cálculo de Despacho

APPSemana6 es una aplicación Android diseñada para calcular costos de envío basándose en la distancia entre la bodega y la ubicación del usuario. Utiliza la fórmula Haversine para calcular dicha distancia y una estructura condicional para determinar el costo según la distancia y el total de la compra.

## Configuración

### 1. Google Cloud API

#### a. AndroidManifest.kt:

Antes de iniciar, es necesario proporcionar tu API Key de Google Cloud en `AndroidManifest.kt`:

```xml
<meta-data
    android:name="com.google.android.geo.API_KEY"
    android:value= "YOUR_GOOGLE_CLOUD_API_KEY"/> <!-- Inserta acá el API de tu Google Maps -->
```

#### b. Menuactivity.kt:

Asegúrate de también añadir tu API Key en `Menuactivity.kt`:

```kotlin
if (!Places.isInitialized()) {
    Places.initialize(applicationContext, "YOUR_GOOGLE_CLOUD_API_KEY") // Deja acá tu API de Google para abrir el mapa
}
```

### 2. Actualizaciones de Dependencias:

Es esencial que los imports y las dependencias en el archivo `build.gradle` (a nivel de proyecto) sean actualizados según las últimas versiones disponibles o las especificadas en la documentación.

### 3. Integración con Firebase:

Para que las funcionalidades de `LoginActivity.kt` y `RegisterActivity.kt` funcionen correctamente, debes agregar el archivo `.json` proporcionado por Firebase a tu proyecto.

## Uso

Una vez configurada la aplicación, puedes iniciar sesión o registrarte mediante las opciones disponibles. Posteriormente, tendrás la opción de buscar una dirección manualmente o utilizar la ubicación actual de tu dispositivo para calcular el costo de envío.

