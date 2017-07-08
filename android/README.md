# ChobiTemp
Aplicación de control de temperatura del sistema de iluminación contextual IoT *ChobiTemp* (de aquí en adelante *chobi*).
## Arquitectura y entorno

### IOT Gateway
La aplicación android está pensada para comunicarse con un servidor intermedio que actúe de gateway con el sistema de IOT. Para su funcionamiento, dicho gateway debe tener los siguientes servicios rest disponibles.

#### Services
##### Temperature
* `/temperature/cloud`: Devuelve en texto plano la temperatura actual en el exterior, basada en la ubicación actual del *chobi* y obtenida de un servicio de terceros.
* `/temperature/chobi`: Devuelve en texto plano la temperatura actual en el interior, basada en el sensor incorporado en el *chobi*.

Al socilitar la temperatura de cualquiera de los 2 servicios, el *chobi* debería pasar a mostrar dicha temperatura. Esto significa, comenzar a utilizar dicho source tanto para el panel digital como para la iluminación contextual.

##### Contextual Lights
* `/switchLights`: Alterna el estado de las luces contextuales, pasando dicho estado a *encendido* o *apagado*, dependiendo el estado actual (siempre invierte). Devuelve "true" en caso de que el __nuevo__ estado (es decir, el estado final) sea *encendido*, y "false" en caso contrario.

### Refresco automático
La aplicacion intenta refrescar automáticamente cada 1 segundo el estado de la temperatura en background. Si falla esto, dicho refresco automático se desactiva, y deberá ser refrescado manualmente o volver a abrir la aplicación para reactivarlo. Al minimizar o cerrar la aplicación esto también se desactiva, para ahorrar batería.
A tener en cuenta que el servidor no necesita refrescar el estado del chobi ante cada pedido, pudiendo utilizar un cache intermedio.
## Tareas pendientes y posibles bugs
Es probable que con la nueva arquitectura de refresco automático, si se cambia de origen de la temperatura (chobi/cloud) mientras se estaba actualizando, el próximo resultado traiga la temperatura para el origen anterior. Sin embargo, el worker debería recargar al segundo la temperatura con el origen correcto.
Puede llegar a causar glitches visuales en redes de bajo rendimiento.
